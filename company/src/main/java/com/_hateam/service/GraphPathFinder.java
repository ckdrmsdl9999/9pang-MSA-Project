package com._hateam.service;

import com._hateam.entity.Hub;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.*;

import static java.util.Collections.singletonList;

public class GraphPathFinder {

    private final HubGraph hubGraph;
    // hubMap: 허브 이름과 Hub 엔티티를 매핑한 맵 (이미 초기화된 데이터를 사용)
    private final Map<String, Hub> hubMap;

    public GraphPathFinder(HubGraph hubGraph, Map<String, Hub> hubMap) {
        this.hubGraph = hubGraph;
        this.hubMap = hubMap;
    }

    /**
     * 수정된 다익스트라 알고리즘:
     * 각 간선의 비용은 Haversine 거리이며, 시작 노드를 제외한 각 이동마다 30km의 페널티를 추가합니다.
     * 추가로, 각 노드까지 누적된 실제 거리를 기록하여, 각 간선(세그먼트) 별 실제 거리도 계산합니다.
     *
     * @param start 시작 허브 이름
     * @param end   도착 허브 이름
     * @return 최단 경로 결과 (경로, 누적 실제 거리, 패널티, 총 비용, 그리고 각 간선의 실제 거리 목록)
     */
    public PathResult findMinimumCostPath(String start, String end) {
        Map<String, Double> totalCostMap = new HashMap<>();
        // 초기 상태: 시작 노드의 비용, 누적 거리, 패널티는 모두 0, 경로=[start], 누적 거리 리스트=[0.0]
        totalCostMap.put(start, 0.0);
        PriorityQueue<NodeState> queue = new PriorityQueue<>(Comparator.comparingDouble(NodeState::getCost));
        queue.add(new NodeState(start, 0.0, 0.0, 0.0, new ArrayList<>(singletonList(start)), new ArrayList<>(singletonList(0.0))));

        while (!queue.isEmpty()) {
            NodeState current = queue.poll();
            String currentNode = current.getNode();
            double currentCost = current.getCost();

            if (currentNode.equals(end)) {
                // 최종 누적 거리 리스트(current.nodeDistances)를 이용해 세그먼트별 거리를 계산하여 반환
                return new PathResult(current.getPath(), current.getActualDistance(), current.getPenalty(), currentCost, current.getNodeDistances());
            }
            if (currentCost > totalCostMap.getOrDefault(currentNode, Double.MAX_VALUE)) {
                continue;
            }
            List<String> neighbors = hubGraph.getNeighbors(currentNode);
            for (String neighbor : neighbors) {
                Hub source = hubMap.get(currentNode);
                Hub dest = hubMap.get(neighbor);
                if (source == null || dest == null) continue;

                double edgeDistance;
                try {
                    double lat1 = Double.parseDouble(source.getLatitude());
                    double lon1 = Double.parseDouble(source.getLongitude());
                    double lat2 = Double.parseDouble(dest.getLatitude());
                    double lon2 = Double.parseDouble(dest.getLongitude());
                    edgeDistance = haversine(lat1, lon1, lat2, lon2);
                } catch (NumberFormatException e) {
                    continue;
                }
                // 시작 노드는 패널티 없이, 그 외에는 30km의 추가 페널티 적용
                double additionalPenalty = currentNode.equals(start) ? 0.0 : 30.0;
                double newActualDistance = current.getActualDistance() + edgeDistance;
                double newPenalty = current.getPenalty() + additionalPenalty;
                double newCost = newActualDistance + newPenalty;

                if (newCost < totalCostMap.getOrDefault(neighbor, Double.MAX_VALUE)) {
                    totalCostMap.put(neighbor, newCost);
                    List<String> newPath = new ArrayList<>(current.getPath());
                    newPath.add(neighbor);
                    List<Double> newCumulativeDistances = new ArrayList<>(current.getNodeDistances());
                    newCumulativeDistances.add(newActualDistance);
                    queue.add(new NodeState(neighbor, newCost, newActualDistance, newPenalty, newPath, newCumulativeDistances));
                }
            }
        }
        return null; // 경로를 찾지 못한 경우
    }

    /**
     * Haversine 공식으로 두 좌표 사이의 거리를 킬로미터 단위로 계산합니다.
     */
    public double haversine(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // 지구의 평균 반지름 (킬로미터)
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return (double) (Math.round(R * c) * 100) / 100;
    }

    public static class NodeState {
        private final String node;
        private final double cost;
        private final double actualDistance;
        private final double penalty;
        private final List<String> path;
        private final List<Double> nodeDistances; // 시작부터 해당 노드까지의 누적 실제 거리

        public NodeState(String node, double cost, double actualDistance, double penalty, List<String> path, List<Double> nodeDistances) {
            this.node = node;
            this.cost = cost;
            this.actualDistance = actualDistance;
            this.penalty = penalty;
            this.path = path;
            this.nodeDistances = nodeDistances;
        }

        public String getNode() {
            return node;
        }

        public double getCost() {
            return cost;
        }

        public double getActualDistance() {
            return actualDistance;
        }

        public double getPenalty() {
            return penalty;
        }

        public List<String> getPath() {
            return path;
        }

        public List<Double> getNodeDistances() {
            return nodeDistances;
        }
    }

    public static class PathResult {
        @JsonProperty
        private final List<String> path;

        @JsonProperty("actualDistance")
        private final double actualDistance;

        @JsonProperty("penaltyDistance")
        private final double penalty;

        @JsonProperty("totalCost")
        private final double totalCost;

        @JsonProperty
        private final List<Double> nodeDistances;    // Cumulative distances (e.g., [0, d1, d1+d2, ...])

        @JsonProperty
        private final List<Double> segmentDistances; // Segment distances (e.g., [d1, d2, ...])

        // Constructor – Jackson will use this if default typing is activated
        public PathResult(List<String> path, double actualDistance, double penalty, double totalCost, List<Double> nodeDistances) {
            this.path = path;
            this.actualDistance = actualDistance;
            this.penalty = penalty;
            this.totalCost = totalCost;
            this.nodeDistances = nodeDistances;
            this.segmentDistances = computeSegmentDistances(nodeDistances);
        }

        private List<Double> computeSegmentDistances(List<Double> cumulativeDistances) {
            List<Double> segments = new ArrayList<>();
            for (int i = 1; i < cumulativeDistances.size(); i++) {
                segments.add(cumulativeDistances.get(i) - cumulativeDistances.get(i - 1));
            }
            return segments;
        }

        public List<String> getPath() {
            return path;
        }

        public double getActualDistance() {
            return actualDistance;
        }

        public double getPenalty() {
            return penalty;
        }

        public double getTotalCost() {
            return totalCost;
        }

        public List<Double> getNodeDistances() {
            return nodeDistances;
        }

        public List<Double> getSegmentDistances() {
            return segmentDistances;
        }
    }
}

package com._hateam.service;

import com._hateam.entity.Hub;

import java.util.*;

public class GraphPathFinder {

    private final HubGraph hubGraph;
    // hubMap: 허브 이름과 Hub 엔티티를 매핑한 맵 (DB 조회 대신 이미 초기화된 데이터를 사용)
    private final Map<String, Hub> hubMap;

    public GraphPathFinder(HubGraph hubGraph, Map<String, Hub> hubMap) {
        this.hubGraph = hubGraph;
        this.hubMap = hubMap;
    }

    /**
     * 각 간선의 비용은 Haversine 거리이며, 시작 노드를 제외한 각 이동마다 30km의 패널티를 추가합니다.
     *
     * @param start 시작 허브 이름
     * @param end   도착 허브 이름
     * @return 최단 경로 결과 (경로 노드 리스트, 실제 거리, 패널티, 총 비용)
     */
    public PathResult findMinimumCostPath(String start, String end) {
        // 각 노드까지의 최소 총 비용 저장
        Map<String, Double> totalCostMap = new HashMap<>();
        // 각 노드까지의 실제 거리 합 저장
        Map<String, Double> distanceMap = new HashMap<>();
        // 각 노드까지의 패널티 합 저장
        Map<String, Double> penaltyMap = new HashMap<>();
        // 경로 저장
        Map<String, List<String>> paths = new HashMap<>();

        // 초기화: 모든 노드에 대해 최대값 설정
        for (String node : hubGraph.getGraph().keySet()) {
            totalCostMap.put(node, Double.MAX_VALUE);
            distanceMap.put(node, Double.MAX_VALUE);
            penaltyMap.put(node, Double.MAX_VALUE);
        }
        totalCostMap.put(start, 0.0);
        distanceMap.put(start, 0.0);
        penaltyMap.put(start, 0.0);
        paths.put(start, new ArrayList<>(Collections.singletonList(start)));

        PriorityQueue<NodeState> queue = new PriorityQueue<>(Comparator.comparingDouble(NodeState::getCost));
        queue.add(new NodeState(start, 0.0, 0.0, 0.0, new ArrayList<>(Collections.singletonList(start))));

        while (!queue.isEmpty()) {
            NodeState current = queue.poll();
            String currentNode = current.getNode();
            double currentCost = current.getCost();

            if (currentNode.equals(end)) {
                // 반환할 결과: 경로, 실제 거리, 패널티, 총 비용
                return new PathResult(current.getPath(), current.getActualDistance(), current.getPenalty(), currentCost);
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
                // 패널티: 시작 노드에서는 0, 그 외에는 30km
                double additionalPenalty = currentNode.equals(start) ? 0 : 30.0;
                double newActualDistance = current.getActualDistance() + edgeDistance;
                double newPenalty = current.getPenalty() + additionalPenalty;
                double newCost = newActualDistance + newPenalty;

                if (newCost < totalCostMap.getOrDefault(neighbor, Double.MAX_VALUE)) {
                    totalCostMap.put(neighbor, newCost);
                    distanceMap.put(neighbor, newActualDistance);
                    penaltyMap.put(neighbor, newPenalty);
                    List<String> newPath = new ArrayList<>(current.getPath());
                    newPath.add(neighbor);
                    paths.put(neighbor, newPath);
                    queue.add(new NodeState(neighbor, newCost, newActualDistance, newPenalty, newPath));
                }
            }
        }
        return null; // 경로를 찾지 못한 경우
    }

    /**
     * Haversine 공식으로 두 좌표 사이의 거리를 계산 (킬로미터 단위)
     */
    public double haversine(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371;
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    public static class NodeState {
        private final String node;
        private final double cost;
        private final double actualDistance;
        private final double penalty;
        private final List<String> path;

        public NodeState(String node, double cost, double actualDistance, double penalty, List<String> path) {
            this.node = node;
            this.cost = cost;
            this.actualDistance = actualDistance;
            this.penalty = penalty;
            this.path = path;
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
    }

    public static class PathResult {
        private final List<String> path;
        private final double actualDistance;
        private final double penalty;
        private final double totalCost;

        public PathResult(List<String> path, double actualDistance, double penalty, double totalCost) {
            this.path = path;
            this.actualDistance = actualDistance;
            this.penalty = penalty;
            this.totalCost = totalCost;
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
    }
}

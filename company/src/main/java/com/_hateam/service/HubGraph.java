package com._hateam.service;

import lombok.Getter;

import java.util.*;

@Getter
public class HubGraph {

    // 노드(허브)와 인접한 노드 리스트를 저장하는 맵
    private Map<String, List<String>> graph;

    public HubGraph() {
        this.graph = new HashMap<>();

        // "경기 남부" 연결: "경기 북부", "서울", "인천", "강원도", "경상북도", "대전", "대구"
        addUndirectedEdge("경기 남부", "경기 북부");
        addUndirectedEdge("경기 남부", "서울");
        addUndirectedEdge("경기 남부", "인천");
        addUndirectedEdge("경기 남부", "강원도");
        addUndirectedEdge("경기 남부", "경상 북도");
        addUndirectedEdge("경기 남부", "대전");
        addUndirectedEdge("경기 남부", "대구");

        // "대전" 연결: "충청남도", "충청북도", "세종", "전라북도", "광주", "전라남도", "경기 남부", "대구"
        addUndirectedEdge("대전", "충청 남도");
        addUndirectedEdge("대전", "충청 북도");
        addUndirectedEdge("대전", "세종");
        addUndirectedEdge("대전", "전라 북도");
        addUndirectedEdge("대전", "광주");
        addUndirectedEdge("대전", "전라 남도");
        addUndirectedEdge("대전", "경기 남부");  // 이미 추가된 경우 중복은 무시됨.
        addUndirectedEdge("대전", "대구");

        // "대구" 연결: "경상북도", "경상남도", "부산", "울산", "경기 남부", "대전"
        addUndirectedEdge("대구", "경상 북도");
        addUndirectedEdge("대구", "경상 남도");
        addUndirectedEdge("대구", "부산");
        addUndirectedEdge("대구", "울산");
        addUndirectedEdge("대구", "경기 남부"); // 이미 추가됨.
        addUndirectedEdge("대구", "대전");           // 이미 추가됨.

        // "경상북도" 연결: "경기 남부", "대구"
        addUndirectedEdge("경상 북도", "경기 남부");
        addUndirectedEdge("경상 북도", "대구");

        // 그래프 출력
        printGraph();
    }

    // 양방향 간선을 추가하는 메소드
    private void addUndirectedEdge(String node1, String node2) {
        addEdge(node1, node2);
        addEdge(node2, node1);
    }

    // 단방향 간선을 추가하는 헬퍼 메소드
    private void addEdge(String from, String to) {
        graph.computeIfAbsent(from, k -> new ArrayList<>());
        if (!graph.get(from).contains(to)) {
            graph.get(from).add(to);
        }
    }

    // 노드의 이웃 리스트 반환
    public List<String> getNeighbors(String node) {
        return graph.getOrDefault(node, Collections.emptyList());
    }

    // 그래프 전체를 출력
    private void printGraph() {
        for (String node : graph.keySet()) {
            System.out.println(node + " -> " + graph.get(node));
        }
    }


}

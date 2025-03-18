//package com._hateam.service;
//
//import com._hateam.dto.HubDto;
//import com._hateam.entity.Hub;
//import com._hateam.entity.HubRoute;
//import com._hateam.repository.HubRepository;
//import com._hateam.repository.HubRouteRepository;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//
//import java.util.*;
//
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class HubGraphService {
//
//    private final HubRepository hubRepository;
//    private final HubRouteRepository hubRouteRepository;
//
//    /**
//     * 다익스트라 알고리즘을 사용하여 최단 경로를 찾는다.
//     */
//    public List<HubDto> findShortestPath(UUID startHubId, UUID endHubId) {
//        Map<UUID, Long> distances = new HashMap<>();
//        Map<UUID, UUID> previousNodes = new HashMap<>();
//        PriorityQueue<HubNode> priorityQueue = new PriorityQueue<>(Comparator.comparingLong(HubNode::getDistance));
//
//        List<Hub> hubs = hubRepository.findAll();
//
//        for (Hub hub : hubs) {
//            distances.put(hub.getId(), Long.MAX_VALUE);
//        }
//        distances.put(startHubId, 0L);
//        priorityQueue.add(new HubNode(startHubId, 0L));
//
//        while (!priorityQueue.isEmpty()) {
//            HubNode current = priorityQueue.poll();
//            UUID currentId = current.getHubId();
//
//            if (currentId.equals(endHubId)) break;
//
//            List<HubRoute> routes = hubRouteRepository.findBySourceHubId(currentId);
//            for (HubRoute route : routes) {
//                UUID neighborId = route.getDestinationHub().getId();
//                long newDist = distances.get(currentId) + route.getDistanceKm();
//
//                if (newDist < distances.get(neighborId)) {
//                    distances.put(neighborId, newDist);
//                    previousNodes.put(neighborId, currentId);
//                    priorityQueue.add(new HubNode(neighborId, newDist));
//                }
//            }
//        }
//
//        return buildPath(previousNodes, startHubId, endHubId);
//    }
//
//    private List<HubDto> buildPath(Map<UUID, UUID> previousNodes, UUID start, UUID end) {
//        List<HubDto> path = new ArrayList<>();
//        UUID current = end;
//
//        while (current != null) {
//            Hub hub = hubRepository.findById(current).orElseThrow();
//            path.add(HubDto.hubToHubDto(hub));
//            current = previousNodes.get(current);
//        }
//
//        Collections.reverse(path);
//        return path;
//    }
//
//    @Data
//    @AllArgsConstructor
//    private static class HubNode {
//        private UUID hubId;
//        private Long distance;
//    }
//}

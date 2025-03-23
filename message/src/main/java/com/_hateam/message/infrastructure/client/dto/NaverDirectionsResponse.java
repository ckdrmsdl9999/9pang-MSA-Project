package com._hateam.message.infrastructure.client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NaverDirectionsResponse {
    private String code;
    private String message;
    private List<Route> route;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Route {
        private String resultCode;
        private String resultMessage;
        private List<Path> path;
        private Summary summary;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Path {
        private List<Double> location;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Summary {
        private Integer distance;
        private Integer duration;
        private TollFare tollFare;
        private FuelPrice fuelPrice;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TollFare {
        private Integer fare;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FuelPrice {
        private Integer gasoline;
        private Integer diesel;
        private Integer lpg;
    }
}
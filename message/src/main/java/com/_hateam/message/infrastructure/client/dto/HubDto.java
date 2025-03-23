package com._hateam.message.infrastructure.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HubDto {
    private UUID id;
    private String name;
    private String address;
    private String latitude;
    private String longitude;
}
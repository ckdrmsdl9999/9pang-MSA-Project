package com._hateam.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HubResponseDto {
    private String name;
    private String address;
    private String latitude;
    private String longitude;
}

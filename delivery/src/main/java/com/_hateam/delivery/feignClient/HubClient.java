package com._hateam.delivery.feignClient;

import com._hateam.common.dto.ResponseDto;
import com._hateam.delivery.dto.response.CompanyClientResponseDto;
import com._hateam.delivery.dto.response.HubClientHubResponseDto;
import com._hateam.delivery.dto.response.HubClientResponseDto;
import jakarta.ws.rs.PathParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "hubs-service")
public interface HubClient {

    @GetMapping("/hubs/location")
    ResponseEntity<ResponseDto<HubClientHubResponseDto>> getNearestHub(@RequestParam double latitude,
                                                                       @RequestParam double longitude);
    // todo: 추후 전달받은 값 확인후 response 수정 필요
    @GetMapping("/api/hubRoutes/{id}")
    ResponseEntity<ResponseDto<?>> getHubRoutesById(@PathVariable("id") Long id);
}


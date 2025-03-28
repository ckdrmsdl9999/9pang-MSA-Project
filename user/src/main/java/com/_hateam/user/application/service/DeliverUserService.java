package com._hateam.user.application.service;

import com._hateam.user.application.dto.*;
import com._hateam.user.domain.enums.DeliverType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface DeliverUserService {
    //배송담당자생성
    DeliverUserResponseDto createDeliverUser(DeliverUserCreateReqDto requestDTO);

    // 권한별 배송담당자 검색 (이름으로 검색)
    Page<DeliverUserResponseDto> searchDeliverUsersByName(String name, String userId, String userRole, String sortBy, String order, Pageable pageable);

    // 배송담당자 관리자 조회 (전체 목록)
    List<DeliverUserResponseDto> getAllDeliverUsers(String userId, String userRole);

    // 배송담당자 단일 조회
    DeliverUserResponseDto getDeliverUserById(UUID deliverId, String userId, String userRole);

    // 배송담당자 수정
    DeliverUserResponseDto updateDeliverUser(UUID deliverId, DeliverUserUpdateReqDto updateDto, String userId, String userRole);

    // 배송담당자 삭제
    void deleteDeliverUser(UUID deliverId, String userId, String userRole);

    //업체 배송 담당자 목록 조회
    List<FeignInCompanyDeliverResDto> getCompanyDeliver();

    //허브 배송 담당자목록 조회
    List<FeignInHubDeliverResDto> getHubDeliver();

    FeignDeliverSlackIdResDto getDeliverSlackUserById(UUID deliverId);


    UUID assignNextDeliverUser(DeliverType deliverType, UUID hubId);

    //DeliverUserResponseDto createDeliverUser(DeliverUserCreateReqDto deliverUserCreateReqDto);


}

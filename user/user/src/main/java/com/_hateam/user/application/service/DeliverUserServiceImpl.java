package com._hateam.user.application.service;

import com._hateam.common.exception.CustomForbiddenException;
import com._hateam.common.exception.CustomNotFoundException;
import com._hateam.user.application.dto.*;
import com._hateam.user.domain.enums.DeliverType;
import com._hateam.user.domain.enums.UserRole;
import com._hateam.user.domain.model.DeliverUser;
import com._hateam.user.infrastructure.security.UserPrincipals;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import com._hateam.user.domain.model.User;
import com._hateam.user.domain.repository.DeliverUserRepository;
import com._hateam.user.domain.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeliverUserServiceImpl implements DeliverUserService {

    private final DeliverUserRepository deliverUserRepository;

    private final UserRepository userRepository;

    @Override
    @Transactional//배송 담당자 생성
    public DeliverUserResponseDto createDeliverUser(DeliverUserCreateReqDto deliverUserCreateReqDto) {
        User user = userRepository.findById(deliverUserCreateReqDto.getUserId())
                .orElseThrow(() -> new CustomNotFoundException("등록하려는 사용자가 존재하지 않습니다. "));

        DeliverUser deliverUser = DeliverUserCreateReqDto.toEntity(deliverUserCreateReqDto,user);
        DeliverUser savedDeliverUser = deliverUserRepository.save(deliverUser);


        // User 배송담당자 여부 업데이트
            user.setDeliver(true);
            userRepository.save(user);


        return DeliverUserResponseDto.from(savedDeliverUser);
    }

@Override//권한별 담당자 검색
@Transactional(readOnly = true)
public Page<DeliverUserResponseDto> searchDeliverUsersByName(String name, UserPrincipals userPrincipals, String sortBy, String order, Pageable pageable) {

    Page<DeliverUser> deliverUserPage;
    //페이지 사이즈 적용
    if(pageable.getPageSize()!=10&&pageable.getPageSize()!=20&&pageable.getPageSize()!=30){
        pageable = PageRequest.of(pageable.getPageNumber(), 10, pageable.getSort());
    }
    // 정렬 설정 적용
    Sort sort = Sort.by(order.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy);
    // PageRequest에 정렬 적용
    PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

    // 권한 검증 및 권한별 처리
    if (userPrincipals.getRole() == UserRole.ADMIN) {
        // 마스터 관리자: 모든 배송담당자 검색 가능
        deliverUserPage = deliverUserRepository.findByNameContainingAndDeletedAtIsNull(name, pageRequest);
    }
    else if (userPrincipals.getRole() == UserRole.HUB) {

        // 허브관리자는 유저에서 추가,허브 관리자는 자신의 HubId를 불러와서 자신의 HubId 가지고 잇는 배송 담당자 검색
        // 애들 검색
        User user = userRepository.findById(userPrincipals.getId()).orElseThrow(() -> new CustomNotFoundException("배송담당자 정보를 찾을 수 없습니다(H). ID: " + userPrincipals.getId()));

        UUID hubId = user.getHubId();
        deliverUserPage = deliverUserRepository.findByNameContainingAndHubIdAndDeletedAtIsNull(name, hubId, pageRequest);
    }
    else if (userPrincipals.getRole() == UserRole.DELIVERY) {
        // 배송 담당자: 본인 정보만 조회 가능(이름이 일치하는 경우만)
        DeliverUser deliverUser = deliverUserRepository.findByUser_UserId(userPrincipals.getId())
                .orElseThrow(() -> new CustomNotFoundException("배송담당자 정보를 찾을 수 없습니다(D). ID: " + userPrincipals.getId()));

        // 자신의 이름이 검색어를 포함하는 경우에만 결과 반환
        if (deliverUser.getName().contains(name)) {
            // 단일 객체를 페이지로 변환
            deliverUserPage = new PageImpl<>(List.of(deliverUser), pageRequest, 1);
        } else {
            deliverUserPage = new PageImpl<>(Collections.emptyList(), pageRequest, 0);
        }
    }
    else { // COMPANY의 경우
        throw new CustomForbiddenException("배송담당자 정보에 접근할 권한이 없습니다.");
    }

    return deliverUserPage.map(DeliverUserResponseDto::from);
}
    // 배송담당자 목록 조회
    @Override
    @Transactional(readOnly = true)
    public List<DeliverUserResponseDto> getAllDeliverUsers(UserPrincipals userPrincipals) {

        // 권한 검증
        DeliverUser searchMan = deliverUserRepository.findByUser_UserId(userPrincipals.getId()).orElseThrow(
                () -> new CustomNotFoundException("배송담당자 정보를 찾을 수 없습니다. ID: " + userPrincipals.getId()));
        List<DeliverUser> deliverUsers = deliverUserRepository.findByDeletedAtIsNull();

        //ADMIN 은 전부조회
        if(userPrincipals.getRole() == UserRole.HUB){
            UUID hubId = searchMan.getHubId(); // 관리자의 허브 ID
            deliverUsers = deliverUserRepository.findByHubIdAndDeletedAtIsNull(hubId);

        }
        else if(userPrincipals.getRole() == UserRole.DELIVERY){
            deliverUsers = List.of(searchMan);
        }
        else if (userPrincipals.getRole() == UserRole.COMPANY) {
            throw new CustomForbiddenException("배송담당자 정보에 접근할 권한이 없습니다.");
        }

        return deliverUsers.stream()
                .map(DeliverUserResponseDto::from)
                .collect(Collectors.toList());
    }

    // 단일 조회
    @Override
    @Transactional(readOnly = true)
    public DeliverUserResponseDto getDeliverUserById(UUID deliverId, UserPrincipals userPrincipals) {
        // 권한 검증
        DeliverUser deliverUser = deliverUserRepository.findByDeliverId(deliverId)
                .orElseThrow(() -> new CustomNotFoundException("배송담당자 정보를 찾을 수 없습니다(d). ID: " + deliverId));
        DeliverUser searchMan = deliverUserRepository.findByUser_UserId(userPrincipals.getId()).orElseThrow(
                () -> new CustomNotFoundException("배송담당자가 아니기 때문에 조회불가합니다. ID: " + userPrincipals.getId())
        );

        //ADMIN 은 전부조회
        if(userPrincipals.getRole() == UserRole.HUB){
            if (!deliverUser.getHubId().equals(searchMan.getHubId())) {//조회자의hubid와 나의 hubid
                throw new CustomForbiddenException("본인 소속 허브의 배송담당자만 조회할 수 있습니다.");
            }
        }
        else if(userPrincipals.getRole() == UserRole.DELIVERY){
            if (!deliverUser.getUser().getUserId().equals(userPrincipals.getId())) {
                throw new CustomForbiddenException("본인의 정보만 조회할 수 있습니다.");}
        }
        else if (userPrincipals.getRole() == UserRole.COMPANY) {
            throw new CustomForbiddenException("배송담당자 정보에 접근할 권한이 없습니다.");
        }

        if (deliverUser.getDeletedAt() != null) {
            throw new CustomNotFoundException("삭제된 배송담당자입니다.");
        }

        return DeliverUserResponseDto.from(deliverUser);
    }


    // 배송 담당자 수정
    @Override
    @Transactional
    public DeliverUserResponseDto updateDeliverUser(UUID deliverId, DeliverUserUpdateReqDto updateDto, UserPrincipals userPrincipals) {
        // 권한 검증
        System.out.println(userPrincipals.getRole()+"관리확인");
        if (userPrincipals.getRole() != UserRole.ADMIN) {
            throw new CustomForbiddenException("관리자 권한이 필요합니다!.");
        }

        DeliverUser deliverUser = deliverUserRepository.findByDeliverId(deliverId)
                .orElseThrow(() -> new CustomNotFoundException("수정하려는 배송담당자 정보를 찾을 수 없습니다. ID: " + deliverId));

        if (deliverUser.getDeletedAt() != null) {
            throw new CustomNotFoundException("삭제된 배송담당자입니다.");
        }

        // 필드 업데이트
        if (updateDto.getHubId() != null) {
            deliverUser.setHubId(updateDto.getHubId());
        }
        if (updateDto.getSlackId() != null) {
            deliverUser.setSlackId(updateDto.getSlackId());
        }
        if (updateDto.getName() != null) {
            deliverUser.setName(updateDto.getName());
        }
        if (updateDto.getDeliverType() != null) {
            deliverUser.setDeliverType(updateDto.getDeliverType());
        }
        if (updateDto.getContactNumber() != null) {
            deliverUser.setContactNumber(updateDto.getContactNumber());
        }
        if (updateDto.getRotationOrder() != null) {
            deliverUser.setRotationOrder(updateDto.getRotationOrder());
        }
        if (updateDto.getStatus() != null) {
            deliverUser.setStatus(updateDto.getStatus());
        }

        deliverUser.setUpdatedBy(userPrincipals.getUsername());
        deliverUser.setUpdatedAt(LocalDateTime.now());

        DeliverUser updatedDeliverUser = deliverUserRepository.save(deliverUser);

        return DeliverUserResponseDto.from(updatedDeliverUser);
    }

    // 배송 담당자 삭제
    @Override
    @Transactional
    public void deleteDeliverUser(UUID deliverId, UserPrincipals userPrincipals) {
        // 권한 검증
        if (userPrincipals.getRole() != UserRole.ADMIN) {
            throw new CustomForbiddenException("관리자 권한이 필요합니다.");
        }

        DeliverUser deliverUser = deliverUserRepository.findByDeliverId(deliverId)
                .orElseThrow(() -> new CustomNotFoundException("삭제하려는 배송담당자 정보를 찾을 수 없습니다. ID: " + deliverId));

        if (deliverUser.getDeletedAt() != null) {
            throw new CustomNotFoundException("이미 삭제된 배송담당자입니다.");
        }

        // 논리적 삭제 처리
        deliverUser.setDeletedAt(LocalDateTime.now());
        deliverUser.setDeletedBy(userPrincipals.getUsername());
        deliverUserRepository.save(deliverUser);

        // 관련 User 엔티티 업데이트
        User user = deliverUser.getUser();
        if (user != null) {
            user.setDeliver(false);
            userRepository.save(user);
        }
    }


    //업체 배송담당자목록 조회(Feign)
    @Override
    public List<FeignInCompanyDeliverResDto> getCompanyDeliver() {
        List<DeliverUser> deliverUser = deliverUserRepository.findByDeliverTypeAndDeletedAtIsNull(DeliverType.DELIVER_COMPANY);
        return deliverUser.stream().map(FeignInCompanyDeliverResDto::from).collect(Collectors.toList());
    }

    //허브 배송담당자목록 조회(Feign)
    @Override
    public List<FeignInHubDeliverResDto> getHubDeliver() {
        List<DeliverUser> deliverUser = deliverUserRepository.findByDeliverTypeAndDeletedAtIsNull(DeliverType.DELIVER_HUB);
        return deliverUser.stream().map(FeignInHubDeliverResDto::from).collect(Collectors.toList());
    }

    // 배송담당자 슬랙 ID조회(Feign)
    @Override
    @Transactional(readOnly = true)
    public FeignDeliverSlackIdResDto getDeliverSlackUserById(UUID deliverId, UserPrincipals userPrincipals) {
        // 권한 검증
        DeliverUser deliverUser = deliverUserRepository.findByDeliverId(deliverId)
                .orElseThrow(() -> new CustomNotFoundException("배송담당자 정보를 찾을 수 없습니다(d). ID: " + deliverId));

        return FeignDeliverSlackIdResDto.from(deliverUser);
    }

}

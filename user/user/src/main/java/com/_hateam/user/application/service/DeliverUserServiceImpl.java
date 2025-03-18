package com._hateam.user.application.service;

import com._hateam.common.exception.CustomForbiddenException;
import com._hateam.common.exception.CustomNotFoundException;
import com._hateam.user.application.dto.DeliverUserCreateReqDto;
import com._hateam.user.application.dto.DeliverUserResponseDto;
import com._hateam.user.application.dto.DeliverUserUpdateReqDto;
import com._hateam.user.domain.enums.UserRole;
import com._hateam.user.domain.model.DeliverUser;
import com._hateam.user.infrastructure.security.UserPrincipals;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com._hateam.user.domain.enums.DeliverType;
import com._hateam.user.domain.model.User;
import com._hateam.user.domain.repository.DeliverUserRepository;
import com._hateam.user.domain.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class DeliverUserServiceImpl implements DeliverUserService {

    private final DeliverUserRepository deliverUserRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional//배송담당자 생성
    public DeliverUserResponseDto createDeliverUser(DeliverUserCreateReqDto deliverUserCreateReqDto) {

        User user = userRepository.findById(deliverUserCreateReqDto.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("등록하려는 사용자가 존재하지 않습니다. "));

        DeliverUser deliverUser = DeliverUserCreateReqDto.toEntity(deliverUserCreateReqDto,user);
        DeliverUser savedDeliverUser = deliverUserRepository.save(deliverUser);

        return DeliverUserResponseDto.from(savedDeliverUser);
    }

@Override//권한별 담당자 검색
@Transactional(readOnly = true)
public List<DeliverUserResponseDto> searchDeliverUsersByName(String name, UserPrincipals userPrincipals) {
    List<DeliverUser> deliverUsers;
    // 권한 검증 및 권한별 처리
    if (userPrincipals.getRole() == UserRole.ADMIN) {
        // 마스터 관리자: 모든 배송담당자 검색 가능
        deliverUsers = deliverUserRepository.findByNameContainingAndDeletedAtIsNull(name);
    }
    else if (userPrincipals.getRole() == UserRole.HUB) {
        // 허브 관리자: 담당 허브의 배송담당자만 검색 가능
        DeliverUser hubAdmin = deliverUserRepository.findByUser_UserId(userPrincipals.getId())
                .orElseThrow(() -> new CustomNotFoundException("배송담당자 정보를 찾을 수 없습니다(H). ID: " + userPrincipals.getId()));

        UUID hubId = hubAdmin.getHubId();
        deliverUsers = deliverUserRepository.findByNameContainingAndHubIdAndDeletedAtIsNull(name, hubId);
    }
    else if (userPrincipals.getRole() == UserRole.DELIVERY) {
        // 배송 담당자: 본인 정보만 조회 가능(이름이 일치하는 경우만)
        DeliverUser deliverUser = deliverUserRepository.findByUser_UserId(userPrincipals.getId())
                .orElseThrow(() -> new CustomNotFoundException("배송담당자 정보를 찾을 수 없습니다(D). ID: " + userPrincipals.getId()));

        // 자신의 이름이 검색어를 포함하는 경우에만 결과 반환
        if (deliverUser.getName().contains(name)) {
            deliverUsers = List.of(deliverUser);
        } else {
            deliverUsers = Collections.emptyList();
        }
    }
    else { // UserRole.COMPANY 등 기타 역할
        throw new CustomForbiddenException("배송담당자 정보에 접근할 권한이 없습니다.");
    }
    return deliverUsers.stream()
            .map(DeliverUserResponseDto::from)
            .collect(Collectors.toList());
}
    // 배송담당자 목록 조회
    @Override
    @Transactional(readOnly = true)
    public List<DeliverUserResponseDto> getAllDeliverUsers(UserPrincipals userPrincipals) {

        // 권한 검증
        // DeliverUser deliverUser = deliverUserRepository.findByDeliverId(deliverId)
        // .orElseThrow(() -> new CustomNotFoundException("배송담당자 정보를 찾을 수 없습니다. ID: " + deliverId));
        DeliverUser searchMan = deliverUserRepository.findByUser_UserId(userPrincipals.getId()).orElseThrow(
                () -> new CustomNotFoundException("배송담당자 정보를 찾을 수 없습니다. ID: " + userPrincipals.getId())
        );
        List<DeliverUser> deliverUsers = deliverUserRepository.findByDeletedAtIsNull();;

        //ADMIN 은 전부조회
        if(userPrincipals.getRole() == UserRole.HUB){
            UUID hubId = searchMan.getHubId(); // 관리자의 허브 ID
            System.out.println("창근hub목록조회"+hubId);
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
        System.out.println(deliverId+"값체크 창근");
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




    // 배송담당자 수정
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

    // 배송담당자 삭제
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



}

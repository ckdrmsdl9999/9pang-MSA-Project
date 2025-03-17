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

    // 배송담당자 관리자 검색
    @Override
    @Transactional(readOnly = true)
    public List<DeliverUserResponseDto> searchDeliverUsersByName(String name, UserPrincipals userPrincipals) {
        // 권한 검증
        if (userPrincipals.getRole() != UserRole.ADMIN) {throw new CustomForbiddenException("관리자 권한이 필요합니다.");}

        List<DeliverUser> deliverUsers = deliverUserRepository.findAll().stream()
                .filter(du -> du.getName().contains(name) && du.getDeletedAt() == null)
                .collect(Collectors.toList());

        return deliverUsers.stream()
                .map(DeliverUserResponseDto::from)
                .collect(Collectors.toList());
    }
    // 배송담당자 관리자 조회 (전체 목록)
    @Override
    @Transactional(readOnly = true)
    public List<DeliverUserResponseDto> getAllDeliverUsers(UserPrincipals userPrincipals) {
        // 권한 검증
        if (userPrincipals.getRole() != UserRole.ADMIN) {
            throw new CustomForbiddenException("관리자 권한이 필요합니다.");
        }

        List<DeliverUser> deliverUsers = deliverUserRepository.findAll().stream()
                .filter(du -> du.getDeletedAt() == null)
                .collect(Collectors.toList());

        return deliverUsers.stream()
                .map(DeliverUserResponseDto::from)
                .collect(Collectors.toList());
    }

    // 배송담당자 단일 조회
    @Override
    @Transactional(readOnly = true)
    public DeliverUserResponseDto getDeliverUserById(UUID deliverId, UserPrincipals userPrincipals) {
        // 권한 검증
//        if (userPrincipals.getRole() != UserRole.ADMIN &&
//                !isOwnDeliverUser(deliverId, userPrincipals.getUserId())) {
//            throw new CustomForbiddenException("해당 배송담당자 정보에 접근할 권한이 없습니다.");
//        }

        DeliverUser deliverUser = deliverUserRepository.findByDeliverId(deliverId)
                .orElseThrow(() -> new CustomNotFoundException("배송담당자 정보를 찾을 수 없습니다. ID: " + deliverId));

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

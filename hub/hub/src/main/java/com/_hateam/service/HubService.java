package com._hateam.service;



import com._hateam.dto.HubDto;
import com._hateam.dto.HubRequestDto;
import com._hateam.entity.Hub;
import com._hateam.feign.Company;
import com._hateam.feign.Product;
import com._hateam.repository.CompanyRepository;
import com._hateam.repository.HubRepository;
import com._hateam.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@Slf4j
@RequiredArgsConstructor
@CacheConfig(cacheNames = "hub")
public class HubService {


    private final HubRepository hubRepository;
    private final CompanyRepository companyRepository;
    private final ProductRepository productRepository;

    @Transactional
    @CachePut(key = "#result.id")
    public HubDto createHub(HubRequestDto requestDto) {
        validateDuplicateHub(requestDto);
        Hub hub = createHubEntity(requestDto);
        hubRepository.save(hub);
        return HubDto.hubToHubDto(hub);
    }


    @Transactional(readOnly = true)
    @Cacheable(key = "'allHubs_' + #page + '_' + #size + '_' + #sortBy + '_' + #isAsc")
    public List<HubDto> getAllHubs(int page, int size, String sortBy, boolean isAsc) {
        List<Hub> hubList = hubInfoPaging(page, size, sortBy, isAsc);
        return hubList.stream()
                .map(HubDto::hubToHubDto)
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public HubDto getHubByCompanyId(UUID companyId) {
        // CompanyRepository에서 업체 조회
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new EntityNotFoundException("Company not found with id: " + companyId));

        // Company 엔티티에 저장된 hub_id로 HubRepository에서 허브 조회
        UUID hubId = company.getHubId();
        Hub hub = hubRepository.findById(hubId)
                .orElseThrow(() -> new EntityNotFoundException("Hub not found for company with id: " + companyId));

        // Hub 엔티티를 HubDto로 변환하여 반환 (회사와 허브 정보를 API 응답용 DTO로 전환)
        return HubDto.hubToHubDto(hub);
    }

    @Transactional(readOnly = true)
    public HubDto getHubByProductId(UUID productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Company not found with id: " + productId));

        UUID hubId = product.getCompany().getHubId();
        Hub hub = hubRepository.findById(hubId)
                .orElseThrow(() -> new EntityNotFoundException("Hub not found for company with id: " + product.getCompany().getId()));

        // Hub 엔티티를 HubDto로 변환하여 반환 (회사와 허브 정보를 API 응답용 DTO로 전환)
        return HubDto.hubToHubDto(hub);
    }




    @Transactional(readOnly = true)
    @Cacheable(key = "#id")
    public HubDto getHub(UUID id) {
        Hub hub = findHub(id);
        return HubDto.hubToHubDto(hub);
    }

    @Transactional
    @CachePut(key = "#id")
    public HubDto updateHub(UUID id, HubRequestDto requestDto) {
        Hub hub = findHub(id);
        updateHubInfo(hub, requestDto);
        return HubDto.hubToHubDto(hub);
    }

    @Transactional
    @CacheEvict(key = "#id")
    public void deleteHub(UUID id) {
        Hub hub = findHub(id);
        hubRepository.delete(hub);
    }

    private void validateDuplicateHub(HubRequestDto requestDto) {
        hubRepository.findByNameAndDeletedAtIsNull(requestDto.getName()).ifPresent(m -> {
            throw new IllegalArgumentException("중복된 허브가 존재합니다.");
        });
    }

    private Hub createHubEntity(HubRequestDto requestDto) {
        Hub hub = Hub.builder().
                name(requestDto.getName()).
                address(requestDto.getAddress()).
                latitude(requestDto.getLatitude()).
                longitude(requestDto.getLongitude()).
                build();

//        // 시큐리티 컨텍스트에서 인증 정보를 가져와 createdBy 필드 설정
//            추후 시큐리티 적용시 다시 수정
//        CreatedInfo createdInfo = new CreatedInfo();
//        hub.setCreatedBy(createdInfo.getCreatedBy());
//        hub.setCreatedAt(createdInfo.getCreatedAt());
        return hub;
    }

    private List<Hub> hubInfoPaging(int page, int size, String sortBy, boolean isAsc) {
        // 10, 30, 50 외의 size는 기본 10으로 설정
        if (size != 10 && size != 30 && size != 50) {
            size = 10;
        }

        // 전체 hub 수 조회
        long totalHubs = hubRepository.count();
        // 전체 페이지 수 계산 (0페이지부터 시작하므로)
        int totalPages = (int) Math.ceil((double) totalHubs / size);

        // 요청한 페이지 번호가 전체 페이지 수 이상이면 예외 처리
        if (page >= totalPages && totalHubs > 0) {
            throw new IllegalArgumentException("요청한 페이지 번호(" + page + ")가 전체 페이지 수(" + totalPages + ")를 초과합니다.");
        }

        Sort sort = Sort.by(isAsc ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy.equals("updatedAt") ? "updatedAt" : "createdAt");
        Pageable pageable = PageRequest.of(page, size, sort);
        return hubRepository.findAll(pageable).getContent();
    }

    private Hub findHub(UUID id) {
        return hubRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Hub not found with id: " + id));
    }

    private void updateHubInfo(Hub hub, HubRequestDto requestDto) {
        hub.setName(requestDto.getName());
        hub.setAddress(requestDto.getAddress());
        hub.setLatitude(requestDto.getLatitude());
        hub.setLongitude(requestDto.getLongitude());
    }


}

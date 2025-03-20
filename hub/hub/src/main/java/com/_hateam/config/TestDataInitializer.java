package com._hateam.config;

import com._hateam.entity.Hub;
import com._hateam.feign.Company;
import com._hateam.feign.CompanyType;
import com._hateam.feign.Product;
import com._hateam.repository.CompanyRepository;
import com._hateam.repository.HubRepository;
import com._hateam.repository.ProductRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class TestDataInitializer {


    private final HubRepository hubRepository;
    private final CompanyRepository companyRepository;
    private final ProductRepository productRepository;

    public UUID getRandomHubId() {
        List<Hub> allHubs = hubRepository.findAll();
        if (allHubs.isEmpty()) {
            throw new IllegalStateException("No hubs available in DB");
        }
        Random random = new Random();
        return allHubs.get(random.nextInt(allHubs.size())).getId();
    }

    @PostConstruct
    @Order(2)
    @Transactional
    public void initTestData() {

        // 각 허브에 소속된 회사 생성
        Company company1 = Company.builder()
                .hubId(getRandomHubId())
                .userId("admin1")
                .name("서울회사1")
                .address("서울특별시 강남구 역삼동")
                .companyType(CompanyType.PRODUCE) // 예시 값
                .postalCode("12345")
                .build();
        company1 = companyRepository.save(company1);

        Company company2 = Company.builder()
                .hubId(getRandomHubId())
                .userId("admin2")
                .name("서울회사2")
                .address("서울특별시 종로구")
                .companyType(CompanyType.RECEIVE)
                .postalCode("54321")
                .build();
        company2 = companyRepository.save(company2);

        Company company3 = Company.builder()
                .hubId(getRandomHubId())
                .userId("admin3")
                .name("부산회사1")
                .address("부산광역시 해운대구")
                .companyType(CompanyType.RECEIVE)
                .postalCode("11111")
                .build();
        company3 = companyRepository.save(company3);

        // 각 회사에 제품 생성
        Product product1 = Product.builder()
                .company(company1)
                .name("제품1")
                .quantity(100)
                .description("서울회사1의 제품1")
                .price(1000)
                .build();
        productRepository.save(product1);

        Product product2 = Product.builder()
                .company(company1)
                .name("제품2")
                .quantity(200)
                .description("서울회사1의 제품2")
                .price(2000)
                .build();
        productRepository.save(product2);

        Product product3 = Product.builder()
                .company(company2)
                .name("제품3")
                .quantity(150)
                .description("서울회사2의 제품3")
                .price(1500)
                .build();
        productRepository.save(product3);

        Product product4 = Product.builder()
                .company(company3)
                .name("제품4")
                .quantity(300)
                .description("부산회사1의 제품4")
                .price(3000)
                .build();
        productRepository.save(product4);

        log.info("Test data initialized: {} hubs, {} companies, {} products",
                hubRepository.count(), companyRepository.count(), productRepository.count());
    }
}


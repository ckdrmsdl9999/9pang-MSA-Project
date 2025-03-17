package com._hateam.common.config;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@SpringBootApplication  // 기본 부트 애플리케이션 설정 포함
@EnableJpaAuditing      // JPA Auditing 활성화
@EnableScheduling       // 스케줄링 활성화
@EnableFeignClients     // Feign Clients 활성화
@EnableCaching          // 캐싱 활성화
public @interface CommonApplication {
}

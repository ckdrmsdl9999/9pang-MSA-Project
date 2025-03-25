# 스파르타 물류 - MSA 기반 국내 물류 관리 및 배송 시스템

## 📌 프로젝트 목적

본 프로젝트의 목적은 기업 간 거래(B2B)를 지원하는 국내 물류 관리 및 배송 시스템을 개발하는 것입니다. 마이크로서비스 아키텍처(MSA)를 사용하여 각 서비스의 독립적인 개발과 관리가 가능하도록 설계하고 구현합니다.

## 📌 프로젝트 개요

스파르타 물류 시스템은 각 지역에 물류 허브를 가지고 있으며, 각 허브는 주문 관리, 재고 관리, 물류 운영을 독립적으로 수행합니다. 기업의 상품은 지역 허브에서 관리되며, 배송 요청이 발생하면 해당 허브 간 물품 이동 및 최종 목적지 업체로의 배송이 이루어집니다.

## 📌 팀원 및 역할

| 이름 | 역할 |
|------|------|
| 유남규 | 허브와 허브 경로 관리, 허브 경로 알고리즘, 업체 관리, 상품 관리, Docker + Eureka 로 MSA 구성 |
| 손민주 | 주문 관리, 슬랙 메시지 관리, Kafka 이벤트 처리, 공통 모듈 생성 |
| 김승수 | 배송 관리, 배송경로 관리, Kafka 관리, 공통모듈 기여 |
| 윤창근 | 유저와 배송담당자 관리, 인증서버(Auth)-게이트웨이(Gateway)의 Jwt토큰 생성 및 검증, Security 설정 |

## 📌 서비스 구성 및 실행 방법

### 서비스 구성

| 서비스 이름        | 기능                                    | 의존성                  |
|------------------|---------------------------------------|----------------------|
| Eureka Server    | 서비스 레지스트리 및 디스커버리               | 모든 서비스              |
| API Gateway      | 모든 외부 요청을 라우팅 및 인증                | User Service, Company Service, Hub Service, Order Service 등 |
| User Service     | 사용자 관리 (회원가입, 로그인 등)             | Auth Service  |
| Company Service  | 업체 정보 관리                             | 없음                     |
| Hub Service      | 허브 및 경로 정보 관리                       | Redis Cache             |
| Auth Service     | 인증 및 권한 관리                           | User Service            |
| Delivery Service | 배송 상태 및 경로 관리                      | 없음                     |
| Order Service    | 주문 생성 및 상태 관리                      | Company Service, Hub Service, Delivery Service |
| Message Service  | 슬랙 메시지 발송 및 관리, 배송 담당자 알림   | User Service, Hub Service |
| Redis Cache      | 데이터 캐싱                               | 없음                     |
| PostgreSQL DB    | 영속적 데이터 저장                         | 없음                     |
| Zipkin           | 분산추적, 흐름추적, 지연시간 분석          | Gateway Service, User Service |
### 실행 방법

```shell
root 디렉토리에서 'docker-compose up --build' 명령어 실행
```

### 각 서비스 엔드포인트

| 서비스 이름     | 엔드포인트 |
|-----------------|------------|
| User Service    |  `/api/users/**`, `/api/users/signup`, `/api/users/getuser`, `/api/users/roles/{userId}`, `/api/users/search`, `/api/users/getusers`,  `/api/delivery-users/**`, `/api/delivery-users/assign`, `/api/delivery-users/init-dummy-data`,  `/api/delivery-users/add`,  `/api/delivery-users/admin/serarch`|
| Company Service | `/companies`, `/companies/{id}`, `/companies/hub/{hubId}/{companyId}`, `/companies/hub/{hubId}`, `/products`, `/products/{id}`, `/products/hub/{hubId}` |
| Hub Service     | `/hubs`,`/hubs/{id}` ,`/hubs/companies/{id}` ,`/hubs/products/{id}` ,`/hub-routes` ,`/hub-routes/{id}` ,`/hub-routes/cache` |
| Auth Service    | `/api/auth/signin` |
| Order Service    | `/api/orders/**`, `/api/orders/{orderId}`, `api/orders/search`, `api/orders/search/reset` |
| Delivery Service  | `/api/deliveries/**`, `/api/delivery-routes/**` |
| Message Service | `/api/slack/**`, `/api/slack/messages`, `/api/slack/messages/delivery-route`, `/api/slack/messages/search`, `/api/slack/messages/{messageId}` |

## 📌 ERD

![ERD 명세서](https://github.com/user-attachments/assets/6f6b057c-16b4-448e-ad57-7c07bf0e99ac)


## 인프라 설계도

![인프라설계도](https://github.com/user-attachments/assets/806ac7a2-b367-436d-93d4-29bfc7c95283)

## 📌 기술 스택

- Backend: Java 17, Spring Boot 3.4, Spring Cloud (Eureka, Gateway, Feign)
- Database: PostgreSQL, Redis
- Infrastructure: Docker, Docker Compose, Kafka
- Communication: REST API, Event-driven architecture (Kafka)
- Authentication: Spring Security, JWT
- API Integration: Slack API
- Documentation: Swagger/OpenAPI
- Build Tool: Gradle

## 📌 트러블슈팅

| 문제 상황                             | 문제 정의 / 문제라고 생각한 이유  | 해결 과정 / 의사결정 과정  |
|------------------------------------|-----------------------------------|-----------------------------|
| **Gateway 401 Unauthorized**           | **문제 정의**: API Gateway가 요청을 받을 때 `401 Unauthorized` 에러가 발생함. <br> **문제 이유**: `spring-boot-starter-security` 의존성이 포함되어 있어, 인증 없이 보호된 엔드포인트를 호출하면 기본적으로 `Spring Security`가 인증 정보를 요구하게 됩니다. <br> **문제라고 생각한 이유**: 요청 시 JWT 토큰을 제공하지 않았기 때문에 기본 `Spring Security`가 요청을 거부한 것으로 보입니다. | **해결 과정**: API Gateway에서 인증을 처리하려면, `Common` 모듈과 같은 보안 설정이 중복되지 않도록 `spring-boot-starter-security` 의존성을 제거하고, 인증 로직을 API Gateway에서만 처리하도록 변경합니다. 또한, API Gateway가 요청을 받을 때 JWT 토큰을 적절히 처리하도록 설정합니다. <br> 추가로, `SecurityConfig`에서 API Gateway의 보안 설정을 명확하게 적용하여 문제를 해결할 수 있습니다. |
| **Eureka 서버 서비스 발견 실패**          | **문제 정의**: Eureka 서버에서 다른 서비스가 발견되지 않음. <br> **문제 이유**: `eureka.client.serviceUrl.defaultZone`에 설정된 Eureka 서버 URL이 잘못되어 있어 서비스 디스커버리가 실패합니다. <br> **문제라고 생각한 이유**: 서비스가 `Eureka Server`에 등록되지 않거나, URL이 잘못되었을 가능성이 커 보였습니다. | **해결 과정**: `serviceUrl` 주소를 내부 도커 네트워크의 올바른 서비스 이름으로 변경했습니다. `<service-name>:<port>` 형식으로 내부 도메인 또는 도커 서비스 이름을 사용하여 URL을 수정했습니다. <br> 예: `eureka.client.serviceUrl.defaultZone=http://eureka-server:8761/eureka/` 로 변경하여 문제를 해결했습니다. |
| **Redis 직렬화 문제**                   | **문제 정의**: Redis에 저장된 객체를 읽어올 때 `ClassCastException`이 발생함. <br> **문제 이유**: Redis에 `HashMap` 형식으로 데이터를 저장할 때 기본 직렬화 방식을 사용하여 저장된 객체를 제대로 읽을 수 없었습니다. <br> **문제라고 생각한 이유**: Redis가 객체를 `HashMap`으로 저장하는 과정에서 직렬화된 데이터 형식이 제대로 처리되지 않았습니다. | **해결 과정**: Redis에서 객체를 저장할 때 `GenericJackson2JsonRedisSerializer`를 사용하여 객체를 JSON 형식으로 직렬화하고 역직렬화하도록 설정했습니다. 이를 통해 객체를 읽어오는 과정에서 발생한 `ClassCastException`을 해결할 수 있었습니다. <br> 예시 코드: `template.setDefaultSerializer(new GenericJackson2JsonRedisSerializer())`|
| **commonModule globalException 처리**    | **문제 정의**: `GlobalExceptionHandler`가 예상대로 동작하지 않음. <br> **문제 이유**: `@SpringBootApplication`이 선언된 클래스와 그 하위 패키지만 자동으로 스캔되며, `commonModule`의 예외 처리 클래스가 스캔되지 않았기 때문입니다. <br> **문제라고 생각한 이유**: 예외 처리기가 `@Component`로 선언되지 않아, 자동으로 스캔되지 않았다고 판단했습니다. | **해결 과정**: `commonModule`의 `GlobalExceptionHandler`가 스캔되도록 `@ComponentScan` 또는 `@Import`를 사용하여 해당 클래스만 명시적으로 스캔하도록 설정했습니다. <br> 예시: `@ComponentScan(basePackages = "com._hateam.common")` 또는 `@Import(CommonExceptionConfig.class)`와 같이 필요한 클래스만 임포트하여 예외 처리기를 활성화했습니다. |


## 📌 API Docs

- [Eureka 대시보드](http://localhost:8761)
- [Swagger API 문서 링크](http://localhost:8080/swagger-ui.html)

---


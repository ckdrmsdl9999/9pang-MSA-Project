# 스파르타 물류 - MSA 기반 국내 물류 관리 및 배송 시스템

## 📌 프로젝트 목적

본 프로젝트의 목적은 기업 간 거래(B2B)를 지원하는 국내 물류 관리 및 배송 시스템을 개발하는 것입니다. 마이크로서비스 아키텍처(MSA)를 사용하여 각 서비스의 독립적인 개발과 관리가 가능하도록 설계하고 구현합니다.

## 📌 프로젝트 개요

스파르타 물류 시스템은 각 지역에 물류 허브를 가지고 있으며, 각 허브는 주문 관리, 재고 관리, 물류 운영을 독립적으로 수행합니다. 기업의 상품은 지역 허브에서 관리되며, 배송 요청이 발생하면 해당 허브 간 물품 이동 및 최종 목적지 업체로의 배송이 이루어집니다.

## 📌 팀원 및 역할

| 이름 | 역할 |
|------|------|
| 팀원1 | 백엔드 개발 및 서비스 설계 |
| 팀원2 | 프론트엔드 개발 및 UI/UX |
| 팀원3 | 데이터베이스 설계 및 관리 |
| 윤창근 | 사용자(유저,배송담당자), 인증서버(Auth)와 게이트웨이(Gateway)의 Jwt토큰 생성 및 검증 작업과 Security 설정 |

## 📌 서비스 구성 및 실행 방법

### 서비스 구성

- Eureka Server
- API Gateway
- User Service
- Company Service
- Hub Service
- Auth Service
- Redis Cache
- PostgreSQL DB

### 실행 방법

```shell
docker-compose up --build
```

### 각 서비스 엔드포인트

| 서비스 이름     | 엔드포인트 |
|-----------------|------------|
| User Service    | `/api/users/**`, `/api/delivery-users/**` |
| Company Service | `/companies/**` |
| Hub Service     | `/hubs/**` |
| Auth Service    | `/api/auth/signIn` |

## 📌 ERD

![ERD 이미지 링크](여기에_ERD_이미지_링크)

## 인프라 설계도(추후변경시 수정)

![인프라설계도](https://github.com/user-attachments/assets/806ac7a2-b367-436d-93d4-29bfc7c95283)

## 📌 기술 스택

- Backend: Java, Spring Boot, Spring Cloud (Eureka, Gateway)
- Frontend: React.js (Optional)
- Database: PostgreSQL, Redis
- Infrastructure: Docker, Docker Compose
- Authentication: JWT

## 📌 트러블슈팅

| 문제 상황                             | 원인 및 해결 방법 |
|------------------------------------|-------------------|
| Gateway 401 Unauthorized           | COMMON 모듈에 Security설정이 되어있는 것이 원인이였으며, spring-boot-starter-security 의존성이 들어가 있다면 기본 Spring Security설정 상태에서 인증정보(JWT토큰 등) 없이 보호된 엔드포인트호출시 401에러가 발생 -> Common모듈과 Security설정이 필요 없는 모듈의 의존성 제거로 해결 |
| Eureka 서버 서비스 발견 실패          | Eureka 설정 파일에 service-url 주소 오기입되어 있었으며, 내부 도커 네트워크 서비스 이름으로 변경하여 해결 |

## 📌 API Docs (선택사항)

- [Swagger API 문서 링크](여기에_Swagger_문서_링크)

---


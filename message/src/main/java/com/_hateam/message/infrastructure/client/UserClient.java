version: '3.8'

services:
eureka-server:
build:
context: .
dockerfile: eureka/Dockerfile
container_name: eureka-server
ports:
        - "8761:8761"
environment:
        - SPRING_PROFILES_ACTIVE=default
      - SERVER_PORT=8761
        # 데이터베이스 설정: eureka-db 컨테이너를 사용
      - SPRING_DATASOURCE_URL=jdbc:postgresql://eureka-db:5432/eurekadb
        - SPRING_DATASOURCE_USERNAME=postgres
      - SPRING_DATASOURCE_PASSWORD=postgres
depends_on:
        - eureka-db
networks:
        - msanet

eureka-db:
image: postgres:14
container_name: eureka-db
environment:
        - POSTGRES_DB=eurekadb
      - POSTGRES_USER=postgres
      - POSTGRES_PASSWORD=postgres
ports:
        - "5433:5432"
networks:
        - msanet

gateway-service:
build:
context: .
dockerfile: gateway/Dockerfile
container_name: gateway-service
ports:
        - "8080:8080"
environment:
        - SPRING_PROFILES_ACTIVE=default
      - SERVER_PORT=8080
        # 내부 네트워크에서 Eureka 서버 접근을 위해 서비스명 사용
      - EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://eureka-server:8761/eureka/
        # 데이터베이스 설정: gateway-db 컨테이너 사용
      - SPRING_DATASOURCE_URL=jdbc:postgresql://gateway-db:5432/gatewaydb
        - SPRING_DATASOURCE_USERNAME=postgres
      - SPRING_DATASOURCE_PASSWORD=postgres
depends_on:
        - gateway-db
      - eureka-server
networks:
        - msanet

gateway-db:
image: postgres:14
container_name: gateway-db
environment:
        - POSTGRES_DB=gatewaydb
      - POSTGRES_USER=postgres
      - POSTGRES_PASSWORD=postgres
ports:
        - "5434:5432"
networks:
        - msanet

hubs-service:
build:
context: .
dockerfile: hub/hub/Dockerfile
container_name: hubs-service
environment:
        - SPRING_PROFILES_ACTIVE=default
      - SERVER_PORT=8080
        - SPRING_APPLICATION_NAME=hubs-service
      - EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://eureka-server:8761/eureka/
        # 데이터베이스 설정: hubs-db 컨테이너 사용
      - SPRING_DATASOURCE_URL=jdbc:postgresql://hubs-db:5432/hubsdb
        - SPRING_DATASOURCE_USERNAME=postgres
      - SPRING_DATASOURCE_PASSWORD=postgres
      - SPRING_DATA_REDIS_HOST=redis
      - SPRING_DATA_REDIS_PORT=6379
depends_on:
        - hubs-db
      - redis
      - eureka-server
networks:
        - msanet

hubs-db:
image: postgres:14
container_name: hubs-db
environment:
        - POSTGRES_DB=hubsdb
      - POSTGRES_USER=postgres
      - POSTGRES_PASSWORD=postgres
ports:
        - "5435:5432"
networks:
        - msanet

company-service:
build:
context: .
dockerfile: company/Dockerfile
container_name: company-service
environment:
        - SPRING_PROFILES_ACTIVE=default
      - SERVER_PORT=8080
        - SPRING_APPLICATION_NAME=company-service
      - EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://eureka-server:8761/eureka/
        # 데이터베이스 설정: company-db 컨테이너 사용
      - SPRING_DATASOURCE_URL=jdbc:postgresql://company-db:5432/companydb
        - SPRING_DATASOURCE_USERNAME=postgres
      - SPRING_DATASOURCE_PASSWORD=postgres
depends_on:
        - company-db
      - eureka-server
networks:
        - msanet

company-db:
image: postgres:14
container_name: company-db
environment:
        - POSTGRES_DB=companydb
      - POSTGRES_USER=postgres
      - POSTGRES_PASSWORD=postgres
ports:
        - "5436:5432"
networks:
        - msanet

user-service:
build:
context: .
dockerfile: user/Dockerfile
container_name: user-service
environment:
        - SPRING_PROFILES_ACTIVE=default
      - SERVER_PORT=8080
        - SPRING_APPLICATION_NAME=user-service
      - EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://eureka-server:8761/eureka/
        # 데이터베이스 설정: hubs-db 컨테이너 사용
      - SPRING_DATASOURCE_URL=jdbc:postgresql://user-db:5432/userdb
        - SPRING_DATASOURCE_USERNAME=postgres
      - SPRING_DATASOURCE_PASSWORD=postgres
      - SPRING_DATA_REDIS_HOST=redis
      - SPRING_DATA_REDIS_PORT=6379
depends_on:
        - user-db
      - redis
      - eureka-server
networks:
        - msanet

user-db:
image: postgres:14
container_name: user-db
environment:
        - POSTGRES_DB=userdb
      - POSTGRES_USER=postgres
      - POSTGRES_PASSWORD=postgres
ports:
        - "5437:5432"
networks:
        - msanet

auth-service:
build:
context: .
dockerfile: auth/Dockerfile
container_name: auth-service
environment:
        - SPRING_PROFILES_ACTIVE=default
      - SERVER_PORT=8080
        - SPRING_APPLICATION_NAME=auth-service
      - EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://eureka-server:8761/eureka/
        # 데이터베이스 설정: hubs-db 컨테이너 사용
      - SPRING_DATA_REDIS_HOST=redis
      - SPRING_DATA_REDIS_PORT=6379
depends_on:
        - redis
      - eureka-server
networks:
        - msanet

redis:
image: redis:7
container_name: redis
ports:
        - "6379:6379"
networks:
        - msanet

order-service:
build:
context: .
dockerfile: order/Dockerfile
container_name: order-service
environment:
        - SPRING_PROFILES_ACTIVE=default
      - SERVER_PORT=8080
        - SPRING_APPLICATION_NAME=order-service
      - EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://eureka-server:8761/eureka/
        - SPRING_DATASOURCE_URL=jdbc:postgresql://order-db:5432/orderdb
        - SPRING_DATASOURCE_USERNAME=postgres
      - SPRING_DATASOURCE_PASSWORD=postgres
      - SPRING_KAFKA_BOOTSTRAP_SERVERS=kafka:9092
        - services.user.url=user-service:8080
        - services.company.url=company-service:8080
        - services.hub.url=hubs-service:8080
        - services.delivery.url=delivery-service:8080
        - services.message.url=message-service:8080
        - SPRING_KAFKA_CONSUMER_GROUP_ID=order-consumer-group
depends_on:
        - order-db
      - eureka-server
      - kafka
networks:
        - msanet

order-db:
image: postgres:14
container_name: order-db
environment:
        - POSTGRES_DB=orderdb
      - POSTGRES_USER=postgres
      - POSTGRES_PASSWORD=postgres
ports:
        - "5438:5432"
networks:
        - msanet

message-service:
env_file:
        - .env
build:
context: .
dockerfile: message/Dockerfile
container_name: message-service
environment:
        - SPRING_PROFILES_ACTIVE=default
      - SERVER_PORT=8080
        - SPRING_APPLICATION_NAME=message-service
      - EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://eureka-server:8761/eureka/
        - SPRING_DATASOURCE_URL=jdbc:postgresql://message-db:5432/messagedb
        - SPRING_DATASOURCE_USERNAME=postgres
      - SPRING_DATASOURCE_PASSWORD=postgres
      - SPRING_KAFKA_BOOTSTRAP_SERVERS=kafka:9092
        - SLACK_TOKEN=${SLACK_TOKEN}
      - NAVER_CLIENT_ID=${NAVER_CLIENT_ID}
      - NAVER_CLIENT_SECRET=${NAVER_CLIENT_SECRET}
      - services.user.url=user-service:8080
        - services.company.url=company-service:8080
        - services.hub.url=hubs-service:8080
        - services.delivery.url=delivery-service:8080
        - EUREKA_CLIENT_ENABLED=true
        - SPRING_KAFKA_CONSUMER_GROUP_ID=message-consumer-group
depends_on:
        - message-db
      - eureka-server
      - kafka
networks:
        - msanet

message-db:
image: postgres:14
container_name: message-db
environment:
        - POSTGRES_DB=messagedb
      - POSTGRES_USER=postgres
      - POSTGRES_PASSWORD=postgres
ports:
        - "5440:5432"
networks:
        - msanet

zookeeper:
image: confluentinc/cp-zookeeper:7.3.0
container_name: zookeeper
environment:
ZOOKEEPER_CLIENT_PORT: 2181
ZOOKEEPER_TICK_TIME: 2000
ports:
        - "2181:2181"
networks:
        - msanet

kafka:
image: confluentinc/cp-kafka:7.3.0
container_name: kafka
depends_on:
        - zookeeper
ports:
        - "9092:9092"
environment:
KAFKA_BROKER_ID: 1
KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka:9092
KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
KAFKA_TRANSACTION_STATE_LOG_MIN_ISR: 1
KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR: 1
networks:
        - msanet

networks:
msanet:
driver: bridge

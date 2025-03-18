package com._hateam.order.application.service;

import com._hateam.common.exception.CustomNotFoundException;
import com._hateam.order.application.dto.OrderRequestDto;
import com._hateam.order.application.dto.OrderResponseDto;
import com._hateam.order.application.dto.OrderSearchDto;
import com._hateam.order.application.dto.OrderUpdateDto;
import com._hateam.order.domain.model.Order;
import com._hateam.order.domain.model.OrderProduct;
import com._hateam.order.domain.repository.OrderRepository;
import com._hateam.order.domain.service.OrderDomainService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderDomainService orderDomainService;

    /**
     * 새로운 주문을 생성합니다.
     *
     * @param requestDto 주문 생성 요청 DTO
     * @return 생성된 주문 정보 응답 DTO
     * @throws IllegalArgumentException 요청 데이터가 유효하지 않은 경우
     */
    @Transactional
    public OrderResponseDto createOrder(OrderRequestDto requestDto) {
        Order order = orderDomainService.createOrder(
                requestDto.getDeliverId(),
                requestDto.getHubId(),
                requestDto.getCompanyId(),
                requestDto.getOrderRequest(),
                requestDto.getDeliveryDeadline()
        );

        order.setTotalPrice(0);

        // 주문 상품 추가
        for (OrderRequestDto.OrderProductDto productDto : requestDto.getProducts()) {
            OrderProduct orderProduct = orderDomainService.createOrderProduct(
                    productDto.getProductId(),
                    productDto.getQuantity(),
                    productDto.getTotalPrice() / productDto.getQuantity() // 단가 계산
            );
            order.addOrderProduct(orderProduct);
        }

        // 총 가격 계산
        order.calculateTotalPrice();

        Order savedOrder = orderRepository.save(order);

        return OrderResponseDto.from(savedOrder);
    }

    /**
     * 주문 ID로 주문을 조회합니다.
     *
     * @param orderId 조회할 주문 ID
     * @return 주문 정보 응답 DTO
     * @throws CustomNotFoundException 주문이 존재하지 않는 경우
     */
    public OrderResponseDto getOrderById(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new CustomNotFoundException("주문을 찾을 수 없습니다. ID: " + orderId));

        return OrderResponseDto.from(order);
    }

    /**
     * 모든 주문을 페이징하여 조회합니다.
     *
     * @param page 페이지 번호 (1부터 시작)
     * @param size 페이지 크기
     * @param sort 정렬 방향 ("asc" 또는 "desc")
     * @return 주문 정보 응답 DTO 목록
     */
    public List<OrderResponseDto> getAllOrders(int page, int size, String sort) {
        List<Order> orders = orderRepository.findAll(page, size, sort);

        return orders.stream()
                .map(OrderResponseDto::from)
                .toList();
    }

    /**
     * 주문을 검색합니다.
     *
     * @param searchDto 주문 검색 조건 DTO
     * @return 주문 정보 응답 DTO 목록
     */
    public List<OrderResponseDto> searchOrders(OrderSearchDto searchDto) {
        try {
            List<Order> orders = orderRepository.search(
                    searchDto.getSearchTerm(),
                    searchDto.getStatus(),
                    searchDto.getStartDate(),
                    searchDto.getEndDate(),
                    searchDto.getCompanyId(),
                    searchDto.getHubId(),
                    searchDto.getPage(),
                    searchDto.getSize(),
                    searchDto.getSort()
            );

            // 검색 결과 총 개수 조회
            long totalCount = orderRepository.countSearchResults(
                    searchDto.getSearchTerm(),
                    searchDto.getStatus(),
                    searchDto.getStartDate(),
                    searchDto.getEndDate(),
                    searchDto.getCompanyId(),
                    searchDto.getHubId()
            );

            return orders.stream()
                    .map(OrderResponseDto::from)
                    .toList();

        } catch (Exception e) {
            return List.of();
        }
    }

    /**
     * 주문을 업데이트합니다.
     *
     * @param orderId 업데이트할 주문 ID
     * @param updateDto 주문 업데이트 DTO
     * @return 업데이트된 주문 정보 응답 DTO
     * @throws CustomNotFoundException 주문이 존재하지 않는 경우
     * @throws IllegalArgumentException 업데이트 데이터가 유효하지 않은 경우
     */
    @Transactional
    public OrderResponseDto updateOrder(UUID orderId, OrderUpdateDto updateDto) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new CustomNotFoundException("주문을 찾을 수 없습니다. ID: " + orderId));

        // 주문 정보 업데이트
        if (updateDto.getDeliverId() != null && updateDto.getHubId() != null &&
                updateDto.getCompanyId() != null) {
            orderDomainService.updateOrderInfo(
                    order,
                    updateDto.getDeliverId(),
                    updateDto.getHubId(),
                    updateDto.getCompanyId(),
                    updateDto.getOrderRequest(),
                    updateDto.getDeliveryDeadline()
            );
        }

        // 주문 상태 업데이트
        if (updateDto.getStatus() != null) {
            orderDomainService.updateOrderStatus(order, updateDto.getStatus());
        }

        // 주문 상품 업데이트
        if (updateDto.getProducts() != null && !updateDto.getProducts().isEmpty()) {
            List<OrderProduct> newProducts = new ArrayList<>();

            for (OrderUpdateDto.OrderProductDto productDto : updateDto.getProducts()) {
                OrderProduct orderProduct = orderDomainService.createOrderProduct(
                        productDto.getProductId(),
                        productDto.getQuantity(),
                        productDto.getTotalPrice() / productDto.getQuantity() // 단가 계산
                );
                newProducts.add(orderProduct);
            }

            orderDomainService.updateOrderProducts(order, newProducts);
        }

        Order savedOrder = orderRepository.save(order);

        return OrderResponseDto.from(savedOrder);
    }

    /**
     * 주문을 삭제합니다. (논리적 삭제)
     *
     * @param orderId 삭제할 주문 ID
     * @throws CustomNotFoundException 주문이 존재하지 않는 경우
     */
    @Transactional
    public void deleteOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new CustomNotFoundException("주문을 찾을 수 없습니다. ID: " + orderId));

        orderRepository.delete(order);
    }
}
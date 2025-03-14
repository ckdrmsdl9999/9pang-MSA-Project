package com._hateam.order.domain.model;

import com._hateam.common.entity.Timestamped;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "p_order_product")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderProduct extends Timestamped {

    @Id
    @Column(name = "order_product_id")
    private UUID orderProductId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(name = "total_quantity", nullable = false)
    private Integer totalQuantity;

    @Column(name = "total_price", nullable = false)
    private Integer totalPrice;

    @PrePersist
    public void prePersist() {
        if (this.orderProductId == null) {
            this.orderProductId = UUID.randomUUID();
        }
    }

    // Order 설정 메서드
    public void setOrder(Order order) {
        this.order = order;
    }

    // 수량 변경 시 가격 계산 메서드
    public void updateQuantity(int quantity, int unitPrice) {
        this.totalQuantity = quantity;
        this.totalPrice = quantity * unitPrice;
    }
}
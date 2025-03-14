package com._hateam.entity;

import com._hateam.common.entity.Timestamped;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE) // 빌더를 통한 생성만 허용
@Builder
@Table(name = "p_hub")
public class Hub extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "hub_id", nullable = false)
    private UUID id;

    @Column(name = "name", nullable = false, length = 50,  unique=true)
    private String name;

    @Column(name = "address", nullable = false, length = 255)
    private String address;

    @Column(name = "latitude", nullable = false, length = 20)
    private String latitude;

    @Column(name = "longitude", nullable = false, length = 20)
    private String longitude;


    public void softDelete(String deletedBy) {
        // Timestamped의 delete() 메서드를 활용하여 deletedAt, deletedBy 설정
        super.delete(deletedBy);
    }
}

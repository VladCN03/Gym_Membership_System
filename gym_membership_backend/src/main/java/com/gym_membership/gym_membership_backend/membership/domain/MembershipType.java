package com.gym_membership.gym_membership_backend.membership.domain;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity @Table(name="membership_type")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class MembershipType {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, unique=true, length=100)
    private String type;

    @Column(nullable=false, precision=10, scale=2)
    private BigDecimal price;
}

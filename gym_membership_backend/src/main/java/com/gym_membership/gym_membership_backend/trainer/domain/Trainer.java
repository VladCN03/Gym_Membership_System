package com.gym_membership.gym_membership_backend.trainer.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name="trainer")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Trainer {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, length=150)
    private String name;

    @Column(length=255)
    private String email;
}

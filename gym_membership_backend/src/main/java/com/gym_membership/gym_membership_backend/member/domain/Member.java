package com.gym_membership.gym_membership_backend.member.domain;

import com.gym_membership.gym_membership_backend.membership.domain.MembershipType;
import com.gym_membership.gym_membership_backend.trainer.domain.Trainer;
import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name="member")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Member {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, length=150)
    private String name;

    @Column(length=255)
    private String email;

    @ManyToOne @JoinColumn(name="membership_type_id")
    private MembershipType membershipType;

    @ManyToOne @JoinColumn(name="trainer_id")
    private Trainer trainer;
}

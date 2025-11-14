package com.gym_membership.gym_membership_backend.member.domain;

import com.gym_membership.gym_membership_backend.membership.domain.MembershipType;
import com.gym_membership.gym_membership_backend.trainer.domain.Trainer;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "member")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;

    // nou:
    private String goal;
    private String experience;
    private String budgetTier;
    private String schedule;

    @ManyToOne
    @JoinColumn(name = "trainer_id")
    private Trainer trainer;

    @ManyToOne
    @JoinColumn(name = "membership_type_id")
    private MembershipType membershipType;
}

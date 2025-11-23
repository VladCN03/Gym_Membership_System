package com.gym_membership.gym_membership_backend.member.domain;

import com.gym_membership.gym_membership_backend.membership.domain.MembershipType;
import com.gym_membership.gym_membership_backend.security.Role;
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

    @Column(nullable = false, unique = true)
    private String email;

    // === câmpuri pentru autentificare ===

    @Column(name = "password")          // în DB deja ai coloana "password"
    private String passwordHash;        // în parolă pui HASH-ul (BCrypt), nu text simplu

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role = Role.MEMBER;    // default MEMBER

    // === câmpurile existente ===

    private String goal;
    private String experience;

    @Column(name = "budget_tier")
    private String budgetTier;

    private String schedule;

    @ManyToOne
    @JoinColumn(name = "trainer_id")
    private Trainer trainer;

    @ManyToOne
    @JoinColumn(name = "membership_type_id")
    private MembershipType membershipType;
}

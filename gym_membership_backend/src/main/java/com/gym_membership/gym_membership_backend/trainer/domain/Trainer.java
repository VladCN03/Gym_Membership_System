package com.gym_membership.gym_membership_backend.trainer.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "trainer")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Trainer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;

    // nou
    private String specialties; // ex: "forta,slabit,yoga"
}


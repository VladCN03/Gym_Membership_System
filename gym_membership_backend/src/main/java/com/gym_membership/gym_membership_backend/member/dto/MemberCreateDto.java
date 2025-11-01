package com.gym_membership.gym_membership_backend.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MemberCreateDto {
    @NotBlank
    private String name;

    @NotBlank @Email
    private String email;

    // optional
    private Long trainerId;   // poate fi null
}

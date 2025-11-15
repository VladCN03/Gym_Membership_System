package com.gym_membership.gym_membership_backend.member.dto;

import jakarta.validation.constraints.*;

public record CreateMemberRequest(
        @NotBlank String name,
        @Email(message="Email invalid") @NotBlank String email,
        Long trainerId
) {}

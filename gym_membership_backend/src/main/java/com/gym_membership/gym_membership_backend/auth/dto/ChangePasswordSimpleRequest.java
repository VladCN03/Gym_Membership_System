package com.gym_membership.gym_membership_backend.auth.dto;

public record ChangePasswordSimpleRequest(
        String email,
        String newPassword
) {}

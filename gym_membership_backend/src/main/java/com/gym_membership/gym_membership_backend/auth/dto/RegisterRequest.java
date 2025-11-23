package com.gym_membership.gym_membership_backend.auth.dto;

public record RegisterRequest(
        String name,
        String email,
        String password,
        String goal,
        String experience,
        String budgetTier,
        String schedule
) {}


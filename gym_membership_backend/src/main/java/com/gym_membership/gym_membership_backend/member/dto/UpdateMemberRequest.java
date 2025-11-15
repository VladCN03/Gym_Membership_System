package com.gym_membership.gym_membership_backend.member.dto;

public record UpdateMemberRequest(
        String name,
        String email,
        Long membershipTypeId,
        Long trainerId,
        String goal,
        String experience,
        String budgetTier,
        String schedule
) {}

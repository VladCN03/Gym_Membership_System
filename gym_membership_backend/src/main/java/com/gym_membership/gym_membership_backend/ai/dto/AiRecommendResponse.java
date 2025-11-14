package com.gym_membership.gym_membership_backend.ai.dto;

public record AiRecommendResponse(
        Long membershipTypeId,
        Long trainerId,
        String rationale
) {}

package com.gym_membership.gym_membership_backend.member.dto;


import jakarta.validation.constraints.*;

public record AssignMembershipRequest(
        @NotNull Long memberId,
        @NotNull Long membershipTypeId
) {}

package com.gym_membership.gym_membership_backend.member.dto;

import lombok.Data;

@Data
public class AssignTrainerRequest {
    private Long memberId;
    private Long trainerId;
}

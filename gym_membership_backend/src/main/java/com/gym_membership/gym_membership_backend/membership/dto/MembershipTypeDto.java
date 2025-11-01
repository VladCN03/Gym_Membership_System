package com.gym_membership.gym_membership_backend.membership.dto;

import lombok.Data;

@Data
public class MembershipTypeDto {
    private String type;   // UI trimite "name"
    private double price;
}

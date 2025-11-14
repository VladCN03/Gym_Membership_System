package com.gym_membership.gym_membership_backend.ai.dto;

import java.util.List;

public record AiRecommendRequest(
        String memberName,
        String goal,            // ex: "slabit", "masa musculara", "mobilitate"
        String experience,      // ex: "incepator", "mediu", "avansat"
        String budgetTier,      // "low", "mid", "high"
        String schedule,        // "dimineata", "seara", etc.
        List<SimpleTrainer> trainers,              // pool de antrenori disponibili
        List<SimpleMembershipType> membershipTypes // tipuri de abonament disponibile
) {
    public record SimpleTrainer(Long id, String name, List<String> specialties) {}
    public record SimpleMembershipType(Long id, String type, double price) {}
}

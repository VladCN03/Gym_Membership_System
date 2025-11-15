package com.gym_membership.gym_membership_backend.ai.controller;

import com.gym_membership.gym_membership_backend.ai.dto.AiRecommendResponse;
import com.gym_membership.gym_membership_backend.ai.service.AiFacadeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiFacadeService aiFacade;

    // Recomandare pe baza datelor reale din DB pentru membrul {memberId}
    @PostMapping("/recommend/{memberId}")
    public ResponseEntity<AiRecommendResponse> recommendForMember(@PathVariable Long memberId) {
        var resp = aiFacade.recommendForMember(memberId);
        return ResponseEntity.ok(resp);
    }
}


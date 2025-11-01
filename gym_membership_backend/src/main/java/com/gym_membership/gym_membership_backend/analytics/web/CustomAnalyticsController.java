package com.gym_membership.gym_membership_backend.analytics.web;

import com.gym_membership.gym_membership_backend.analytics.projection.*;
import com.gym_membership.gym_membership_backend.member.repo.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/api/custom") @RequiredArgsConstructor
public class CustomAnalyticsController {
    private final MemberRepository members;

    @GetMapping("/members-per-trainer")
    public List<MembersPerTrainerView> membersPerTrainer(){
        return members.getMembersPerTrainer();
    }

    @GetMapping("/membership-stats-by-type")
    public List<MembershipStatsView> membershipStats(){
        return members.getMembershipStatsByType();
    }
}

package com.gym_membership.gym_membership_backend.ai.service;

import com.gym_membership.gym_membership_backend.ai.dto.AiRecommendRequest;
import com.gym_membership.gym_membership_backend.ai.dto.AiRecommendResponse;
import com.gym_membership.gym_membership_backend.member.domain.Member;
import com.gym_membership.gym_membership_backend.member.repo.MemberRepository;
import com.gym_membership.gym_membership_backend.membership.repo.MembershipTypeRepository;
import com.gym_membership.gym_membership_backend.trainer.repo.TrainerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AiFacadeService {

    private final MemberRepository memberRepo;
    private final TrainerRepository trainerRepo;
    private final MembershipTypeRepository typeRepo;
    private final GeminiService gemini;

    public AiRecommendResponse recommendForMember(Long memberId) {
        Member m = memberRepo.findById(memberId).orElseThrow();

        var trainers = trainerRepo.findAll().stream()
                .map(t -> new AiRecommendRequest.SimpleTrainer(
                        t.getId(),
                        t.getName(),
                        t.getSpecialties() == null || t.getSpecialties().isBlank()
                                ? List.of()
                                : Arrays.stream(t.getSpecialties().split(","))
                                .map(String::trim)
                                .filter(s -> !s.isEmpty()).toList()
                ))
                .toList();

        var types = typeRepo.findAll().stream()
                .map(mt -> new AiRecommendRequest.SimpleMembershipType(
                        mt.getId(),
                        mt.getType(),
                        mt.getPrice().doubleValue()
                ))
                .toList();

        var req = new AiRecommendRequest(
                m.getName(),
                m.getGoal(),
                m.getExperience(),
                m.getBudgetTier(),
                m.getSchedule(),
                trainers,
                types
        );

        return gemini.recommend(req);
    }
}


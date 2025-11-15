package com.gym_membership.gym_membership_backend.member.service;

import com.gym_membership.gym_membership_backend.member.domain.Member;
import com.gym_membership.gym_membership_backend.member.dto.AssignMembershipRequest;
import com.gym_membership.gym_membership_backend.member.dto.CreateMemberRequest;
import com.gym_membership.gym_membership_backend.member.dto.UpdateMemberRequest;
import com.gym_membership.gym_membership_backend.member.repo.MemberRepository;
import com.gym_membership.gym_membership_backend.membership.domain.MembershipType;
import com.gym_membership.gym_membership_backend.membership.repo.MembershipTypeRepository;
import com.gym_membership.gym_membership_backend.trainer.domain.Trainer;
import com.gym_membership.gym_membership_backend.trainer.repo.TrainerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;
    private final MembershipTypeRepository membershipTypes;
    private final TrainerRepository trainerRepository;

    // --- CREATE MEMBER ---
    public Member add(CreateMemberRequest req) {
        Member m = new Member();
        m.setName(req.name());
        m.setEmail(req.email());

        if (req.trainerId() != null) {
            Trainer tr = trainerRepository.findById(req.trainerId())
                    .orElseThrow(() -> new NoSuchElementException("Trainer not found"));
            m.setTrainer(tr);
        }

        m.setGoal(req.goal());
        m.setExperience(req.experience());
        m.setBudgetTier(req.budgetTier());
        m.setSchedule(req.schedule());

        return memberRepository.save(m);
    }

    // --- GET ALL MEMBERS ---
    @Transactional(readOnly = true)
    public List<Member> all() {
        return memberRepository.findAll();
    }

    // --- ASSIGN MEMBERSHIP ---
    public Member assignMembership(AssignMembershipRequest req) {
        Member m = memberRepository.findById(req.memberId())
                .orElseThrow(() -> new NoSuchElementException("Member not found"));
        MembershipType mt = membershipTypes.findById(req.membershipTypeId())
                .orElseThrow(() -> new NoSuchElementException("Membership type not found"));
        m.setMembershipType(mt);
        return memberRepository.save(m);
    }

    // --- ASSIGN / REMOVE TRAINER ---
    public void assignTrainer(Long memberId, Long trainerId) {
        Member m = memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException("Member not found"));

        if (trainerId == null) {
            m.setTrainer(null); // remove trainer
        } else {
            Trainer t = trainerRepository.findById(trainerId)
                    .orElseThrow(() -> new NoSuchElementException("Trainer not found"));
            m.setTrainer(t); // set trainer
        }

        memberRepository.save(m);
    }

    public Member update(Long id, UpdateMemberRequest dto) {
        Member m = memberRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Member not found"));

        if (dto.name() != null) m.setName(dto.name());
        if (dto.email() != null) m.setEmail(dto.email());
        if (dto.goal() != null) m.setGoal(dto.goal());
        if (dto.experience() != null) m.setExperience(dto.experience());
        if (dto.budgetTier() != null) m.setBudgetTier(dto.budgetTier());
        if (dto.schedule() != null) m.setSchedule(dto.schedule());

        // membership
        if (dto.membershipTypeId() != null) {
            MembershipType mt = membershipTypes.findById(dto.membershipTypeId())
                    .orElseThrow(() -> new NoSuchElementException("Membership type not found"));
            m.setMembershipType(mt);
        }

        // trainer
        if (dto.trainerId() != null) {
            Trainer t = trainerRepository.findById(dto.trainerId())
                    .orElseThrow(() -> new NoSuchElementException("Trainer not found"));
            m.setTrainer(t);
        }

        return memberRepository.save(m);
    }
}

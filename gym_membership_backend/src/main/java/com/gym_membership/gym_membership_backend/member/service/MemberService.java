package com.gym_membership.gym_membership_backend.member.service;

import com.gym_membership.gym_membership_backend.member.domain.Member;
import com.gym_membership.gym_membership_backend.member.dto.AssignMembershipRequest;
import com.gym_membership.gym_membership_backend.member.dto.CreateMemberRequest;
import com.gym_membership.gym_membership_backend.member.repo.MemberRepository;
import com.gym_membership.gym_membership_backend.membership.repo.MembershipTypeRepository;
import com.gym_membership.gym_membership_backend.trainer.domain.Trainer;
import com.gym_membership.gym_membership_backend.trainer.repo.TrainerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.List;

@Service @RequiredArgsConstructor @Transactional
public class MemberService {
    private final MemberRepository members;
    private final MembershipTypeRepository types;
    private final TrainerRepository trainers;

    private final MemberRepository memberRepository;
    private final TrainerRepository trainerRepository;

    public Member add(CreateMemberRequest req){
        var builder = Member.builder().name(req.name()).email(req.email());
        if(req.trainerId()!=null){
            var tr = trainers.findById(req.trainerId())
                    .orElseThrow(() -> new NoSuchElementException("Trainer not found"));
            builder.trainer(tr);
        }
        return members.save(builder.build());
    }

    @Transactional(readOnly = true)
    public List<Member> all(){ return members.findAll(); }

    public Member assignMembership(AssignMembershipRequest req){
        var m  = members.findById(req.memberId())
                .orElseThrow(() -> new NoSuchElementException("Member not found"));
        var mt = types.findById(req.membershipTypeId())
                .orElseThrow(() -> new NoSuchElementException("Membership type not found"));
        m.setMembershipType(mt);
        return members.save(m);
    }

    @Transactional
    public void assignTrainer(Long memberId, Long trainerId) {
        Member m = memberRepository.findById(memberId).orElseThrow();
        if (trainerId == null) {
            m.setTrainer(null);               // remove trainer
        } else {
            Trainer t = trainerRepository.findById(trainerId).orElseThrow();
            m.setTrainer(t);                  // set new trainer
        }
        memberRepository.save(m);
    }
}

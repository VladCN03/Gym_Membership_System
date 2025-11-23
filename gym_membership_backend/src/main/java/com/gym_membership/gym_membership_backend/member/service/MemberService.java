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
import com.gym_membership.gym_membership_backend.security.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final MembershipTypeRepository membershipTypes;
    private final TrainerRepository trainerRepository;

    // --- CREATE MEMBER (folosit la admin) ---
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

        // pentru userii creați din admin poți seta implicit rolul
        if (m.getRole() == null) {
            m.setRole(Role.MEMBER);
        }

        return memberRepository.save(m);
    }

    // --- UPDATE MEMBER (folosit de MemberController, linia 52) ---
    public Member update(Long id, UpdateMemberRequest dto) {
        Member m = memberRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Member not found"));

        m.setName(dto.name());
        m.setEmail(dto.email());

        // membership type
        if (dto.membershipTypeId() != null) {
            MembershipType mt = membershipTypes.findById(dto.membershipTypeId())
                    .orElseThrow(() -> new NoSuchElementException("Membership type not found"));
            m.setMembershipType(mt);
        } else {
            m.setMembershipType(null);
        }

        // trainer
        if (dto.trainerId() != null) {
            Trainer tr = trainerRepository.findById(dto.trainerId())
                    .orElseThrow(() -> new NoSuchElementException("Trainer not found"));
            m.setTrainer(tr);
        } else {
            m.setTrainer(null);
        }

        // câmpurile noi
        m.setGoal(dto.goal());
        m.setExperience(dto.experience());
        m.setBudgetTier(dto.budgetTier());
        m.setSchedule(dto.schedule());

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
            m.setTrainer(null);
        } else {
            Trainer t = trainerRepository.findById(trainerId)
                    .orElseThrow(() -> new NoSuchElementException("Trainer not found"));
            m.setTrainer(t);
        }

        memberRepository.save(m);
    }

    // --- UserDetailsService pentru autentificare pe baza MEMBER + ADMIN ---
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Încercăm mai întâi după email
        Member m = memberRepository.findByEmail(username)
                // dacă nu găsim după email, încercăm după name (ex: "admin")
                .orElseGet(() -> memberRepository.findByName(username)
                        .orElseThrow(() -> new UsernameNotFoundException("Member not found")));

        // dacă nu are rol setat în DB, îl tratăm ca MEMBER
        String roleName = (m.getRole() != null) ? m.getRole().name() : Role.MEMBER.name();

        return User.builder()
                // username-ul “oficial” va fi email-ul din DB
                .username(m.getEmail())
                // !!! aici folosim hash-ul din câmpul passwordHash
                .password(m.getPasswordHash())
                .authorities(new SimpleGrantedAuthority("ROLE_" + roleName))
                .build();
    }
}

package com.gym_membership.gym_membership_backend.member.web;

import com.gym_membership.gym_membership_backend.member.domain.Member;
import com.gym_membership.gym_membership_backend.member.dto.*;
import com.gym_membership.gym_membership_backend.member.repo.MemberRepository;
import com.gym_membership.gym_membership_backend.member.service.MemberService;
import com.gym_membership.gym_membership_backend.trainer.domain.Trainer;
import com.gym_membership.gym_membership_backend.trainer.repo.TrainerRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController @RequestMapping("/api/members") @RequiredArgsConstructor
public class MemberController {
    private final MemberService service;
    private final MemberService memberService;
    private final MemberRepository memberRepository;   // injectat
    private final TrainerRepository trainerRepository;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody MemberCreateDto dto) {
        try {
            Member m = new Member();
            m.setName(dto.getName());
            m.setEmail(dto.getEmail());

            if (dto.getTrainerId() != null) {
                Trainer t = trainerRepository    // <-- NU clasa, ci instanța injectată
                        .findById(dto.getTrainerId())
                        .orElseThrow(() -> new RuntimeException("Trainer not found"));
                m.setTrainer(t);
            }

            Member saved = memberRepository.save(m);
            return ResponseEntity.ok(saved);

        } catch (DataIntegrityViolationException ex) {
            return ResponseEntity.badRequest()
                    .body(Map.of("errors", List.of("Duplicate or invalid data")));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestBody UpdateMemberRequest dto) {

        return ResponseEntity.ok(service.update(id, dto));
    }


    @GetMapping
    public List<Member> all(){ return service.all(); }

    @PostMapping("/assign-membership")
    public Member assign(@Valid @RequestBody AssignMembershipRequest req){
        return service.assignMembership(req);
    }

    @PostMapping("/assign-trainer")
    public ResponseEntity<Void> assignTrainer(@RequestBody AssignTrainerRequest req) {
        memberService.assignTrainer(req.getMemberId(), req.getTrainerId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        memberRepository.deleteById(id); // 👈 folosești instanța, nu clasa
        return ResponseEntity.noContent().build();
    }
}

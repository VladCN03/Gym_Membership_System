package com.gym_membership.gym_membership_backend.trainer.web;

import com.gym_membership.gym_membership_backend.trainer.domain.Trainer;
import com.gym_membership.gym_membership_backend.trainer.repo.TrainerRepository;
import com.gym_membership.gym_membership_backend.trainer.service.TrainerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/api/trainers") @RequiredArgsConstructor
public class TrainerController {
    private final TrainerService service;
    private final TrainerRepository trainerRepository;

    @PostMapping
    public Trainer add(@RequestBody Trainer t){ return service.add(t); }

    @GetMapping
    public List<Trainer> all(){ return service.all(); }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody Trainer dto) {
        var t = trainerRepository.findById(id).orElseThrow();
        t.setName(dto.getName());
        t.setEmail(dto.getEmail());
        trainerRepository.save(t);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        trainerRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

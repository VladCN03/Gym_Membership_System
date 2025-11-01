package com.gym_membership.gym_membership_backend.trainer.service;

import com.gym_membership.gym_membership_backend.trainer.domain.Trainer;
import com.gym_membership.gym_membership_backend.trainer.repo.TrainerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service @RequiredArgsConstructor @Transactional
public class TrainerService {
    private final TrainerRepository repo;

    public Trainer add(Trainer t){ return repo.save(t); }
    @Transactional(readOnly = true)
    public List<Trainer> all(){ return repo.findAll(); }
}

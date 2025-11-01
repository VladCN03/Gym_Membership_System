package com.gym_membership.gym_membership_backend.trainer.repo;

import com.gym_membership.gym_membership_backend.trainer.domain.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrainerRepository extends JpaRepository<Trainer, Long> {}
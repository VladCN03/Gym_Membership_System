package com.gym_membership.gym_membership_backend.membership.repo;

import com.gym_membership.gym_membership_backend.membership.domain.MembershipType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MembershipTypeRepository extends JpaRepository<MembershipType, Long> {
    Optional<MembershipType> findByType(String type);
}

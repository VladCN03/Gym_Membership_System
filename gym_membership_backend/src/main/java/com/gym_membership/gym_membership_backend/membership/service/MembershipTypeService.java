package com.gym_membership.gym_membership_backend.membership.service;

import com.gym_membership.gym_membership_backend.membership.domain.MembershipType;
import com.gym_membership.gym_membership_backend.membership.repo.MembershipTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service @RequiredArgsConstructor @Transactional
public class MembershipTypeService {
    private final MembershipTypeRepository repo;

    public MembershipType add(MembershipType mt){ return repo.save(mt); }
    @Transactional(readOnly = true)
    public List<MembershipType> all(){ return repo.findAll(); }
}

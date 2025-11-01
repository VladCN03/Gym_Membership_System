package com.gym_membership.gym_membership_backend.membership.web;

import com.gym_membership.gym_membership_backend.membership.domain.MembershipType;
import com.gym_membership.gym_membership_backend.membership.dto.MembershipTypeDto;
import com.gym_membership.gym_membership_backend.membership.repo.MembershipTypeRepository;
import com.gym_membership.gym_membership_backend.membership.service.MembershipTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController @RequestMapping("/api/membership-types") @RequiredArgsConstructor
public class MembershipTypeController {
    private final MembershipTypeService service;
    private final MembershipTypeRepository repo;

    // 🔹 GET — toate abonamentele
    @GetMapping
    public List<MembershipType> all() {
        return service.all();
    }

    // 🔹 POST — adaugă un nou tip de abonament
    @PostMapping
    public ResponseEntity<MembershipType> create(@RequestBody MembershipTypeDto dto) {
        MembershipType mt = new MembershipType();
        mt.setType(dto.getType()); // <- Folosește direct "type" din UI
        mt.setPrice(BigDecimal.valueOf(dto.getPrice()));
        return ResponseEntity.ok(repo.save(mt));
    }

    // 🔹 PUT — actualizează un abonament existent
    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody MembershipTypeDto dto) {
        var mt = repo.findById(id).orElseThrow();
        mt.setType(dto.getType()); // <- actualizează "type"
        mt.setPrice(BigDecimal.valueOf(dto.getPrice()));
        repo.save(mt);
        return ResponseEntity.ok().build();
    }

    // 🔹 DELETE — șterge un abonament
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

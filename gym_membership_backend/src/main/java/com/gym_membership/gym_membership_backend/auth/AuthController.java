package com.gym_membership.gym_membership_backend.auth;

import com.gym_membership.gym_membership_backend.auth.dto.ChangePasswordSimpleRequest;
import com.gym_membership.gym_membership_backend.auth.dto.RegisterRequest;
import com.gym_membership.gym_membership_backend.member.domain.Member;
import com.gym_membership.gym_membership_backend.member.repo.MemberRepository;
import com.gym_membership.gym_membership_backend.security.JwtUtil;
import com.gym_membership.gym_membership_backend.security.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
//@CrossOrigin(origins = {"http://localhost:5173"}, allowCredentials = "true")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtUtil jwt;
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepo;

    // ================== LOGIN ==================

    public record LoginReq(String username, String password) {}

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginReq req) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.username(), req.password())
        );

        UserDetails user = (UserDetails) auth.getPrincipal();
        String role = user.getAuthorities()
                .iterator()
                .next()
                .getAuthority()
                .replace("ROLE_", "");

        String token = jwt.generate(user.getUsername(), role);

        // căutăm Member-ul după email (username = email la noi)
        Member m = memberRepo.findByEmail(user.getUsername())
                .orElseThrow(() -> new RuntimeException("Member not found for email " + user.getUsername()));

        return ResponseEntity.ok(
                Map.of(
                        "token", token,
                        "email", m.getEmail(),
                        "role", role,
                        "name", m.getName(),
                        "memberId", m.getId()
                )
        );
    }

    // ================== REGISTER ==================

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {

        // 1. Verificăm dacă email-ul există deja
        if (memberRepo.findByEmail(req.email()).isPresent()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Email already used"));
        }

        // 2. Creăm Member (profil + user de autentificare)
        Member m = new Member();
        m.setName(req.name());
        m.setEmail(req.email());
        m.setGoal(req.goal());
        m.setExperience(req.experience());
        m.setBudgetTier(req.budgetTier());
        m.setSchedule(req.schedule());

        // parolă criptată
        m.setPasswordHash(passwordEncoder.encode(req.password()));
        // rol implicit MEMBER
        m.setRole(Role.MEMBER);

        memberRepo.save(m);

        // 3. Generăm token JWT pentru noul user
        String role = m.getRole().name();
        String token = jwt.generate(m.getEmail(), role);

        System.out.println(">>> REGISTER CALLED from " + req.email());

        return ResponseEntity.ok(
                Map.of(
                        "token", token,
                        "email", m.getEmail(),
                        "role", role,
                        "name", m.getName(),
                        "memberId", m.getId()
                )
        );


    }

    @PostMapping("/reset-password-simple")
    public ResponseEntity<?> resetPasswordSimple(@RequestBody ChangePasswordSimpleRequest req) {

        var optMember = memberRepo.findByEmail(req.email());
        if (optMember.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", "Email not found"));
        }

        var member = optMember.get();
        member.setPasswordHash(passwordEncoder.encode(req.newPassword()));
        memberRepo.save(member);

        return ResponseEntity.ok().build();
    }


}

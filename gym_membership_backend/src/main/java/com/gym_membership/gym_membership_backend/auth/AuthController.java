package com.gym_membership.gym_membership_backend.auth;

import com.gym_membership.gym_membership_backend.security.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:5173"}, allowCredentials = "true")
public class AuthController {
    private final AuthenticationManager authManager;
    private final JwtUtil jwt;

    public AuthController(AuthenticationManager am, JwtUtil jwt) {
        this.authManager = am; this.jwt = jwt;
    }

    public record LoginReq(String username, String password){}

    @PostMapping("/login")
    public Map<String,Object> login(@RequestBody LoginReq req){
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.username(), req.password()));
        UserDetails user = (UserDetails) auth.getPrincipal();
        String role = user.getAuthorities().iterator().next().getAuthority().replace("ROLE_","");
        String token = jwt.generate(user.getUsername(), role);
        return Map.of("token", token, "username", user.getUsername(), "role", role);
    }


}

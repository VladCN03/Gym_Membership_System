package com.gym_membership.gym_membership_backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    public JwtAuthFilter(JwtUtil jwtUtil, UserDetailsService uds) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = uds;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");

        // 👉 dacă nu avem Bearer token, lăsăm cererea să meargă mai departe (fără autentificare)
        if (header == null || !header.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }


        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                var jws = jwtUtil.parse(token);
                String username = jws.getBody().getSubject();
                Object roleClaim = jws.getBody().get("role");

                List<GrantedAuthority> auths;
                if (roleClaim != null) {
                    auths = List.of(new SimpleGrantedAuthority("ROLE_" + roleClaim.toString()));
                } else {
                    var ud = userDetailsService.loadUserByUsername(username);
                    auths = new ArrayList<>(ud.getAuthorities());     // ⬅️ fall back
                }

                var auth = new UsernamePasswordAuthenticationToken(username, null, auths);
                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (Exception ignored) { }
        }
        chain.doFilter(request, response);
    }
}


package com.example.User.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtutil) {
        this.jwtUtil = jwtutil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if ("/auth/login".equals(request.getServletPath())
                || "/auth/refresh".equals(request.getServletPath())
                || "/api/user/create".equals(request.getServletPath())
                || request.getServletPath().startsWith("/swagger-ui/")
                || request.getServletPath().startsWith("/v3/api-docs")
                || request.getServletPath().startsWith("/error")
                || request.getServletPath().equals("/swagger-ui.html")
        ) {
            filterChain.doFilter(request, response);
            return;
        }
        final String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            if (jwtUtil.validateToken(token, false)) {
                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(jwtUtil.getSecretKey(false)) // Use your secret key
                        .build()
                        .parseClaimsJws(token)
                        .getBody();

                String email = claims.getSubject();
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        email, null, Collections.emptyList()
                );
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
                filterChain.doFilter(request, response);
            }
        }

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.getWriter().flush();
        response.getWriter().close();
    }
}
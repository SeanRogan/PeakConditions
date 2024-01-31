package com.capstone.D424.service.impl;

import com.capstone.D424.dto.AuthenticationRequest;
import com.capstone.D424.dto.AuthenticationResponse;
import com.capstone.D424.dto.RegisterRequest;
import com.capstone.D424.dto.RegistrationResponse;
import com.capstone.D424.entities.User;
import com.capstone.D424.entities.UserProfile;
import com.capstone.D424.repository.UserRepository;
import com.capstone.D424.service.AuthenticationService;
import com.capstone.D424.service.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Service
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;

    public RegistrationResponse register(RegisterRequest request) {
        RegistrationResponse registrationResponse = new RegistrationResponse();
        //if the email doesnt exist in the repository, continue with registration
        if(repository.findByEmail(request.getEmail()).isEmpty()) {
            User user = User.builder()
                    .email(request.getEmail())
                    .username(request.getUsername())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .profile(new UserProfile())
                    .role(request.getRole())
                    .build();
            repository.save(user);
            AuthenticationResponse authenticationResponse = AuthenticationResponse.builder()
                    .user(user)
                    .build();
            registrationResponse.setAuthenticationResponse(authenticationResponse);
            registrationResponse.setSuccess(true);
        } else {
            //if the email already exists, return no user and set success to false.
            registrationResponse.setAuthenticationResponse(null);
            registrationResponse.setSuccess(false);
        }
        return registrationResponse;
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        User user = null;
        boolean authenticated =  false;
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
            authenticated = true;
        } catch (AuthenticationException ae) {
            log.warn("Authentication manager failed to authenticate user: " + request.getEmail() + " ->>" + ae);
        }
        if(authenticated) {
            try {
                user = repository.findByEmail(request.getEmail())
                        .orElseThrow();
            } catch (NoSuchElementException nse) {
                log.error("Could not find the user in the database" + nse);
            }
        }
            return AuthenticationResponse.builder()
                    .user(user)
                    .build();

    }


    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String username;
        String refreshToken = "";
        if(authHeader!= null && authHeader.startsWith("Bearer ")) refreshToken = authHeader.substring(7);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            Cookie[] cookies = request.getCookies();
            for(Cookie cookie : cookies) {
                if(cookie.getName().equals("refresh_token")) refreshToken = cookie.getValue();
            }
        }
        username = jwtService.extractUsername(refreshToken);
        if (username != null && !jwtService.isTokenExpired(refreshToken)) {
            UserDetails user = userDetailsService.loadUserByUsername(username);
            if (jwtService.isTokenValid(refreshToken, user)) {
                var newAccessToken = jwtService.generateToken(user);
                var newRefreshToken = jwtService.generateRefreshToken(user);
                Cookie accessCookie = new Cookie("access_token", newAccessToken);
                accessCookie.setHttpOnly(true);
                accessCookie.setSecure(true);
                accessCookie.setMaxAge(3600);
                accessCookie.setPath("/");
                response.addCookie(accessCookie);
                Cookie refreshCookie = new Cookie("refresh_token", newRefreshToken);
                refreshCookie.setHttpOnly(true);
                refreshCookie.setSecure(true);
                refreshCookie.setMaxAge(604800);
                refreshCookie.setPath("/");
                response.addCookie(refreshCookie);
            }
        }
    }

}

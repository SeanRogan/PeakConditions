package com.capstone.D424.controller;

import com.capstone.D424.dto.AuthenticationRequest;
import com.capstone.D424.dto.AuthenticationResponse;
import com.capstone.D424.dto.RegisterRequest;
import com.capstone.D424.dto.RegistrationResponse;
import com.capstone.D424.entities.User;
import com.capstone.D424.service.AuthenticationService;
import com.capstone.D424.service.JwtService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j

public class AuthController {

    private final JwtService jwtService;
    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody AuthenticationRequest authRequest, HttpServletResponse response) {
        try {
            AuthenticationResponse authenticationResponse = authenticationService.authenticate(authRequest);
            //
            if (authenticationResponse != null) {
                addCookies(response, authenticationResponse);
                return new ResponseEntity<>(authenticationResponse, HttpStatus.OK);
            } else {
                log.warn("Authentication failed at login attempt");
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (NullPointerException npe) {
            log.warn("Null pointer exception caught. This can happen when a user attemps to login with invalid credentials. Details: " + npe);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception ex) {
            log.warn("something unexpected went wrong in the login controller: " + ex);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<RegistrationResponse> register(@RequestBody RegisterRequest registerRequest, HttpServletResponse response) {
        try {
            RegistrationResponse registrationResponse = authenticationService.register(registerRequest);
            if (!registrationResponse.isSuccess()) {
                return new ResponseEntity<>(registrationResponse, HttpStatus.BAD_REQUEST);
            } else {
                AuthenticationResponse authenticationResponse = registrationResponse.getAuthenticationResponse();
                addCookies(response, authenticationResponse);
                return new ResponseEntity<>(registrationResponse, HttpStatus.CREATED);
            }
        } catch (Exception e) {
            log.warn("registration failed :" + e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/refresh")
    public void refresh(HttpServletRequest request, HttpServletResponse response) {
        try {
            authenticationService.refreshToken(request, response);
        } catch (IOException e) {
            log.warn("refresh token attempt failed: " + e);
        }
    }

    private void addCookies(HttpServletResponse response, AuthenticationResponse authenticationResponse) {
        User user = authenticationResponse.getUser();

        // Access Token Cookie
        String accessToken = jwtService.generateToken(user);
        String accessCookieStr = "access_token=" + accessToken +
                "; HttpOnly; Secure; Path=/; Max-Age=3600; SameSite=None";
        response.setHeader("Set-Cookie", accessCookieStr);

        // Refresh Token Cookie
        String refreshToken = jwtService.generateRefreshToken(user);
        String refreshCookieStr = "refresh_token=" + refreshToken +
                "; HttpOnly; Secure; Path=/; Max-Age=604800; SameSite=None";
        response.addHeader("Set-Cookie", refreshCookieStr);
//
//        Cookie accessCookie = new Cookie("access_token", jwtService.generateToken(user));
//        accessCookie.setHttpOnly(true);
//        accessCookie.setSecure(true); // Set to true in production for HTTPS
//        accessCookie.setPath("/");
//        accessCookie.setMaxAge(3600); // Expires in 1 hour
//        response.addCookie(accessCookie);
//
//        Cookie refreshCookie = new Cookie("refresh_token", jwtService.generateRefreshToken(user));
//        refreshCookie.setHttpOnly(true);
//        refreshCookie.setSecure(true); // Set to true in production for HTTPS
//        refreshCookie.setPath("/");
//        refreshCookie.setMaxAge(604800); // Expires in 1 week
//
//        response.addCookie(refreshCookie);
    }

}



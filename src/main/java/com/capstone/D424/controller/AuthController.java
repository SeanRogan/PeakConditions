package com.capstone.D424.controller;

import com.capstone.D424.dto.AuthenticationRequest;
import com.capstone.D424.dto.AuthenticationResponse;
import com.capstone.D424.dto.RegisterRequest;
import com.capstone.D424.dto.RegistrationResponse;
import com.capstone.D424.entities.User;
import com.capstone.D424.service.AuthenticationService;
import com.capstone.D424.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * Controller class handling HTTP requests related to authentication and registration of users. provides endpoints for
 * users to register and login to their account and to refresh access token cookies.
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j

public class AuthController {

    private final JwtService jwtService;
    private final AuthenticationService authenticationService;

    /**
     * Logs in the user to their account with login credentials. returns a ResponseEntity with an AuthenticationResponse for the
     * body if the request was valid. No response body is returned if request or credentials are not valid.
     *
     * @param authRequest the body of the request is a json object which should contain the user's login credentials.
     * @param response    the servlet response object.
     * @return a responseEntity is returned with the status code
     * and the AuthenticationResponse as a response body if the request was valid.
     * @see AuthenticationResponse
     * @see AuthenticationRequest
     */
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

    /**
     * Registers a new user account with the provided credentials. returns a ResponseEntity with RegistrationResponse for the
     * body if the request was valid. No response body is returned if request or credentials are not valid.
     *
     * @param registerRequest the body of the request is a json object which should contain the registration credentials.
     * @param response        the servlet response object.
     * @return a responseEntity is returned with the status code
     * and the RegistrationResponse as a response body if the request was valid.
     * @see RegistrationResponse
     * @see RegisterRequest
     */
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

    /**
     * Refreshes the access-token cookie and refresh-token cookie, with a valid refresh-token in the request.
     *
     * @param request  The HTTP servlet request object, containing the request's refresh-token.
     * @param response The HTTP servlet response object with updated cookies.
     */
    @PostMapping("/refresh")
    public void refresh(HttpServletRequest request, HttpServletResponse response) {
        try {
            authenticationService.refreshToken(request, response);
        } catch (IOException e) {
            log.warn("refresh token attempt failed: " + e);
        }
    }

    /**
     * Creates access-token and refresh-token cookies to be associated with an HTTP servlet response.
     *
     * @param response               the http servlet response to be returned.
     * @param authenticationResponse the response from the authentication service.
     * @see AuthenticationResponse
     */
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

    }

}



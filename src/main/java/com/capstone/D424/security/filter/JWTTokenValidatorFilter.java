package com.capstone.D424.security.filter;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.capstone.D424.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JWTTokenValidatorFilter extends OncePerRequestFilter {


    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        Cookie[] cookies = request.getCookies();
        String token = "";
        if(cookies != null && cookies.length > 0){
            for(Cookie cookie: cookies) {
                if (cookie.getName().equals("access_token")) {
                    log.info("token from cookie is: " + cookie);
                    token = cookie.getValue();
                }
            }
        }
        //if there is no cookie based token, check for an Authorization header token
        if(token.isEmpty()) {
            log.info("no access token found in cookies, checking for Authorization Header..");

            String authHeader = request.getHeader("Authorization");
            if(authHeader == null || !authHeader.startsWith("Bearer ")) {
                log.info("no Bearer token found");
            filterChain.doFilter(request,response);
            return;
            } else token = authHeader.substring(7);
        }

        try {
            String username = jwtService.extractUsername(token);
            UserDetails user = userDetailsService.loadUserByUsername(username);
            if (jwtService.isTokenExpired(token)) {
                throw new JWTVerificationException("Token is expired!");
            }
            if (!jwtService.isTokenValid(token, user)) {
                throw new JWTVerificationException("Token is invalid!");
            }
            if (!jwtService.checkForAuthorities(token)) {
                throw new JWTVerificationException("Authorities claims null or empty. possible refresh token used in place of access token");
            }
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, null, user.getAuthorities());
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
        } catch (Exception e) {
            log.error("Something unexpected went wrong during token validation." + e);
            throw new BadCredentialsException("Invalid Token received!");
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.equals("/api/v1/auth/refresh") || path.equals("/api/v1/auth/register") || path.startsWith("/api/v1/public");
    }
}

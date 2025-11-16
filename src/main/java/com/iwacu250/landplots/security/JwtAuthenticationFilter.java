package com.iwacu250.landplots.security;

import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @PostConstruct
    public void init() {
        logger.info("JWT Authentication Filter initialized");
    }

    @Override
    protected void doFilterInternal(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response, @Nonnull FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String jwt = getJwtFromRequest(request);
            logger.debug("JWT filter processing request: {} {}", request.getMethod(), request.getRequestURI());
            logger.debug("JWT token found: {}", jwt != null ? "YES" : "NO");
            
            // Special logging for upload endpoints
            if (request.getRequestURI().contains("/uploadImage")) {
                System.out.println("JWT Filter: Processing uploadImage request");
                System.out.println("JWT Filter: Method: " + request.getMethod());
                System.out.println("JWT Filter: URI: " + request.getRequestURI());
                System.out.println("JWT Filter: Content Type: " + request.getContentType());
                System.out.println("JWT Filter: Authorization: " + (request.getHeader("Authorization") != null ? "PRESENT" : "MISSING"));
            }

            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                Long userId = tokenProvider.getUserIdFromToken(jwt);
                logger.debug("JWT token validated for user ID: {}", userId);

                UserDetails userDetails = userDetailsService.loadUserById(userId);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
                logger.debug("Authentication set in security context for user: {}", userDetails.getUsername());
            } else {
                logger.debug("JWT token missing or invalid, continuing without authentication");
            }
        } catch (Exception ex) {
            logger.error("Could not set user authentication in security context", ex);
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        logger.debug("Authorization header: {}", bearerToken != null ? "PRESENT" : "MISSING");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            String token = bearerToken.substring(7);
            logger.debug("Extracted JWT token (first 20 chars): {}", token.length() > 20 ? token.substring(0, 20) + "..." : token);
            return token;
        }
        return null;
    }
}
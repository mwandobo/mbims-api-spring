package com.mwalimubank.mbimsapi.core.services;

import com.mwalimubank.mbimsapi.core.utils.JwtUtil;
import com.mwalimubank.mbimsapi.features.user.UserEntity;
import com.mwalimubank.mbimsapi.features.user.UserRepository;
import com.mwalimubank.mbimsapi.features.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


@Service
@RequiredArgsConstructor
public class CurrentUserService {

    private final UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

//    public UserEntity getCurrentUser() {  // also fixed the typo in the method name
//        String token = extractTokenFromRequest();
//        if (token != null && jwtUtil.validateToken(token)) {
//            Long userId = jwtUtil.extractUserId(token);
//            return userRepository.findById(userId)
//                    .orElseThrow(() -> new IllegalStateException("User not authenticated or invalid token"));
//        }
//        throw new IllegalStateException("User not authenticated or invalid token");
//    }

    public UserEntity getCurrentUser() {
        String token = extractTokenFromRequest();
        if (token != null && jwtUtil.validateToken(token)) {
            Long userId = jwtUtil.extractUserId(token);
            return userRepository.findByIdWithRole(userId)
                    .orElseThrow(() -> new IllegalStateException("User not authenticated or invalid token"));
        }
        throw new IllegalStateException("User not authenticated or invalid token");
    }

    public Long getCurrentUserId() {
        String token = extractTokenFromRequest();
        if (token != null && jwtUtil.validateToken(token)) {
            return jwtUtil.extractUserId(token);
        }
        throw new IllegalStateException("User not authenticated or invalid token");
    }

    public Long getCurrentUserRoleId() {
        String token = extractTokenFromRequest();
        if (token != null && jwtUtil.validateToken(token)) {
            return jwtUtil.extractUserRoleId(token);
        }
        throw new IllegalStateException("User not authenticated or invalid token");
    }

    public String getCurrentUserEmail() {
        String token = extractTokenFromRequest();
        if (token != null && jwtUtil.validateToken(token)) {
            return jwtUtil.extractEmail(token);
        }
        throw new IllegalStateException("User not authenticated or invalid token");
    }

    private String extractTokenFromRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            String authorizationHeader = request.getHeader("Authorization");

            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                return authorizationHeader.substring(7);
            }
        }
        return null;
    }

    // Alternative method using SecurityContext
    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return null;
    }
}
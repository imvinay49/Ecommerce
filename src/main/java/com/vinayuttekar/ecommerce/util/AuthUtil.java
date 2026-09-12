package com.vinayuttekar.ecommerce.util;

import com.vinayuttekar.ecommerce.entity.User;
import com.vinayuttekar.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthUtil {

    private final UserRepository userRepository;

    public Authentication authentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new IllegalStateException("User is not authenticated");
        }
        return authentication;
    }

    public String loggedInEmail() {
        return authentication().getName();
    }

    public Long loggedInUserId() {
        return loggedInUser().getUserId();
    }

    public User loggedInUser() {
        return userRepository.findByEmail(loggedInEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + loggedInEmail()));
    }
}

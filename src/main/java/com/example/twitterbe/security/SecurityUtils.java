package com.example.twitterbe.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.util.WebUtils;

@Component
public class SecurityUtils {
    public String getTokenFromRequest(HttpServletRequest request) {
        String token = null;
        Cookie cookieToken = WebUtils.getCookie(request, "token");
        if (cookieToken != null) {
            token = cookieToken.getValue();
        } else {
            String bearerToken = request.getHeader("Authorization");
            System.out.println("bearerToken 1 "+ bearerToken);
            if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
                token = bearerToken.substring(7);
            }
        }
        return token;
    }
}

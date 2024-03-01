package com.example.twitterbe.api;

import com.example.twitterbe.security.CustomPrincipal;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@EnableAutoConfiguration(exclude= SecurityAutoConfiguration.class)
public class TestController {
    @GetMapping(value = "/api/employees")
    public CustomPrincipal getEmployee(@AuthenticationPrincipal CustomPrincipal customPrincipal)  {
        System.out.println(customPrincipal); //POC that the user is actually logged and we have his information on session
        return customPrincipal;
    }
}


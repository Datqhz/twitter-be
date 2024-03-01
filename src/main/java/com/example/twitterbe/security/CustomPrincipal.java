package com.example.twitterbe.security;

import lombok.Data;

@Data
public class CustomPrincipal {
    private String uid;
    private String email;
}

package com.ensayo.api.auth;

public record TokenResponse(String token, long expiresAt) {
}
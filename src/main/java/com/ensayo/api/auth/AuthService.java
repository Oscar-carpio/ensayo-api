package com.ensayo.api.auth;

import com.ensayo.api.user.User;
import com.ensayo.api.user.UserRepository;
import java.util.Locale;
import java.util.UUID;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthService {

    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public AuthService(
            UserRepository users,
            PasswordEncoder passwordEncoder,
            TokenService tokenService
    ) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    public TokenResponse register(RegisterRequest request) {
        String email = normalize(request.email());
        if (users.existsByEmail(email)) {
            throw new EmailAlreadyUsedException();
        }
        User user = new User(
                UUID.randomUUID(),
                email,
                passwordEncoder.encode(request.password()),
                System.currentTimeMillis()
        );
        users.save(user);
        return tokenService.createToken(user.getId());
    }

    @Transactional(readOnly = true)
    public TokenResponse login(LoginRequest request) {
        User user = users.findByEmail(normalize(request.email()))
                .filter(found -> passwordEncoder.matches(request.password(), found.getPasswordHash()))
                .orElseThrow(InvalidCredentialsException::new);
        return tokenService.createToken(user.getId());
    }

    // "Carpio@Mail.com " y "carpio@mail.com" son la misma cuenta
    private static String normalize(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
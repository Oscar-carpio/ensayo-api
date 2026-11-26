package com.ensayo.api.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ensayo.api.TestcontainersConfiguration;
import com.ensayo.api.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@Transactional
class AuthServiceTest {

    @Autowired
    private AuthService service;

    @Autowired
    private UserRepository users;

    @Autowired
    private JwtDecoder decoder;

    @Test
    void registrarCreaElUsuarioConLaContrasenaCifrada() {
        service.register(new RegisterRequest("Carpio@Example.com", "supersecreta"));

        var user = users.findByEmail("carpio@example.com").orElseThrow();
        assertThat(user.getPasswordHash()).isNotEqualTo("supersecreta");
    }

    @Test
    void elTokenLlevaElIdDelUsuario() {
        TokenResponse response = service.register(new RegisterRequest("a@example.com", "supersecreta"));

        Jwt jwt = decoder.decode(response.token());

        var user = users.findByEmail("a@example.com").orElseThrow();
        assertThat(jwt.getSubject()).isEqualTo(user.getId().toString());
    }

    @Test
    void noSePuedeRegistrarDosVecesElMismoEmail() {
        service.register(new RegisterRequest("b@example.com", "supersecreta"));

        assertThatThrownBy(() -> service.register(new RegisterRequest("B@example.com", "otraclave1")))
                .isInstanceOf(EmailAlreadyUsedException.class);
    }

    @Test
    void loginCorrectoDevuelveToken() {
        service.register(new RegisterRequest("c@example.com", "supersecreta"));

        TokenResponse response = service.login(new LoginRequest("c@example.com", "supersecreta"));

        assertThat(response.token()).isNotBlank();
    }

    @Test
    void loginConContrasenaIncorrectaFalla() {
        service.register(new RegisterRequest("d@example.com", "supersecreta"));

        assertThatThrownBy(() -> service.login(new LoginRequest("d@example.com", "equivocada")))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    void loginConEmailQueNoExisteFalla() {
        assertThatThrownBy(() -> service.login(new LoginRequest("nadie@example.com", "supersecreta")))
                .isInstanceOf(InvalidCredentialsException.class);
    }
}
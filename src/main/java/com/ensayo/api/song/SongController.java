package com.ensayo.api.song;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/songs")
public class SongController {

    private final SongService service;

    public SongController(SongService service) {
        this.service = service;
    }

    @GetMapping
    public List<SongResponse> findAll(@AuthenticationPrincipal Jwt jwt) {
        return service.findAll(userId(jwt));
    }

    @GetMapping("/{id}")
    public SongResponse findById(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID id) {
        return service.findById(userId(jwt), id);
    }

    @PutMapping("/{id}")
    public SongResponse save(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID id,
            @Valid @RequestBody SongRequest request
    ) {
        return service.save(userId(jwt), id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID id) {
        service.delete(userId(jwt), id);
    }

    // El "sub" del token es el id del usuario: lo pusimos nosotros en TokenService
    private static UUID userId(Jwt jwt) {
        return UUID.fromString(jwt.getSubject());
    }
}
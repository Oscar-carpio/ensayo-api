package com.ensayo.api.song;

import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class SongNotFoundException extends RuntimeException {

    public SongNotFoundException(UUID id) {
        super("Canción no encontrada: " + id);
    }
}
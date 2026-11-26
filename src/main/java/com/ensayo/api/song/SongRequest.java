package com.ensayo.api.song;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SongRequest(
        @NotBlank @Size(max = 200) String title,
        @Size(max = 200) String artist,
        @NotBlank @Size(max = 10) String musicalKey,
        @NotNull String content
) {
}
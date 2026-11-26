package com.ensayo.api.song;

import java.util.UUID;

public record SongResponse(
        UUID id,
        String title,
        String artist,
        String musicalKey,
        String content,
        long createdAt,
        long updatedAt
) {

    static SongResponse from(Song song) {
        return new SongResponse(
                song.getId(),
                song.getTitle(),
                song.getArtist(),
                song.getMusicalKey(),
                song.getContent(),
                song.getCreatedAt(),
                song.getUpdatedAt()
        );
    }
}
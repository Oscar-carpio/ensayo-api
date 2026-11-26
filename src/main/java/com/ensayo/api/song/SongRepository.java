package com.ensayo.api.song;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SongRepository extends JpaRepository<Song, UUID> {

    List<Song> findByDeletedFalseOrderByTitleAsc();
}

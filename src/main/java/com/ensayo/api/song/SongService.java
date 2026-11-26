package com.ensayo.api.song;

import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SongService {

    private final SongRepository repository;

    public SongService(SongRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<SongResponse> findAll() {
        return repository.findByDeletedFalseOrderByTitleAsc().stream()
                .map(SongResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public SongResponse findById(UUID id) {
        return SongResponse.from(getActive(id));
    }

    // Crear o actualizar (upsert): el id lo decide el cliente
    public SongResponse save(UUID id, SongRequest request) {
        long now = System.currentTimeMillis();
        Song song = repository.findById(id).orElseGet(() -> {
            Song created = new Song(id);
            created.setCreatedAt(now);
            return created;
        });
        song.setTitle(request.title());
        song.setArtist(request.artist());
        song.setMusicalKey(request.musicalKey());
        song.setContent(request.content());
        song.setDeleted(false);
        song.setUpdatedAt(now);
        return SongResponse.from(repository.save(song));
    }

    // Borrado lógico: la fila se queda para que los otros móviles se enteren al sincronizar
    public void delete(UUID id) {
        Song song = getActive(id);
        song.setDeleted(true);
        song.setUpdatedAt(System.currentTimeMillis());
    }

    private Song getActive(UUID id) {
        return repository.findById(id)
                .filter(song -> !song.isDeleted())
                .orElseThrow(() -> new SongNotFoundException(id));
    }
}
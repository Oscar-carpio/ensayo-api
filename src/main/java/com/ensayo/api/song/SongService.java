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
    public List<SongResponse> findAll(UUID ownerId) {
        return repository.findByOwnerIdAndDeletedFalseOrderByTitleAsc(ownerId).stream()
                .map(SongResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public SongResponse findById(UUID ownerId, UUID id) {
        return SongResponse.from(getActive(ownerId, id));
    }

    public SongResponse save(UUID ownerId, UUID id, SongRequest request) {
        long now = System.currentTimeMillis();
        Song song = repository.findById(id).orElseGet(() -> {
            Song created = new Song(id, ownerId);
            created.setCreatedAt(now);
            return created;
        });
        // Ese id ya es de otra persona: respondemos 404, sin revelar que existe
        if (!song.getOwnerId().equals(ownerId)) {
            throw new SongNotFoundException(id);
        }
        song.setTitle(request.title());
        song.setArtist(request.artist());
        song.setMusicalKey(request.musicalKey());
        song.setContent(request.content());
        song.setDeleted(false);
        song.setUpdatedAt(now);
        return SongResponse.from(repository.save(song));
    }

    public void delete(UUID ownerId, UUID id) {
        Song song = getActive(ownerId, id);
        song.setDeleted(true);
        song.setUpdatedAt(System.currentTimeMillis());
    }

    private Song getActive(UUID ownerId, UUID id) {
        return repository.findById(id)
                .filter(song -> song.getOwnerId().equals(ownerId) && !song.isDeleted())
                .orElseThrow(() -> new SongNotFoundException(id));
    }
}
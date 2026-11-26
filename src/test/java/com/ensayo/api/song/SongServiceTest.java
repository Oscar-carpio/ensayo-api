package com.ensayo.api.song;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ensayo.api.TestcontainersConfiguration;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@Transactional // cada test deshace sus cambios al terminar
class SongServiceTest {

    @Autowired
    private SongService service;

    private final SongRequest amazingGrace =
            new SongRequest("Amazing Grace", "John Newton", "G", "[G]Amazing [C]grace");

    @Test
    void guardarUnaCancionNuevaLaCrea() {
        UUID id = UUID.randomUUID();

        SongResponse saved = service.save(id, amazingGrace);

        assertThat(saved.id()).isEqualTo(id);
        assertThat(service.findById(id).title()).isEqualTo("Amazing Grace");
    }

    @Test
    void guardarDosVecesActualizaYConservaLaFechaDeCreacion() {
        UUID id = UUID.randomUUID();
        SongResponse first = service.save(id, amazingGrace);

        SongRequest edited = new SongRequest("Amazing Grace", "John Newton", "A", "[A]Amazing");
        SongResponse second = service.save(id, edited);

        assertThat(second.musicalKey()).isEqualTo("A");
        assertThat(second.createdAt()).isEqualTo(first.createdAt());
        assertThat(service.findAll()).hasSize(1);
    }

    @Test
    void borrarOcultaLaCancionDeLaLista() {
        UUID id = UUID.randomUUID();
        service.save(id, amazingGrace);

        service.delete(id);

        assertThat(service.findAll()).isEmpty();
        assertThatThrownBy(() -> service.findById(id))
                .isInstanceOf(SongNotFoundException.class);
    }

    @Test
    void borrarUnaCancionQueNoExisteDaNotFound() {
        assertThatThrownBy(() -> service.delete(UUID.randomUUID()))
                .isInstanceOf(SongNotFoundException.class);
    }
}
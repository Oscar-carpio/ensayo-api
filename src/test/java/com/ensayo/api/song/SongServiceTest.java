package com.ensayo.api.song;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ensayo.api.TestcontainersConfiguration;
import com.ensayo.api.user.User;
import com.ensayo.api.user.UserRepository;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@Transactional
class SongServiceTest {

    @Autowired
    private SongService service;

    @Autowired
    private UserRepository users;

    private UUID owner;

    private final SongRequest amazingGrace =
            new SongRequest("Amazing Grace", "John Newton", "G", "[G]Amazing [C]grace");

    @BeforeEach
    void createOwner() {
        owner = createUser("musico@example.com");
    }

    @Test
    void guardarUnaCancionNuevaLaCrea() {
        UUID id = UUID.randomUUID();

        SongResponse saved = service.save(owner, id, amazingGrace);

        assertThat(saved.id()).isEqualTo(id);
        assertThat(service.findById(owner, id).title()).isEqualTo("Amazing Grace");
    }

    @Test
    void guardarDosVecesActualizaYConservaLaFechaDeCreacion() {
        UUID id = UUID.randomUUID();
        SongResponse first = service.save(owner, id, amazingGrace);

        SongRequest edited = new SongRequest("Amazing Grace", "John Newton", "A", "[A]Amazing");
        SongResponse second = service.save(owner, id, edited);

        assertThat(second.musicalKey()).isEqualTo("A");
        assertThat(second.createdAt()).isEqualTo(first.createdAt());
        assertThat(service.findAll(owner)).hasSize(1);
    }

    @Test
    void borrarOcultaLaCancionDeLaLista() {
        UUID id = UUID.randomUUID();
        service.save(owner, id, amazingGrace);

        service.delete(owner, id);

        assertThat(service.findAll(owner)).isEmpty();
        assertThatThrownBy(() -> service.findById(owner, id))
                .isInstanceOf(SongNotFoundException.class);
    }

    @Test
    void borrarUnaCancionQueNoExisteDaNotFound() {
        assertThatThrownBy(() -> service.delete(owner, UUID.randomUUID()))
                .isInstanceOf(SongNotFoundException.class);
    }

    @Test
    void otroUsuarioNoVeNiPuedeTocarMisCanciones() {
        UUID id = UUID.randomUUID();
        service.save(owner, id, amazingGrace);
        UUID intruso = createUser("intruso@example.com");

        assertThat(service.findAll(intruso)).isEmpty();
        assertThatThrownBy(() -> service.findById(intruso, id))
                .isInstanceOf(SongNotFoundException.class);
        assertThatThrownBy(() -> service.save(intruso, id, amazingGrace))
                .isInstanceOf(SongNotFoundException.class);
        assertThatThrownBy(() -> service.delete(intruso, id))
                .isInstanceOf(SongNotFoundException.class);
    }

    private UUID createUser(String email) {
        User user = new User(UUID.randomUUID(), email, "hash", System.currentTimeMillis());
        return users.save(user).getId();
    }
}
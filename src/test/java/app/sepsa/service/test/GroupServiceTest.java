package app.sepsa.service.test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import progettone.listaspesa.dto.GroupDTO;
import progettone.listaspesa.entities.User;
import progettone.listaspesa.service.GroupService;
import progettone.utils.ExternalGroup;

public class GroupServiceTest {

    private static final GroupService service = GroupService.getInstance();

    private User buildAdmin() {
        User admin = new User();
        admin.setId(1L);
        admin.setEmail("admin@example.com");
        admin.setFirstName("Admin");
        admin.setLastName("User");
        return admin;
    }

    @Test
    void testRegisterAndFindById() {
        User admin = buildAdmin();

        // Simuliamo una richiesta esterna (API)
        GroupDTO dto = ExternalGroup.randomGroup();

        GroupDTO created = service.register(dto, admin);

        assertNotNull(created.getId(), "L'ID dovrebbe essere generato dal DB");

        Optional<GroupDTO> foundOpt = service.findById(created.getId());
        assertTrue(foundOpt.isPresent(), "Gruppo non trovato dopo la registrazione");

        GroupDTO found = foundOpt.get();

        assertEquals(dto.getName(), found.getName(), "Il nome del gruppo non corrisponde");
        assertEquals(dto.getDescription(), found.getDescription(), "La descrizione non corrisponde");
    }

    @Test
    void testUpdate() {
        User admin = buildAdmin();
        GroupDTO dto = ExternalGroup.randomGroup();

        GroupDTO created = service.register(dto, admin);
        assertNotNull(created.getId());

        // Simuliamo una modifica
        GroupDTO updateDto = new GroupDTO();
        updateDto.setId(created.getId());                  // 🔥 OBBLIGATORIO
        updateDto.setName("Gruppo Aggiornato");
        updateDto.setDescription("Descrizione aggiornata");

        service.update(updateDto, admin);

        Optional<GroupDTO> updatedOpt = service.findById(created.getId());
        assertTrue(updatedOpt.isPresent(), "Dopo l'update il gruppo dovrebbe esistere");

        GroupDTO updated = updatedOpt.get();
        assertEquals("Gruppo Aggiornato", updated.getName(), "Il nome non è stato aggiornato correttamente");
        assertEquals("Descrizione aggiornata", updated.getDescription(),
                "La descrizione non è stata aggiornata correttamente");
    }

    @Test
    void testFindAllAndFindAllActive() {
        User admin = buildAdmin();

        GroupDTO dto1 = ExternalGroup.randomGroup();
        GroupDTO dto2 = ExternalGroup.randomGroup();

        service.register(dto1, admin);
        service.register(dto2, admin);

        List<GroupDTO> all = service.findAll();
        assertTrue(all.size() >= 2, "Dovrebbero esserci almeno 2 gruppi");

        List<GroupDTO> active = service.findAllActive();
        assertTrue(active.size() >= 2, "I gruppi attivi devono essere almeno 2");
    }

    @Test
    void testSoftDelete() {
        User admin = buildAdmin();
        GroupDTO dto = ExternalGroup.randomGroup();

        GroupDTO created = service.register(dto, admin);
        assertNotNull(created.getId());

        boolean deleted = service.delete(created.getId(), admin);
        assertTrue(deleted, "La cancellazione soft dovrebbe restituire true");

        Optional<GroupDTO> foundOpt = service.findById(created.getId());

        // Il gruppo esiste ancora nel DB ma è deleted=true → il DTO non contiene deleted
        assertTrue(foundOpt.isPresent(), "Il gruppo dovrebbe essere ancora presente dopo soft delete");
    }

    @Test
    void testInvalidInputs() {
        User admin = buildAdmin();

        // DTO nullo
        assertThrows(IllegalArgumentException.class, () -> {
            service.register(null, admin);
        });

        // Nome mancante
        GroupDTO dto = new GroupDTO();
        dto.setDescription("Gruppo senza nome");
        assertThrows(IllegalArgumentException.class, () -> {
            service.register(dto, admin);
        });
    }
}

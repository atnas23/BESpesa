package app.sepsa.service.test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import progettone.listaspesa.dto.UserDTO;
import progettone.listaspesa.entities.User;
import progettone.listaspesa.service.UserService;
import progettone.utils.ExternalUser;

public class UserServiceTest {

	private static final UserService service = UserService.getInstance();

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

		// simuliamo richiesta esterna (es. da API)
		UserDTO dto = ExternalUser.randomUser();

		User created = service.register(dto, admin);

		assertNotNull(created.getId(), "L'ID dovrebbe essere generato dal DB");

		UserDTO found = service.findById(created.getId());
		assertNotNull(found, "Utente non trovato dopo la registrazione");
		assertEquals(dto.getEmail(), found.getEmail());
		assertEquals(dto.getFullName(), found.getFullName());
	}

	@Test
	void testUpdate() {
		User admin = buildAdmin();
		UserDTO dto = ExternalUser.randomUser();

		User created = service.register(dto, admin);

		// Simuliamo una modifica che arriva da un'altra richiesta esterna
		UserDTO updatedRequest = new UserDTO();
//		updatedRequest.setId(created.getId());		
		updatedRequest.setEmail(created.getEmail());
		updatedRequest.setPassword(created.getPassword());
		updatedRequest.setFirstName("Aggiornato");
		updatedRequest.setLastName(dto.getLastName());
		updatedRequest.setDateOfBirth(dto.getDateOfBirth());
		updatedRequest.setDeleted(false);

		service.update(updatedRequest, admin);

		UserDTO updated = service.findById(created.getId());
		assertEquals("Aggiornato", updated.getFirstName(), "Il nome non è stato aggiornato correttamente");
	}

	@Test
	void testFindAllAndFindAllActive() {
		User admin = buildAdmin();

		UserDTO dto1 = ExternalUser.randomUser();
		UserDTO dto2 = ExternalUser.randomUser();

		service.register(dto1, admin);
		service.register(dto2, admin);

		List<UserDTO> all = service.findAll();
		assertTrue(all.size() >= 2, "Dovrebbero esserci almeno 2 utenti");

		List<UserDTO> active = service.findAllActive();
		assertTrue(active.stream().allMatch(u -> !u.isDeleted()),
				"Tutti gli utenti attivi dovrebbero avere deleted=false");
	}

	@Test
	void testSoftDelete() {
		User admin = buildAdmin();
		UserDTO dto = ExternalUser.randomUser();

		User created = service.register(dto, admin);
		assertNotNull(created.getId());

		boolean deleted = service.delete(created.getId(), admin);
		assertTrue(deleted, "La cancellazione soft dovrebbe restituire true");

		UserDTO found = service.findById(created.getId());
		assertNotNull(found, "L'utente dovrebbe esistere ma con deleted=true");
		assertTrue(found.isDeleted(), "L'utente non risulta marcato come cancellato");
	}

	@Test
	void testInvalidInputs() {
		User admin = buildAdmin();

		// DTO nullo
		assertThrows(IllegalArgumentException.class, () -> {
			service.register(null, admin);
		});

		// Email mancante
		UserDTO dto = new UserDTO();
		dto.setFirstName("Test");
		assertThrows(IllegalArgumentException.class, () -> {
			service.register(dto, admin);
		});
	}
}

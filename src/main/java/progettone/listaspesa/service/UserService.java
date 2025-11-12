package progettone.listaspesa.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import progettone.listaspesa.dto.UserDTO;
import progettone.listaspesa.entities.User;
import progettone.listaspesa.repository.UserRepository;

public class UserService extends BaseService {

	private static final UserService INSTANCE = new UserService();
	private static final Logger logger = LogManager.getLogger(UserService.class);
	private final UserRepository userRepo = UserRepository.getInstance();

	private UserService() {
	}

	public static UserService getInstance() {
		return INSTANCE;
	}

	public User register(UserDTO dto, User currentUser) {

		if (dto == null) {
			logger.error("Tentativo di registrazione con DTO nullo");
			throw new IllegalArgumentException("UserDTO nullo");
		}

		if (dto.getEmail() == null || dto.getEmail().isBlank()) {
			logger.warn("Tentativo di registrazione con email non valida: {}", dto.getEmail());
			throw new IllegalArgumentException("Email utente non valida");
		}

		User entity = new User();
		try {
			reflectionMapper(entity, dto);
		} catch (Exception e) {
			logger.info("Errore nel mapping DTO → Entity per utente {}", dto.getEmail(), e.getMessage());
			throw new RuntimeException("Errore interno nel mapping utente", e);
		}

		// valorizza i metadati tecnici
		entity.setCreatedAt(LocalDateTime.now());
		entity.setCreatedBy(currentUser != null ? currentUser.getId() : 0L);

		try {
			userRepo.save(entity);
			logger.info("Utente registrato con successo: {}", entity.getEmail());
		} catch (Exception e) {
			logger.error("Errore nel salvataggio utente {}", entity.getEmail(), e.getMessage());
			throw new RuntimeException("Errore durante il salvataggio dell’utente", e);
		}

		return entity;

	}

	public UserDTO findById(Long id) {
		if (id == null || id <= 0) {
			throw new IllegalArgumentException("ID non valido");
		}

		Optional<User> opt = userRepo.findById(id);
		if (opt.isEmpty()) {
			logger.warn("Nessun utente trovato con ID {}", id);
			return null;
		}

		UserDTO dto = new UserDTO();
		reflectionMapper(dto, opt.get());
		return dto;
	}

	public List<UserDTO> findAll() {
		List<User> users = userRepo.findAll();
		List<UserDTO> result = new ArrayList<>();
		for (User u : users) {
			UserDTO dto = new UserDTO();
			reflectionMapper(dto, u);
			result.add(dto);
		}
		return result;
	}

	public List<UserDTO> findAllActive() {
		List<User> users = userRepo.findAllActive();
		List<UserDTO> result = new ArrayList<>();
		for (User u : users) {
			if (!u.isDeleted()) {
				UserDTO dto = new UserDTO();
				reflectionMapper(dto, u);
				result.add(dto);
			}
		}
		return result;
	}

	public User update(UserDTO dto, User currentUser) {
		if (dto == null) {
			throw new IllegalArgumentException("UserDTO nullo");
		}

		Optional<User> existingOpt = userRepo.findByEmail(dto.getEmail());
		if (existingOpt.isEmpty()) {
			logger.warn("Utente con Email {} non trovato per update", dto.getEmail());
			throw new IllegalArgumentException("Utente non trovato");
		}

		User existing = existingOpt.get();
		reflectionMapper(existing, dto);

		existing.setModifiedAt(LocalDateTime.now());
		existing.setModifiedBy(currentUser != null ? currentUser.getId() : 0L);

		try {
			userRepo.update(existing);
			logger.info("Utente aggiornato: {}", existing.getEmail());
		} catch (Exception e) {
			logger.error("Errore aggiornando utente {}", existing.getEmail(), e.getMessage());
			throw new RuntimeException("Errore aggiornando utente", e);
		}

		return existing;
	}

	public boolean delete(Long id, User currentUser) {
		if (id == null || id <= 0) {
			throw new IllegalArgumentException("ID non valido");
		}

		Optional<User> existingOpt = userRepo.findById(id);
		if (existingOpt.isEmpty()) {
			logger.warn("Tentativo di cancellare utente non esistente ID={}", id);
			return false;
		}

		User existing = existingOpt.get();
		existing.setModifiedAt(LocalDateTime.now());
		existing.setModifiedBy(currentUser != null ? currentUser.getId() : 0L);

		try {
			userRepo.delete(existing);
			logger.info("Utente ID={} soft deleted", id);
			return true;
		} catch (Exception e) {
			logger.error("Errore nella soft delete dell’utente ID={}", id, e.getMessage());
			throw new RuntimeException("Errore nella cancellazione", e);
		}
	}

}

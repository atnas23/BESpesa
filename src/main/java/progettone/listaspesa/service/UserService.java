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

public class UserService {

	private static final UserService INSTANCE = new UserService();
	private static final Logger logger = LogManager.getLogger(UserService.class);
	private final UserRepository userRepo = UserRepository.getInstance();

	private UserService() {
	}

	public static UserService getInstance() {
		return INSTANCE;
	}


	public UserDTO register(UserDTO dto, User currentUser) {
		if (dto == null) {
			logger.error("Tentativo di registrazione con DTO nullo");
			throw new IllegalArgumentException("UserDTO nullo");
		}
		
		if (currentUser == null) {
			logger.error("Tentativo di registrazione user con currentUser nullo");
			throw new IllegalArgumentException("currentUser nullo");
		}
		
		if (dto.getEmail() == null || dto.getEmail().isBlank()) {
			logger.warn("Tentativo di registrazione con email non valida: {}", dto.getEmail());
			throw new IllegalArgumentException("Email utente non valida");
		}

		User entity = new User();
		entity.setEmail(dto.getEmail());
		entity.setPassword(dto.getPassword());
		entity.setFirstName(dto.getFirstName());
		entity.setLastName(dto.getLastName());
		entity.setDateOfBirth(dto.getDateOfBirth());
		entity.setCreatedAt(LocalDateTime.now());
		entity.setCreatedBy(currentUser != null ? currentUser.getId() : 0L);
		entity.setDeleted(false);

		try {
			userRepo.save(entity);
			logger.info("Utente registrato con successo: {}", entity.getEmail());
		} catch (Exception e) {
			logger.error("Errore nel salvataggio utente {}", entity.getEmail(), e);
			throw new RuntimeException("Errore durante il salvataggio dell’utente", e);
		}
		dto.setId(entity.getId());
		
		return userToDTO(entity);
	}


	public Optional<UserDTO> findById(Long id) {
		if (id == null || id <= 0) {
			throw new IllegalArgumentException("ID non valido");
		}

		Optional<User> opt = userRepo.findById(id);
		if (opt.isEmpty()) {
			logger.warn("Nessun utente trovato con ID {}", id);
			return Optional.empty();
		}

		return opt.map(this::userToDTO);
	}


	public List<UserDTO> findAll() {
		List<User> users = userRepo.findAll();
		List<UserDTO> result = new ArrayList<>();
		for (User u : users) {
			result.add(userToDTO(u));
		}
		return result;
	}

	public List<UserDTO> findAllActive() {
		List<User> users = userRepo.findAllActive();
		List<UserDTO> result = new ArrayList<>();
		for (User u : users) {
			if (!u.isDeleted()) {
				result.add(userToDTO(u));
			}
		}
		return result;
	}


	public User update(UserDTO dto, User currentUser) {
		if (dto == null) {
			throw new IllegalArgumentException("UserDTO nullo");
		}
		
		if (currentUser == null) {
			logger.error("Tentativo di aggiornamento user con currentUser nullo");
			throw new IllegalArgumentException("currentUser nullo");
		}

		Optional<User> existingOpt = userRepo.findById(dto.getId());
		if (existingOpt.isEmpty()) {
			logger.warn("Utente con id {} non trovato per update", dto.getId());
			throw new IllegalArgumentException("Utente non trovato");
		}

		User existing = existingOpt.get();
		if (existing.isDeleted()) {
			throw new IllegalStateException("Impossibile aggiornare un user eliminato");
		}
		existing.setPassword(dto.getPassword());
		existing.setEmail(dto.getEmail());
		existing.setFirstName(dto.getFirstName());
		existing.setLastName(dto.getLastName());
		existing.setDateOfBirth(dto.getDateOfBirth());
		existing.setModifiedAt(LocalDateTime.now());
		existing.setModifiedBy(currentUser != null ? currentUser.getId() : 0L);
		existing.setDeleted(dto.isDeleted());

		try {
			userRepo.update(existing);
			logger.info("Utente aggiornato: {}", existing.getEmail());
		} catch (Exception e) {
			logger.error("Errore aggiornando utente {}", existing.getEmail(), e);
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
			logger.error("Errore nella soft delete dell’utente ID={}", id, e);
			throw new RuntimeException("Errore nella cancellazione", e);
		}
	}

	/*
	 * ================================================================
	 * METODI PRIVATI DI MAPPING
	 * ================================================================
	 */

	private UserDTO userToDTO(User user) {
		UserDTO dto = new UserDTO();
		dto.setId(user.getId());
		dto.setEmail(user.getEmail());
		dto.setPassword(user.getPassword());
		dto.setFirstName(user.getFirstName());
		dto.setLastName(user.getLastName());
		dto.setDateOfBirth(user.getDateOfBirth());
		dto.setDeleted(user.isDeleted());
		return dto;
	}
}

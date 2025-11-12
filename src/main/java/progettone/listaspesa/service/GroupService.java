package progettone.listaspesa.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import progettone.listaspesa.dto.GroupDTO;
import progettone.listaspesa.entities.Group;
import progettone.listaspesa.entities.User;
import progettone.listaspesa.repository.GroupRepository;

public class GroupService extends BaseService {

	private static final GroupService INSTANCE = new GroupService();
	private static final Logger logger = LogManager.getLogger(GroupService.class);
	private final GroupRepository groupRepo = GroupRepository.getInstance();

	private GroupService() {
	}

	public static GroupService getInstance() {
		return INSTANCE;
	}

	/**
	 * Registra un nuovo gruppo
	 */
	public Group register(GroupDTO dto, User currentUser) {

		if (dto == null) {
			logger.error("Tentativo di registrazione gruppo con DTO nullo");
			throw new IllegalArgumentException("GroupDTO nullo");
		}

		if (dto.getName() == null || dto.getName().isBlank()) {
			logger.warn("Tentativo di registrazione con nome gruppo non valido: {}", dto.getName());
			throw new IllegalArgumentException("Nome gruppo non valido");
		}

		Group entity = new Group();
		try {
			reflectionMapper(entity, dto);
		} catch (Exception e) {
			logger.error("Errore nel mapping DTO → Entity per gruppo {}", dto.getName(), e);
			throw new RuntimeException("Errore interno nel mapping gruppo", e);
		}

		entity.setCreatedAt(LocalDateTime.now());
		entity.setCreatedBy(currentUser != null ? currentUser.getId() : 0L);

		try {
			groupRepo.save(entity);
			logger.info("Gruppo registrato con successo: {}", entity.getName());
		} catch (Exception e) {
			logger.error("Errore nel salvataggio del gruppo {}", entity.getName(), e);
			throw new RuntimeException("Errore durante il salvataggio del gruppo", e);
		}

		return entity;
	}

	/**
	 * Trova un gruppo per ID
	 */
	public GroupDTO findById(Long id) {
		if (id == null || id <= 0) {
			throw new IllegalArgumentException("ID non valido");
		}

		Optional<Group> opt = groupRepo.findById(id);
		if (opt.isEmpty()) {
			logger.warn("Nessun gruppo trovato con ID {}", id);
			return null;
		}

		GroupDTO dto = new GroupDTO();
		reflectionMapper(dto, opt.get());
		return dto;
	}

	/**
	 * Restituisce tutti i gruppi
	 */
	public List<GroupDTO> findAll() {
		List<Group> groups = groupRepo.findAll();
		List<GroupDTO> result = new ArrayList<>();
		for (Group g : groups) {
			GroupDTO dto = new GroupDTO();
			reflectionMapper(dto, g);
			result.add(dto);
		}
		return result;
	}

	/**
	 * Restituisce tutti i gruppi attivi (non cancellati)
	 */
	public List<GroupDTO> findAllActive() {
		List<Group> groups = groupRepo.findAllActive();
		List<GroupDTO> result = new ArrayList<>();
		for (Group g : groups) {
			if (!g.isDeleted()) {
				GroupDTO dto = new GroupDTO();
				reflectionMapper(dto, g);
				result.add(dto);
			}
		}
		return result;
	}

	/**
	 * Aggiorna i dati di un gruppo esistente
	 */
	public Group update(GroupDTO dto, User currentUser) {
		if (dto == null) {
			throw new IllegalArgumentException("GroupDTO nullo");
		}

		if (dto.getId() == null) {
			logger.warn("Tentativo di aggiornamento gruppo senza ID valido");
			throw new IllegalArgumentException("ID gruppo non valido");
		}

		Optional<Group> existingOpt = groupRepo.findById(dto.getId());
		if (existingOpt.isEmpty()) {
			logger.warn("Gruppo con ID {} non trovato per update", dto.getId());
			throw new IllegalArgumentException("Gruppo non trovato");
		}

		Group existing = existingOpt.get();
		reflectionMapper(existing, dto);

		existing.setModifiedAt(LocalDateTime.now());
		existing.setModifiedBy(currentUser != null ? currentUser.getId() : 0L);

		try {
			groupRepo.update(existing);
			logger.info("Gruppo aggiornato: {}", existing.getName());
		} catch (Exception e) {
			logger.error("Errore aggiornando gruppo {}", existing.getName(), e);
			throw new RuntimeException("Errore aggiornando gruppo", e);
		}

		return existing;
	}

	/**
	 * Esegue una soft delete su un gruppo
	 */
	public boolean delete(Long id, User currentUser) {
		if (id == null || id <= 0) {
			throw new IllegalArgumentException("ID non valido");
		}

		Optional<Group> existingOpt = groupRepo.findById(id);
		if (existingOpt.isEmpty()) {
			logger.warn("Tentativo di cancellare gruppo non esistente ID={}", id);
			return false;
		}

		Group existing = existingOpt.get();
		existing.setModifiedAt(LocalDateTime.now());
		existing.setModifiedBy(currentUser != null ? currentUser.getId() : 0L);

		try {
			groupRepo.delete(existing);
			logger.info("Gruppo ID={} soft deleted", id);
			return true;
		} catch (Exception e) {
			logger.error("Errore nella soft delete del gruppo ID={}", id, e);
			throw new RuntimeException("Errore nella cancellazione del gruppo", e);
		}
	}
}

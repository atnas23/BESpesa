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

public class GroupService {

	private static final GroupService INSTANCE = new GroupService();
	private static final Logger logger = LogManager.getLogger(GroupService.class);
	private final GroupRepository groupRepo = GroupRepository.getInstance();

	private GroupService() {
	}

	public static GroupService getInstance() {
		return INSTANCE;
	}

	public GroupDTO register(GroupDTO dto, User currentUser) {
		if (dto == null) {
			logger.error("Tentativo di registrazione gruppo con DTO nullo");
			throw new IllegalArgumentException("GroupDTO nullo");
		}
		if (currentUser == null) {
			logger.error("Tentativo di registrazione gruppo con currentUser nullo");
			throw new IllegalArgumentException("currentUser nullo");
		}

		if (dto.getName() == null || dto.getName().isBlank()) {
			logger.warn("Tentativo di registrazione con nome gruppo non valido: {}", dto.getName());
			throw new IllegalArgumentException("Nome gruppo non valido");
		}

		Group entity = new Group();
		entity.setName(dto.getName());
		entity.setDescription(dto.getDescription());
		entity.setCreatedAt(LocalDateTime.now());
		entity.setCreatedBy(currentUser != null ? currentUser.getId() : 0L);
		entity.setDeleted(false);

		try {
			groupRepo.save(entity);
			logger.info("Gruppo registrato con successo: {}", entity.getName());
		} catch (Exception e) {
			logger.error("Errore nel salvataggio del gruppo {}", entity.getName(), e);
			throw new RuntimeException("Errore durante il salvataggio del gruppo", e);
		}

		dto.setId(entity.getId());
		
		return groupToDTO(entity);

	}

	public Optional<GroupDTO> findById(Long id) {
		if (id == null || id <= 0) {
			throw new IllegalArgumentException("ID non valido");
		}

		Optional<Group> group = groupRepo.findById(id);
		if (group.isEmpty()) {
			logger.warn("Nessun gruppo trovato con ID {}", id);
			return Optional.empty();
		}

		return groupRepo.findById(id)
                .map(this::groupToDTO);
	}


	public List<GroupDTO> findAll() {
		List<Group> groups = groupRepo.findAll();
		List<GroupDTO> result = new ArrayList<>();
		for (Group g : groups) {
			result.add(groupToDTO(g));
		}
		return result;
	}


	public List<GroupDTO> findAllActive() {
		List<Group> groups = groupRepo.findAllActive();
		List<GroupDTO> result = new ArrayList<>();
		for (Group g : groups) {
			if (!g.isDeleted()) {
				result.add(groupToDTO(g));
			}
		}
		return result;
	}


	public Group update(GroupDTO dto, User currentUser) {
		if (dto == null) {
			throw new IllegalArgumentException("GroupDTO nullo");
		}

		if (currentUser == null) {
			logger.error("Tentativo di aggiornamento gruppo con currentUser nullo");
			throw new IllegalArgumentException("currentUser nullo");
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

		if (existing.isDeleted()) {
			throw new IllegalStateException("Impossibile aggiornare un gruppo eliminato");
		}
		
		existing.setName(dto.getName());
		existing.setDescription(dto.getDescription());
		existing.setModifiedAt(LocalDateTime.now());
		existing.setModifiedBy(currentUser.getId());

		try {
			groupRepo.update(existing);
			logger.info("Gruppo aggiornato: {}", existing.getName());
		} catch (Exception e) {
			logger.error("Errore aggiornando gruppo {}", existing.getName(), e);
			throw new RuntimeException("Errore aggiornando gruppo", e);
		}

		return existing;
	}

	public boolean delete(Long id, User currentUser) {
		if (id == null || id <= 0) {
			throw new IllegalArgumentException("ID non valido");
		}

		if (currentUser == null) {
			logger.error("Tentativo di registrazione gruppo con currentUser nullo");
			throw new IllegalArgumentException("currentUser nullo");
		}
		
		Optional<Group> existingOpt = groupRepo.findById(id);
		if (existingOpt.isEmpty()) {
			logger.warn("Tentativo di cancellare gruppo non esistente ID={}", id);
			return false;
		}

		Group existing = existingOpt.get();
		existing.setModifiedAt(LocalDateTime.now());
		existing.setModifiedBy(currentUser.getId());
		existing.setDeleted(true);

		try {
			groupRepo.delete(existing);
			logger.info("Gruppo ID={} soft deleted", id);
			return true;
		} catch (Exception e) {
			logger.error("Errore nella soft delete del gruppo ID={}", id, e);
			throw new RuntimeException("Errore nella cancellazione del gruppo", e);
		}
	}

	/*
	 * ================================================================
	 * METODI PRIVATI DI MAPPING
	 * ================================================================
	 */

	private GroupDTO groupToDTO(Group g) {
		GroupDTO dto = new GroupDTO();
		dto.setId(g.getId());
		dto.setName(g.getName());
		dto.setDescription(g.getDescription());
		return dto;
	}
	
}

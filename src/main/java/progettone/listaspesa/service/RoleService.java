package progettone.listaspesa.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import progettone.listaspesa.dto.RoleDTO;
import progettone.listaspesa.entities.Role;
import progettone.listaspesa.entities.User;
import progettone.listaspesa.repository.RoleRepository;

public class RoleService {

    private static final RoleService INSTANCE = new RoleService();
    private static final Logger logger = LogManager.getLogger(RoleService.class);

    private final RoleRepository repo = RoleRepository.getInstance();

    private RoleService() {}

    public static RoleService getInstance() {
        return INSTANCE;
    }

    // ============================================================
    // REGISTER
    // ============================================================
    public RoleDTO register(RoleDTO dto, User currentUser) {
        if (dto == null)
            throw new IllegalArgumentException("RoleDTO nullo");

        if (currentUser == null)
            throw new IllegalArgumentException("currentUser nullo");

        if (dto.getName() == null || dto.getName().isBlank()) {
            logger.warn("Tentativo di registrazione ruolo con name invalido");
            throw new IllegalArgumentException("Name non valido");
        }

        Role entity = new Role();
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setCreatedAt(LocalDateTime.now());
        entity.setCreatedBy(currentUser.getId());
        entity.setDeleted(false);

        try {
            repo.save(entity);
            logger.info("Ruolo registrato: {}", entity.getName());
        } catch (Exception e) {
            logger.error("Errore nel salvataggio ruolo {}", entity.getName(), e);
            throw new RuntimeException("Errore salvataggio ruolo", e);
        }

        return roleToDTO(entity);
    }

    // ============================================================
    // FIND BY ID
    // ============================================================
    public Optional<RoleDTO> findById(Long id) {
        if (id == null || id <= 0)
            throw new IllegalArgumentException("ID non valido");

        return repo.findById(id).map(this::roleToDTO);
    }

    // ============================================================
    // FIND ALL
    // ============================================================
    public List<RoleDTO> findAll() {
        List<RoleDTO> list = new ArrayList<>();
        for (Role r : repo.findAll()) {
            list.add(roleToDTO(r));
        }
        return list;
    }

    public List<RoleDTO> findAllActive() {
        List<RoleDTO> list = new ArrayList<>();
        for (Role r : repo.findAllActive()) {
            list.add(roleToDTO(r));
        }
        return list;
    }

    // ============================================================
    // UPDATE
    // ============================================================
    public Role update(RoleDTO dto, User currentUser) {
        if (dto == null)
            throw new IllegalArgumentException("RoleDTO nullo");

        if (currentUser == null)
            throw new IllegalArgumentException("currentUser nullo");

        Optional<Role> opt = repo.findById(dto.getId());
        if (opt.isEmpty()) {
            logger.warn("Role ID {} non trovato per update", dto.getId());
            throw new IllegalArgumentException("Ruolo non trovato");
        }

        Role existing = opt.get();

        if (existing.isDeleted())
            throw new IllegalStateException("Impossibile aggiornare un ruolo eliminato");

        existing.setName(dto.getName());
        existing.setDescription(dto.getDescription());
        existing.setModifiedAt(LocalDateTime.now());
        existing.setModifiedBy(currentUser.getId());
        existing.setDeleted(dto.isDeleted());

        try {
            repo.update(existing);
            logger.info("Ruolo aggiornato: {}", existing.getName());
        } catch (Exception e) {
            logger.error("Errore aggiornando ruolo {}", existing.getName(), e);
            throw new RuntimeException("Errore aggiornamento ruolo", e);
        }

        return existing;
    }

    // ============================================================
    // DELETE
    // ============================================================
    public boolean delete(Long id, User currentUser) {
        if (id == null || id <= 0)
            throw new IllegalArgumentException("ID non valido");

        Optional<Role> opt = repo.findById(id);
        if (opt.isEmpty()) {
            logger.warn("Tentativo di cancellare role non esistente ID={}", id);
            return false;
        }

        Role existing = opt.get();
        existing.setModifiedAt(LocalDateTime.now());
        existing.setModifiedBy(currentUser.getId());

        try {
            repo.delete(existing);
            logger.info("Ruolo ID={} soft deleted", id);
            return true;
        } catch (Exception e) {
            logger.error("Errore soft delete role {}", id, e);
            throw new RuntimeException("Errore cancellazione", e);
        }
    }

    // ============================================================
    // MAPPING
    // ============================================================
    private RoleDTO roleToDTO(Role role) {
        RoleDTO dto = new RoleDTO();
        dto.setId(role.getId());
        dto.setName(role.getName());
        dto.setDescription(role.getDescription());
        dto.setDeleted(role.isDeleted());
        return dto;
    }
}

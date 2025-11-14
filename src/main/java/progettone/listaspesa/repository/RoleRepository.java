package progettone.listaspesa.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import progettone.listaspesa.entities.Role;
import progettone.listaspesa.exception.DatabaseException;

public class RoleRepository extends BaseRepository implements IRepo<Role> {

    private static final RoleRepository INSTANCE = new RoleRepository();

    private RoleRepository() {}

    public static RoleRepository getInstance() {
        return INSTANCE;
    }

    private Role mapRow(ResultSet rs) throws SQLException {
        Role role = new Role();
        role.setId(rs.getLong("id"));
        role.setName(rs.getString("name"));
        role.setDescription(rs.getString("description"));
        role.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        role.setCreatedBy(rs.getLong("created_by"));
        role.setModifiedAt(rs.getTimestamp("modified_at") != null 
                ? rs.getTimestamp("modified_at").toLocalDateTime() 
                : null);
        role.setModifiedBy(rs.getLong("modified_by"));
        role.setDeleted(rs.getBoolean("deleted"));
        return role;
    }

    @Override
    public List<Role> findAll() {
        List<Role> roles = new ArrayList<>();
        String sql = """
            SELECT id, name, description, created_at, created_by,
                   modified_at, modified_by, deleted
            FROM roles
        """;

        try (Connection conn = openConnection()) {
            PreparedStatement pstm = conn.prepareStatement(sql);
            ResultSet rs = pstm.executeQuery();

            while (rs.next()) {
                roles.add(mapRow(rs));
            }

        } catch (SQLException e) {
            throw new DatabaseException("Errore nel caricamento di tutti i ruoli", e);
        }

        return roles;
    }

    public List<Role> findAllActive() {
        List<Role> roles = new ArrayList<>();
        String sql = """
            SELECT id, name, description, created_at, created_by,
                   modified_at, modified_by, deleted
            FROM roles
            WHERE deleted = false
        """;

        try (Connection conn = openConnection()) {
            PreparedStatement pstm = conn.prepareStatement(sql);
            ResultSet rs = pstm.executeQuery();

            while (rs.next()) {
                roles.add(mapRow(rs));
            }

        } catch (SQLException e) {
            throw new DatabaseException("Errore nel caricamento dei ruoli attivi", e);
        }

        return roles;
    }

    @Override
    public Optional<Role> findById(Long id) {
        String sql = """
            SELECT id, name, description, created_at, created_by,
                   modified_at, modified_by, deleted
            FROM roles
            WHERE id = ?
        """;

        try (Connection conn = openConnection()) {
            PreparedStatement pstm = conn.prepareStatement(sql);
            pstm.setLong(1, id);
            ResultSet rs = pstm.executeQuery();

            if (rs.next()) {
                return Optional.of(mapRow(rs));
            }

        } catch (SQLException e) {
            throw new DatabaseException("Errore nel caricamento del ruolo ID: " + id, e);
        }

        return Optional.empty();
    }

    @Override
    public void save(Role role) {
        String sql = """
            INSERT INTO roles (name, description, created_at, created_by, deleted)
            VALUES (?, ?, ?, ?, ?)
        """;

        try (Connection conn = openConnection()) {
            conn.setAutoCommit(false);

            PreparedStatement pstm = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            pstm.setString(1, role.getName());
            pstm.setString(2, role.getDescription());
            pstm.setTimestamp(3, java.sql.Timestamp.valueOf(role.getCreatedAt()));
            pstm.setLong(4, role.getCreatedBy());
            pstm.setBoolean(5, false);

            int rows = pstm.executeUpdate();
            if (rows == 0)
                throw new DatabaseException("Inserimento fallito: nessuna riga inserita.");

            ResultSet keys = pstm.getGeneratedKeys();
            if (keys.next()) {
                role.setId(keys.getLong(1));
            }

            conn.commit();

        } catch (SQLException e) {
            throw new DatabaseException("Errore nella creazione del ruolo", e);
        }
    }

    @Override
    public void update(Role role) {
        String sql = """
            UPDATE roles SET
                name = ?, 
                description = ?, 
                modified_at = ?, 
                modified_by = ?,
                deleted = ?
            WHERE id = ? AND deleted = false
        """;

        try (Connection conn = openConnection()) {
            conn.setAutoCommit(false);

            PreparedStatement pstm = conn.prepareStatement(sql);
            pstm.setString(1, role.getName());
            pstm.setString(2, role.getDescription());
            pstm.setTimestamp(3, java.sql.Timestamp.valueOf(role.getModifiedAt()));
            pstm.setLong(4, role.getModifiedBy());
            pstm.setBoolean(5, role.isDeleted());
            pstm.setLong(6, role.getId());

            pstm.executeUpdate();
            conn.commit();

        } catch (SQLException e) {
            throw new DatabaseException("Errore nell'aggiornamento del ruolo ID: " + role.getId(), e);
        }
    }

    @Override
    public void delete(Role role) {
        String sql = "UPDATE roles SET deleted = true, modified_at = ?, modified_by = ? WHERE id = ?";

        try (Connection conn = openConnection()) {
            conn.setAutoCommit(false);

            PreparedStatement pstm = conn.prepareStatement(sql);
            pstm.setTimestamp(1, java.sql.Timestamp.valueOf(LocalDateTime.now()));
            pstm.setLong(2, role.getModifiedBy());
            pstm.setLong(3, role.getId());

            pstm.executeUpdate();
            conn.commit();

        } catch (SQLException e) {
            throw new DatabaseException("Errore nella soft delete del ruolo ID: " + role.getId(), e);
        }
    }
}

package progettone.listaspesa.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import progettone.listaspesa.entities.Group;
import progettone.listaspesa.exception.DatabaseException;

public class GroupRepository extends BaseRepository implements IRepo<Group> {

    private static final GroupRepository INSTANCE = new GroupRepository();

    private GroupRepository() {
    }

    public static GroupRepository getInstance() {
        return INSTANCE;
    }

    private Group mapRowToGroup(ResultSet rs) throws SQLException {
        Group group = new Group();
        group.setId(rs.getLong("id"));
        group.setName(rs.getString("name"));
        group.setDescription(rs.getString("description"));
        group.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        group.setCreatedBy(rs.getLong("created_by"));
        group.setModifiedAt(
                rs.getTimestamp("modified_at") != null ? rs.getTimestamp("modified_at").toLocalDateTime() : null);
        group.setModifiedBy(rs.getLong("modified_by"));
        group.setDeleted(rs.getBoolean("deleted"));
        return group;
    }

    @Override
    public List<Group> findAll() {
        List<Group> groups = new ArrayList<>();

        String sql = """
                SELECT id, name, description, created_at, created_by,
                       modified_at, modified_by, deleted
                FROM group_tab
                """;

        try (Connection conn = openConnection()) {
            PreparedStatement pstm = conn.prepareStatement(sql);
            ResultSet rs = pstm.executeQuery();

            while (rs.next()) {
                groups.add(mapRowToGroup(rs));
            }

        } catch (SQLException e) {
            throw new DatabaseException("Errore nella ricerca di tutti i gruppi", e);
        }

        return groups;
    }

    public List<Group> findAllActive() {
        List<Group> groups = new ArrayList<>();

        String sql = """
                SELECT id, name, description, created_at, created_by,
                       modified_at, modified_by, deleted
                FROM group_tab
                WHERE deleted = false
                """;

        try (Connection conn = openConnection()) {
            PreparedStatement pstm = conn.prepareStatement(sql);
            ResultSet rs = pstm.executeQuery();

            while (rs.next()) {
                groups.add(mapRowToGroup(rs));
            }

        } catch (SQLException e) {
            throw new DatabaseException("Errore nella ricerca dei gruppi attivi", e);
        }

        return groups;
    }

    @Override
    public Optional<Group> findById(Long id) {
        String sql = """
                SELECT id, name, description, created_at, created_by,
                       modified_at, modified_by, deleted
                FROM group_tab
                WHERE id = ?
                """;

        try (Connection conn = openConnection()) {
            PreparedStatement pstm = conn.prepareStatement(sql);
            pstm.setLong(1, id);
            ResultSet rs = pstm.executeQuery();

            if (rs.next()) {
                return Optional.of(mapRowToGroup(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Errore nella ricerca del gruppo con ID: " + id, e);
        }

        return Optional.empty();
    }

    @Override
    public void save(Group group) {
        String sql = """
                INSERT INTO group_tab
                (name, description, created_at, created_by, deleted)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection conn = openConnection()) {
            conn.setAutoCommit(false);

            PreparedStatement pstm = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            int i = 1;
            pstm.setString(i++, group.getName());
            pstm.setString(i++, group.getDescription());
            pstm.setTimestamp(i++, java.sql.Timestamp.valueOf(group.getCreatedAt()));
            pstm.setLong(i++, group.getCreatedBy());
            pstm.setBoolean(i++, false);

            int rows = pstm.executeUpdate();
            if (rows == 0) {
                throw new DatabaseException("Creazione gruppo fallita, nessuna riga inserita.");
            }

            ResultSet generatedKeys = pstm.getGeneratedKeys();
            if (generatedKeys.next()) {
                group.setId(generatedKeys.getLong(1));
            }

            conn.commit();
        } catch (SQLException e) {
            throw new DatabaseException("Errore durante il salvataggio del gruppo", e);
        }
    }

    @Override
    public void update(Group group) {
        String sql = """
                UPDATE group_tab SET
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
            int i = 1;
            pstm.setString(i++, group.getName());
            pstm.setString(i++, group.getDescription());
            pstm.setTimestamp(i++, java.sql.Timestamp.valueOf(LocalDateTime.now()));
            pstm.setLong(i++, group.getModifiedBy());
            pstm.setBoolean(i++, group.isDeleted());
            pstm.setLong(i++, group.getId());

            pstm.executeUpdate();

            conn.commit();
        } catch (SQLException e) {
            throw new DatabaseException("Errore durante l'aggiornamento del gruppo con ID: " + group.getId(), e);
        }
    }

    @Override
    public void delete(Group group) {
        String sql = "UPDATE group_tab SET deleted = true, modified_at = ?, modified_by = ? WHERE id = ?";

        try (Connection conn = openConnection()) {
            conn.setAutoCommit(false);

            PreparedStatement pstm = conn.prepareStatement(sql);
            pstm.setTimestamp(1, java.sql.Timestamp.valueOf(LocalDateTime.now()));
            pstm.setLong(2, group.getModifiedBy());
            pstm.setLong(3, group.getId());

            pstm.executeUpdate();

            conn.commit();
        } catch (SQLException e) {
            throw new DatabaseException("Errore durante la soft delete del gruppo con ID: " + group.getId(), e);
        }
    }
}

package progettone.listaspesa.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import progettone.listaspesa.entities.User;
import progettone.listaspesa.exception.DatabaseException;

public class UserRepository extends BaseRepository implements IRepo<User> {

	private static final UserRepository INSTANCE = new UserRepository();

	private UserRepository() {
	}

	public static UserRepository getInstance() {
		return INSTANCE;
	}

	private User mapRowToUser(ResultSet rs) throws SQLException {
		User user = new User();
		user.setId(rs.getLong("id"));
		user.setPassword(rs.getString("password"));
		user.setEmail(rs.getString("email"));
		user.setFirstName(rs.getString("first_name"));
		user.setLastName(rs.getString("last_name"));
		user.setDateOfBirth(rs.getDate("date_of_birth") != null ? rs.getDate("date_of_birth").toLocalDate() : null);
		user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
		user.setCreatedBy(rs.getLong("created_by"));
		user.setModifiedAt(
				rs.getTimestamp("modified_at") != null ? rs.getTimestamp("modified_at").toLocalDateTime() : null);
		user.setModifiedBy(rs.getLong("modified_by"));
		user.setDeleted(rs.getBoolean("deleted"));
		return user;
	}

	@Override
	public List<User> findAll() {
		List<User> users = new ArrayList<>();

		String sql = """
				SELECT id, password, email, first_name, last_name, date_of_birth,
				       created_at, created_by, modified_at, modified_by, deleted
				FROM users
				""";

		try (Connection conn = openConnection()) {
			PreparedStatement pstm = conn.prepareStatement(sql);
			ResultSet rs = pstm.executeQuery();

			while (rs.next()) {
				users.add(mapRowToUser(rs));
			}

		} catch (SQLException e) {
			throw new DatabaseException("Errore nella ricerca di tutti gli utenti", e);
		}

		return users;
	}

	public List<User> findAllActive() {
		List<User> users = new ArrayList<>();

		String sql = """
				SELECT id, password, email, first_name, last_name, date_of_birth,
				       created_at, created_by, modified_at, modified_by, deleted
				FROM users
				WHERE deleted = false
				""";

		try (Connection conn = openConnection()) {
			PreparedStatement pstm = conn.prepareStatement(sql);
			ResultSet rs = pstm.executeQuery();

			while (rs.next()) {
				users.add(mapRowToUser(rs));
			}

		} catch (SQLException e) {
			throw new DatabaseException("Errore nella ricerca degli utenti attivi", e);
		}

		return users;
	}

	@Override
	public Optional<User> findById(Long id) {
		String sql = """
				SELECT id, password, email, first_name, last_name, date_of_birth,
				       created_at, created_by, modified_at, modified_by, deleted
				FROM users
				WHERE id = ?
				""";

		try (Connection conn = openConnection()) {
			PreparedStatement pstm = conn.prepareStatement(sql);
			pstm.setLong(1, id);
			ResultSet rs = pstm.executeQuery();

			if (rs.next()) {
				return Optional.of(mapRowToUser(rs));
			}

		} catch (SQLException e) {
			throw new DatabaseException("Errore nella ricerca dello user con ID: " + id, e);
		}
		return Optional.empty();
	}

	public Optional<User> findByEmail(String email) {
		String sql = """
				SELECT id, password, email, first_name, last_name, date_of_birth,
				       created_at, created_by, modified_at, modified_by, deleted
				FROM users
				WHERE email = ?
				""";

		try (Connection conn = openConnection()) {
			PreparedStatement pstm = conn.prepareStatement(sql);
			pstm.setString(1, email);
			ResultSet rs = pstm.executeQuery();

			if (rs.next()) {
				return Optional.of(mapRowToUser(rs));
			}

		} catch (SQLException e) {
			throw new DatabaseException("Errore nella ricerca dello user per email", e);
		}
		return Optional.empty();
	}

	@Override
	public void save(User user) {
		String sql = """
				INSERT INTO users
				(password, email, first_name, last_name, date_of_birth, created_at, created_by, deleted)
				VALUES (?, ?, ?, ?, ?, ?, ?, ?)
				""";

		try (Connection conn = openConnection()) {
			conn.setAutoCommit(false);

			PreparedStatement pstm = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
			int i = 1;
			pstm.setString(i++, user.getPassword());
			pstm.setString(i++, user.getEmail());
			pstm.setString(i++, user.getFirstName());
			pstm.setString(i++, user.getLastName());
			pstm.setDate(i++, user.getDateOfBirth() != null ? java.sql.Date.valueOf(user.getDateOfBirth()) : null);
			pstm.setTimestamp(i++, java.sql.Timestamp.valueOf(user.getCreatedAt()));
			pstm.setLong(i++, user.getCreatedBy());
			pstm.setBoolean(i++, false);

			int rows = pstm.executeUpdate();
			if (rows == 0) {
				throw new DatabaseException("Creazione utente fallita, nessuna riga inserita.");
			}

			ResultSet generatedKeys = pstm.getGeneratedKeys();
			if (generatedKeys.next()) {
				user.setId(generatedKeys.getInt(1));
			}

			conn.commit();

		} catch (SQLException e) {
			throw new DatabaseException("Errore durante il salvataggio dell'utente", e);
		}
	}

	@Override
	public void update(User user) {
		String sql = """
				UPDATE users SET
				    password = ?,
				    email = ?,
				    first_name = ?,
				    last_name = ?,
				    date_of_birth = ?,
				    modified_at = ?,
				    modified_by = ?,
				    deleted = ?
				WHERE id = ? AND deleted = false
				""";

		try (Connection conn = openConnection()) {
			conn.setAutoCommit(false);

			PreparedStatement pstm = conn.prepareStatement(sql);
			int i = 1;
			pstm.setString(i++, user.getPassword());
			pstm.setString(i++, user.getEmail());
			pstm.setString(i++, user.getFirstName());
			pstm.setString(i++, user.getLastName());
			pstm.setDate(i++, user.getDateOfBirth() != null ? java.sql.Date.valueOf(user.getDateOfBirth()) : null);
			pstm.setTimestamp(i++, java.sql.Timestamp.valueOf(LocalDateTime.now()));
			pstm.setLong(i++, user.getModifiedBy());
			pstm.setBoolean(i++, user.isDeleted());
			pstm.setLong(i++, user.getId());

			pstm.executeUpdate();

			conn.commit();

		} catch (SQLException e) {
			throw new DatabaseException("Errore durante l'aggiornamento dell'utente con ID: " + user.getId(), e);
		}
	}

	@Override
	public void delete(User user) {
		String sql = "UPDATE users SET deleted = true, modified_at = ?, modified_by = ? WHERE id = ?";

		try (Connection conn = openConnection()) {
			conn.setAutoCommit(false);

			PreparedStatement pstm = conn.prepareStatement(sql);
			pstm.setTimestamp(1, java.sql.Timestamp.valueOf(LocalDateTime.now()));
			pstm.setLong(2, user.getModifiedBy());
			pstm.setLong(3, user.getId());

			pstm.executeUpdate();

			conn.commit();

		} catch (SQLException e) {
			throw new DatabaseException("Errore durante la soft delete dell'utente con ID: " + user.getId(), e);
		}
	}
}

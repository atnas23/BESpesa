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
import progettone.listaspesa.interfaces.IRepo;

public class UserDBRepository extends BaseRepository implements IRepo<User> {

	private static final UserDBRepository INSTANCE = new UserDBRepository();

	private UserDBRepository() {}

	public static UserDBRepository getInstance() {
		return INSTANCE;
	}

	private User mapRowToUser(ResultSet rs) throws SQLException {
		User user = new User();
		user.setId(rs.getInt("id"));
		user.setPassword(rs.getString("password"));
		user.setEmail(rs.getString("email"));
		user.setFirstName(rs.getString("first_name"));
		user.setLastName(rs.getString("last_name"));
		user.setDateOfBirth(rs.getDate("date_of_birth") != null ? rs.getDate("date_of_birth").toLocalDate() : null);
		user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
		user.setCreatedBy(rs.getString("created_by"));
		user.setModifiedAt(
				rs.getTimestamp("modified_at") != null ? rs.getTimestamp("modified_at").toLocalDateTime() : null);
		user.setModifiedBy(rs.getString("modified_by"));
		user.setDeleted(rs.getBoolean("deleted"));
		return user;
	}

	// restituisce tutti gli utenti nel db
	@Override
	public List<User> findAll() {
		List<User> users = new ArrayList<>();

		String sql = "SELECT id, password, email, first_name, last_name, date_of_birth, "
				+ "created_at, created_by, modified_at, modified_by FROM user";

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

	// restituisce tutti gli utenti non cancellati nel db
	public List<User> findAllActive() {
		List<User> users = new ArrayList<>();

		String sql = "SELECT id, password, email, first_name, last_name, date_of_birth, "
				+ "created_at, created_by, modified_at, modified_by FROM user WHERE deleted = false";

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

	@Override
	public Optional<User> findById(int id) {

		String sql = "SELECT id, password, email, first_name, last_name, date_of_birth, created_at, "
				+ "created_by, modified_at, modified_by FROM user WHERE id = ?";

		try (Connection conn = openConnection()) {

			PreparedStatement pstm = conn.prepareStatement(sql);

			pstm.setInt(1, id);

			ResultSet rs = pstm.executeQuery();

			if (rs.next()) {
				return Optional.of(mapRowToUser(rs));
			}

		} catch (SQLException e) {
			throw new DatabaseException("Errore nella ricerca dello user", e);
		}
		return Optional.empty();
	}

// 	TODO: da spostare nel service
//	user.setCreatedAt(LocalDateTime.now());
//	user.setCreatedBy("admin");

	@Override
	public void save(User user) {

		String sql = "INSERT INTO user (password, email, first_name, last_name, date_of_birth, "
				+ "created_at, created_by, deleted) " + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

		try (Connection conn = openConnection()) {
			
			PreparedStatement pstm = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
			
			conn.setAutoCommit(false);
			
			int i = 1;
			pstm.setString(i++, user.getPassword());
			pstm.setString(i++, user.getEmail());
			pstm.setString(i++, user.getFirstName());
			pstm.setString(i++, user.getLastName());
			pstm.setDate(i++, user.getDateOfBirth() != null ? java.sql.Date.valueOf(user.getDateOfBirth()) : null);
			pstm.setTimestamp(i++, java.sql.Timestamp.valueOf(user.getCreatedAt()));
			pstm.setString(i++, user.getCreatedBy());
			pstm.setBoolean(i++, false); // nuovo utente → deleted = false

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
				    UPDATE user SET
				        password = ?,
				        email = ?,
				        first_name = ?,
				        last_name = ?,
				        date_of_birth = ?,
				        modified_at = ?,
				        modified_by = ?
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
			pstm.setString(i++, user.getModifiedBy());
			pstm.setLong(i++, user.getId());

			pstm.executeUpdate();

			conn.commit();

		} catch (SQLException e) {
			throw new DatabaseException("Errore durante l'aggiornamento dell'utente con ID: " + user.getId(), e);
		}
	}

	@Override
	public void delete(User user) {
		String sql = "UPDATE user SET deleted = true, modified_at = ?, modified_by = ? WHERE id = ?";

		try (Connection conn = openConnection()) {

			conn.setAutoCommit(false);

			PreparedStatement pstm = conn.prepareStatement(sql);

			pstm.setTimestamp(1, java.sql.Timestamp.valueOf(LocalDateTime.now()));
			pstm.setString(2, user.getModifiedBy());
			pstm.setLong(3, user.getId());

			pstm.executeUpdate();

			conn.commit();

		} catch (SQLException e) {
			throw new DatabaseException("Errore durante la soft delete dell'utente con ID: " + user.getId(), e);
		}
	}

}

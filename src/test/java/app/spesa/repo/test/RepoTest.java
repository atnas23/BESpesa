package app.spesa.repo.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import progettone.listaspesa.entities.User;
import progettone.listaspesa.repository.UserDBRepository;

public class RepoTest {
	

	    private static Connection conn;
	    private static UserDBRepository repo;

	    
	    @Test
	    void testSaveAndFindById() {
	        User user = buildUser("mario.rossi@example.com");
	        user.setCreatedAt(LocalDateTime.now());
	        user.setCreatedBy("admin");

	        repo.save(user);

	        assertTrue(user.getId() > 0, "L'ID dovrebbe essere generato dal DB");

	        Optional<User> found = repo.findById((long) user.getId());
	        assertTrue(found.isPresent());
	        assertEquals("Mario", found.get().getFirstName());
	        assertEquals("admin", found.get().getCreatedBy());
	        assertFalse(found.get().isDeleted());
	    }

	    @Test
	    void testFindAllAndFindAllActive() {
	        User u1 = buildUser("a@example.com");
	        u1.setCreatedAt(LocalDateTime.now());
	        u1.setCreatedBy("admin");
	        repo.save(u1);

	        User u2 = buildUser("b@example.com");
	        u2.setCreatedAt(LocalDateTime.now());
	        u2.setCreatedBy("admin");
	        repo.save(u2);

	        List<User> all = repo.findAll();
	        assertEquals(2, all.size());

	        // soft delete del primo
	        u1.setModifiedBy("deleter");
	        repo.delete(u1);

	        List<User> active = repo.findAllActive();
	        assertEquals(1, active.size());
	        assertEquals("b@example.com", active.get(0).getEmail());
	    }

	    @Test
	    void testUpdate() {
	        User user = buildUser("update@example.com");
	        user.setCreatedAt(LocalDateTime.now());
	        user.setCreatedBy("creator");
	        repo.save(user);

	        user.setFirstName("Updated");
	        user.setModifiedBy("editor");
	        repo.update(user);

	        Optional<User> updated = repo.findById((long) user.getId());
	        assertTrue(updated.isPresent());
	        assertEquals("Updated", updated.get().getFirstName());
	        assertEquals("editor", updated.get().getModifiedBy());
	    }

	    @Test
	    void testSoftDelete() {
	        User user = buildUser("delete@example.com");
	        user.setCreatedAt(LocalDateTime.now());
	        user.setCreatedBy("creator");
	        repo.save(user);

	        user.setModifiedBy("deleter");
	        repo.delete(user);

	        // Non deve apparire nei risultati attivi
	        Optional<User> found = repo.findById((long) user.getId());
	        assertTrue(found.isPresent(), "L'utente è nel DB, ma con deleted=true");

	        // Verifica che il flag deleted sia true
	        try (PreparedStatement ps = conn.prepareStatement("SELECT deleted, modified_by FROM user WHERE id = ?")) {
	            ps.setLong(1, user.getId());
	            ResultSet rs = ps.executeQuery();
	            assertTrue(rs.next());
	            assertTrue(rs.getBoolean("deleted"));
	            assertEquals("deleter", rs.getString("modified_by"));
	        } catch (SQLException e) {
	            fail("Errore durante la verifica del soft delete", e);
	        }
	    }

	    // ------------------ HELPER ------------------

	    private User buildUser(String email) {
	        User u = new User();
	        u.setPassword("password123");
	        u.setEmail(email);
	        u.setFirstName("Mario");
	        u.setLastName("Rossi");
	        u.setDateOfBirth(LocalDate.of(1990, 1, 15));
	        return u;
	    }
	}



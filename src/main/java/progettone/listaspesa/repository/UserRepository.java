package progettone.listaspesa.repository;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import progettone.listaspesa.entities.Group;
import progettone.listaspesa.entities.User;
import progettone.listaspesa.interfaces.IRepo;

public class UserRepository implements IRepo<User> {

	private static final UserRepository INSTANCE = new UserRepository();

	private Set<User> users = new HashSet<>();
	private static int SEQ_ID = 1;

	private UserRepository() {

	}

	public static UserRepository getInstance() {
		return INSTANCE;
	}

	@Override
	public Set<User> findAll() {
		return users;
	}

	@Override
	public Optional<User> findById(int id) {
		for (User u : this.users) {
			if (u.getId() == id) {
				return Optional.of(u);
			}
		}
		return Optional.empty();
	}

	@Override
	public void save(User user) {

		user.setId(SEQ_ID++);

		user.setCreatedAt(LocalDateTime.now());
		// TODO: trovare modo per scegliere chi crea
		user.setCreatedBy("admin");

		this.users.add(user);
	}

	@Override
	public void update(User user) {
		for (User u : users) {

			if (users.contains(u)) {
				u = user;
				return;
			}
		}

	}

	@Override
	public void delete(int id) {

		for (User u : this.users) {
			if (u.getId() == id) {
				this.users.remove(u);
			}
		}

	}

	@Override
	public void delete(User user) {

		for (User u : this.users) {
			if (users.contains(u)) {
				this.users.remove(u);
				return;
			}
		}

	}

}

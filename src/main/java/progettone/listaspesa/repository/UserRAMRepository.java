package progettone.listaspesa.repository;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import progettone.listaspesa.entities.User;

public class UserRAMRepository extends BaseRepository implements IRepo<User> {

	private static final UserRAMRepository INSTANCE = new UserRAMRepository();

	private Set<User> users = new HashSet<>();
	private static int SEQ_ID = 1;

	private UserRAMRepository() {

	}

	public static UserRAMRepository getInstance() {
		return INSTANCE;
	}


	@Override
	public Optional<User> findById(Long id) {
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
	public void delete(User user) {

		for (User u : this.users) {
			if (users.contains(u)) {
				this.users.remove(u);
				return;
			}
		}

	}

	@Override
	public List<User> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

}

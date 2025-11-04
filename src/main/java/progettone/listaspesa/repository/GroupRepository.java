package progettone.listaspesa.repository;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import progettone.listaspesa.entities.Group;
import progettone.listaspesa.interfaces.IRepo;

public class GroupRepository implements IRepo<Group> {

	private static final GroupRepository INSTANCE = new GroupRepository();

	private Set<Group> groups = new HashSet<>();
	private static int SEQ_ID = 1;

	private GroupRepository() {
		// Costruttore privato per singleton
	}

	public static GroupRepository getInstance() {
		return INSTANCE;
	}

	@Override
	public Set<Group> findAll() {
		return groups;
	}

	@Override
	public Optional<Group> findById(int id) {
		for (Group g : this.groups) {
			if (g.getId() == id) {
				return Optional.of(g);
			}
		}
		return Optional.empty();
	}

	@Override
	public void save(Group group) {

		group.setId(SEQ_ID++);

		group.setCreatedAt(LocalDateTime.now());
		// TODO: trovare modo per scegliere chi crea
		group.setCreatedBy("admin");

		this.groups.add(group);
	}

	@Override
	public void update(Group group) {
		
		for (Group g : groups) {
			if (this.groups.contains(g)) {
//				this.groups.remove(g.get());
				  // trova l'elemento esistente
				
//			    Optional<Movimento> esistente = findById(movimento.getId());
//			    if (esistente.isPresent()) {
//			        movimenti.remove(esistente.get()); // rimuove quello vecchio
//			        movimenti.add(movimento);          // aggiunge quello aggiornato
//			    }
				
				return;
			}
		}

	}

	@Override
	public void delete(int id) {

		for (Group g : this.groups) {
			if (g.getId() == id) {
				this.groups.remove(g);
			}
		}

	}

	@Override
	public void delete(Group group) {

		for (Group g : this.groups) {
			if (groups.contains(g)) {
				this.groups.remove(g);
				return;
			}
		}

	}

}

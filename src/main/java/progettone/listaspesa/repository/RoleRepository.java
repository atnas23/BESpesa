package progettone.listaspesa.repository;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import progettone.listaspesa.entities.Role;
import progettone.listaspesa.interfaces.IRepo;

public class RoleRepository implements IRepo<Role>{
	
    private static final RoleRepository INSTANCE = new RoleRepository();

    private Set<Role> roles = new HashSet<>();
    private static int SEQ_ID = 1;

    private RoleRepository() {
    }

    public static RoleRepository getInstance() {
        return INSTANCE;
    }

	@Override
	public Set<Role> findAll() {
		return roles;
	}

	@Override
	public Optional<Role> findById(int id) {
		for (Role r : this.roles) {
			if (r.getId() == id) {
				return Optional.of(r);
			}
		}
		return Optional.empty();
	}

	@Override
	public void save(Role role) {

		
		role.setId(SEQ_ID++);
		
		role.setCreatedAt(LocalDateTime.now());
		//TODO: trovare modo per scegliere chi crea
		role.setCreatedBy("admin");
		
		this.roles.add(role);

	}

	@Override
	public void update(Role param) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(int id) {
		
		for (Role r : this.roles) {
			if (r.getId() == id) {
				this.roles.remove(r);
			}
		}
		
		
	}

	@Override
	public void delete(Role role) {
		
		for (Role r : this.roles) {
			if (roles.contains(r)) {
				this.roles.remove(r);
				return;
			}
		}
		
	}

	

}

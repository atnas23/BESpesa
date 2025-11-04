package progettone.listaspesa.interfaces;

import java.util.Optional;
import java.util.Set;

import progettone.listaspesa.entities.GenericEntity;

public interface IRepo<T extends GenericEntity> {

	public Set<T> findAll();

	public Optional<T> findById(int id);

	public void save(T param);

	public void update(T param);

	public void delete(int id);

	public void delete(T param);

}

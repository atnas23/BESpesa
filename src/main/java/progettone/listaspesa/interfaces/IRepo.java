package progettone.listaspesa.interfaces;

import java.util.List;
import java.util.Optional;

import progettone.listaspesa.entities.GenericEntity;

public interface IRepo<T extends GenericEntity> {

	public List<T> findAll();

	public Optional<T> findById(int id);

	public void save(T param);

	public void update(T param);

	public void delete(T param);

}

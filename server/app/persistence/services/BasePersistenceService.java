package persistence.services;

import java.util.List;

import persistence.filters.Filter;
import persistence.model.AbstractEntity;

public interface BasePersistenceService<T extends AbstractEntity> {
	void save(T entity);

	void delete(T entity);

	List<T> getBy(Filter filter);

	T getSingleBy(Filter filter);

	List<T> getAll();
}

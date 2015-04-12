package persistence.services;

import java.util.List;

import persistence.filters.Filter;
import persistence.model.AbstractEntity;
import play.db.ebean.Model;

public interface BasePersistenceService<T extends Model> {
	void save(T entity);

	void delete(T entity);

	List<T> getBy(Filter filter);

	T getSingleBy(Filter filter);

	List<T> getAll();
}

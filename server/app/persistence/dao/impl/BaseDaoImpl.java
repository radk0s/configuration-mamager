package persistence.dao.impl;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import persistence.dao.BaseDao;
import persistence.filters.Filter;
import persistence.model.AbstractEntity;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Expr;
import com.avaje.ebean.Expression;
import com.avaje.ebean.Query;

public abstract class BaseDaoImpl<T extends AbstractEntity> implements BaseDao<T> {

	private Class<T> clazz;

	protected BaseDaoImpl(Class<T> clazz) {
		this.clazz = clazz;
	}

	@Override
	public void save(T entity) {
		if (entity.getId() != null)
			Ebean.update(entity);
		else
			Ebean.save(entity);
	}

	@Override
	public void delete(T entity) {
		Ebean.delete(entity);
	}

	@Override
	public List<T> getBy(Filter filter) {

		Query<T> query = createQuery(filter);
		List<T> list = query.findList();
		return list;
	}

	private Query<T> createQuery(Filter filter) {
		if (filter == null)
			filter = Filter.create();

		Expression finalExpr = null;
		for (Pair<String, Object> pair : filter.getAndList()) {
			Expression newEqual = Expr.eq(pair.getLeft(), pair.getRight());
			if (finalExpr == null)
				finalExpr = newEqual;
			else
				finalExpr = Expr.and(finalExpr, newEqual);

		}
		Query<T> query = Ebean.createQuery(clazz);
		if (finalExpr != null)
			query.where().add(finalExpr);
		return query;
	}

	@Override
	public T getSingleBy(Filter filter) {
		List<T> results = getBy(filter);
		if (results.size() > 1)
			throw new RuntimeException("Not unique object for class: " + clazz + ". Found objects: " + results.size());
		T singleResult = null;
		if (results.size() == 1)
			singleResult = results.get(0);

		return singleResult;
	}
}

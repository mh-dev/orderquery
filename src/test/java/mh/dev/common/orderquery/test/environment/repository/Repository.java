package mh.dev.common.orderquery.test.environment.repository;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import mh.dev.common.orderquery.Order;
import mh.dev.common.orderquery.OrderQueryBuilder;
import mh.dev.common.orderquery.OrderState;

public abstract class Repository<E> {

	@PersistenceContext
	private EntityManager em;

	@Inject
	private OrderQueryBuilder orderQueryBuilder;

	public E save(E entity) {
		em.persist(entity);
		return entity;
	}

	public void delete(E entity) {
		entity = em.merge(entity);
		em.remove(entity);
	}

	@SuppressWarnings("unchecked")
	public List<E> all(OrderState orderState) {
		Query query = em.createQuery(orderQueryBuilder.render(orderState));
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<E> all(String queryName, List<String> columns, Map<String, Order> orders) {
		Query query = em.createQuery(orderQueryBuilder.render(queryName, columns, orders));
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<E> all(String queryName, List<String> columns, List<Order> orders) {
		Query query = em.createQuery(orderQueryBuilder.render(queryName, columns, orders));
		return query.getResultList();
	}
}

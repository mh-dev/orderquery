package mh.dev.common.orderquery.test.environment.repository;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import mh.dev.common.orderquery.OrderQueryBuilder;
import mh.dev.common.orderquery.OrderState;
import mh.dev.common.orderquery.test.environment.model.AnnotationModel;

@Stateless
public class AnnotationModelRepository {

	@PersistenceContext
	private EntityManager em;

	@Inject
	private OrderQueryBuilder orderQueryBuilder;

	public AnnotationModel save(AnnotationModel annotationModel) {
		em.persist(annotationModel);
		return annotationModel;
	}

	public void delete(AnnotationModel annotationModel) {
		annotationModel = em.merge(annotationModel);
		em.remove(annotationModel);
	}

	@SuppressWarnings("unchecked")
	public List<AnnotationModel> all(OrderState orderState) {
		Query query = em.createQuery(orderQueryBuilder.render(orderState));
		return query.getResultList();
	}
}

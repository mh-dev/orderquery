package mh.dev.common.orderquery.test.environment.service;

import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import mh.dev.common.orderquery.Order;
import mh.dev.common.orderquery.OrderState;
import mh.dev.common.orderquery.test.environment.model.AnnotationModel;
import mh.dev.common.orderquery.test.environment.repository.AnnotationModelRepository;

@Stateless
public class AnnotationModelService {

	@EJB
	private AnnotationModelRepository repository;

	public AnnotationModel save(AnnotationModel annotationModel) {
		return repository.save(annotationModel);
	}

	public void deleteAll(List<AnnotationModel> annotationModels) {
		for (AnnotationModel annotationModel : annotationModels) {
			repository.delete(annotationModel);
		}
	}

	public List<AnnotationModel> all(OrderState orderState) {
		return repository.all(orderState);
	}

	public List<AnnotationModel> all(String queryName, List<String> columns, Map<String, Order> orders) {
		return repository.all(queryName, columns, orders);
	}

	public List<AnnotationModel> all(String queryName, List<String> columns, List<Order> orders) {
		return repository.all(queryName, columns, orders);
	}

}

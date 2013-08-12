package mh.dev.common.orderquery.test.environment.service;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import mh.dev.common.orderquery.OrderState;
import mh.dev.common.orderquery.test.environment.model.MixedModel;
import mh.dev.common.orderquery.test.environment.repository.MixedModelRepository;

@Stateless
public class MixedModelService {

	@EJB
	private MixedModelRepository repository;

	public MixedModel save(MixedModel mixedModel) {
		return repository.save(mixedModel);
	}

	public void deleteAll(List<MixedModel> mixedModel) {
		for (MixedModel xmlModel : mixedModel) {
			repository.delete(xmlModel);
		}
	}

	public List<MixedModel> all(OrderState orderState) {
		return repository.all(orderState);
	}

}

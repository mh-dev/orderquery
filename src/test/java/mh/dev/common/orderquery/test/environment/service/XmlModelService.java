package mh.dev.common.orderquery.test.environment.service;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import mh.dev.common.orderquery.OrderState;
import mh.dev.common.orderquery.test.environment.model.XmlModel;
import mh.dev.common.orderquery.test.environment.repository.XmlModelRepository;

@Stateless
public class XmlModelService {

	@EJB
	private XmlModelRepository repository;

	public XmlModel save(XmlModel xmlModel) {
		return repository.save(xmlModel);
	}

	public void deleteAll(List<XmlModel> xmlModels) {
		for (XmlModel xmlModel : xmlModels) {
			repository.delete(xmlModel);
		}
	}

	public List<XmlModel> all(OrderState orderState) {
		return repository.all(orderState);
	}

}

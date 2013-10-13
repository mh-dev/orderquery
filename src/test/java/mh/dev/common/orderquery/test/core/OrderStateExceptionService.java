package mh.dev.common.orderquery.test.core;

import javax.ejb.Stateless;
import javax.inject.Inject;

import mh.dev.common.orderquery.OrderState;
import mh.dev.common.orderquery.annotation.OrderStateConfig;

@Stateless
public class OrderStateExceptionService {

	@Inject
	@OrderStateConfig(queryName = "annotationModel")
	private OrderState orderState;

	public OrderState getOrderState() {
		return orderState;
	}

	public void setOrderState(OrderState orderState) {
		this.orderState = orderState;
	}

}

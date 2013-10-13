package mh.dev.common.orderquery.core;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;
import javax.inject.Singleton;

import mh.dev.common.orderquery.OrderQueryBuilder;
import mh.dev.common.orderquery.OrderState;
import mh.dev.common.orderquery.annotation.OrderStateConfig;

@Singleton
public class OrderStateProducer {

	@Inject
	private OrderQueryBuilder orderQueryRenderer;

	@Produces
	@OrderStateConfig(queryName = "")
	public OrderState produceOrderQueryBuilder(InjectionPoint injectionPoint) {
		if (orderQueryRenderer != null) {
			OrderStateConfig orderStateConfig = injectionPoint.getAnnotated().getAnnotation(OrderStateConfig.class);
			return orderQueryRenderer.orderState(orderStateConfig.queryName());
		}
		return null;
	}
}

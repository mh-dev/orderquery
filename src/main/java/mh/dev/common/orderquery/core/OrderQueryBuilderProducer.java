package mh.dev.common.orderquery.core;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Singleton;

import mh.dev.common.orderquery.OrderQueryBuilder;
import mh.dev.common.orderquery.annotation.OrderStateConfig;

@Singleton
public class OrderQueryBuilderProducer {

	private OrderQueryBuilder orderQueryBuilder;

	@PostConstruct
	public void postConstruct() {
		OrderQueryBuilderImpl orderQueryBuilderImpl = new OrderQueryBuilderImpl();
		orderQueryBuilderImpl.load();
		orderQueryBuilder = orderQueryBuilderImpl;
	}

	@Produces
	@OrderStateConfig(queryName = "")
	public OrderQueryBuilder produceOrderQueryBuilder(InjectionPoint injectionPoint) {
		return orderQueryBuilder;
	}
}

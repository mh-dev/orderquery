package mh.dev.common.orderquery;


public interface OrderQueryBuilder {

	String render(OrderState orderState);

	OrderState orderState(String queryName);
}

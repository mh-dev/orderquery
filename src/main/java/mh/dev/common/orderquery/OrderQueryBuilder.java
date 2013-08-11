package mh.dev.common.orderquery;

import javax.ejb.Local;

@Local
public interface OrderQueryBuilder {

	String render(OrderState orderState);

	OrderState orderState(String queryName);
}

package mh.dev.common.orderquery.core.loader;

import java.util.List;

import mh.dev.common.orderquery.core.model.OrderQuery;

public interface QueryLoader extends OrderQueryLoader {

	List<OrderQuery> loadOrderQueries();
}

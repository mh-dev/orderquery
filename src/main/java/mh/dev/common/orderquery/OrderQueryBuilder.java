package mh.dev.common.orderquery;

import java.util.List;
import java.util.Map;

public interface OrderQueryBuilder {

	/**
	 * Renders the query for the given orderState
	 * 
	 * @param orderState
	 * @return query append with the order by statement
	 */
	String render(OrderState orderState);

	/**
	 * Renders the query with the given queryName and appends the order columns in the order of the orderColumnNames parameter. The order direction comes from
	 * the orderMappings argument.
	 * 
	 * @param queryName
	 *            name of the query
	 * @param orderedColumnNames
	 *            column names as defined the configuration (xml, annotation,..)
	 * @param orderMappings
	 *            contains the ordering of a column
	 * @return query append with the order by statement
	 */
	String render(String queryName, List<String> orderedColumnNames, Map<String, Order> orderMappings);

	/**
	 * Renders the query with the given queryName and appends the order columns in the order of the orderColumnNames parameter. The order direction comes from
	 * the index bases mapping from the order argument
	 * 
	 * @param queryName
	 *            name of the query
	 * @param orderedColumnNames
	 *            column names as defined in the configuration (xml, annotation,...)
	 * @param order
	 *            contains the ordering of a column
	 * @return query append with the order by statement
	 */
	String render(String queryName, List<String> orderedColumnNames, List<Order> order);

	/**
	 * Allows to generated a {@link OrderState} instead of injecting it.
	 * 
	 * @param queryName
	 *            name of the query
	 * @return {@link OrderState} which manages the order state and ordering.
	 */
	OrderState orderState(String queryName);
}

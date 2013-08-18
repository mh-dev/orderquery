package mh.dev.common.orderquery.core;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.enterprise.inject.Alternative;

import mh.dev.common.orderquery.Order;
import mh.dev.common.orderquery.OrderQueryBuilder;
import mh.dev.common.orderquery.OrderState;
import mh.dev.common.orderquery.core.exception.OrderQueryException;
import mh.dev.common.orderquery.core.loader.OrderQueryRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Loads the hole configuration for the order query module.<br>
 * Included xml and annotations bases configuration.
 * 
 * @author Mathias Hauser
 * 
 */
@Alternative
public class OrderQueryBuilderImpl implements OrderQueryBuilder {

	private Logger log = LoggerFactory.getLogger(OrderQueryBuilderImpl.class);
	private OrderQueryRepository orderQueryRepository;

	public OrderQueryBuilderImpl(OrderQueryRepository orderQueryRepository) {
		this.orderQueryRepository = orderQueryRepository;
	}

	@Override
	public String render(OrderState orderState) {
		if (orderState instanceof OrderStateImpl) {
			OrderStateImpl orderStateImpl = (OrderStateImpl) orderState;
			if (orderQueryRepository.queryExists(orderStateImpl.getQueryName())) {
				return appendOrderByStatement(orderQueryRepository.query(orderStateImpl.getQueryName()), orderStateImpl);
			} else {
				throw new OrderQueryException(String.format("Query %s does not exist", orderStateImpl.getQueryName()));
			}
		} else {
			throw new OrderQueryException("The orderState object has the wrong type - you may tried to use an own implementation which will not work!");
		}
	}

	@Override
	public String render(String queryName, List<String> orderedColumnNames, Map<String, Order> orderMappings) {
		if (orderedColumnNames.size() == orderMappings.keySet().size()) {
			StringBuilder query = new StringBuilder(orderQueryRepository.query(queryName));
			List<String> existingQueryColumns = orderQueryRepository.queryColumns(queryName);
			int used = 0;
			for (String definedColumnName : orderedColumnNames) {
				if (orderMappings.containsKey(definedColumnName)) {
					Order order = orderMappings.get(definedColumnName);
					if (!Order.NONE.equals(order)) {
						for (String existingColumnName : existingQueryColumns) {
							String existingDefinedColumnName = orderQueryRepository.definedColumn(existingColumnName);
							if (existingDefinedColumnName.equals(definedColumnName)) {
								if (used == 0)
									query.append(" Order By ");
								if (used != 0)
									query.append(", ");
								query.append(orderQueryRepository.columnQuery(existingColumnName)).append(" ").append(order);
								used++;
							}
						}
					}
				} else {
					throw new OrderQueryException(String.format("Column %s for query name %s does not have an order direction", definedColumnName, queryName));
				}
			}
			return query.toString();
		} else {
			throw new OrderQueryException(String.format("orderColumnNames and orderMappings do not have the same size"));
		}
	}

	@Override
	public String render(String queryName, List<String> orderedColumnNames, List<Order> orders) {
		if (orderedColumnNames.size() == orders.size()) {
			StringBuilder query = new StringBuilder(orderQueryRepository.query(queryName));
			List<String> existingQueryColumns = orderQueryRepository.queryColumns(queryName);
			int used = 0;
			for (int index = 0; index < orderedColumnNames.size(); index++) {
				String definedColumnName = orderedColumnNames.get(index);
				Order order = orders.get(index);
				if (!Order.NONE.equals(order)) {
					for (String existingColumnName : existingQueryColumns) {
						String existingDefinedColumnName = orderQueryRepository.definedColumn(existingColumnName);
						if (existingDefinedColumnName.equals(definedColumnName)) {
							if (used == 0)
								query.append(" Order By ");
							if (used != 0)
								query.append(", ");
							query.append(orderQueryRepository.columnQuery(existingColumnName)).append(" ").append(order);
							used++;
						}
					}
				}
			}
			return query.toString();
		} else {
			throw new OrderQueryException(String.format("orderColumnNames and orderMappings do not have the same size"));
		}
	}

	/**
	 * Appends the the Order By statement to the a query if orderStateImpl has a order column
	 * 
	 * @param query
	 *            which should be append
	 * @param orderStateImpl
	 *            the current order configuration
	 * @return query appended with the order by statement
	 */
	private String appendOrderByStatement(String query, OrderStateImpl orderStateImpl) {
		StringBuilder builder = new StringBuilder(query);
		int orderColumns = orderStateImpl.orderedColumns().size();
		if (orderColumns > 0) {
			builder.append(" Order By ");
			for (int index = 0; index < orderColumns; index++) {
				String publicColumnName = orderStateImpl.orderedColumns().get(index);
				String columnName = orderStateImpl.internalColumnName(publicColumnName);
				builder.append(orderQueryRepository.columnQuery(columnName)).append(" ").append(orderStateImpl.state(publicColumnName));
				if (index != (orderColumns - 1))
					builder.append(", ");
			}
		}
		log.trace(builder.toString());
		return builder.toString();
	}

	@Override
	public OrderState orderState(String queryName) {
		if (orderQueryRepository.queryExists(queryName)) {
			ConcurrentHashMap<String, Order> orders = new ConcurrentHashMap<>();
			ConcurrentHashMap<String, String> internalColumns = new ConcurrentHashMap<>();
			for (String column : orderQueryRepository.queryColumns(queryName)) {
				String publicColumnName = orderQueryRepository.definedColumn(column);
				orders.put(publicColumnName, Order.NONE);
				internalColumns.put(publicColumnName, column);
			}
			OrderStateImpl orderStateImpl = new OrderStateImpl(queryName, orders, internalColumns);
			if (orderQueryRepository.hasDefaultOrdering(queryName)) {
				orderStateImpl.order(orderQueryRepository.defaultColumn(queryName), orderQueryRepository.defaultOrder(queryName));
			}
			return orderStateImpl;
		} else {
			throw new OrderQueryException(String.format("Query %s does not exist", queryName));
		}
	}
}

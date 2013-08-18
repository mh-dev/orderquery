package mh.dev.common.orderquery.core;

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

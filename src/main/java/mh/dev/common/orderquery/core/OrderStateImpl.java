package mh.dev.common.orderquery.core;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.enterprise.inject.Alternative;

import mh.dev.common.orderquery.Order;
import mh.dev.common.orderquery.OrderState;

@Alternative
public class OrderStateImpl implements OrderState {

	private String queryName;
	private List<String> ordering = new ArrayList<>();
	private ConcurrentHashMap<String, Order> columnOrderMapping = new ConcurrentHashMap<>();
	private ConcurrentHashMap<String, String> internalColumnNames = new ConcurrentHashMap<>();

	public OrderStateImpl(String queryName, ConcurrentHashMap<String, Order> orders, ConcurrentHashMap<String, String> internalColumnNames) {
		this.queryName = queryName;
		this.columnOrderMapping = orders;
		this.internalColumnNames = internalColumnNames;
	}

	@Override
	public void order(String column, Order order) {
		if (columnOrderMapping.containsKey(column)) {
			columnOrderMapping.put(column, order);
			ordering.remove(column);
			if (!order.equals(Order.NONE))
				ordering.add(column);
		}
	}

	@Override
	public void remove(String column, Order order) {
		if (columnOrderMapping.containsKey(column)) {
			columnOrderMapping.put(column, Order.NONE);
			ordering.remove(column);
		}
	}

	@Override
	public Order state(String column) {
		return columnOrderMapping.get(column);
	}

	@Override
	public List<String> orderedColumns() {
		return ordering;
	}

	public String getQueryName() {
		return queryName;
	}

	/**
	 * Returns the internal column name for the given column
	 * 
	 * @param column
	 * @return
	 */
	public String internalColumnName(String column) {
		return internalColumnNames.get(column);
	}

}

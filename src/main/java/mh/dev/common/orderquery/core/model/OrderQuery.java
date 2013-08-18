package mh.dev.common.orderquery.core.model;

import java.util.ArrayList;
import java.util.List;

import mh.dev.common.orderquery.Order;

public class OrderQuery {

	private String model;
	private Class<?> type;
	private String name;
	private String query;
	private String defaultColumn;
	private Order defaultOrder;

	List<OrderQueryColumn> columns = new ArrayList<>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public String getDefaultColumn() {
		return defaultColumn;
	}

	public void setDefaultColumn(String defaultColumn) {
		this.defaultColumn = defaultColumn;
	}

	public Order getDefaultOrder() {
		return defaultOrder;
	}

	public void setDefaultOrder(Order defaultOrder) {
		this.defaultOrder = defaultOrder;
	}

	public List<OrderQueryColumn> getColumns() {
		return columns;
	}

	public void setColumns(List<OrderQueryColumn> columns) {
		this.columns = columns;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public Class<?> getType() {
		return type;
	}

	public void setType(Class<?> type) {
		this.type = type;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("OrderQuery [model=").append(model).append(", type=").append(type).append(", name=").append(name).append(", query=").append(query)
				.append(", defaultColumn=").append(defaultColumn).append(", defaultOrder=").append(defaultOrder).append(", columns=").append(columns)
				.append("]");
		return builder.toString();
	}

}

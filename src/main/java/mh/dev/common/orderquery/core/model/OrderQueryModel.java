package mh.dev.common.orderquery.core.model;

import java.util.HashMap;

public class OrderQueryModel {

	private String name;
	private Class<?> clazz;

	private HashMap<String, OrderQueryColumn> columns = new HashMap<>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Class<?> getClazz() {
		return clazz;
	}

	public void setClazz(Class<?> clazz) {
		this.clazz = clazz;
	}

	public HashMap<String, OrderQueryColumn> getColumns() {
		return columns;
	}

	public void setColumns(HashMap<String, OrderQueryColumn> columns) {
		this.columns = columns;
	}

	public void add(OrderQueryColumn orderQueryColumn) {
		columns.put(orderQueryColumn.getName(), orderQueryColumn);
	}

}

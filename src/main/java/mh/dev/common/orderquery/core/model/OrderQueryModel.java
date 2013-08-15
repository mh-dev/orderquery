package mh.dev.common.orderquery.core.model;

import java.util.ArrayList;
import java.util.List;

public class OrderQueryModel {

	private String name;
	private Class<?> type;

	private List<OrderQueryColumn> columns = new ArrayList<>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Class<?> getType() {
		return type;
	}

	public void setType(Class<?> clazz) {
		this.type = clazz;
	}

	public List<OrderQueryColumn> getColumns() {
		return columns;
	}

	public void setColumns(List<OrderQueryColumn> columns) {
		this.columns = columns;
	}

}

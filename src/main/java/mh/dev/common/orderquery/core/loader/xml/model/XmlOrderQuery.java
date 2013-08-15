package mh.dev.common.orderquery.core.loader.xml.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import mh.dev.common.orderquery.Order;

/**
 * Represents an orderable jpql query with an optional default column and order.
 * 
 * @author Mathias Hauser
 * 
 */
public class XmlOrderQuery {

	private String name;

	private String query;

	private String defaultColumn;

	private Order defaultOrder;

	private String model;

	private List<XmlColumn> columns = new ArrayList<>();

	@XmlAttribute(required = true)
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

	@XmlAttribute
	public String getDefaultColumn() {
		return defaultColumn;
	}

	public void setDefaultColumn(String defaultColumn) {
		this.defaultColumn = defaultColumn;
	}

	@XmlAttribute
	public Order getDefaultOrder() {
		return defaultOrder;
	}

	public void setDefaultOrder(Order defaultOrder) {
		this.defaultOrder = defaultOrder;
	}

	@XmlElement(name = "column")
	public List<XmlColumn> getColumns() {
		return columns;
	}

	public void setColumns(List<XmlColumn> columns) {
		this.columns = columns;
	}

	@XmlAttribute(name = "model")
	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("OrderQuery [name=").append(name).append(", query=").append(query).append(", defaultColumn=").append(defaultColumn)
				.append(", defaultOrder=").append(defaultOrder).append(", model=").append(model).append(", columns=").append(columns).append("]");
		return builder.toString();
	}

}

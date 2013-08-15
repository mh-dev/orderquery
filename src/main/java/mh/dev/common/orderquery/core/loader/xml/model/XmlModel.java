package mh.dev.common.orderquery.core.loader.xml.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 * Declares a model class with a given type and and a list of available {@link XmlColumn}'s. <br/>
 * Its simply a container for {@link XmlColumn}'s which are bound to a model object.
 * 
 * @author Mathias Hauser
 * 
 */
public class XmlModel {

	private String name;

	private String type;

	private List<XmlColumn> columns = new ArrayList<>();

	@XmlAttribute(required = true)
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@XmlElement(name = "column")
	public List<XmlColumn> getColumns() {
		return columns;
	}

	public void setColumns(List<XmlColumn> columns) {
		this.columns = columns;
	}

	@XmlAttribute(name = "name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Model [name=").append(name).append(", type=").append(type).append(", columns=").append(columns).append("]");
		return builder.toString();
	}
}

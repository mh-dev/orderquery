package mh.dev.common.orderquery.xml;

import javax.xml.bind.annotation.XmlAttribute;

/**
 * Declares an available order column for orderqueries and their jpql representation which can be binded to a model object or an orderquery.
 * 
 * @author Mathias Hauser
 * 
 */
public class XmlColumn {

	private String name;

	private String query;

	@XmlAttribute(required = true)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlAttribute(required = true)
	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Column [name=").append(name).append(", query=").append(query).append("]");
		return builder.toString();
	}

}

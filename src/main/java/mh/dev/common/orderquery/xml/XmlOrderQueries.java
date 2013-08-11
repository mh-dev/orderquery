package mh.dev.common.orderquery.xml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Container for the orderquery framework configuration.
 * 
 * @author Mathias Hauser
 * 
 */
@XmlRootElement(name = "orderqueries")
public class XmlOrderQueries {

	private String basePackage;

	private List<XmlOrderQuery> orderQueries = new ArrayList<>();

	private List<XmlModel> models = new ArrayList<>();

	@XmlAttribute
	public String getBasePackage() {
		return basePackage;
	}

	public void setBasePackage(String basePackage) {
		this.basePackage = basePackage;
	}

	@XmlElement(name = "orderquery")
	public List<XmlOrderQuery> getOrderQueries() {
		return orderQueries;
	}

	public void setOrderQueries(List<XmlOrderQuery> orderQueries) {
		this.orderQueries = orderQueries;
	}

	@XmlElement(name = "model")
	public List<XmlModel> getModels() {
		return models;
	}

	public void setModels(List<XmlModel> models) {
		this.models = models;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("OrderQueries [orderQueries=").append(orderQueries).append(", models=").append(models).append("]");
		return builder.toString();
	}

}

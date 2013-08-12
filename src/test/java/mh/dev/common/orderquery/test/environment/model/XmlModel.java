package mh.dev.common.orderquery.test.environment.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class XmlModel {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String field1;

	private String field2;

	public XmlModel() {
	}

	public XmlModel(String field1, String field2) {
		super();
		this.field1 = field1;
		this.field2 = field2;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getField1() {
		return field1;
	}

	public void setField1(String field1) {
		this.field1 = field1;
	}

	public String getField2() {
		return field2;
	}

	public void setField2(String field2) {
		this.field2 = field2;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("XmlModel [id=").append(id).append(", field1=").append(field1).append(", field2=").append(field2).append("]");
		return builder.toString();
	}
}

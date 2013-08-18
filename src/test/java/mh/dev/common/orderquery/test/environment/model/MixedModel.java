package mh.dev.common.orderquery.test.environment.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import mh.dev.common.orderquery.Order;
import mh.dev.common.orderquery.annotation.OrderQueries;
import mh.dev.common.orderquery.annotation.OrderQuery;
import mh.dev.common.orderquery.annotation.OrderQueryModel;

@Entity
@OrderQueryModel
@OrderQueries({ @OrderQuery(name = "mixedModel", query = "Select mm From MixedModel mm"),
		@OrderQuery(name = "mixedModelASC", query = "Select mm From MixedModel mm", defaultColumn = "field1", defaultOrder = Order.ASC),
		@OrderQuery(name = "mixedModelDESC", query = "Select mm From MixedModel mm", defaultColumn = "field1", defaultOrder = Order.DESC) })
public class MixedModel {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String field1;

	private String field2;

	public MixedModel() {
	}

	public MixedModel(String field1, String field2) {
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
		builder.append("MixedModel [id=").append(id).append(", field1=").append(field1).append(", field2=").append(field2).append("]");
		return builder.toString();
	}

}

package mh.dev.common.orderquery.test.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.InputStream;
import java.util.List;

import mh.dev.common.orderquery.core.loader.xml.XmlLoader;
import mh.dev.common.orderquery.core.model.OrderQueryColumn;
import mh.dev.common.orderquery.core.model.OrderQueryModel;
import mh.dev.common.orderquery.test.environment.model.XmlModel;

import org.junit.Before;
import org.junit.Test;

public class XmlLoaderTests {

	private XmlLoader xmlLoader;
	private static final String ORDER_QUERY_XML_LOCATION = "META-INF/orderquery.xml";

	@Before
	public void before() {
		InputStream orderQueryFile = this.getClass().getClassLoader().getResourceAsStream(ORDER_QUERY_XML_LOCATION);
		xmlLoader = new XmlLoader(orderQueryFile);
		xmlLoader.initialize();
	}

	@Test
	public void loadModels() {
		List<OrderQueryModel> orderQueryModels = xmlLoader.loadModels();
		OrderQueryModel orderQueryModel = findOrderQueryModel(orderQueryModels, "xmlModel");
		assertNotNull(orderQueryModel);
		assertEquals("xmlModel", orderQueryModel.getName());
		assertEquals(XmlModel.class, orderQueryModel.getType());
		OrderQueryColumn orderQueryColumnField1 = findOrderQueryColumn(orderQueryModel.getColumns(), "field1");
		assertNotNull(orderQueryColumnField1);
		assertEquals("field1", orderQueryColumnField1.getName());
		assertEquals("xm.field1", orderQueryColumnField1.getQuery());
		OrderQueryColumn orderQueryColumnField2 = findOrderQueryColumn(orderQueryModel.getColumns(), "field2");
		assertNotNull(orderQueryColumnField2);
		assertEquals("field2", orderQueryColumnField2.getName());
		assertEquals("xm.field2", orderQueryColumnField2.getQuery());

	}

	private OrderQueryModel findOrderQueryModel(List<OrderQueryModel> orderQueryModels, String model) {
		OrderQueryModel orderQueryModel = null;
		for (OrderQueryModel oqm : orderQueryModels) {
			if (oqm.getName() != null && oqm.getName().equals(model)) {
				orderQueryModel = oqm;
			}
		}
		return orderQueryModel;
	}

	private OrderQueryColumn findOrderQueryColumn(List<OrderQueryColumn> orderQueryColumns, String column) {
		OrderQueryColumn orderQueryColumn = null;
		for (OrderQueryColumn oqc : orderQueryColumns) {
			if (oqc.getName() != null && oqc.getName().equals(column)) {
				orderQueryColumn = oqc;
			}
		}
		return orderQueryColumn;
	}
}

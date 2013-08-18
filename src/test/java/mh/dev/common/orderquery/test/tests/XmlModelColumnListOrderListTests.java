package mh.dev.common.orderquery.test.tests;

import static junit.framework.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;

import mh.dev.common.orderquery.Order;
import mh.dev.common.orderquery.test.core.TestCore;
import mh.dev.common.orderquery.test.environment.model.XmlModel;
import mh.dev.common.orderquery.test.environment.service.XmlModelService;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class XmlModelColumnListOrderListTests extends TestCore {

	@EJB
	private XmlModelService service;

	private List<XmlModel> xmlModels = new ArrayList<>();

	@Before
	public void before() {
		xmlModels.add(service.save(new XmlModel("a", "a")));
		xmlModels.add(service.save(new XmlModel("c", "a")));
		xmlModels.add(service.save(new XmlModel("b", "c")));
		xmlModels.add(service.save(new XmlModel("f", "c")));
		xmlModels.add(service.save(new XmlModel("g", "b")));
		xmlModels.add(service.save(new XmlModel("e", "c")));
	}

	@After
	public void after() {
		service.deleteAll(xmlModels);
		xmlModels.clear();
	}

	@Test
	public void orderByAttribute1Desc() {
		List<String> column = new ArrayList<>();
		column.add("field1");
		List<Order> orders = new ArrayList<>();
		orders.add(Order.DESC);
		List<XmlModel> orderedModels = service.all("xmlModel", column, orders);
		assertEquals(xmlModels.get(4).getField1(), orderedModels.get(0).getField1());
		assertEquals(xmlModels.get(3).getField1(), orderedModels.get(1).getField1());
		assertEquals(xmlModels.get(5).getField1(), orderedModels.get(2).getField1());
		assertEquals(xmlModels.get(1).getField1(), orderedModels.get(3).getField1());
		assertEquals(xmlModels.get(2).getField1(), orderedModels.get(4).getField1());
		assertEquals(xmlModels.get(0).getField1(), orderedModels.get(5).getField1());
	}

	@Test
	public void orderByAttribute1Asc() {
		List<String> column = new ArrayList<>();
		column.add("field1");
		List<Order> orders = new ArrayList<>();
		orders.add(Order.ASC);
		List<XmlModel> orderedModels = service.all("xmlModel", column, orders);
		assertEquals(xmlModels.get(0).getField1(), orderedModels.get(0).getField1());
		assertEquals(xmlModels.get(2).getField1(), orderedModels.get(1).getField1());
		assertEquals(xmlModels.get(1).getField1(), orderedModels.get(2).getField1());
		assertEquals(xmlModels.get(5).getField1(), orderedModels.get(3).getField1());
		assertEquals(xmlModels.get(3).getField1(), orderedModels.get(4).getField1());
		assertEquals(xmlModels.get(4).getField1(), orderedModels.get(5).getField1());
	}

	@Test
	public void orderByAttribute2DescAndAttribute1Desc() {
		List<String> column = new ArrayList<>();
		column.add("field2");
		column.add("field1");
		List<Order> orders = new ArrayList<>();
		orders.add(Order.DESC);
		orders.add(Order.DESC);
		List<XmlModel> orderedModels = service.all("xmlModel", column, orders);
		assertEquals(xmlModels.get(3).getField1(), orderedModels.get(0).getField1());
		assertEquals(xmlModels.get(5).getField1(), orderedModels.get(1).getField1());
		assertEquals(xmlModels.get(2).getField1(), orderedModels.get(2).getField1());
		assertEquals(xmlModels.get(4).getField1(), orderedModels.get(3).getField1());
		assertEquals(xmlModels.get(1).getField1(), orderedModels.get(4).getField1());
		assertEquals(xmlModels.get(0).getField1(), orderedModels.get(5).getField1());
	}

	@Test
	public void orderByAttribute2AscAndAttribute1Asc() {
		List<String> column = new ArrayList<>();
		column.add("field2");
		column.add("field1");
		List<Order> orders = new ArrayList<>();
		orders.add(Order.ASC);
		orders.add(Order.ASC);
		List<XmlModel> orderedModels = service.all("xmlModel", column, orders);
		assertEquals(xmlModels.get(0).getField1(), orderedModels.get(0).getField1());
		assertEquals(xmlModels.get(1).getField1(), orderedModels.get(1).getField1());
		assertEquals(xmlModels.get(4).getField1(), orderedModels.get(2).getField1());
		assertEquals(xmlModels.get(2).getField1(), orderedModels.get(3).getField1());
		assertEquals(xmlModels.get(5).getField1(), orderedModels.get(4).getField1());
		assertEquals(xmlModels.get(3).getField1(), orderedModels.get(5).getField1());
	}

	@Test
	public void orderByAttribute2DescAndAttribute1Asc() {
		List<String> column = new ArrayList<>();
		column.add("field2");
		column.add("field1");
		List<Order> orders = new ArrayList<>();
		orders.add(Order.DESC);
		orders.add(Order.ASC);
		List<XmlModel> orderedModels = service.all("xmlModel", column, orders);
		assertEquals(xmlModels.get(2).getField1(), orderedModels.get(0).getField1());
		assertEquals(xmlModels.get(5).getField1(), orderedModels.get(1).getField1());
		assertEquals(xmlModels.get(3).getField1(), orderedModels.get(2).getField1());
		assertEquals(xmlModels.get(4).getField1(), orderedModels.get(3).getField1());
		assertEquals(xmlModels.get(0).getField1(), orderedModels.get(4).getField1());
		assertEquals(xmlModels.get(1).getField1(), orderedModels.get(5).getField1());
	}

	@Test
	public void orderByAttribute2AscAndAttribute1Desc() {
		List<String> column = new ArrayList<>();
		column.add("field2");
		column.add("field1");
		List<Order> orders = new ArrayList<>();
		orders.add(Order.ASC);
		orders.add(Order.DESC);
		List<XmlModel> orderedModels = service.all("xmlModel", column, orders);
		assertEquals(xmlModels.get(1).getField1(), orderedModels.get(0).getField1());
		assertEquals(xmlModels.get(0).getField1(), orderedModels.get(1).getField1());
		assertEquals(xmlModels.get(4).getField1(), orderedModels.get(2).getField1());
		assertEquals(xmlModels.get(3).getField1(), orderedModels.get(3).getField1());
		assertEquals(xmlModels.get(5).getField1(), orderedModels.get(4).getField1());
		assertEquals(xmlModels.get(2).getField1(), orderedModels.get(5).getField1());
	}
}

package mh.dev.common.orderquery.test.tests;

import static junit.framework.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;

import mh.dev.common.orderquery.Order;
import mh.dev.common.orderquery.test.core.TestCore;
import mh.dev.common.orderquery.test.environment.model.MixedModel;
import mh.dev.common.orderquery.test.environment.service.MixedModelService;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class MixedModelColumnListOrderMapTests extends TestCore {

	@EJB
	private MixedModelService service;

	private List<MixedModel> mixedModels = new ArrayList<>();

	@Before
	public void before() {
		mixedModels.add(service.save(new MixedModel("a", "a")));
		mixedModels.add(service.save(new MixedModel("c", "a")));
		mixedModels.add(service.save(new MixedModel("b", "c")));
		mixedModels.add(service.save(new MixedModel("f", "c")));
		mixedModels.add(service.save(new MixedModel("g", "b")));
		mixedModels.add(service.save(new MixedModel("e", "c")));
	}

	@After
	public void after() {
		service.deleteAll(mixedModels);
		mixedModels.clear();
	}

	@Test
	public void orderByAttribute1Desc() {
		List<String> column = new ArrayList<>();
		column.add("field1");
		Map<String, Order> orders = new HashMap<>();
		orders.put("field1", Order.DESC);
		List<MixedModel> orderedModels = service.all("mixedModel", column, orders);
		assertEquals(mixedModels.get(4).getField1(), orderedModels.get(0).getField1());
		assertEquals(mixedModels.get(3).getField1(), orderedModels.get(1).getField1());
		assertEquals(mixedModels.get(5).getField1(), orderedModels.get(2).getField1());
		assertEquals(mixedModels.get(1).getField1(), orderedModels.get(3).getField1());
		assertEquals(mixedModels.get(2).getField1(), orderedModels.get(4).getField1());
		assertEquals(mixedModels.get(0).getField1(), orderedModels.get(5).getField1());
	}

	@Test
	public void orderByAttribute1Asc() {
		List<String> column = new ArrayList<>();
		column.add("field1");
		Map<String, Order> orders = new HashMap<>();
		orders.put("field1", Order.ASC);
		List<MixedModel> orderedModels = service.all("mixedModel", column, orders);
		assertEquals(mixedModels.get(0).getField1(), orderedModels.get(0).getField1());
		assertEquals(mixedModels.get(2).getField1(), orderedModels.get(1).getField1());
		assertEquals(mixedModels.get(1).getField1(), orderedModels.get(2).getField1());
		assertEquals(mixedModels.get(5).getField1(), orderedModels.get(3).getField1());
		assertEquals(mixedModels.get(3).getField1(), orderedModels.get(4).getField1());
		assertEquals(mixedModels.get(4).getField1(), orderedModels.get(5).getField1());
	}

	@Test
	public void orderByAttribute2DescAndAttribute1Desc() {
		List<String> column = new ArrayList<>();
		column.add("field2");
		column.add("field1");
		Map<String, Order> orders = new HashMap<>();
		orders.put("field2", Order.DESC);
		orders.put("field1", Order.DESC);
		List<MixedModel> orderedModels = service.all("mixedModel", column, orders);
		assertEquals(mixedModels.get(3).getField1(), orderedModels.get(0).getField1());
		assertEquals(mixedModels.get(5).getField1(), orderedModels.get(1).getField1());
		assertEquals(mixedModels.get(2).getField1(), orderedModels.get(2).getField1());
		assertEquals(mixedModels.get(4).getField1(), orderedModels.get(3).getField1());
		assertEquals(mixedModels.get(1).getField1(), orderedModels.get(4).getField1());
		assertEquals(mixedModels.get(0).getField1(), orderedModels.get(5).getField1());
	}

	@Test
	public void orderByAttribute2AscAndAttribute1Asc() {
		List<String> column = new ArrayList<>();
		column.add("field2");
		column.add("field1");
		Map<String, Order> orders = new HashMap<>();
		orders.put("field2", Order.ASC);
		orders.put("field1", Order.ASC);
		List<MixedModel> orderedModels = service.all("mixedModel", column, orders);
		assertEquals(mixedModels.get(0).getField1(), orderedModels.get(0).getField1());
		assertEquals(mixedModels.get(1).getField1(), orderedModels.get(1).getField1());
		assertEquals(mixedModels.get(4).getField1(), orderedModels.get(2).getField1());
		assertEquals(mixedModels.get(2).getField1(), orderedModels.get(3).getField1());
		assertEquals(mixedModels.get(5).getField1(), orderedModels.get(4).getField1());
		assertEquals(mixedModels.get(3).getField1(), orderedModels.get(5).getField1());
	}

	@Test
	public void orderByAttribute2DescAndAttribute1Asc() {
		List<String> column = new ArrayList<>();
		column.add("field2");
		column.add("field1");
		Map<String, Order> orders = new HashMap<>();
		orders.put("field2", Order.DESC);
		orders.put("field1", Order.ASC);
		List<MixedModel> orderedModels = service.all("mixedModel", column, orders);
		assertEquals(mixedModels.get(2).getField1(), orderedModels.get(0).getField1());
		assertEquals(mixedModels.get(5).getField1(), orderedModels.get(1).getField1());
		assertEquals(mixedModels.get(3).getField1(), orderedModels.get(2).getField1());
		assertEquals(mixedModels.get(4).getField1(), orderedModels.get(3).getField1());
		assertEquals(mixedModels.get(0).getField1(), orderedModels.get(4).getField1());
		assertEquals(mixedModels.get(1).getField1(), orderedModels.get(5).getField1());
	}

	@Test
	public void orderByAttribute2AscAndAttribute1Desc() {
		List<String> column = new ArrayList<>();
		column.add("field2");
		column.add("field1");
		Map<String, Order> orders = new HashMap<>();
		orders.put("field2", Order.ASC);
		orders.put("field1", Order.DESC);
		List<MixedModel> orderedModels = service.all("mixedModel", column, orders);
		assertEquals(mixedModels.get(1).getField1(), orderedModels.get(0).getField1());
		assertEquals(mixedModels.get(0).getField1(), orderedModels.get(1).getField1());
		assertEquals(mixedModels.get(4).getField1(), orderedModels.get(2).getField1());
		assertEquals(mixedModels.get(3).getField1(), orderedModels.get(3).getField1());
		assertEquals(mixedModels.get(5).getField1(), orderedModels.get(4).getField1());
		assertEquals(mixedModels.get(2).getField1(), orderedModels.get(5).getField1());
	}
}

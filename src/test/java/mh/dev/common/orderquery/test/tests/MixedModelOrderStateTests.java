package mh.dev.common.orderquery.test.tests;

import static junit.framework.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.inject.Inject;

import mh.dev.common.orderquery.Order;
import mh.dev.common.orderquery.OrderState;
import mh.dev.common.orderquery.annotation.OrderStateConfig;
import mh.dev.common.orderquery.test.core.TestCore;
import mh.dev.common.orderquery.test.environment.model.MixedModel;
import mh.dev.common.orderquery.test.environment.service.MixedModelService;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class MixedModelOrderStateTests extends TestCore {

	@EJB
	private MixedModelService service;

	@Inject
	@OrderStateConfig(queryName = "mixedModel")
	private OrderState orderState;
	@Inject
	@OrderStateConfig(queryName = "mixedModelASC")
	private OrderState orderStateDefaultASC;
	@Inject
	@OrderStateConfig(queryName = "mixedModelDESC")
	private OrderState orderStateDefaultDESC;

	private List<MixedModel> mixedModel = new ArrayList<>();

	@Before
	public void before() {
		mixedModel.add(service.save(new MixedModel("a", "a")));
		mixedModel.add(service.save(new MixedModel("c", "a")));
		mixedModel.add(service.save(new MixedModel("b", "c")));
		mixedModel.add(service.save(new MixedModel("f", "c")));
		mixedModel.add(service.save(new MixedModel("g", "b")));
		mixedModel.add(service.save(new MixedModel("e", "c")));
	}

	@After
	public void after() {
		service.deleteAll(mixedModel);
		mixedModel.clear();
	}

	@Test
	public void orderByDefaultAttribute1Desc() {
		List<MixedModel> orderedModels = service.all(orderStateDefaultDESC);
		assertEquals(mixedModel.get(4).getField1(), orderedModels.get(0).getField1());
		assertEquals(mixedModel.get(3).getField1(), orderedModels.get(1).getField1());
		assertEquals(mixedModel.get(5).getField1(), orderedModels.get(2).getField1());
		assertEquals(mixedModel.get(1).getField1(), orderedModels.get(3).getField1());
		assertEquals(mixedModel.get(2).getField1(), orderedModels.get(4).getField1());
		assertEquals(mixedModel.get(0).getField1(), orderedModels.get(5).getField1());
	}

	@Test
	public void orderByDefaultAttribute1Asc() {
		List<MixedModel> orderedModels = service.all(orderStateDefaultASC);
		assertEquals(mixedModel.get(0).getField1(), orderedModels.get(0).getField1());
		assertEquals(mixedModel.get(2).getField1(), orderedModels.get(1).getField1());
		assertEquals(mixedModel.get(1).getField1(), orderedModels.get(2).getField1());
		assertEquals(mixedModel.get(5).getField1(), orderedModels.get(3).getField1());
		assertEquals(mixedModel.get(3).getField1(), orderedModels.get(4).getField1());
		assertEquals(mixedModel.get(4).getField1(), orderedModels.get(5).getField1());
	}

	@Test
	public void orderByAttribute1Desc() {
		orderState.order("field1", Order.DESC);
		List<MixedModel> orderedModels = service.all(orderState);
		assertEquals(mixedModel.get(4).getField1(), orderedModels.get(0).getField1());
		assertEquals(mixedModel.get(3).getField1(), orderedModels.get(1).getField1());
		assertEquals(mixedModel.get(5).getField1(), orderedModels.get(2).getField1());
		assertEquals(mixedModel.get(1).getField1(), orderedModels.get(3).getField1());
		assertEquals(mixedModel.get(2).getField1(), orderedModels.get(4).getField1());
		assertEquals(mixedModel.get(0).getField1(), orderedModels.get(5).getField1());
	}

	@Test
	public void orderByAttribute1Asc() {
		orderState.order("field1", Order.ASC);
		List<MixedModel> orderedModels = service.all(orderState);
		assertEquals(mixedModel.get(0).getField1(), orderedModels.get(0).getField1());
		assertEquals(mixedModel.get(2).getField1(), orderedModels.get(1).getField1());
		assertEquals(mixedModel.get(1).getField1(), orderedModels.get(2).getField1());
		assertEquals(mixedModel.get(5).getField1(), orderedModels.get(3).getField1());
		assertEquals(mixedModel.get(3).getField1(), orderedModels.get(4).getField1());
		assertEquals(mixedModel.get(4).getField1(), orderedModels.get(5).getField1());
	}

	@Test
	public void orderByAttribute2DescAndAttribute1Desc() {
		orderState.order("field2", Order.DESC);
		orderState.order("field1", Order.DESC);
		List<MixedModel> orderedModels = service.all(orderState);
		assertEquals(mixedModel.get(3).getField1(), orderedModels.get(0).getField1());
		assertEquals(mixedModel.get(5).getField1(), orderedModels.get(1).getField1());
		assertEquals(mixedModel.get(2).getField1(), orderedModels.get(2).getField1());
		assertEquals(mixedModel.get(4).getField1(), orderedModels.get(3).getField1());
		assertEquals(mixedModel.get(1).getField1(), orderedModels.get(4).getField1());
		assertEquals(mixedModel.get(0).getField1(), orderedModels.get(5).getField1());
	}

	@Test
	public void orderByAttribute2AscAndAttribute1Asc() {
		orderState.order("field2", Order.ASC);
		orderState.order("field1", Order.ASC);
		List<MixedModel> orderedModels = service.all(orderState);
		assertEquals(mixedModel.get(0).getField1(), orderedModels.get(0).getField1());
		assertEquals(mixedModel.get(1).getField1(), orderedModels.get(1).getField1());
		assertEquals(mixedModel.get(4).getField1(), orderedModels.get(2).getField1());
		assertEquals(mixedModel.get(2).getField1(), orderedModels.get(3).getField1());
		assertEquals(mixedModel.get(5).getField1(), orderedModels.get(4).getField1());
		assertEquals(mixedModel.get(3).getField1(), orderedModels.get(5).getField1());
	}

	@Test
	public void orderByAttribute2DescAndAttribute1Asc() {
		orderState.order("field2", Order.DESC);
		orderState.order("field1", Order.ASC);
		List<MixedModel> orderedModels = service.all(orderState);
		assertEquals(mixedModel.get(2).getField1(), orderedModels.get(0).getField1());
		assertEquals(mixedModel.get(5).getField1(), orderedModels.get(1).getField1());
		assertEquals(mixedModel.get(3).getField1(), orderedModels.get(2).getField1());
		assertEquals(mixedModel.get(4).getField1(), orderedModels.get(3).getField1());
		assertEquals(mixedModel.get(0).getField1(), orderedModels.get(4).getField1());
		assertEquals(mixedModel.get(1).getField1(), orderedModels.get(5).getField1());
	}

	@Test
	public void orderByAttribute2AscAndAttribute1Desc() {
		orderState.order("field2", Order.ASC);
		orderState.order("field1", Order.DESC);
		List<MixedModel> orderedModels = service.all(orderState);
		assertEquals(mixedModel.get(1).getField1(), orderedModels.get(0).getField1());
		assertEquals(mixedModel.get(0).getField1(), orderedModels.get(1).getField1());
		assertEquals(mixedModel.get(4).getField1(), orderedModels.get(2).getField1());
		assertEquals(mixedModel.get(3).getField1(), orderedModels.get(3).getField1());
		assertEquals(mixedModel.get(5).getField1(), orderedModels.get(4).getField1());
		assertEquals(mixedModel.get(2).getField1(), orderedModels.get(5).getField1());
	}

}

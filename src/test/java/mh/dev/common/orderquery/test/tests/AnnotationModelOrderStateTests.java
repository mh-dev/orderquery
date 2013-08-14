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
import mh.dev.common.orderquery.test.environment.model.AnnotationModel;
import mh.dev.common.orderquery.test.environment.service.AnnotationModelService;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
@RunWith(Arquillian.class)
public class AnnotationModelOrderStateTests extends TestCore {

	@EJB
	private AnnotationModelService service;

	@Inject
	@OrderStateConfig(queryName = "annotationModel")
	private OrderState orderState;

	private List<AnnotationModel> annotationModels = new ArrayList<>();

	@Before
	public void before() {
		annotationModels.add(service.save(new AnnotationModel("a", "a")));
		annotationModels.add(service.save(new AnnotationModel("c", "a")));
		annotationModels.add(service.save(new AnnotationModel("b", "c")));
		annotationModels.add(service.save(new AnnotationModel("f", "c")));
		annotationModels.add(service.save(new AnnotationModel("g", "b")));
		annotationModels.add(service.save(new AnnotationModel("e", "c")));
	}

	@After
	public void after() {
		service.deleteAll(annotationModels);
		annotationModels.clear();
	}

	@Test
	public void orderByAttribute1Desc() {
		orderState.order("field1", Order.DESC);
		List<AnnotationModel> orderedModels = service.all(orderState);
		assertEquals(annotationModels.get(4).getField1(), orderedModels.get(0).getField1());
		assertEquals(annotationModels.get(3).getField1(), orderedModels.get(1).getField1());
		assertEquals(annotationModels.get(5).getField1(), orderedModels.get(2).getField1());
		assertEquals(annotationModels.get(1).getField1(), orderedModels.get(3).getField1());
		assertEquals(annotationModels.get(2).getField1(), orderedModels.get(4).getField1());
		assertEquals(annotationModels.get(0).getField1(), orderedModels.get(5).getField1());
	}

	@Test
	public void orderByAttribute1Asc() {
		orderState.order("field1", Order.ASC);
		List<AnnotationModel> orderedModels = service.all(orderState);
		assertEquals(annotationModels.get(0).getField1(), orderedModels.get(0).getField1());
		assertEquals(annotationModels.get(2).getField1(), orderedModels.get(1).getField1());
		assertEquals(annotationModels.get(1).getField1(), orderedModels.get(2).getField1());
		assertEquals(annotationModels.get(5).getField1(), orderedModels.get(3).getField1());
		assertEquals(annotationModels.get(3).getField1(), orderedModels.get(4).getField1());
		assertEquals(annotationModels.get(4).getField1(), orderedModels.get(5).getField1());
	}

	@Test
	public void orderByAttribute2DescAndAttribute1Desc() {
		orderState.order("field2", Order.DESC);
		orderState.order("field1", Order.DESC);
		List<AnnotationModel> orderedModels = service.all(orderState);
		assertEquals(annotationModels.get(3).getField1(), orderedModels.get(0).getField1());
		assertEquals(annotationModels.get(5).getField1(), orderedModels.get(1).getField1());
		assertEquals(annotationModels.get(2).getField1(), orderedModels.get(2).getField1());
		assertEquals(annotationModels.get(4).getField1(), orderedModels.get(3).getField1());
		assertEquals(annotationModels.get(1).getField1(), orderedModels.get(4).getField1());
		assertEquals(annotationModels.get(0).getField1(), orderedModels.get(5).getField1());
	}

	@Test
	public void orderByAttribute2AscAndAttribute1Asc() {
		orderState.order("field2", Order.ASC);
		orderState.order("field1", Order.ASC);
		List<AnnotationModel> orderedModels = service.all(orderState);
		assertEquals(annotationModels.get(0).getField1(), orderedModels.get(0).getField1());
		assertEquals(annotationModels.get(1).getField1(), orderedModels.get(1).getField1());
		assertEquals(annotationModels.get(4).getField1(), orderedModels.get(2).getField1());
		assertEquals(annotationModels.get(2).getField1(), orderedModels.get(3).getField1());
		assertEquals(annotationModels.get(5).getField1(), orderedModels.get(4).getField1());
		assertEquals(annotationModels.get(3).getField1(), orderedModels.get(5).getField1());
	}

	@Test
	public void orderByAttribute2DescAndAttribute1Asc() {
		orderState.order("field2", Order.DESC);
		orderState.order("field1", Order.ASC);
		List<AnnotationModel> orderedModels = service.all(orderState);
		assertEquals(annotationModels.get(2).getField1(), orderedModels.get(0).getField1());
		assertEquals(annotationModels.get(5).getField1(), orderedModels.get(1).getField1());
		assertEquals(annotationModels.get(3).getField1(), orderedModels.get(2).getField1());
		assertEquals(annotationModels.get(4).getField1(), orderedModels.get(3).getField1());
		assertEquals(annotationModels.get(0).getField1(), orderedModels.get(4).getField1());
		assertEquals(annotationModels.get(1).getField1(), orderedModels.get(5).getField1());
	}

	@Test
	public void orderByAttribute2AscAndAttribute1Desc() {
		orderState.order("field2", Order.ASC);
		orderState.order("field1", Order.DESC);
		List<AnnotationModel> orderedModels = service.all(orderState);
		assertEquals(annotationModels.get(1).getField1(), orderedModels.get(0).getField1());
		assertEquals(annotationModels.get(0).getField1(), orderedModels.get(1).getField1());
		assertEquals(annotationModels.get(4).getField1(), orderedModels.get(2).getField1());
		assertEquals(annotationModels.get(3).getField1(), orderedModels.get(3).getField1());
		assertEquals(annotationModels.get(5).getField1(), orderedModels.get(4).getField1());
		assertEquals(annotationModels.get(2).getField1(), orderedModels.get(5).getField1());
	}

}

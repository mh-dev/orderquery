package mh.dev.common.orderquery.core;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Singleton;

import mh.dev.common.orderquery.OrderQueryBuilder;
import mh.dev.common.orderquery.annotation.OrderStateConfig;
import mh.dev.common.orderquery.core.loader.ModelLoader;
import mh.dev.common.orderquery.core.loader.OrderQueryRepository;
import mh.dev.common.orderquery.core.loader.QueryLoader;
import mh.dev.common.orderquery.core.loader.annotation.AnnotationLoader;
import mh.dev.common.orderquery.core.loader.xml.XmlLoader;

@Singleton
public class OrderQueryBuilderProducer {

	private OrderQueryRepository orderQueryRepository;

	@PostConstruct
	public void postConstruct() {
		orderQueryRepository = new OrderQueryRepository();
		XmlLoader xmlLoader = new XmlLoader();
		xmlLoader.initialize();
		AnnotationLoader annotationLoader = new AnnotationLoader(xmlLoader.basePackage());
		List<QueryLoader> queryLoaders = new ArrayList<>();
		queryLoaders.add(xmlLoader);
		queryLoaders.add(annotationLoader);
		for (QueryLoader queryLoader : queryLoaders) {
			queryLoader.initialize();
		}
		for (QueryLoader queryLoader : queryLoaders) {
			if (queryLoader instanceof ModelLoader) {
				orderQueryRepository.addModels(queryLoader.getClass().getName(), ((ModelLoader) queryLoader).loadModels());
			}
		}
		for (QueryLoader queryLoader : queryLoaders) {
			if (queryLoader instanceof QueryLoader) {
				orderQueryRepository.addQueries(queryLoader.getClass().getName(), ((QueryLoader) queryLoader).loadOrderQueries());
			}
		}
	}

	@Produces
	@OrderStateConfig(queryName = "")
	public OrderQueryBuilder produceOrderQueryBuilder(InjectionPoint injectionPoint) {
		return new OrderQueryBuilderImpl(orderQueryRepository);
	}
}

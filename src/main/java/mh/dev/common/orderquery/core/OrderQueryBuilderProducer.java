package mh.dev.common.orderquery.core;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;
import javax.inject.Singleton;

import mh.dev.common.orderquery.OrderQueryBuilder;
import mh.dev.common.orderquery.annotation.OrderStateConfig;
import mh.dev.common.orderquery.core.loader.ModelLoader;
import mh.dev.common.orderquery.core.loader.OrderQueryRepository;
import mh.dev.common.orderquery.core.loader.QueryLoader;
import mh.dev.common.orderquery.core.loader.annotation.AnnotationLoader;
import mh.dev.common.orderquery.core.loader.xml.XmlLoader;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

/**
 * Produces {@link OrderQueryBuilder}'s for the rendering of dynamic ordered JPQL queries
 * 
 * @author Mathias Hauser
 * 
 */
@Singleton
public class OrderQueryBuilderProducer {
	@Inject
	private Logger log;

	private OrderQueryRepository orderQueryRepository;

	@PostConstruct
	public void postConstruct() {
		orderQueryRepository = new OrderQueryRepository();
		List<QueryLoader> queryLoaders = new ArrayList<>();
		XmlLoader xmlLoader = new XmlLoader();
		xmlLoader.initialize();
		queryLoaders.add(xmlLoader);
		if (StringUtils.isNotBlank(xmlLoader.basePackage())) {
			AnnotationLoader annotationLoader = new AnnotationLoader(xmlLoader.basePackage());
			queryLoaders.add(annotationLoader);
		}
		log.trace("Starting initialization of the model and query loader classes");
		// possible initialization
		for (QueryLoader queryLoader : queryLoaders) {
			queryLoader.initialize();
		}
		// read the model declarations
		for (QueryLoader queryLoader : queryLoaders) {
			if (queryLoader instanceof ModelLoader) {
				orderQueryRepository.addModels(queryLoader.getClass().getName(), ((ModelLoader) queryLoader).loadModels());
			}
		}
		// read the query declarations
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

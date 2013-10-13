package mh.dev.common.orderquery.core;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;
import javax.inject.Singleton;

import mh.dev.common.orderquery.OrderQueryBuilder;
import mh.dev.common.orderquery.annotation.OrderStateConfig;
import mh.dev.common.orderquery.core.exception.OrderQueryException;
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

	private static final String ORDER_QUERY_XML_LOCATION = "META-INF/orderquery.xml";

	@PostConstruct
	public void postConstruct() {
		List<QueryLoader> queryLoaders = new ArrayList<>();
		InputStream orderQueryFile = Thread.currentThread().getContextClassLoader().getResourceAsStream(ORDER_QUERY_XML_LOCATION);
		if (orderQueryFile != null) {
			orderQueryRepository = new OrderQueryRepository();
			XmlLoader xmlLoader = new XmlLoader(orderQueryFile);
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
		} else {
			throw new OrderQueryException("orderquery.xml could not be found in the META-INF folder");
		}
	}

	@Produces
	@OrderStateConfig(queryName = "")
	public OrderQueryBuilder produceOrderQueryBuilder(InjectionPoint injectionPoint) {
		if (orderQueryRepository != null) {
			return new OrderQueryBuilderImpl(orderQueryRepository);
		}
		return null;
	}
}

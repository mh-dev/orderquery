package mh.dev.common.orderquery.core.loader.annotation;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mh.dev.common.orderquery.annotation.OrderQueries;
import mh.dev.common.orderquery.core.loader.ModelLoader;
import mh.dev.common.orderquery.core.loader.QueryLoader;
import mh.dev.common.orderquery.core.model.OrderQuery;
import mh.dev.common.orderquery.core.model.OrderQueryColumn;
import mh.dev.common.orderquery.core.model.OrderQueryModel;

import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Loads the annotation configuration
 * 
 * @author Mathias Hauser
 * 
 */
public class AnnotationLoader implements ModelLoader, QueryLoader {
	private static final Logger log = LoggerFactory.getLogger(AnnotationLoader.class);
	private String basePackage;
	private Set<Class<?>> modelClasses = new HashSet<>();
	private Set<Class<?>> queryClasses = new HashSet<>();

	public AnnotationLoader(String basePackage) {
		this.basePackage = basePackage;
	}

	@Override
	public void initialize() {
		log.debug("Initialize the annotation configuration");
		Reflections reflections = new Reflections(basePackage);
		modelClasses = reflections.getTypesAnnotatedWith(mh.dev.common.orderquery.annotation.OrderQueryModel.class);
		queryClasses = reflections.getTypesAnnotatedWith(OrderQueries.class);
	}

	@Override
	public List<OrderQueryModel> loadModels() {
		List<OrderQueryModel> orderQueryModels = new ArrayList<>();
		for (Class<?> clazz : modelClasses) {
			OrderQueryModel orderQueryModel = new OrderQueryModel();
			mh.dev.common.orderquery.annotation.OrderQueryModel annotationModel = clazz
					.getAnnotation(mh.dev.common.orderquery.annotation.OrderQueryModel.class);
			String modelName = annotationModel.name();
			if (StringUtils.isBlank(modelName)) {
				modelName = StringUtils.uncapitalize(clazz.getSimpleName());
			}
			orderQueryModel.setName(modelName);
			orderQueryModel.setType(clazz);
			for (Field field : clazz.getFields()) {
				if (field.isAnnotationPresent(mh.dev.common.orderquery.annotation.OrderQueryColumn.class)) {
					OrderQueryColumn orderQueryColumn = new OrderQueryColumn();
					mh.dev.common.orderquery.annotation.OrderQueryColumn annotationColumn = field
							.getAnnotation(mh.dev.common.orderquery.annotation.OrderQueryColumn.class);
					String columnName = annotationColumn.name();
					if (StringUtils.isBlank(columnName)) {
						columnName = StringUtils.uncapitalize(field.getName());
					}
					orderQueryColumn.setName(columnName);
					orderQueryColumn.setQuery(annotationColumn.query());
				}
			}
			orderQueryModels.add(orderQueryModel);
		}
		return orderQueryModels;
	}

	@Override
	public List<OrderQuery> loadOrderQueries() {
		List<OrderQuery> orderQueries = new ArrayList<>();
		for (Class<?> clazz : queryClasses) {
			OrderQueries annotationOrderQueries = clazz.getAnnotation(OrderQueries.class);
			for (mh.dev.common.orderquery.annotation.OrderQuery annotationOrderQuery : annotationOrderQueries.value()) {
				OrderQuery orderQuery = new OrderQuery();
				orderQuery.setType(clazz);
				orderQuery.setName(annotationOrderQuery.name());
				orderQuery.setQuery(annotationOrderQuery.query());
				orderQuery.setDefaultColumn(annotationOrderQuery.defaultColumn());
				orderQuery.setDefaultOrder(annotationOrderQuery.defaultOrder());
				for (mh.dev.common.orderquery.annotation.OrderQueryColumn annotationOrderQueryColumn : annotationOrderQuery.orderQueryColumns()) {
					OrderQueryColumn orderQueryColumn = new OrderQueryColumn();
					orderQueryColumn.setName(annotationOrderQueryColumn.name());
					orderQueryColumn.setQuery(annotationOrderQueryColumn.query());
					orderQuery.getColumns().add(orderQueryColumn);
				}
				System.out.println(orderQuery);
				orderQueries.add(orderQuery);
			}
		}
		return orderQueries;
	}
}

package mh.dev.common.orderquery.core;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.enterprise.inject.Alternative;

import mh.dev.common.orderquery.Order;
import mh.dev.common.orderquery.OrderQueryBuilder;
import mh.dev.common.orderquery.OrderState;
import mh.dev.common.orderquery.annotation.OrderQueries;
import mh.dev.common.orderquery.annotation.OrderQuery;
import mh.dev.common.orderquery.annotation.OrderQueryColumn;
import mh.dev.common.orderquery.annotation.OrderQueryModel;
import mh.dev.common.orderquery.core.exception.OrderQueryException;
import mh.dev.common.orderquery.xml.XmlColumn;
import mh.dev.common.orderquery.xml.XmlModel;
import mh.dev.common.orderquery.xml.XmlOrderQueries;
import mh.dev.common.orderquery.xml.XmlOrderQuery;
import mh.dev.common.util.xml.XMLUtils;
import mh.dev.common.util.xml.exception.UnmarshalFailedException;

import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Loads the hole configuration for the order query module.<br>
 * Included xml and annotations bases configuration.
 * 
 * @author Mathias Hauser
 * 
 */
@Alternative
public class OrderQueryBuilderImpl implements OrderQueryBuilder {

	private Logger log = LoggerFactory.getLogger(OrderQueryBuilderImpl.class);

	// Column configuration
	/**
	 * modelName, columnNames
	 */
	private ConcurrentHashMap<String, List<String>> modelColumns = new ConcurrentHashMap<>();
	/**
	 * internal columnName, public column name
	 */
	private ConcurrentHashMap<String, String> definedColumnNames = new ConcurrentHashMap<>();
	/**
	 * columnName, jpqlQuery
	 */
	private ConcurrentHashMap<String, String> columnQueries = new ConcurrentHashMap<>();

	// Query configuration
	/**
	 * queryName, columnNames
	 */
	private ConcurrentHashMap<String, List<String>> queryColumns = new ConcurrentHashMap<>();
	/**
	 * queryName, jpqlQuery
	 */
	private ConcurrentHashMap<String, String> queries = new ConcurrentHashMap<>();

	// Model configuration
	/**
	 * class, modelName
	 */
	private ConcurrentHashMap<Class<?>, String> classes = new ConcurrentHashMap<>();

	private final static String MODEL_COLUMN = "model";
	private final static String QUERY_COLUMN = "query";

	public void load() {
		log.debug("Loading the configuration");
		// parsing orderquery.xml
		XmlOrderQueries xmlOrderQueries = xmlConfiguration();
		// load model column configuration
		loadModels(xmlOrderQueries.getModels(), orderQueryModelClasses(xmlOrderQueries.getBasePackage()));
		// load query configuration
		loadQueries(xmlOrderQueries.getOrderQueries(), orderQueriesClasses(xmlOrderQueries.getBasePackage()));
		log.info("OrderQuery is loaded");

	}

	@Override
	public String render(OrderState orderState) {
		if (orderState instanceof OrderStateImpl) {
			OrderStateImpl orderStateImpl = (OrderStateImpl) orderState;
			if (queries.containsKey(orderStateImpl.getQueryName())) {
				return appendOrderByStatement(queries.get(orderStateImpl.getQueryName()), orderStateImpl);
			} else {
				throw new OrderQueryException(String.format("Query %s does not exist", orderStateImpl.getQueryName()));
			}
		} else {
			throw new OrderQueryException("The orderState object has the wrong type - you may tried to use an own implementation which will not work!");
		}
	}

	/**
	 * Appends the the Order By statement to the a query if orderStateImpl has a order column
	 * 
	 * @param query
	 *            which should be append
	 * @param orderStateImpl
	 *            the current order configuration
	 * @return query appended with the order by statement
	 */
	private String appendOrderByStatement(String query, OrderStateImpl orderStateImpl) {
		StringBuilder builder = new StringBuilder(query);
		int orderColumns = orderStateImpl.orderedColumns().size();
		if (orderColumns > 0) {
			builder.append(" Order By ");
			for (int index = 0; index < orderColumns; index++) {
				String publicColumnName = orderStateImpl.orderedColumns().get(index);
				String columnName = orderStateImpl.internalColumnName(publicColumnName);
				builder.append(columnQueries.get(columnName)).append(" ").append(orderStateImpl.state(publicColumnName));
				if (index != (orderColumns - 1))
					builder.append(", ");
			}
		}
		log.trace(builder.toString());
		return builder.toString();
	}

	@Override
	public OrderState orderState(String queryName) {
		if (queryColumns.containsKey(queryName)) {
			ConcurrentHashMap<String, Order> orders = new ConcurrentHashMap<>();
			ConcurrentHashMap<String, String> internalColumns = new ConcurrentHashMap<>();
			for (String column : queryColumns.get(queryName)) {
				String publicColumnName = definedColumnNames.get(column);
				orders.put(publicColumnName, Order.NONE);
				internalColumns.put(publicColumnName, column);
			}
			return new OrderStateImpl(queryName, orders, internalColumns);
		} else {
			throw new OrderQueryException(String.format("Query %s does not exist", queryName));
		}
	}

	/**
	 * Loads the xml and annotation based model configuration
	 * 
	 * @param xmlModels
	 *            unmarshaled xml configuration
	 * @param orderQueryModelClasses
	 *            annotation based configured classes
	 */
	private void loadModels(List<XmlModel> xmlModels, Set<Class<?>> orderQueryModelClasses) {
		log.debug("Load OrderQuery models");
		log.debug("Load xml model configuration");
		// highest priority has the xml configuration
		for (XmlModel xmlModel : xmlModels) {
			Class<?> clazz = readClass(xmlModel.getType());
			// continue if the class exists otherwise print an error message bug and try to read the next model
			if (clazz != null) {
				String modelName = getModelName(xmlModel, clazz);
				// read the data from this model class if it is not configured
				if (!modelColumns.containsKey(modelName)) {
					log.debug(String.format("Add model %s with class %s to the index", modelName, clazz.getName()));
					classes.put(clazz, modelName);
					for (XmlColumn xmlColumn : xmlModel.getColumns()) {
						log.debug(String.format("Add column name %s with query %s to model %s", xmlColumn.getName(), xmlColumn.getQuery(), modelName));
						storeColumnData(modelName, xmlColumn.getName(), xmlColumn.getQuery());
					}
				} else {
					throw new OrderQueryException(String.format("Duplicate model definition for model  %s", modelName));
				}
			} else {
				throw new OrderQueryException(String.format("Class for type %s could not be found", xmlModel.getType()));
			}
		}
		log.debug("Load annotation model configuration");
		// read all annotation configured model classes
		for (Class<?> clazz : orderQueryModelClasses) {
			OrderQueryModel orderQueryModel = clazz.getAnnotation(OrderQueryModel.class);
			String modelName = getModelName(clazz);
			// read the configuration for this model class if it is not configured
			if (!modelColumns.containsKey(modelName)) {
				log.debug(String.format("Add model %s with class %s to the index", modelName, clazz.getName()));
				classes.put(clazz, modelName);
				for (OrderQueryColumn orderQueryColumn : orderQueryModel.orderQueryColumns()) {
					storeColumnData(modelName, orderQueryColumn.name(), orderQueryColumn.query());
				}
			} else if (!classes.containsKey(clazz) || !classes.get(clazz).equals(modelName)) {
				throw new OrderQueryException(String.format("Duplicate model definition for name %s", modelName));
			}
			// read the configuration from the fields if it is not configured
			for (Field field : clazz.getDeclaredFields()) {
				if (field.isAnnotationPresent(OrderQueryColumn.class)) {
					OrderQueryColumn orderQueryColumn = field.getAnnotation(OrderQueryColumn.class);
					String fieldName = StringUtils.isNotBlank(orderQueryColumn.name()) ? orderQueryColumn.name() : field.getName();
					log.debug(String.format("Add column name %s with query %s to model %s", fieldName, orderQueryColumn.query(), modelName));
					storeColumnData(modelName, fieldName, orderQueryColumn.query());
				}
			}
		}
	}

	private void loadQueries(List<XmlOrderQuery> xmlOrderQueries, Set<Class<?>> orderQueriesClasses) {
		log.debug("Load OrderQuery queries");
		log.debug("Load xml query configuration");
		// highest priority has the xml configuration
		for (XmlOrderQuery xmlOrderQuery : xmlOrderQueries) {
			if (StringUtils.isNotBlank(xmlOrderQuery.getName()) && !queryColumns.containsKey(xmlOrderQuery.getName())) {
				log.debug(String.format("Add orderquery name %s with query %s", xmlOrderQuery.getName(), xmlOrderQuery.getQuery()));
				queries.put(xmlOrderQuery.getName(), xmlOrderQuery.getQuery());
				queryColumns.putIfAbsent(xmlOrderQuery.getName(), new ArrayList<String>());
				// query order column configuration overrules model configuration so we put it in first
				for (XmlColumn xmlColumn : xmlOrderQuery.getColumns()) {
					storeQueryData(xmlOrderQuery.getName(), xmlColumn.getName(), xmlColumn.getQuery());
				}
				// now we add the model based configuration
				if (StringUtils.isNotBlank(xmlOrderQuery.getModel())) {
					if (modelColumns.containsKey(xmlOrderQuery.getModel())) {
						for (String columnName : modelColumns.get(xmlOrderQuery.getModel())) {
							queryColumns.putIfAbsent(xmlOrderQuery.getName(), new ArrayList<String>());
							if (!isColumnNameMapped(xmlOrderQuery.getName(), columnName)) {
								queryColumns.get(xmlOrderQuery.getName()).add(columnName);
							} else {
								log.info(String.format("Model column definition for column %s was overruled buy a query column definition",
										definedColumnNames.get(columnName)));
							}
						}
					} else {
						throw new OrderQueryException(String.format("Could not find model %s for query %s", xmlOrderQuery.getModel(), xmlOrderQuery.getName()));
					}
				}
			} else {
				throw new OrderQueryException(String.format("Duplicate or empty query definition for %s", xmlOrderQuery.getName()));
			}
		}
		log.debug("Load annotation query configuration");
		// load the annotation configuration
		for (Class<?> orderQueriesClass : orderQueriesClasses) {
			OrderQueries orderQueries = orderQueriesClass.getAnnotation(OrderQueries.class);
			for (OrderQuery orderQuery : orderQueries.value()) {
				if (StringUtils.isNotBlank(orderQuery.name()) && !queryColumns.containsKey(orderQuery.name())) {
					log.debug(String.format("Add orderquery name %s with query %s", orderQuery.name(), orderQuery.query()));
					queries.put(orderQuery.name(), orderQuery.query());
					queryColumns.putIfAbsent(orderQuery.name(), new ArrayList<String>());
					// query order column configuration overrules model configuration so we put it in first
					for (OrderQueryColumn orderQueryColumn : orderQuery.orderQueryColumns()) {
						storeQueryData(orderQuery.name(), orderQueryColumn.name(), orderQueryColumn.query());
					}
				} else {
					throw new OrderQueryException(String.format("Duplicate or empty query definition for %s", orderQuery.name()));
				}
				// load the model columns
				if (classes.containsKey(orderQueriesClass)) {
					String modelName = classes.get(orderQueriesClass);
					for (String columnName : modelColumns.get(modelName)) {
						queryColumns.putIfAbsent(modelName, new ArrayList<String>());
						if (!isColumnNameMapped(modelName, columnName)) {
							queryColumns.get(modelName).add(columnName);
						} else {
							log.info(String.format("Model column definition for column %s was overruled buy a query column definition",
									definedColumnNames.get(columnName)));
						}
					}
				}
			}
		}

	}

	/**
	 * Checks if the column name is already bounded to the orderQuery. <br>
	 * Keep in mind that columnName is not the name in the configuration. The internal framework adds a prefixes to ensure that we don't get false positive
	 * duplicates.
	 * 
	 * @param orderQueryName
	 *            name of the orderQuery
	 * @param columnName
	 *            name to check
	 * @return true if the original column name does already exist.
	 */
	private boolean isColumnNameMapped(String orderQueryName, String columnName) {
		for (String queryColumn : queryColumns.get(orderQueryName)) {
			if (definedColumnNames.get(queryColumn).equals(definedColumnNames.get(columnName))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Stores a column configuration for a specific model
	 * 
	 * @param modelName
	 *            name of the model
	 * @param fieldName
	 *            the name of the field which should be sortable
	 * @param query
	 *            rendered jpql query part
	 */
	private void storeColumnData(String modelName, String fieldName, String query) {
		String columnName = MODEL_COLUMN.concat(modelName).concat(fieldName);
		// we need this to ensure that we don't have a false positive duplicate
		if (StringUtils.isNotBlank(fieldName) && !modelColumns.containsKey(columnName)) {
			modelColumns.putIfAbsent(modelName, new ArrayList<String>());
			modelColumns.get(modelName).add(columnName);
			definedColumnNames.put(columnName, fieldName);
			columnQueries.put(columnName, query);
		} else {
			throw new OrderQueryException(String.format("Column definition for column %s of model %s is a duplicate or empty", fieldName, modelName));
		}
	}

	/**
	 * Stores a column configuration for a specific query
	 * 
	 * @param queryName
	 *            name of the query
	 * @param fieldName
	 *            the name of the field which should be sortable
	 * @param query
	 *            rendered jpql query part
	 */
	private void storeQueryData(String queryName, String fieldName, String query) {
		String columnName = QUERY_COLUMN.concat(queryName).concat(fieldName);
		if (StringUtils.isNotBlank(queryName) && !queryColumns.containsKey(columnName)) {
			queryColumns.putIfAbsent(queryName, new ArrayList<String>());
			queryColumns.get(queryName).add(columnName);
			definedColumnNames.put(columnName, fieldName);
			columnQueries.put(columnName, query);
		} else {
			throw new OrderQueryException(String.format("Duplicate column definition for column %s of query %s", fieldName, queryName));
		}
	}

	/**
	 * Reads or generates the model name. <br>
	 * Therefore it tries three ways to do this, but it only tries the next step if the step before does not lead to an actual name.
	 * <ol>
	 * <li>name attribute in the xml configuration of model</li>
	 * <li>name attribute of the {@link OrderQueryModel} annotation in the with type attribute configured class</li>
	 * <li>generates the model name bases on class.getSimpleName() with {@link StringUtils} uncapitalize</li>
	 * <ol>
	 * 
	 * @param xmlModel
	 *            xml configuration of the model we search the name for
	 * @param clazz
	 *            which is referenced from this given xml model
	 * @return name read of generated name of this model as described in the general description
	 */
	private String getModelName(XmlModel xmlModel, Class<?> clazz) {
		String name = xmlModel.getName();
		if (StringUtils.isBlank(name)) {
			name = getModelName(clazz);
		}
		return name;
	}

	/**
	 * Reads or generates the model name. <br>
	 * Therefore it tries two ways to do this, but it only tries the next step if the step before does not lead to an actual name.
	 * <ol>
	 * <li>name attribute of the {@link OrderQueryModel} annotation in the with type attribute configured class</li>
	 * <li>generates the model name bases on class.getSimpleName() with {@link StringUtils} uncapitalize</li>
	 * <ol>
	 * 
	 * @param clazz
	 *            which is referenced from this given xml model
	 * @return name read of generated name of this model as described in the general description
	 */
	private String getModelName(Class<?> clazz) {
		String name = null;
		if (clazz.isAnnotationPresent(OrderQueryModel.class)) {
			name = clazz.getAnnotation(OrderQueryModel.class).name();
		}
		if (StringUtils.isBlank(name)) {
			name = StringUtils.uncapitalize(clazz.getSimpleName());
		}
		return name;
	}

	/**
	 * Tries to load the class for the given fully qualified class name
	 * 
	 * @param fullyQualifiedClassName
	 *            name of the class inclusive package
	 * @return the class with the given fully qualified class name or null if could not be found.
	 */
	private Class<?> readClass(String fullyQualifiedClassName) {
		try {
			return Class.forName(fullyQualifiedClassName);
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

	/**
	 * Reads the xml configuration from META-INF/orderquery.xml
	 * 
	 * @return unmarshaled xml configuration
	 */
	private XmlOrderQueries xmlConfiguration() {
		XmlOrderQueries orderQueries = null;
		InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("META-INF/orderquery.xml");
		if (inputStream != null) {
			try {
				orderQueries = XMLUtils.unmarshal(inputStream, XmlOrderQueries.class);
			} catch (UnmarshalFailedException e) {
				throw new OrderQueryException(e);
			}
		}
		return orderQueries;
	}

	/**
	 * Searches and returns all classes annotated with {@link OrderQueryModel}
	 * 
	 * @return annotated classes
	 */
	private Set<Class<?>> orderQueryModelClasses(String basePackage) {
		return reflections(basePackage).getTypesAnnotatedWith(OrderQueryModel.class);
	}

	/**
	 * Searches and returns all classes annotated with {@link OrderQueries}
	 * 
	 * @return
	 */
	private Set<Class<?>> orderQueriesClasses(String basePackage) {
		return reflections(basePackage).getTypesAnnotatedWith(OrderQueries.class);
	}

	/**
	 * Generates a {@link Reflections} instance which uses the current {@link ClassLoader}
	 * 
	 * @return
	 */
	private Reflections reflections(String basePackage) {
		return new Reflections(basePackage);
	}
}

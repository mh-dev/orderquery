package mh.dev.common.orderquery.core.loader;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import mh.dev.common.orderquery.core.exception.OrderQueryException;
import mh.dev.common.orderquery.core.model.OrderQuery;
import mh.dev.common.orderquery.core.model.OrderQueryColumn;
import mh.dev.common.orderquery.core.model.OrderQueryModel;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrderQueryRepository {

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

	/**
	 * queryName, modelName
	 */
	private ConcurrentHashMap<String, String> queryModels = new ConcurrentHashMap<>();

	// Model configuration
	/**
	 * class, modelName
	 */
	private ConcurrentHashMap<Class<?>, String> classes = new ConcurrentHashMap<>();

	// Sources
	private List<String> sources = new ArrayList<>();

	private final static String MODEL_COLUMN = "model";
	private final static String QUERY_COLUMN = "query";

	private Logger log = LoggerFactory.getLogger(OrderQueryRepository.class);

	public void addModels(String source, List<OrderQueryModel> orderQueryModels) {
		log.info(String.format("load orderquery models from source %s", source));
		sources.add(source);
		for (OrderQueryModel orderQueryModel : orderQueryModels) {
			if (!classes.containsKey(orderQueryModel.getName()) || classes.get(orderQueryModel.getName()).equals(orderQueryModel.getType())) {
				log.info(String.format("Add model %s with class %s to the index", orderQueryModel.getName(), orderQueryModel.getType().getName()));
				classes.put(orderQueryModel.getType(), orderQueryModel.getName());
				for (OrderQueryColumn column : orderQueryModel.getColumns()) {
					log.info(String.format("Add column name %s with query %s to model %s", column.getName(), column.getQuery(), orderQueryModel.getName()));
					String columnName = MODEL_COLUMN.concat(source).concat(orderQueryModel.getName()).concat(column.getName());
					// we need this to ensure that we don't have a false positive duplicate
					if (StringUtils.isNotBlank(column.getName()) && !modelColumns.containsKey(columnName)) {
						if (!modelColumns.containsKey(orderQueryModel.getName())) {
							modelColumns.put(orderQueryModel.getName(), new ArrayList<String>());
						}
						modelColumns.get(orderQueryModel.getName()).add(columnName);
						definedColumnNames.put(columnName, column.getName());
						columnQueries.put(columnName, column.getQuery());
					} else {
						throw new OrderQueryException(String.format("Column definition for source %s at column %s of model %s is a duplicate or empty", source,
								column.getName(), orderQueryModel.getName()));
					}
				}
			} else {
				throw new OrderQueryException(String.format("Model with name %s is configured with different types -> class %s and %s",
						orderQueryModel.getName(), orderQueryModel.getType(), classes.get(orderQueryModel.getName())));
			}
		}
	}

	public void addQueries(String source, List<OrderQuery> orderQueries) {
		log.info(String.format("load orderquery queries from source %s", source));
		for (OrderQuery orderQuery : orderQueries) {
			if (StringUtils.isNotBlank(orderQuery.getName()) && !queryColumns.containsKey(orderQuery.getName())) {
				log.debug(String.format("Add orderquery name %s with query %s", orderQuery.getName(), orderQuery.getQuery()));
				if (StringUtils.isNotBlank(orderQuery.getModel()) && orderQuery.getType() != null) {
					String modelName = classes.get(orderQuery.getType());
					if (modelName.equals(orderQuery.getModel())) {
						queryModels.put(orderQuery.getName(), modelName);
					} else {
						throw new OrderQueryException(String.format("Inconsitent configuration for queryName %s with source %s modelName %s and type %s",
								orderQuery.getName(), source, modelName, orderQuery.getType().getName()));
					}
				} else if (StringUtils.isNotBlank(orderQuery.getModel())) {
					queryModels.put(orderQuery.getName(), orderQuery.getModel());
				} else if (orderQuery.getType() != null) {
					if (classes.containsKey(orderQuery.getType())) {
						queryModels.put(orderQuery.getName(), classes.get(orderQuery.getType()));
					} else {
						throw new OrderQueryException(String.format("Type %s for queryname %s with source %s could not be mapped to model configuration"));
					}
				}
				queries.put(orderQuery.getName(), orderQuery.getQuery());
				if (!queryColumns.containsKey(orderQuery.getName())) {
					queryColumns.put(orderQuery.getName(), new ArrayList<String>());
				}
				for (OrderQueryColumn orderQueryColumn : orderQuery.getColumns()) {
					String columnName = QUERY_COLUMN.concat(orderQuery.getName()).concat(orderQueryColumn.getName());
					if (StringUtils.isNotBlank(orderQuery.getName()) && !queryColumns.containsKey(columnName)) {
						queryColumns.get(orderQuery.getName()).add(columnName);
						definedColumnNames.put(columnName, orderQueryColumn.getName());
						columnQueries.put(columnName, orderQueryColumn.getQuery());
					} else {
						throw new OrderQueryException(String.format("Duplicate column definition for column %s of query %s", orderQueryColumn.getName(),
								orderQueryColumn.getQuery()));
					}
				}
				if (StringUtils.isNotBlank(orderQuery.getModel())) {
					if (modelColumns.containsKey(orderQuery.getModel())) {
						for (String columnName : modelColumns.get(orderQuery.getModel())) {
							if (!isColumnNameMapped(orderQuery.getName(), columnName)) {
								queryColumns.get(orderQuery.getName()).add(columnName);
							} else {
								log.info(String.format("Model column definition for column %s was overruled buy a query column definition",
										definedColumnNames.get(columnName)));
							}
						}
					} else {
						throw new OrderQueryException(String.format("Could not find model %s for query %s", orderQuery.getModel(), orderQuery.getName()));
					}
				}
			} else {
				throw new OrderQueryException(String.format("Duplicate or empty query definition for %s", orderQuery.getName()));
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

	public boolean queryExists(String queryName) {
		return queries.containsKey(queryName);
	}

	public String query(String queryName) {
		return queries.get(queryName);
	}

	public String columnQuery(String column) {
		return columnQueries.get(column);
	}

	public String definedColumn(String column) {
		return definedColumnNames.get(column);
	}

	public List<String> queryColumns(String queryName) {
		List<String> columns = new ArrayList<>();
		Set<String> mappedDefinedColumns = new HashSet<>();
		for (String column : queryColumns.get(queryName)) {
			String definedColumn = definedColumnNames.get(column);
			if (!mappedDefinedColumns.contains(definedColumn)) {
				mappedDefinedColumns.add(definedColumn);
				columns.add(column);
			}
		}
		String modelName = queryModels.get(queryName);
		for (String column : modelColumns.get(modelName)) {
			String definedColumn = definedColumnNames.get(column);
			if (!mappedDefinedColumns.contains(definedColumn)) {
				mappedDefinedColumns.add(definedColumn);
				columns.add(column);
			}
		}
		return columns;
	}
}

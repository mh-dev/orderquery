package mh.dev.common.orderquery.core.loader.xml;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import mh.dev.common.orderquery.core.exception.OrderQueryException;
import mh.dev.common.orderquery.core.loader.ConfigLoader;
import mh.dev.common.orderquery.core.loader.ModelLoader;
import mh.dev.common.orderquery.core.loader.xml.model.XmlColumn;
import mh.dev.common.orderquery.core.loader.xml.model.XmlModel;
import mh.dev.common.orderquery.core.loader.xml.model.XmlOrderQueries;
import mh.dev.common.orderquery.core.model.OrderQueryColumn;
import mh.dev.common.orderquery.core.model.OrderQueryModel;
import mh.dev.common.util.xml.XMLUtils;
import mh.dev.common.util.xml.exception.UnmarshalFailedException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Loads xml configuration from orderquery.xml file which must be in the META-INF folder
 * 
 * @author Mathias Hauser
 * 
 */
public class XmlLoader implements ModelLoader, ConfigLoader {

	private XmlOrderQueries xmlOrderQueries;

	private static final Logger log = LoggerFactory.getLogger(XmlLoader.class);
	private static final String ORDER_QUERY_XML_LOCATION = "META-INF/orderquery.xml";

	@Override
	public void initialize() {
		log.debug("Initialize the xml configuration");
		InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(ORDER_QUERY_XML_LOCATION);
		if (inputStream != null) {
			try {
				xmlOrderQueries = XMLUtils.unmarshal(inputStream, XmlOrderQueries.class);
			} catch (UnmarshalFailedException e) {
				throw new OrderQueryException(e);
			}
		} else {
			throw new OrderQueryException("orderquery.xml does not exist in the META-INF folder");
		}

	}

	@Override
	public List<OrderQueryModel> loadModel() {
		List<OrderQueryModel> orderQueryModels = new ArrayList<>();
		for (XmlModel xmlModel : xmlOrderQueries.getModels()) {
			OrderQueryModel orderQueryModel = new OrderQueryModel();
			try {
				Class<?> clazz = Class.forName(xmlModel.getType());
				String modelName = xmlModel.getName();
				if (StringUtils.isBlank(modelName)) {
					modelName = StringUtils.uncapitalize(clazz.getSimpleName());
				}
				orderQueryModel.setName(modelName);
				for (XmlColumn xmlColumn : xmlModel.getColumns()) {
					OrderQueryColumn orderQueryColumn = new OrderQueryColumn();
					orderQueryColumn.setName(xmlColumn.getName());
					orderQueryColumn.setQuery(xmlColumn.getQuery());
					orderQueryModel.add(orderQueryColumn);
				}

			} catch (ClassNotFoundException e) {
				throw new OrderQueryException(String.format("Class for model %s with type %s could not be found", xmlModel.getName(), xmlModel.getType()));
			}
		}
		return orderQueryModels;
	}

	@Override
	public String basePackage() {
		return xmlOrderQueries.getBasePackage();
	}
}

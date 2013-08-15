package mh.dev.common.orderquery.core.loader;

import java.util.List;

import mh.dev.common.orderquery.core.model.OrderQueryModel;

public interface ModelLoader extends OrderQueryLoader {

	List<OrderQueryModel> loadModel();
}

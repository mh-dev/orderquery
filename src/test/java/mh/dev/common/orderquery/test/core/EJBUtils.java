package mh.dev.common.orderquery.test.core;

import javax.ejb.SessionContext;

public class EJBUtils {

	@SuppressWarnings("unchecked")
	public static <T> T ejb(SessionContext sessionContext, Class<T> clazz) {
		String className = clazz.getSimpleName();
		return (T) sessionContext.lookup("java:global/orderQuery/".concat(className.substring(0, 1).concat(className.substring(1))));
	}
}

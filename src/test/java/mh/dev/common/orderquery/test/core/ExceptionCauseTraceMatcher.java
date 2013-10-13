package mh.dev.common.orderquery.test.core;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

public class ExceptionCauseTraceMatcher extends BaseMatcher<Object> {
	private Class<? extends Throwable>[] classes;

	@SafeVarargs
	public ExceptionCauseTraceMatcher(Class<? extends Throwable>... classes) {
		this.classes = classes;
	}

	@Override
	public boolean matches(Object item) {
		if (item instanceof Throwable) {
			for (Class<? extends Throwable> clazz : classes) {
				if (clazz.isInstance(item)) {
					item = ((Throwable) item).getCause();
				} else {
					boolean found = false;
					while (((Throwable) item).getCause() != null) {
						item = ((Throwable) item).getCause();
						if (clazz.isInstance(item)) {
							found = true;
							break;
						}
					}
					if (!found) {
						return false;
					}
				}
			}
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void describeTo(Description descr) {
		descr.appendText("unexpected exception");
	}
}
package mh.dev.common.orderquery.core.exception;

public class OrderQueryException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public OrderQueryException() {
		super();
	}

	public OrderQueryException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public OrderQueryException(String message, Throwable cause) {
		super(message, cause);
	}

	public OrderQueryException(String message) {
		super(message);
	}

	public OrderQueryException(Throwable cause) {
		super(cause);
	}

}

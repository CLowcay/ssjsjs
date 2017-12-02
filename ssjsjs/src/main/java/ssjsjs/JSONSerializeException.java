package ssjsjs;

/**
 * An error occurred when serializing to JSON.
 * */
public class JSONSerializeException extends Exception {
	public JSONSerializeException() {
		super();
	}

	public JSONSerializeException(final String message) {
		super(message);
	}

	public JSONSerializeException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public JSONSerializeException(final Throwable cause) {
		super(cause);
	}
}


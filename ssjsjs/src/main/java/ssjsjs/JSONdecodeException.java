package ssjsjs;

/**
 * An error occurred when attempting to deserialize JSON.
 * */
public class JSONdecodeException extends Exception {
	public JSONdecodeException() {
		super();
	}

	public JSONdecodeException(final String message) {
		super(message);
	}

	public JSONdecodeException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public JSONdecodeException(final Throwable cause) {
		super(cause);
	}
}


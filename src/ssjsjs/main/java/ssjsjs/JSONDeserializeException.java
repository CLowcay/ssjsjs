package ssjsjs;

/**
 * An error occurred when attempting to deserialize JSON.
 * */
public class JSONDeserializeException extends Exception {
	public JSONDeserializeException() {
		super();
	}

	public JSONDeserializeException(final String message) {
		super(message);
	}

	public JSONDeserializeException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public JSONDeserializeException(final Throwable cause) {
		super(cause);
	}
}


package ssjsjs;

/**
 * An error occurred when serializing to JSON.
 * */
public class JSONencodeException extends Exception {
	public JSONencodeException() {
		super();
	}

	public JSONencodeException(final String message) {
		super(message);
	}

	public JSONencodeException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public JSONencodeException(final Throwable cause) {
		super(cause);
	}
}


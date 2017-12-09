package ssjsjs.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mark a constructor argument as being supplied from the environment during
 * deserialization, and not part of the serialized output.
 * */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Implicit {
	/**
	 * The key by which this value will be looked up during deserialization.
	 * */
	String value();
}


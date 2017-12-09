package ssjsjs.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specify the name of the field that supplies the value for this constructor
 * parameter.
 * */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Field {
	/**
	 * The name of the field that supplies the value when serializing.
	 * */
	String value();
}


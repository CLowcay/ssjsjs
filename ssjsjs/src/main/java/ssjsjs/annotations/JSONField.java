package ssjsjs.annotations;

/**
 * Serialize this field.
 * @param alias the label to use for this field in the JSON output
 * @param nullable true if this field is allowed to be null, otherwise false
 * @param defaultValue the default value to use when deserializing this field
 * */
public @interface JSONField {
	String alias();
	boolean nullable() default false;
	String defaultValue();
}


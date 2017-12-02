package ssjsjs.annotations;

/**
 * Serialize this field.
 * @param alias the label to use for this field in the JSON output
 * @param nullable true if this field is allowed to be null, otherwise false
 * @param defaultValue the default value to use when deserializing this field
 * */
@interface JSONField {
	String alias() default null;
	boolean nullable() default false;
	Object defaultValue() default null;
}


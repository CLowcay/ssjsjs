package ssjsjs.annotations;

/**
 * Do not serialize this field
 * @param defaultValue the default value to apply to this field when deserializing
 * */
public @interface JSONIgnore {
	String defaultValue();
}


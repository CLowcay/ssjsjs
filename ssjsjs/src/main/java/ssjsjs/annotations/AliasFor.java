package ssjsjs.annotations;

/**
 * Mark a parameter name as an alias for a field (used during serialization).
 * */
public @interface AliasFor {
	String field();
}


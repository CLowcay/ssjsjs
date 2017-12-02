package ssjsjs.annotations;

import java.util.Collection;

/**
 * Serialize this collection field.
 * @param alias the label to use for this field in the JSON output
 * @param use the concrete class used to instantiate this field when deserializing
 * */
public @interface JSONCollection {
	String alias();
	Class<? extends Collection> use();
}


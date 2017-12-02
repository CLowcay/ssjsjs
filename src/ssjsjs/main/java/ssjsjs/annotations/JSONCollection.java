package ssjsjs.annotations;

import java.util.Collection;

/**
 * Serialize this collection field.
 * @param alias the label to use for this field in the JSON output
 * @param use the concrete class used to instantiate this field when deserializing
 * */
@interface JSONCollection {
	String alias() default null;
	Class<? extends Collection> use() default null;
}


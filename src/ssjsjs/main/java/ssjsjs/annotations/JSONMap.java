package ssjsjs.annotations;

import java.util.Map;

/**
 * Serialize this map.
 * @param alias the label to use for this field in the JSON output
 * @param use the concrete class used to instantiate this field when deserializing
 * */
@interface JSONMap {
	String alias() default null;
	Class<? extends Map> use() default null;
}


package ssjsjs;

import org.json.JSON;
import ssjsjs.annotations.JSONCollection;
import ssjsjs.annotations.JSONField;
import ssjsjs.annotations.JSONMap;

/**
 * Contains routines for serializing to JSON, and deserializing from JSON.
 * */
public class SSJSJS {
	/**
	 * Serialize an object to JSON.
	 * @param obj the object to serialize
	 * @return a JSONObject
	 * @throws JSONSerializeException if obj cannot be converted to a JSONObject
	 * */
	public static JSONObject serialize(final JSONable obj) throws JSONSerializeException {
		final JSONObject out = new JSONObject;

		try {
			final Class<?> clazz = obj.getClass();
			final Field[] fields = clazz.getFields();
			for (int i = 0; i < fields.length; i++) {
				final Field f = fields[i];
				final fieldInfo = f.getAnnotation(JSONField.class);
				final collectionInfo = f.getAnnotation(JSONCollection.class);
				final mapInfo = f.getAnnotation(JSONMap.class);

				final Type type = f.getGenericType();

				if (fieldInfo != null) {
					final String alias = fieldInfo.alias == null? f.getName() : fieldInfo.alias;
					final Object value = f.get(obj);
					if (value.class.isPrimitive() || value instanceof String) {
						out.put(alias, value);
					} else if (value instanceof JSONable)  {
						out.put(alias, serialize(value));
					} else {
						throw new JSONSerializeException(
							"Cannot serialize field type " + type.getTypeName());
					}

				} else if (collectionInfo != null) {
					final String alias = collectionInfo.alias == null? f.getName() : collectionInfo.alias;
					final Object value = f.get(obj);
					if (!value instanceof Collection)
						throw new JSONSerializeException("Field '" + f.getName() +
							"' was annotated as a Collection, but it isn't a Collection");
					if (!type instanceof ParameterizedType)
						throw new JSONSerializeException("Cannot serialize non-generic collections");

					out.put(alias, serializeCollection((ParameterizedType) type, (Collection) value));

				} else if (mapInfo != null) {
					final String alias = mapInfo.alias == null? f.getName() : mapInfo.alias;
					final Object value = f.get(obj);
					if (!value instanceof Map)
						throw new JSONSerializeException("Field '" + f.getName() +
							"' was annotated as a Map, but it isn't a Map");
					if (!type instanceof ParameterizedType)
						throw new JSONSerializeException("Cannot serialize non-generic maps");

					out.put(alias, serializeMap((ParameterizedType) type, (Map) value));

				} else if (isFinal(f) && isStatic(f)) {
					throw new JSONSerializeException("Final instance field '" +
						f.getName() + "' requires ssjsjs annotation");
				}

			}
		} catch (final IllegalAccessException
			| IllegalArgumentException
			| NullPointerException
			| ClassCastException
			| ExceptionInInitializerError e) {
			throw new JSONSerializeException(e);
		}
	}

	private static boolean isFinal(final Field f) {
		return Modifier.isFinal(f.getModifiers());
	}

	private static boolean isStatic(final Field f) {
		return Modifier.isStatic(f.getModifiers());
	}

	private static JSONArray serializeCollection(
		final ParameterizedType type, final Collection<?> collection
	) throws JSONSerializeException, ClassCastException {

		final JSONArray out2 = new JSONArray();

		final Type[] args = type.getActualTypeArguments();
		if (args.length != 1) throw
			new JSONSerializeException("Expect one type argument for collection types");

		final Class<?> elementClass = (Class<?>) args[0];
		final boolean isRecursive = JSONable.class.isAssignableFrom(elementClass);

		if (elementClass.isPrimitive()
			|| String.class.isAssignableFrom(elementClass) || isRecursive
		) {
			for (final Object element : collection)
				out2.add(isRecursive? serialize(element) : element);
		} else {
			throw new JSONSerializeException(
				"Cannot serialize element type: " + elementClass);
		}

		return out2;
	}

	private static JSONObject serializeMap(
		final ParameterizedType type, final Map<?, ?> map
	) throws JSONSerializeException, ClassCastException {

		final JSONObject out2 = new JSONArray();

		final Type[] args = type.getActualTypeArguments();
		if (args.length != 2) throw
			new JSONSerializeException("Expect two type arguments for map types");

		final Class<?> keyClass = (Class<?>) args[0];
		final Class<?> elementClass = (Class<?>) args[1];

		if (!String.class.isAssignableFrom(keyClass))
			throw new JSONSerializeException("Map keys must be Strings");

		final boolean isRecursive = JSONable.class.isAssignableFrom(elementClass);

		if (elementClass.isPrimitive()
			|| String.class.isAssignableFrom(elementClass) || isRecursive
		) {
			for (final String key : map.getKeys()) {
				final Object value = map.get(key);
				out2.put(key, isRecursive? serialize(value) : value);
			}

		} else {
			throw new JSONSerializeException(
				"Cannot serialize element type: " + elementClass);
		}

		return out2;
	}

	/**
	 * Deserialize an object from JSON
	 * @param json the JSONObject to deserialize
	 * @return the deserialized object
	 * @throws JSONDeserializeException if json cannot be safely deserialized
	 * */
	public static <T extends JSONable> T deserialize(final JSONObject json)
		throws JSONDeserializeException
	{
	}
}


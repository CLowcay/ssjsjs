package ssjsjs;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;
import ssjsjs.annotations.AliasFor;
import ssjsjs.annotations.JSONConstructor;

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
		final JSONObject out = new JSONObject();

		try {
			final Class<?> clazz = obj.getClass();
			final Constructor<?> constructor = getJSONConstructor(clazz);
			final Parameter[] parameters = constructor.getParameters();

			for (int i = 0; i < parameters.length; i++) {
				final Parameter p = parameters[i];
				final String name = p.getName();
				final Type type = p.getParameterizedType();

				final AliasFor alias = p.getAnnotation(AliasFor.class);
				final String fieldName = alias == null? p.getName() : alias.field();
				final Field f = clazz.getField(fieldName);

				if (f.getType().isAssignableFrom(p.getType())
					&& p.getType().isAssignableFrom(f.getType())
				) {
					final Object value = f.get(obj);

					if (value instanceof Collection) {
						if (!((Class<?>) type).isAssignableFrom(Collection.class))
							throw new JSONSerializeException("Collection parameters must be declared Collection<T>");

						if (!(type instanceof ParameterizedType))
							throw new JSONSerializeException("Cannot serialize non-generic collections");

						out.put(name,
							serializeCollection((ParameterizedType) type, (Collection) value));

					} else if (value instanceof Map) {
						if (!((Class<?>) type).isAssignableFrom(Map.class))
							throw new JSONSerializeException("Map parameters must be declared Map<T>");

						if (!(type instanceof ParameterizedType))
							throw new JSONSerializeException("Cannot serialize non-generic maps");

						out.put(name, serializeMap((ParameterizedType) type, (Map) value));

					} else if (value instanceof JSONable) {
						out.put(name, serialize((JSONable) value));

					} else if (value.getClass().isPrimitive() || value instanceof String) {
						out.put(name, value);

					} else {
						throw new JSONSerializeException(
							"Cannot serialize field type " + f.getGenericType().getTypeName());
					}
				} else {
					throw new JSONSerializeException("Type mismatch error, field '" +
						f.getName() + "' is not type compatible with constructor argument '" +
						p.getName() + "'");
				}
			}

			return out;
		} catch (final IllegalAccessException
			| IllegalArgumentException
			| NoSuchFieldException
			| SecurityException
			| NullPointerException
			| ClassCastException
			| ExceptionInInitializerError e) {
			throw new JSONSerializeException(e);
		}
	}

	/**
	 * Deserialize an object from JSON
	 * @param json the JSONObject to deserialize
	 * @return the deserialized object
	 * @throws JSONDeserializeException if json cannot be safely deserialized
	 * */
	public static <T extends JSONable> T deserialize(
		final JSONObject json, final Class<T> clazz
	) throws JSONDeserializeException
	{
		try {
			final Constructor<T> constructor = getJSONConstructor(clazz);
			final Parameter[] parameters = constructor.getParameters();
			final Object[] values = new Object[parameters.length];

			for (int i = 0; i < parameters.length; i++) {
				final Parameter p = parameters[i];
				values[i] = deserializeField(
					p.getName(),
					(Class<?>) p.getType(),
					p.getParameterizedType(),
					json.get(p.getName()));
			}

			return constructor.newInstance(values);

		} catch (final SecurityException
			| InstantiationException
			| IllegalAccessException
			| IllegalArgumentException
			| InvocationTargetException
			| JSONSerializeException e) {
			throw new JSONDeserializeException(e);
		}
	}


	private static boolean isFinal(final Field f) {
		return Modifier.isFinal(f.getModifiers());
	}

	private static boolean isStatic(final Field f) {
		return Modifier.isStatic(f.getModifiers());
	}

	private static <T> Constructor<T> getJSONConstructor(Class<T> clazz)
		throws JSONSerializeException, SecurityException
	{
			final Constructor<?>[] constructors = clazz.getConstructors();

			Constructor<?> cOut = null;
			for (int i = 0; i < constructors.length; i++) {
				final Constructor<?> c = constructors[i];
				if (c.getAnnotation(JSONConstructor.class) != null) {
					cOut = c;
					break;
				}
			}

			if (cOut == null) throw new JSONSerializeException(
				"No constructor found with the JSONConstructor annotation");
			@SuppressWarnings("unchecked") final Constructor<T> constructor =
				(Constructor<T>) cOut;

			return constructor;
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
				out2.put(isRecursive? serialize((JSONable) element) : element);
		} else {
			throw new JSONSerializeException(
				"Cannot serialize element type: " + elementClass);
		}

		return out2;
	}

	private static JSONObject serializeMap(
		final ParameterizedType type, final Map<?, ?> map0
	) throws JSONSerializeException, ClassCastException {

		final JSONObject out2 = new JSONObject();

		final Type[] args = type.getActualTypeArguments();
		if (args.length != 2) throw
			new JSONSerializeException("Expect two type arguments for map types");

		final Class<?> keyClass = (Class<?>) args[0];
		final Class<?> elementClass = (Class<?>) args[1];

		if (!String.class.isAssignableFrom(keyClass))
			throw new JSONSerializeException("Map keys must be Strings");

		@SuppressWarnings("unchecked") final Map<String, ?> map = (Map<String, ?>) map0;

		final boolean isRecursive = JSONable.class.isAssignableFrom(elementClass);

		if (elementClass.isPrimitive()
			|| String.class.isAssignableFrom(elementClass) || isRecursive
		) {
			for (final String key : map.keySet()) {
				final Object value = map.get(key);
				out2.put(key, isRecursive? serialize((JSONable) value) : value);
			}

		} else {
			throw new JSONSerializeException(
				"Cannot serialize element type: " + elementClass);
		}

		return out2;
	}

	private static Object deserializeField(
		final String fieldName,
		final Class<?> intendedClass,
		final Type intendedType,
		final Object value
	) throws JSONDeserializeException {
		if (value == null || intendedClass.isInstance(value)) {
			return value;

		} else if (value instanceof Number) {
			if (intendedClass.isAssignableFrom(Byte.class)) {
				return ((Number) value).byteValue();
			} else if (intendedClass.isAssignableFrom(Double.class)) {
				return ((Number) value).doubleValue();
			} else if (intendedClass.isAssignableFrom(Float.class)) {
				return ((Number) value).floatValue();
			} else if (intendedClass.isAssignableFrom(Integer.class)) {
				return ((Number) value).intValue();
			} else if (intendedClass.isAssignableFrom(Long.class)) {
				return ((Number) value).longValue();
			} else if (intendedClass.isAssignableFrom(Short.class)) {
				return ((Number) value).shortValue();
			} else {
				throw new JSONDeserializeException("Expected a '" +
					intendedClass + "' for field '" + fieldName + "', but got a Number");
			}

		} else if (value instanceof JSONObject
			&& JSONable.class.isAssignableFrom(intendedClass)
		) {
			@SuppressWarnings("unchecked") final Class<JSONable> deserializeAs =
				(Class<JSONable>) intendedClass;
			return (Object) deserialize((JSONObject) value, deserializeAs);

		} else if (value instanceof JSONArray
			&& intendedClass.isAssignableFrom(Collection.class)
		) {
			if (intendedType instanceof ParameterizedType) {
				final Type[] typeArgs = ((ParameterizedType) intendedType).getActualTypeArguments();
				if (typeArgs.length != 1) throw new JSONDeserializeException(
					"Expected exactly 1 type argument for '" + fieldName + "' field");

				final Type innerType = typeArgs[0];

				final Collection<Object> array = new ArrayList<>();
				for (final Object innerValue : (JSONArray) value) {
					array.add(deserializeField(
						fieldName + "[]",
						(Class<?>) innerType,
						innerType,
						innerValue));
				}

				return array;

			} else {
				throw new JSONDeserializeException(
					"Required generic type for '" + fieldName + "' field");
			}

		} else if (value instanceof JSONObject
			&& intendedClass.isAssignableFrom(Map.class)
		) {
			if (intendedType instanceof ParameterizedType) {
				final Type[] typeArgs = ((ParameterizedType) intendedType).getActualTypeArguments();
				if (typeArgs.length != 2) throw new JSONDeserializeException(
					"Expected exactly 2 type arguments for '" + fieldName + "' field");

				final Type keyType = typeArgs[0];
				final Type innerType = typeArgs[1];

				if (!((Class<?>) keyType).isAssignableFrom(String.class))
					throw new JSONDeserializeException(
						"Cannot deserialize maps with non-string keys in field '" + fieldName + "'");

				final JSONObject object = (JSONObject) value;
				final Map<String, Object> map = new HashMap<>();
				for (final String key : object.keySet()) {
					map.put(key, deserializeField(
						fieldName + "{}",
						(Class<?>) innerType,
						innerType,
						object.get(key)));
				}

				return map;

			} else {
				throw new JSONDeserializeException(
					"Required generic type for '" + fieldName + "' field");
			}

		} else {
			throw new JSONDeserializeException("Cannot deserialize field '" +
				fieldName + "' of type '" + fieldName + "' from object of type '" +
				value.getClass() + "' (did you forget to make it JSONable?)");
		}
	}
}


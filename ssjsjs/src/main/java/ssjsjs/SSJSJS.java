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
import ssjsjs.annotations.Alias;
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
				final Type type = p.getParameterizedType();

				final Alias alias = p.getAnnotation(Alias.class);
				if (alias == null) throw new JSONSerializeException(
					"Missing required @Alias annotation for field " + p.getName());

				final String fieldName = alias.value();
				final Field f = clazz.getField(fieldName);

				final Object value = f.get(obj);

				if (value instanceof Collection) {
					if (!p.getType().isAssignableFrom(Collection.class))
						throw new JSONSerializeException("Collection parameters must be declared Collection<T>");

					if (!(type instanceof ParameterizedType))
						throw new JSONSerializeException("Cannot serialize non-generic collections");

					out.put(fieldName,
						serializeCollection((ParameterizedType) type, (Collection) value));

				} else if (value instanceof Map) {
					if (!p.getType().isAssignableFrom(Map.class))
						throw new JSONSerializeException("Map parameters must be declared Map<T>");

					if (!(type instanceof ParameterizedType))
						throw new JSONSerializeException("Cannot serialize non-generic maps");

					out.put(fieldName, serializeMap((ParameterizedType) type, (Map) value));

				} else if (value instanceof JSONable) {
					out.put(fieldName, serialize((JSONable) value));

				} else if (isJSONPrimitive(f.getType())) {
					out.put(fieldName, makeJSONPrimitive(value));

				} else {
					throw new JSONSerializeException(
						"Cannot serialize field '" + fieldName + "' of type " + f.getGenericType().getTypeName());
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

	private static boolean isJSONPrimitive(final Class<?> clazz) {
		return 
			(Number.class.isAssignableFrom(clazz)) ||
			(Character.class.isAssignableFrom(clazz)) ||
			(Boolean.class.isAssignableFrom(clazz)) ||
			(String.class.isAssignableFrom(clazz)) ||
			clazz == byte.class ||
			clazz == char.class ||
			clazz == short.class ||
			clazz == int.class ||
			clazz == long.class ||
			clazz == float.class ||
			clazz == double.class ||
			clazz == boolean.class;
	}

	private static Object makeJSONPrimitive(final Object obj) {
		if (obj instanceof Byte) return obj;
		else if (obj instanceof Character) return Character.toString(((Character) obj).charValue());
		else if (obj instanceof Short) return obj;
		else if (obj instanceof Integer) return obj;
		else if (obj instanceof Long) return obj;
		else if (obj instanceof Float) return obj;
		else if (obj instanceof Double) return obj;
		else if (obj instanceof Boolean) return obj;
		else if (obj instanceof String) return obj;
		else return null;
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

				final Alias alias = p.getAnnotation(Alias.class);
				if (alias == null) throw new JSONDeserializeException(
					"Missing required @Alias annotation for parameter " + p.getName());

				final String fieldName = alias.value();

				values[i] = deserializeField(
					fieldName,
					(Class<?>) p.getType(),
					p.getParameterizedType(),
					json.opt(fieldName));
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

	private static <T> Constructor<T> getJSONConstructor(final Class<T> clazz)
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

		if (isJSONPrimitive(elementClass) || isRecursive) {
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

		if (isJSONPrimitive(elementClass) || isRecursive) {
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

		} else if (value instanceof String &&
			((String) value).length() == 1 &&
			(intendedClass.isAssignableFrom(Character.class) || intendedClass.isAssignableFrom(char.class))
		) {
			return ((String) value).charAt(0);

		} else if (value instanceof Boolean &&
			(intendedClass.isAssignableFrom(Boolean.class) || intendedClass.isAssignableFrom(boolean.class))
		) {
			return value;

		} else if (value instanceof Number) {
			if (intendedClass.isAssignableFrom(Byte.class) || intendedClass.isAssignableFrom(byte.class)) {
				return ((Number) value).byteValue();
			} else if (intendedClass.isAssignableFrom(Double.class) || intendedClass.isAssignableFrom(double.class)) {
				return ((Number) value).doubleValue();
			} else if (intendedClass.isAssignableFrom(Float.class) || intendedClass.isAssignableFrom(float.class)) {
				return ((Number) value).floatValue();
			} else if (intendedClass.isAssignableFrom(Integer.class) || intendedClass.isAssignableFrom(int.class)) {
				return ((Number) value).intValue();
			} else if (intendedClass.isAssignableFrom(Long.class) || intendedClass.isAssignableFrom(long.class)) {
				return ((Number) value).longValue();
			} else if (intendedClass.isAssignableFrom(Short.class) || intendedClass.isAssignableFrom(short.class)) {
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


package ssjsjs;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONObject;
import ssjsjs.annotations.As;
import ssjsjs.annotations.Implicit;
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

				final ssjsjs.annotations.Field alias = p.getAnnotation(ssjsjs.annotations.Field.class);
				if (alias == null) {
					if (p.isAnnotationPresent(Implicit.class)) continue;

					throw new JSONSerializeException(
						"Missing required @Field  or @Implicit annotation for field " + p.getName());
				}

				final String fieldName = alias.value();

				final As as  = p.getAnnotation(As.class);
				final String outputFieldName = as == null? fieldName : as.value();

				if (out.has(outputFieldName)) throw new JSONSerializeException(
					"Duplicate field name: " + outputFieldName);

				final Field f = clazz.getDeclaredField(fieldName);
				f.setAccessible(true);

				final Object value = f.get(obj);

				if (value instanceof Collection) {
					if (!p.getType().isAssignableFrom(Collection.class))
						throw new JSONSerializeException("Collection parameters must be declared Collection<T>");

					if (!(type instanceof ParameterizedType))
						throw new JSONSerializeException("Cannot serialize non-generic collections");

					out.put(outputFieldName,
						serializeCollection((ParameterizedType) type, (Collection) value));

				} else if (value instanceof Map) {
					if (!p.getType().isAssignableFrom(Map.class))
						throw new JSONSerializeException("Map parameters must be declared Map<T>");

					if (!(type instanceof ParameterizedType))
						throw new JSONSerializeException("Cannot serialize non-generic maps");

					out.put(outputFieldName, serializeMap((ParameterizedType) type, (Map) value));

				} else if (value instanceof Optional) {
					if (!(type instanceof ParameterizedType))
						throw new JSONSerializeException("Cannot serialize non-generic optionals");

						final Type[] args = ((ParameterizedType) type).getActualTypeArguments();
						if (args.length != 1) throw
							new JSONSerializeException("Expect one type argument for optional types");

						@SuppressWarnings("unchecked")
						final Object innerValue = ((Optional<Object>) value).orElse(null);
						final Class<?> elementClass = (Class<?>) args[0];

						// swap the null types
						if (innerValue == null) {
							/* do nothing */
						} else if (isJSONPrimitive(elementClass)) {
							out.put(outputFieldName, innerValue);
						} else if (elementClass.isEnum()) {
							out.put(outputFieldName, innerValue.toString());
						} else if (JSONable.class.isAssignableFrom(elementClass)) {
							out.put(outputFieldName, serialize((JSONable) innerValue));
						} else {
							throw new JSONSerializeException(
								"Cannot serialize collection element type: " + elementClass);
						}

				} else if (value instanceof JSONable) {
					out.put(outputFieldName, serialize((JSONable) value));

				} else if (value == null || isJSONPrimitive(f.getType())) {
					out.put(outputFieldName, makeJSONPrimitive(value));

				} else if (value.getClass().isEnum()) {
					out.put(outputFieldName, value.toString());

				} else if (value.getClass().isArray()) {
					final Class<?> elementType = value.getClass().getComponentType();
					out.put(outputFieldName, serializeArray(value, elementType));

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
	 * @param class the class to deserialize as
	 * @return the deserialized object
	 * @throws JSONDeserializeException if json cannot be safely deserialized
	 * */
	public static <T extends JSONable> T deserialize(
		final JSONObject json, final Class<T> clazz
	) throws JSONDeserializeException {
		return deserialize(json, clazz, null);
	}

	/**
	 * Deserialize an object from JSON
	 * @param json the JSONObject to deserialize
	 * @param class the class to deserialize as
	 * @param environment a global environment that supplies the values of implicit fields (may be null)
	 * @return the deserialized object
	 * @throws JSONDeserializeException if json cannot be safely deserialized
	 * */
	public static <T extends JSONable> T deserialize(
		final JSONObject json, final Class<T> clazz, final Map<String, Object> environment
	) throws JSONDeserializeException
	{
		if (!JSONable.class.isAssignableFrom(clazz)) throw new JSONDeserializeException(
			"Cannot deserialize object of type " + clazz);

		final Set<String> seen = new HashSet<>();

		try {
			final Constructor<T> constructor = getJSONConstructor(clazz);
			final Parameter[] parameters = constructor.getParameters();
			final Object[] values = new Object[parameters.length];

			for (int i = 0; i < parameters.length; i++) {
				final Parameter p = parameters[i];

				final ssjsjs.annotations.Field alias = p.getAnnotation(ssjsjs.annotations.Field.class);
				if (alias == null) {
					final Implicit envVariable = p.getAnnotation(Implicit.class);
					if (envVariable != null) {
						values[i] = environment == null? null : environment.get(envVariable.value());
						if (values[i] == null) {
							throw new JSONDeserializeException(
								"Missing value for implicit field '" + envVariable.value() + "'");
						} else if (!autoUnboxTypeMatch(p.getType(), values[i].getClass())) {
							throw new JSONDeserializeException(
								"Wrong type for implicit field '" + envVariable.value() +
								"', expected a " + p.getType().getTypeName() +
								" but got a " + values[i].getClass().getTypeName());
						} else {
							continue;
						}
					} else {
						throw new JSONDeserializeException(
							"Missing required @Field  or @Implicit annotation for parameter " + p.getName());
					}
				}

				final As as = p.getAnnotation(As.class);
				final String fieldName = as == null? alias.value() : as.value();

				if (seen.contains(fieldName)) throw new JSONDeserializeException(
					"Duplicate field '" + fieldName + "' in class '" + clazz + "'");
				seen.add(fieldName);

				values[i] = deserializeField(
					fieldName,
					(Class<?>) p.getType(),
					p.getParameterizedType(),
					json.opt(fieldName),
					environment);
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

	private static boolean autoUnboxTypeMatch(final Class<?> intended, final Class<?> actual) {
		return 
			intended.isAssignableFrom(actual) ||
			(intended == boolean.class && Boolean.class.isAssignableFrom(actual)) ||
			(intended == char.class && Character.class.isAssignableFrom(actual)) ||
			(intended == byte.class && Byte.class.isAssignableFrom(actual)) ||
			(intended == short.class && Short.class.isAssignableFrom(actual)) ||
			(intended == int.class && Integer.class.isAssignableFrom(actual)) ||
			(intended == long.class && Long.class.isAssignableFrom(actual)) ||
			(intended == float.class && Float.class.isAssignableFrom(actual)) ||
			(intended == double.class && Double.class.isAssignableFrom(actual));
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

	private static JSONArray serializeArray(
		final Object array, final Class<?> elementClass
	) throws JSONSerializeException, ClassCastException {
		final JSONArray out = new JSONArray();

		List<Object> collection;
		if (array instanceof byte[]) {
			final byte[] a = (byte[]) array;
			for (int i = 0; i < a.length; i++) out.put(a[i]);
		} else if (array instanceof char[]) {
			final char[] a = (char[]) array;
			for (int i = 0; i < a.length; i++) out.put(String.valueOf(a[i]));
		} else if (array instanceof short[]) {
			final short[] a = (short[]) array;
			for (int i = 0; i < a.length; i++) out.put(a[i]);
		} else if (array instanceof int[]) {
			final int[] a = (int[]) array;
			for (int i = 0; i < a.length; i++) out.put(a[i]);
		} else if (array instanceof long[]) {
			final long[] a = (long[]) array;
			for (int i = 0; i < a.length; i++) out.put(a[i]);
		} else if (array instanceof float[]) {
			final float[] a = (float[]) array;
			for (int i = 0; i < a.length; i++) out.put(a[i]);
		} else if (array instanceof double[]) {
			final double[] a = (double[]) array;
			for (int i = 0; i < a.length; i++) out.put(a[i]);
		} else if (array instanceof boolean[]) {
			final boolean[] a = (boolean[]) array;
			for (int i = 0; i < a.length; i++) out.put(a[i]);
		} else {
			final Object[] a = (Object[]) array;
			if (isJSONPrimitive(elementClass)) {
				for (int i = 0; i < a.length; i++) out.put(a[i]);
			} else if (elementClass.isEnum()) {
				for (int i = 0; i < a.length; i++) out.put(a[i].toString());
			} else if (JSONable.class.isAssignableFrom(elementClass)) {
				for (int i = 0; i < a.length; i++) out.put(serialize((JSONable) a[i]));
			} else {
				throw new JSONSerializeException(
					"Cannot serialize array element type: " + elementClass);
			}
		}

		return out;
	}

	private static JSONArray serializeCollection(
		final ParameterizedType type, final Collection<?> collection
	) throws JSONSerializeException, ClassCastException {

		final JSONArray out2 = new JSONArray();

		final Type[] args = type.getActualTypeArguments();
		if (args.length != 1) throw
			new JSONSerializeException("Expect one type argument for collection types");

		final Class<?> elementClass = (Class<?>) args[0];

		if (isJSONPrimitive(elementClass)) {
			for (final Object element : collection) out2.put(element);
		} else if (elementClass.isEnum()) {
			for (final Object element : collection) out2.put(element.toString());
		} else if (JSONable.class.isAssignableFrom(elementClass)) {
			for (final Object element : collection) out2.put(serialize((JSONable) element));
		} else {
			throw new JSONSerializeException(
				"Cannot serialize collection element type: " + elementClass);
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

		if (isJSONPrimitive(elementClass)) {
			for (final String key : map.keySet()) out2.put(key, map.get(key));
		} else if (elementClass.isEnum()) {
			for (final String key : map.keySet()) out2.put(key, map.get(key).toString());
		} else if (JSONable.class.isAssignableFrom(elementClass)) {
			for (final String key : map.keySet()) out2.put(key, serialize((JSONable) map.get(key)));
		} else {
			throw new JSONSerializeException(
				"Cannot serialize map element type: " + elementClass);
		}

		return out2;
	}

	private static Object deserializeField(
		final String fieldName,
		final Class<?> intendedClass,
		final Type intendedType,
		final Object value,
		final Map<String, Object> environment
	) throws JSONDeserializeException {
		if (Optional.class.isAssignableFrom(intendedClass)) {
			if (value == null || value == JSONObject.NULL) return Optional.empty();
			else {
				final Type[] typeArgs = ((ParameterizedType) intendedType).getActualTypeArguments();
				if (typeArgs.length != 1) throw new JSONDeserializeException(
					"Expected exactly 1 type argument for '" + fieldName + "' field");
				final Type innerType = typeArgs[0];

				return Optional.of(deserializeField(
					fieldName + "___OptionalValue__",
					(Class<?>) innerType,
					innerType,
					value, environment));
			}
			
		} else if (value == null || value == JSONObject.NULL || intendedClass.isInstance(value)) {
			if (!(
				JSONable.class.isAssignableFrom(intendedClass) ||
				String.class.isAssignableFrom(intendedClass) ||
				Byte.class.isAssignableFrom(intendedClass) ||
				Short.class.isAssignableFrom(intendedClass) ||
				Integer.class.isAssignableFrom(intendedClass) ||
				Long.class.isAssignableFrom(intendedClass) ||
				Double.class.isAssignableFrom(intendedClass) ||
				Float.class.isAssignableFrom(intendedClass) ||
				Boolean.class.isAssignableFrom(intendedClass) ||
				Character.class.isAssignableFrom(intendedClass) ||
				intendedClass.isAssignableFrom(Collection.class) ||
				intendedClass.isAssignableFrom(Map.class) ||
				intendedClass.isEnum() || intendedClass.isArray()
			)) throw new JSONDeserializeException(
				"Cannot deserialize field '" + fieldName + "' of type '" + intendedClass + "'");

			return value == JSONObject.NULL? null : value;

		} else if (value instanceof String) {
			if (((String) value).length() == 1 &&
				(intendedClass.isAssignableFrom(Character.class)
					|| intendedClass.isAssignableFrom(char.class))
			) {
				return ((String) value).charAt(0);
			} else if (intendedClass.isEnum()) {
				try {
					@SuppressWarnings("unchecked") final Object r =
						Enum.valueOf((Class) intendedClass, (String) value);
					return r;
				} catch (final IllegalArgumentException e) {
					throw new JSONDeserializeException(e);
				}
			} else {
				return value;
			}

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
			return (Object) deserialize((JSONObject) value, deserializeAs, environment);

		} else if (value instanceof JSONArray
			&& (intendedClass.isAssignableFrom(Collection.class) || intendedClass.isArray())
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
						innerValue, environment));
				}

				return array;

			} else if (((Class<?>) intendedType).isArray()) {
				final List<Object> array = new ArrayList<>();
				final Class<?> elementClass = ((Class<?>) intendedType).getComponentType();

				for (final Object innerValue : (JSONArray) value) {
					array.add(deserializeField(
						fieldName + "[]",
						elementClass,
						elementClass,
						innerValue, environment));
				}

				if (elementClass.isAssignableFrom(byte.class)) {
					final byte[] r = new byte[array.size()];
					for (int i = 0; i < r.length; i++) r[i] = (Byte) array.get(i);
					return r;
				} else if (elementClass.isAssignableFrom(char.class)) {
					final char[] r = new char[array.size()];
					for (int i = 0; i < r.length; i++) r[i] = (Character) array.get(i);
					return r;
				} else if (elementClass.isAssignableFrom(short.class)) {
					final short[] r = new short[array.size()];
					for (int i = 0; i < r.length; i++) r[i] = (Short) array.get(i);
					return r;
				} else if (elementClass.isAssignableFrom(int.class)) {
					final int[] r = new int[array.size()];
					for (int i = 0; i < r.length; i++) r[i] = (Integer) array.get(i);
					return r;
				} else if (elementClass.isAssignableFrom(long.class)) {
					final long[] r = new long[array.size()];
					for (int i = 0; i < r.length; i++) r[i] = (Long) array.get(i);
					return r;
				} else if (elementClass.isAssignableFrom(float.class)) {
					final float[] r = new float[array.size()];
					for (int i = 0; i < r.length; i++) r[i] = (Float) array.get(i);
					return r;
				} else if (elementClass.isAssignableFrom(double.class)) {
					final double[] r = new double[array.size()];
					for (int i = 0; i < r.length; i++) r[i] = (Double) array.get(i);
					return r;
				} else if (elementClass.isAssignableFrom(boolean.class)) {
					final boolean[] r = new boolean[array.size()];
					for (int i = 0; i < r.length; i++) r[i] = (Boolean) array.get(i);
					return r;
				} else {
					try {
						final Object[] outArray = (Object[]) Array.newInstance(elementClass, array.size());
						for (int i = 0; i < outArray.length; i++) outArray[i] = array.get(i);
						return outArray;

					} catch (final IllegalArgumentException e) {
						throw new JSONDeserializeException(
							"Cannot create array of type '" + intendedType.getTypeName() +
							"' for field '" + fieldName + "'");
					}
				}

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
						object.get(key),
						environment));
				}

				return map;

			} else {
				throw new JSONDeserializeException(
					"Required generic type for '" + fieldName + "' field");
			}

		} else {
			throw new JSONDeserializeException("Cannot deserialize field '" +
				fieldName + "' of type '" + intendedType.getTypeName() + "' from object of type '" +
				value.getClass() + "' (did you forget to make it JSONable?)");
		}
	}
}


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
import ssjsjs.annotations.JSON;
import ssjsjs.annotations.Nullable;

/**
 * Contains routines for serializing to JSON, and deserializing from JSON.
 * */
public class SSJSJS {
	/**
	 * Serialize an object to JSON.
	 * @param obj the object to serialize
	 * @return a JSONObject
	 * @throws JSONencodeException if obj cannot be converted to a JSONObject
	 * */
	public static JSONObject encode(final JSONable obj) throws JSONencodeException {
		final JSONObject out = new JSONObject();

		try {
			final Class<?> clazz = obj.getClass();
			final Constructor<?> constructor = getJSON(clazz);
			final Parameter[] parameters = constructor.getParameters();

			for (int i = 0; i < parameters.length; i++) {
				final Parameter p = parameters[i];
				final Type type = p.getParameterizedType();

				final ssjsjs.annotations.Field alias = p.getAnnotation(ssjsjs.annotations.Field.class);
				if (alias == null) {
					if (p.isAnnotationPresent(Implicit.class)) continue;

					throw new JSONencodeException(
						"Missing required @Field  or @Implicit annotation for field " + p.getName());
				}

				final String fieldName = alias.value();

				final As as  = p.getAnnotation(As.class);
				final String outputFieldName = as == null? fieldName : as.value();

				if (out.has(outputFieldName)) throw new JSONencodeException(
					"Duplicate field name: " + outputFieldName);

				final boolean nullable = p.getAnnotation(Nullable.class) != null;

				final Field f = getAnyField(clazz, fieldName);
				f.setAccessible(true);

				try {
					final Object sval = serializeField(f.get(obj), type, nullable);
					if (sval != null) out.put(outputFieldName, sval);

				} catch (final JSONencodeException e) {
					throw new JSONencodeException(
						"Cannot serialize field '" + fieldName +
						"' of type " + f.getGenericType().getTypeName() +
						" because " + e.getMessage());
				}
			}

			return out;
		} catch (final IllegalAccessException
			| IllegalArgumentException
			| SecurityException
			| NoSuchFieldException
			| NullPointerException
			| ClassCastException
			| ExceptionInInitializerError e) {
			throw new JSONencodeException(e);
		}
	}

	private static Field getAnyField(final Class<?> clazz, final String name)
		throws SecurityException, NoSuchFieldException, NullPointerException
	{
		try {
			return clazz.getDeclaredField(name);
		} catch (final NoSuchFieldException e) {
			final Class<?> spr = clazz.getSuperclass();
			if (spr == null) throw e; else return getAnyField(spr, name);
		}
	}

	private static boolean isCollection(final Object value) {
		return
			value instanceof Collection ||
			value instanceof List ||
			value instanceof Set;
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

	private static Object serializeField(
		final Object value, final Type type, final boolean nullable
	) throws JSONencodeException
	{
		if (value == null || value == JSONObject.NULL) {
			if (!nullable) throw new NullPointerException();
			return null;

		} else if (isCollection(value)) {
			if (!(type instanceof ParameterizedType))
				throw new JSONencodeException("Cannot serialize non-generic collections");
			return serializeCollection((ParameterizedType) type, (Collection<?>) value);

		} else if (value instanceof Map) {
			if (!(type instanceof ParameterizedType))
				throw new JSONencodeException("Cannot serialize non-generic maps");

			return serializeMap((ParameterizedType) type, (Map) value);

		} else if (value instanceof Optional) {
			if (!(type instanceof ParameterizedType))
				throw new JSONencodeException("Cannot serialize non-generic optionals");

				final Type[] args = ((ParameterizedType) type).getActualTypeArguments();
				if (args.length != 1) throw
					new JSONencodeException("Expect one type argument for optional types");

				@SuppressWarnings("unchecked")
				final Object innerValue = ((Optional<Object>) value).orElse(null);
				final Type elementType = args[0];

				return serializeField(innerValue, elementType, true);

		} else if (value instanceof JSONable) {
			return encode((JSONable) value);

		} else if (isJSONPrimitive(value.getClass())) {
			return makeJSONPrimitive(value);

		} else if (value.getClass().isEnum()) {
			return value.toString();

		} else if (value.getClass().isArray()) {
			final Class<?> elementType = value.getClass().getComponentType();
			return serializeArray(value, elementType);

		} else {
			throw new JSONencodeException("Cannot serialize fields of type '" +
				value.getClass().getTypeName() + "'");
		}
	}

	/**
	 * Deserialize an object from JSON
	 * @param json the JSONObject to deserialize
	 * @param class the class to deserialize as
	 * @return the deserialized object
	 * @throws JSONdecodeException if json cannot be safely deserialized
	 * */
	public static <T extends JSONable> T decode(
		final JSONObject json, final Class<T> clazz
	) throws JSONdecodeException {
		return decode(json, clazz, null);
	}

	/**
	 * Deserialize an object from JSON
	 * @param json the JSONObject to deserialize
	 * @param class the class to deserialize as
	 * @param environment a global environment that supplies the values of implicit fields (may be null)
	 * @return the deserialized object
	 * @throws JSONdecodeException if json cannot be safely deserialized
	 * */
	public static <T extends JSONable> T decode(
		final JSONObject json, final Class<T> clazz, final Map<String, Object> environment
	) throws JSONdecodeException
	{
		if (!JSONable.class.isAssignableFrom(clazz)) throw new JSONdecodeException(
			"Cannot deserialize object of type " + clazz);

		final Set<String> seen = new HashSet<>();

		try {
			final Constructor<T> constructor = getJSON(clazz);
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
							throw new JSONdecodeException(
								"Missing value for implicit field '" + envVariable.value() + "'");
						} else if (!autoUnboxTypeMatch(p.getType(), values[i].getClass())) {
							throw new JSONdecodeException(
								"Wrong type for implicit field '" + envVariable.value() +
								"', expected a " + p.getType().getTypeName() +
								" but got a " + values[i].getClass().getTypeName());
						} else {
							continue;
						}
					} else {
						throw new JSONdecodeException(
							"Missing required @Field  or @Implicit annotation for parameter " + p.getName());
					}
				}

				final As as = p.getAnnotation(As.class);
				final String fieldName = as == null? alias.value() : as.value();

				final boolean nullable = p.getAnnotation(Nullable.class) != null;

				if (seen.contains(fieldName)) throw new JSONdecodeException(
					"Duplicate field '" + fieldName + "' in class '" + clazz + "'");
				seen.add(fieldName);

				values[i] = deserializeField(
					fieldName,
					(Class<?>) p.getType(),
					p.getParameterizedType(),
					json.opt(fieldName),
					environment,
					nullable);
			}

			return constructor.newInstance(values);

		} catch (final SecurityException
			| InstantiationException
			| IllegalAccessException
			| IllegalArgumentException
			| InvocationTargetException
			| JSONencodeException e) {
			throw new JSONdecodeException(e);
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

	private static <T> Constructor<T> getJSON(final Class<T> clazz)
		throws JSONencodeException, SecurityException
	{
			final Constructor<?>[] constructors = clazz.getConstructors();

			Constructor<?> cOut = null;
			for (int i = 0; i < constructors.length; i++) {
				final Constructor<?> c = constructors[i];
				if (c.getAnnotation(JSON.class) != null) {
					cOut = c;
					break;
				}
			}

			if (cOut == null) throw new JSONencodeException(
				"No constructor found with the JSON annotation");
			@SuppressWarnings("unchecked") final Constructor<T> constructor =
				(Constructor<T>) cOut;

			return constructor;
	}

	private static JSONArray serializeArray(
		final Object array, final Class<?> elementClass
	) throws JSONencodeException, ClassCastException {
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
				for (int i = 0; i < a.length; i++) out.put(encode((JSONable) a[i]));
			} else {
				throw new JSONencodeException(
					"Cannot serialize array element type: " + elementClass);
			}
		}

		return out;
	}

	private static JSONArray serializeCollection(
		final ParameterizedType type, final Collection<?> collection
	) throws JSONencodeException, ClassCastException {

		final JSONArray out2 = new JSONArray();

		final Type[] args = type.getActualTypeArguments();
		if (args.length != 1) throw
			new JSONencodeException("Expect one type argument for collection types");

		final Type elementType = args[0];

		try {
			for (final Object element : collection)
				out2.put(serializeField(element, elementType, true));
		} catch (final JSONencodeException e) {
			throw new JSONencodeException(
				"Cannot serialize collection element type: " + elementType);
		}

		return out2;
	}

	private static JSONObject serializeMap(
		final ParameterizedType type, final Map<?, ?> map0
	) throws JSONencodeException, ClassCastException {

		final JSONObject out2 = new JSONObject();

		final Type[] args = type.getActualTypeArguments();
		if (args.length != 2) throw
			new JSONencodeException("Expect two type arguments for map types");

		final Type keyType = args[0];
		final Type elementType = args[1];

		try {
			if (!String.class.isAssignableFrom((Class<?>) keyType))
				throw new JSONencodeException("Map keys must be Strings");
		} catch (final ClassCastException e) {
			throw new JSONencodeException("Map keys must be Strings");
		}

		@SuppressWarnings("unchecked") final Map<String, ?> map = (Map<String, ?>) map0;

		try {
			for (final String key : map.keySet())
				out2.put(key, serializeField(map.get(key), elementType, true));
		} catch (final JSONencodeException e) {
			throw new JSONencodeException(
				"Cannot serialize map element type: " + elementType);
		}

		return out2;
	}

	private static Class<?> typeToClass(final Type type) throws ClassCastException {
		if (type instanceof ParameterizedType) {
			return (Class<?>) ((ParameterizedType) type).getRawType();
		} else {
			return (Class<?>) type;
		}
	}

	private static Object deserializeField(
		final String fieldName,
		final Class<?> intendedClass,
		final Type intendedType,
		final Object value,
		final Map<String, Object> environment,
		final boolean nullable
	) throws JSONdecodeException {
		if (Optional.class.isAssignableFrom(intendedClass)) {
			if (value == null || value == JSONObject.NULL) return Optional.empty();
			else {
				final Type[] typeArgs = ((ParameterizedType) intendedType).getActualTypeArguments();
				if (typeArgs.length != 1) throw new JSONdecodeException(
					"Expected exactly 1 type argument for '" + fieldName + "' field");
				final Type innerType = typeArgs[0];

				try {
				return Optional.of(deserializeField(
					fieldName + "___OptionalValue__",
					typeToClass(innerType),
					innerType,
					value, environment, true));
				} catch (final ClassCastException e) {
					throw new JSONdecodeException("Java reflection error", e);
				}
			}
			
		} else if (value == null || value == JSONObject.NULL) {
			if (!nullable) throw new JSONdecodeException(
				"Cannot deserialize non-nullable field '" + fieldName + "' of type '"
				+ intendedClass + "'. Input JSON has no value for this field.");

			return null;

		} else if (intendedClass.isInstance(value)) {
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
			)) throw new JSONdecodeException(
				"Cannot deserialize field '" + fieldName + "' of type '" + intendedClass + "'");

			return value;

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
					throw new JSONdecodeException(e);
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
				throw new JSONdecodeException("Expected a '" +
					intendedClass + "' for field '" + fieldName + "', but got a Number");
			}

		} else if (value instanceof JSONObject
			&& JSONable.class.isAssignableFrom(intendedClass)
		) {
			@SuppressWarnings("unchecked") final Class<JSONable> deserializeAs =
				(Class<JSONable>) intendedClass;
			return (Object) decode((JSONObject) value, deserializeAs, environment);

		} else if (value instanceof JSONArray &&
			(intendedClass.isAssignableFrom(List.class) ||
			 intendedClass.isAssignableFrom(Set.class) ||
			intendedClass.isArray())
		) {
			if (intendedType instanceof ParameterizedType) {
				final Type[] typeArgs = ((ParameterizedType) intendedType).getActualTypeArguments();
				if (typeArgs.length != 1) throw new JSONdecodeException(
					"Expected exactly 1 type argument for '" + fieldName + "' field");

				final Type innerType = typeArgs[0];

				final List<Object> array = new ArrayList<>();
				for (final Object innerValue : (JSONArray) value) {
					try {
						array.add(deserializeField(
							fieldName + "[]",
							typeToClass(innerType),
							innerType,
							innerValue, environment, true));
					} catch (final ClassCastException e) {
						throw new JSONdecodeException("Java reflection error", e);
					}
				}

				if (intendedClass.isAssignableFrom(Set.class)) {
					return new HashSet<>(array);
				} else {
					return array;
				}

			} else if (((Class<?>) intendedType).isArray()) {
				final List<Object> array = new ArrayList<>();
				final Class<?> elementClass = ((Class<?>) intendedType).getComponentType();

				for (final Object innerValue : (JSONArray) value) {
					array.add(deserializeField(
						fieldName + "[]",
						elementClass,
						elementClass,
						innerValue, environment, true));
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
						throw new JSONdecodeException(
							"Cannot create array of type '" + intendedType.getTypeName() +
							"' for field '" + fieldName + "'");
					}
				}

			} else {
				throw new JSONdecodeException(
					"Required generic type for '" + fieldName + "' field");
			}

		} else if (value instanceof JSONObject
			&& intendedClass.isAssignableFrom(Map.class)
		) {
			if (intendedType instanceof ParameterizedType) {
				final Type[] typeArgs = ((ParameterizedType) intendedType).getActualTypeArguments();
				if (typeArgs.length != 2) throw new JSONdecodeException(
					"Expected exactly 2 type arguments for '" + fieldName + "' field");

				final Type keyType = typeArgs[0];
				final Type innerType = typeArgs[1];

				if (!((Class<?>) keyType).isAssignableFrom(String.class))
					throw new JSONdecodeException(
						"Cannot deserialize maps with non-string keys in field '" + fieldName + "'");

				final JSONObject object = (JSONObject) value;
				final Map<String, Object> map = new HashMap<>();
				for (final String key : object.keySet()) {
					try {
						map.put(key, deserializeField(
							fieldName + "{}",
							typeToClass(innerType),
							innerType,
							object.get(key),
							environment, true));
					} catch (final ClassCastException e) {
						throw new JSONdecodeException("Java reflection error", e);
					}
				}

				return map;

			} else {
				throw new JSONdecodeException(
					"Required generic type for '" + fieldName + "' field");
			}

		} else {
			throw new JSONdecodeException("Cannot deserialize field '" +
				fieldName + "' of type '" + intendedType.getTypeName() + "' from object of type '" +
				value.getClass() + "' (did you forget to make it JSONable?)");
		}
	}
}


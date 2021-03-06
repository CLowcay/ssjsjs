package ssjsjs.test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import ssjsjs.JSONable;
import ssjsjs.JSONdecodeException;
import ssjsjs.JSONencodeException;
import ssjsjs.SSJSJS;
import static org.junit.Assert.*;

public class SSJSJSTest {
	@Test
	public void emptyClass() throws Exception {
		final Empty obj = new Empty();
		final JSONObject out = SSJSJS.encode(obj);
		assertEquals(0, out.length());
	}

	@Test
	public void primitivesRoundtrip() throws Exception {
		final Primitives obj = new Primitives((byte) 0);
		final Primitives obj2 = SSJSJS.decode(SSJSJS.encode(obj), Primitives.class);
		assertEquals(obj, obj2);
	}

	@Test
	public void primitivesLongRoundtrip() throws Exception {
		final Primitives obj = new Primitives((byte) 0);
		final Primitives obj2 = SSJSJS.decode(
			new JSONObject(SSJSJS.encode(obj).toString()), Primitives.class);
		assertEquals(obj, obj2);
	}

	@Test
	public void primitivesOutput() throws Exception {
		final Primitives obj = new Primitives((byte) 0);
		final JSONObject out = SSJSJS.encode(obj);
		assertTrue(out.opt("byteVal") instanceof Number);
		assertTrue(out.opt("charVal") instanceof String);
		assertTrue(out.opt("shortVal") instanceof Number);
		assertTrue(out.opt("intVal") instanceof Number);
		assertTrue(out.opt("longVal") instanceof Number);
		assertTrue(out.opt("floatVal") instanceof Number);
		assertTrue(out.opt("doubleVal") instanceof Number);
		assertTrue(out.opt("booleanVal") instanceof Boolean);
		assertTrue(out.opt("stringVal1") instanceof String);
		assertNull(out.opt("stringVal2"));

		assertEquals(obj.byteVal,    ((Number) out.get("byteVal")).byteValue());
		assertEquals(Character.toString(obj.charVal), (String) out.get("charVal"));
		assertEquals(obj.shortVal,   ((Number) out.get("shortVal")).shortValue());
		assertEquals(obj.intVal,     ((Number) out.get("intVal")).intValue());
		assertEquals(obj.longVal,    ((Number) out.get("longVal")).longValue());
		assertEquals(obj.floatVal,   ((Number) out.get("floatVal")).floatValue(), 0.00000001);
		assertEquals(obj.doubleVal,  ((Number) out.get("doubleVal")).doubleValue(), 0.00000001);
		assertEquals(obj.booleanVal, (Boolean) out.get("booleanVal"));
		assertEquals(obj.stringVal1, (String) out.get("stringVal1"));
	}

	@Test
	public void collectionsRoundtrip() throws Exception {
		final WithCollections obj = new WithCollections(0);
		final WithCollections obj2 = SSJSJS.decode(SSJSJS.encode(obj), WithCollections.class);
		assertEquals(obj, obj2);
	}

	@Test
	public void collectionsLongRoundtrip() throws Exception {
		final WithCollections obj = new WithCollections(0);
		final WithCollections obj2 = SSJSJS.decode(
			new JSONObject(SSJSJS.encode(obj).toString()), WithCollections.class);
		assertEquals(obj, obj2);
	}

	@Test
	public void collectionsOutput() throws Exception {
		final WithCollections obj = new WithCollections(0);
		final JSONObject out = SSJSJS.encode(obj);

		assertTrue(out.get("emptyList") instanceof JSONArray);
		assertTrue(out.get("numbers") instanceof JSONArray);
		assertTrue(out.get("strings") instanceof JSONArray);
		assertTrue(out.get("primitives1") instanceof JSONArray);
		assertTrue(out.get("primitives2") instanceof JSONArray);
	}

	@Test
	public void mapsRoundtrip() throws Exception {
		final WithMaps obj = new WithMaps(0);
		final WithMaps obj2 = SSJSJS.decode(SSJSJS.encode(obj), WithMaps.class);
		assertEquals(obj, obj2);

		final WithMaps obj3 = new WithMaps(42);
		final WithMaps obj4 = SSJSJS.decode(SSJSJS.encode(obj3), WithMaps.class);
		assertEquals(obj3, obj4);
	}

	@Test
	public void mapsLongRoundtrip() throws Exception {
		final WithMaps obj = new WithMaps(0);
		final WithMaps obj2 = SSJSJS.decode(
			new JSONObject(SSJSJS.encode(obj).toString()), WithMaps.class);
		assertEquals(obj, obj2);

		final WithMaps obj3 = new WithMaps(42);
		final WithMaps obj4 = SSJSJS.decode(
			new JSONObject(SSJSJS.encode(obj3).toString()), WithMaps.class);
		assertEquals(obj3, obj4);
	}

	@Test
	public void emptyBoxes() throws Exception {
		final EmptyBoxes obj = new EmptyBoxes();
		final EmptyBoxes obj2 = SSJSJS.decode(SSJSJS.encode(obj), EmptyBoxes.class);
		assertEquals(obj, obj2);
	}

	@Test
	public void customLabelsRoundtrip() throws Exception {
		final CustomLabels obj = new CustomLabels();
		final CustomLabels obj2 = SSJSJS.decode(SSJSJS.encode(obj), CustomLabels.class);
		assertEquals(obj, obj2);
	}

	@Test
	public void customLabelsLongRoundtrip() throws Exception {
		final CustomLabels obj = new CustomLabels();
		final CustomLabels obj2 = SSJSJS.decode(
			new JSONObject(SSJSJS.encode(obj).toString()), CustomLabels.class);
		assertEquals(obj, obj2);
	}

	@Test
	public void customLabelsOutput() throws Exception {
		final CustomLabels obj = new CustomLabels();
		final JSONObject out = SSJSJS.encode(obj);

		assertTrue(out.has("a"));
		assertTrue(out.has("custom"));
		assertFalse(out.has("b"));

		assertTrue(out.get("a") instanceof String);
		assertTrue(out.get("custom") instanceof String);

		assertEquals(obj.a, out.get("a"));
		assertEquals(obj.b, out.get("custom"));
	}

	@Test
	public void enumFieldsRoundtrip() throws Exception {
		final WithEnums obj1 = new WithEnums(WithEnums.SomeEnum.SOME_VALUE);
		final WithEnums obj2 = SSJSJS.decode(SSJSJS.encode(obj1), WithEnums.class);
		assertEquals(obj1, obj2);

		final WithEnums obj3 = new WithEnums(WithEnums.SomeEnum.SOME_OTHER_VALUE);
		final WithEnums obj4 = SSJSJS.decode(SSJSJS.encode(obj3), WithEnums.class);
		assertEquals(obj3, obj4);
	}

	@Test
	public void enumFieldsLongRoundtrip() throws Exception {
		final WithEnums obj1 = new WithEnums(WithEnums.SomeEnum.SOME_VALUE);
		final WithEnums obj2 = SSJSJS.decode(
			new JSONObject(SSJSJS.encode(obj1).toString()), WithEnums.class);
		assertEquals(obj1, obj2);

		final WithEnums obj3 = new WithEnums(WithEnums.SomeEnum.SOME_OTHER_VALUE);
		final WithEnums obj4 = SSJSJS.decode(
			new JSONObject(SSJSJS.encode(obj3).toString()), WithEnums.class);
		assertEquals(obj3, obj4);
	}

	@Test
	public void optionalFieldsRoundtrip() throws Exception {
		final WithOptionals obj1 = new WithOptionals(true);
		final WithOptionals obj2 = SSJSJS.decode(SSJSJS.encode(obj1), WithOptionals.class);
		System.err.println("optional fields json: " + SSJSJS.encode(obj1));
		assertEquals(obj1, obj2);

		final WithOptionals obj3 = new WithOptionals(false);
		final WithOptionals obj4 = SSJSJS.decode(SSJSJS.encode(obj3), WithOptionals.class);
		assertEquals(obj3, obj4);
	}

	@Test
	public void optionalFieldsLongRoundtrip() throws Exception {
		final WithOptionals obj1 = new WithOptionals(true);
		final WithOptionals obj2 = SSJSJS.decode(
			new JSONObject(SSJSJS.encode(obj1).toString()), WithOptionals.class);
		assertEquals(obj1, obj2);

		final WithOptionals obj3 = new WithOptionals(false);
		final WithOptionals obj4 = SSJSJS.decode(
			new JSONObject(SSJSJS.encode(obj3).toString()), WithOptionals.class);
		assertEquals(obj3, obj4);
	}

	@Test(expected = JSONdecodeException.class)
	public void cannotDeserializeBadEnumValue() throws Exception {
		final JSONObject json = new JSONObject();
		json.put("enumField", "NOT_A_VALID_VALUE");
		SSJSJS.decode(json, WithEnums.class);
	}

	@Test
	public void privateFieldsRoundtrip() throws Exception {
		final WithPrivateFields obj = new WithPrivateFields("whatever");
		final WithPrivateFields obj2 = SSJSJS.decode(SSJSJS.encode(obj), WithPrivateFields.class);
		assertEquals(obj, obj2);
	}

	@Test
	public void implicitRoundtrip() throws Exception {
		final Map<String, Object> env = new HashMap<>();
		final WierdType wt = new WierdType("example");
		env.put("fromEnv", wt);
		final ImplicitFields obj = new ImplicitFields("whatever", wt,
			Optional.of(new ImplicitFields("whatever 2", wt, Optional.empty())));
		final ImplicitFields obj2 = SSJSJS.decode(SSJSJS.encode(obj), ImplicitFields.class, env);
		assertEquals(obj, obj2);
	}

	@Test
	public void implicitPrimitives() throws Exception {
		final ImplicitPrimitives obj = new ImplicitPrimitives();
		final ImplicitPrimitives obj2 = SSJSJS.decode(
			SSJSJS.encode(obj), ImplicitPrimitives.class, obj.getEnvironment());
		assertEquals(obj, obj2);
	}

	@Test(expected = JSONdecodeException.class)
	public void missingImplicit() throws Exception {
		final JSONObject json = new JSONObject();
		json.put("something", "good");
		final ImplicitFields obj = SSJSJS.decode(json, ImplicitFields.class);
	}

	@Test(expected = JSONdecodeException.class)
	public void wrongTypeImplicit() throws Exception {
		final JSONObject json = new JSONObject();
		json.put("something", "good");

		final Map<String, Object> env = new HashMap<>();
		env.put("fromEnv", "wrong type");

		final ImplicitFields obj = SSJSJS.decode(json, ImplicitFields.class, env);
	}

	@Test
	public void arraysRoundtrip() throws Exception {
		final WithArrays obj = new WithArrays();
		System.err.println(SSJSJS.encode(obj));
		final WithArrays obj2 = SSJSJS.decode(SSJSJS.encode(obj), WithArrays.class);
		assertEquals(obj, obj2);
	}

	@Test
	public void arraysLongRoundtrip() throws Exception {
		final WithArrays obj = new WithArrays();
		final WithArrays obj2 = SSJSJS.decode(
			new JSONObject(SSJSJS.encode(obj).toString()), WithArrays.class);
		assertEquals(obj, obj2);
	}

	@Test
	public void overridenFieldsCanBeSerialized() throws Exception {
		final Override1 obj1 = new Override1((byte) 0);
		final Override1 obj2 = SSJSJS.decode(SSJSJS.encode(obj1), Override1.class);
		assertEquals(obj1, obj2);

		final Override2 obj3 = new Override2((byte) 0);
		final Override2 obj4 = SSJSJS.decode(SSJSJS.encode(obj3), Override2.class);
		assertEquals(obj3, obj4);
	}

	@Test
	public void otherCollectionTypesRoundtrip() throws Exception {
		final WithCollectionTypes obj1 = new WithCollectionTypes();
		final WithCollectionTypes obj2 = SSJSJS.decode(
			SSJSJS.encode(obj1), WithCollectionTypes.class);
		assertEquals(obj1, obj2);
	}

	@Test
	public void otherCollectionTypesLongRoundtrip() throws Exception {
		final WithCollectionTypes obj1 = new WithCollectionTypes();
		final WithCollectionTypes obj2 = SSJSJS.decode(
			new JSONObject(SSJSJS.encode(obj1).toString()), WithCollectionTypes.class);
		assertEquals(obj1, obj2);
	}

	@Test
	public void nestedCollectionsRoundtrip() throws Exception {
		final NestedCollections obj1 = new NestedCollections();
		final NestedCollections obj2 = SSJSJS.decode(
			SSJSJS.encode(obj1), NestedCollections.class);
		assertEquals(obj1, obj2);
	}

	@Test
	public void nestedCollectionsLongRoundtrip() throws Exception {
		final NestedCollections obj1 = new NestedCollections();
		final NestedCollections obj2 = SSJSJS.decode(
			new JSONObject(SSJSJS.encode(obj1).toString()), NestedCollections.class);
		assertEquals(obj1, obj2);
	}

	@Test(expected = JSONencodeException.class)
	public void requireConstructorAnnotation() throws Exception {
		SSJSJS.encode(new NoConstructorAnnotation());
	}

	@Test(expected = JSONdecodeException.class)
	public void requireConstructorAnnotation2() throws Exception {
		final JSONObject obj = new JSONObject();
		SSJSJS.decode(obj, NoConstructorAnnotation.class);
	}

	@Test(expected = JSONencodeException.class)
	public void requireFieldAnnotations() throws Exception {
		SSJSJS.encode(new MissingFieldAnnotation("v1", "v2"));
	}

	@Test(expected = JSONdecodeException.class)
	public void requireFieldAnnotations2() throws Exception {
		final JSONObject obj = new JSONObject();
		obj.put("a", "v1");
		obj.put("b", "v2");
		SSJSJS.decode(obj, MissingFieldAnnotation.class);
	}

	@Test(expected = JSONencodeException.class)
	public void cannotSerializeArbitraryFields() throws Exception {
		SSJSJS.encode(new UnserializableField(new StringBuilder()));
	}

	@Test(expected = JSONdecodeException.class)
	public void cannotDeserializeArbitraryFields() throws Exception {
		final JSONObject obj = new JSONObject();
		obj.put("a", "This cannot be decoded");
		SSJSJS.decode(obj, UnserializableField.class);
	}

	@Test(expected = JSONdecodeException.class)
	public void cannotDeserializeArbitraryFields2() throws Exception {
		final JSONObject obj = new JSONObject();
		SSJSJS.decode(obj, UnserializableField.class);
	}

	@Test(expected = JSONencodeException.class)
	public void cannotSerializeDuplicateAliases() throws Exception {
		SSJSJS.encode(new DuplicateAliases("v1", "v2"));
	}

	@Test(expected = JSONdecodeException.class)
	public void cannotDeserializeDuplicateAliases() throws Exception {
		final JSONObject obj = new JSONObject();
		obj.put("a", "It's a string");
		SSJSJS.decode(obj, DuplicateAliases.class);
	}

	@SuppressWarnings("unchecked")
	@Test(expected = JSONdecodeException.class)
	public void cannotDeserializeArbitraryObjects() throws Exception {
		final JSONObject obj = new JSONObject();
		obj.put("a",1);
		obj.put("b",2);
		final Class<?> clazz = NotSerializable.class;
		SSJSJS.decode(obj, (Class<JSONable>) clazz);
	}

	@Test(expected = JSONencodeException.class)
	public void cannotSerializeNonNullableNullField() throws Exception {
		final NotNullable obj = new NotNullable();
		SSJSJS.encode(obj);
	}

	@Test(expected = JSONdecodeException.class)
	public void cannotDeserializeNonNullableNullField() throws Exception {
		final JSONObject obj = new JSONObject();
		SSJSJS.decode(obj, NotNullable.class);
	}
}


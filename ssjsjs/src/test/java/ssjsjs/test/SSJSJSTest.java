package ssjsjs.test;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import ssjsjs.JSONable;
import ssjsjs.JSONDeserializeException;
import ssjsjs.JSONSerializeException;
import ssjsjs.SSJSJS;
import static org.junit.Assert.*;

public class SSJSJSTest {
	@Test
	public void emptyClass() throws Exception {
		final Empty obj = new Empty();
		final JSONObject out = SSJSJS.serialize(obj);
		assertEquals(0, out.length());
	}

	@Test
	public void primitivesRoundtrip() throws Exception {
		final Primitives obj = new Primitives((byte) 0);
		final Primitives obj2 = SSJSJS.deserialize(SSJSJS.serialize(obj), Primitives.class);
		assertEquals(obj, obj2);
	}

	@Test
	public void primitivesLongRoundtrip() throws Exception {
		final Primitives obj = new Primitives((byte) 0);
		final Primitives obj2 = SSJSJS.deserialize(
			new JSONObject(SSJSJS.serialize(obj).toString()), Primitives.class);
		assertEquals(obj, obj2);
	}

	@Test
	public void primitivesOutput() throws Exception {
		final Primitives obj = new Primitives((byte) 0);
		final JSONObject out = SSJSJS.serialize(obj);
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
		final WithCollections obj2 = SSJSJS.deserialize(SSJSJS.serialize(obj), WithCollections.class);
		assertEquals(obj, obj2);
	}

	@Test
	public void collectionsLongRoundtrip() throws Exception {
		final WithCollections obj = new WithCollections(0);
		final WithCollections obj2 = SSJSJS.deserialize(
			new JSONObject(SSJSJS.serialize(obj).toString()), WithCollections.class);
		assertEquals(obj, obj2);
	}

	@Test
	public void collectionsOutput() throws Exception {
		final WithCollections obj = new WithCollections(0);
		final JSONObject out = SSJSJS.serialize(obj);

		assertTrue(out.get("emptyList") instanceof JSONArray);
		assertTrue(out.get("numbers") instanceof JSONArray);
		assertTrue(out.get("strings") instanceof JSONArray);
		assertTrue(out.get("primitives1") instanceof JSONArray);
		assertTrue(out.get("primitives2") instanceof JSONArray);
	}

	@Test
	public void mapsRoundtrip() throws Exception {
		final WithMaps obj = new WithMaps(0);
		final WithMaps obj2 = SSJSJS.deserialize(SSJSJS.serialize(obj), WithMaps.class);
		assertEquals(obj, obj2);

		final WithMaps obj3 = new WithMaps(42);
		final WithMaps obj4 = SSJSJS.deserialize(SSJSJS.serialize(obj3), WithMaps.class);
		assertEquals(obj3, obj4);
	}

	@Test
	public void mapsLongRoundtrip() throws Exception {
		final WithMaps obj = new WithMaps(0);
		final WithMaps obj2 = SSJSJS.deserialize(
			new JSONObject(SSJSJS.serialize(obj).toString()), WithMaps.class);
		assertEquals(obj, obj2);

		final WithMaps obj3 = new WithMaps(42);
		final WithMaps obj4 = SSJSJS.deserialize(
			new JSONObject(SSJSJS.serialize(obj3).toString()), WithMaps.class);
		assertEquals(obj3, obj4);
	}

	@Test
	public void emptyBoxes() throws Exception {
		final EmptyBoxes obj = new EmptyBoxes();
		final EmptyBoxes obj2 = SSJSJS.deserialize(SSJSJS.serialize(obj), EmptyBoxes.class);
		assertEquals(obj, obj2);
	}

	@Test
	public void customLabelsRoundtrip() throws Exception {
		final CustomLabels obj = new CustomLabels();
		final CustomLabels obj2 = SSJSJS.deserialize(SSJSJS.serialize(obj), CustomLabels.class);
		assertEquals(obj, obj2);
	}

	@Test
	public void customLabelsLongRoundtrip() throws Exception {
		final CustomLabels obj = new CustomLabels();
		final CustomLabels obj2 = SSJSJS.deserialize(
			new JSONObject(SSJSJS.serialize(obj).toString()), CustomLabels.class);
		assertEquals(obj, obj2);
	}

	@Test
	public void customLabelsOutput() throws Exception {
		final CustomLabels obj = new CustomLabels();
		final JSONObject out = SSJSJS.serialize(obj);

		assertTrue(out.has("a"));
		assertTrue(out.has("custom"));
		assertFalse(out.has("b"));

		assertTrue(out.get("a") instanceof String);
		assertTrue(out.get("custom") instanceof String);

		assertEquals(obj.a, out.get("a"));
		assertEquals(obj.b, out.get("custom"));
	}

	@Test(expected = JSONSerializeException.class)
	public void requireConstructorAnnotation() throws Exception {
		SSJSJS.serialize(new NoConstructorAnnotation());
	}

	@Test(expected = JSONDeserializeException.class)
	public void requireConstructorAnnotation2() throws Exception {
		final JSONObject obj = new JSONObject();
		SSJSJS.deserialize(obj, NoConstructorAnnotation.class);
	}

	@Test(expected = JSONSerializeException.class)
	public void requireFieldAnnotations() throws Exception {
		SSJSJS.serialize(new MissingFieldAnnotation("v1", "v2"));
	}

	@Test(expected = JSONDeserializeException.class)
	public void requireFieldAnnotations2() throws Exception {
		final JSONObject obj = new JSONObject();
		obj.put("a", "v1");
		obj.put("b", "v2");
		SSJSJS.deserialize(obj, MissingFieldAnnotation.class);
	}

	@Test(expected = JSONSerializeException.class)
	public void cannotSerializeArbitraryFields() throws Exception {
		SSJSJS.serialize(new UnserializableField(new StringBuilder()));
	}

	@Test(expected = JSONDeserializeException.class)
	public void cannotDeserializeArbitraryFields() throws Exception {
		final JSONObject obj = new JSONObject();
		obj.put("a", "This cannot be deserialized");
		SSJSJS.deserialize(obj, UnserializableField.class);
	}

	@Test(expected = JSONDeserializeException.class)
	public void cannotDeserializeArbitraryFields2() throws Exception {
		final JSONObject obj = new JSONObject();
		SSJSJS.deserialize(obj, UnserializableField.class);
	}

	@Test(expected = JSONSerializeException.class)
	public void cannotSerializeDuplicateAliases() throws Exception {
		SSJSJS.serialize(new DuplicateAliases("v1", "v2"));
	}

	@Test(expected = JSONDeserializeException.class)
	public void cannotDeserializeDuplicateAliases() throws Exception {
		final JSONObject obj = new JSONObject();
		obj.put("a", "It's a string");
		SSJSJS.deserialize(obj, DuplicateAliases.class);
	}

	@SuppressWarnings("unchecked")
	@Test(expected = JSONDeserializeException.class)
	public void cannotDeserializeArbitraryObjects() throws Exception {
		final JSONObject obj = new JSONObject();
		obj.put("a",1);
		obj.put("b",2);
		final Class<?> clazz = NotSerializable.class;
		SSJSJS.deserialize(obj, (Class<JSONable>) clazz);
	}
}


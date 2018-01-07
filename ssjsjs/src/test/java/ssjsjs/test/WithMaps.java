package ssjsjs.test;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import ssjsjs.JSONable;
import ssjsjs.annotations.Field;
import ssjsjs.annotations.JSONConstructor;

public class WithMaps implements JSONable {
	public final Map<String, Integer> emptyMap;
	public final Map<String, Integer> numbers;
	public final Map<String, String> strings;
	public final Map<String, Primitives> primitives;

	public WithMaps(int x) {
		this.emptyMap = new HashMap<>();
		this.numbers = new TreeMap<>();
		this.strings = new HashMap<>();
		this.primitives = new HashMap<>();

		numbers.put("one", 1 + x);
		numbers.put("two", 2 + x);
		numbers.put("three", 3 + x);
		numbers.put("", 4 + x);

		strings.put("one", "It's a string " + x);
		strings.put("two", "");

		primitives.put("one", new Primitives((byte) (1 + (byte) x)));
		primitives.put("two", new Primitives((byte) (2 + (byte) x)));
		primitives.put("three", new Primitives((byte) (3 + (byte) x)));
		primitives.put("", new Primitives((byte) (4 + (byte) x)));
	}

	@JSONConstructor
	public WithMaps(
		@Field("emptyMap") final Map<String, Integer> emptyMap,
		@Field("numbers") final Map<String, Integer> numbers,
		@Field("strings") final Map<String, String> strings,
		@Field("primitives") final Map<String, Primitives> primitives
	) {
		this.emptyMap = emptyMap;
		this.numbers = numbers;
		this.strings = strings;
		this.primitives = primitives;
	}

	@Override
	public String toString() {
		return "WithMaps:\n"  +
			"  " + emptyMap.toString() + "\n" +
			"  " + numbers.toString() + "\n" +
			"  " + strings.toString() + "\n" +
			"  " + primitives.toString();
	}

	@Override
	public int hashCode() {
		return
			this.emptyMap.hashCode() +
			this.numbers.hashCode() +
			this.strings.hashCode() +
			this.primitives.hashCode();
	}

	@Override
	public boolean equals(final Object other) {
		if (other == null) return false;
		else if (!(other instanceof WithMaps)) return false;
		else {
			final WithMaps o = (WithMaps) other;
			return
				this.emptyMap.equals(o.emptyMap) &&
				this.numbers.equals(o.numbers) &&
				this.strings.equals(o.strings) &&
				this.primitives.equals(o.primitives);
		}
	}
}


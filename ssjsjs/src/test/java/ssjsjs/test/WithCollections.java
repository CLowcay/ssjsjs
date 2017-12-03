package ssjsjs.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import ssjsjs.annotations.Field;
import ssjsjs.annotations.JSONConstructor;
import ssjsjs.JSONable;

public class WithCollections implements JSONable {
	public final List<Integer> emptyList;
	public final List<Integer> numbers;
	public final List<String> strings;
	public final List<Primitives> primitives1;
	public final Set<Primitives> primitives2;

	public WithCollections(int x) {
		this.emptyList = new ArrayList<>();
		this.numbers = new LinkedList<>();
		this.strings = new ArrayList<>();
		this.primitives1 = new ArrayList<>();
		this.primitives2 = new HashSet<>();

		this.numbers.add(1 + x);
		this.numbers.add(3 + x);
		this.numbers.add(4 + x);
		this.numbers.add(6 + x);

		this.strings.add("It's a string " + x);
		this.strings.add("");
		this.strings.add(null);

		primitives1.add(new Primitives((byte) (1 + (byte) x)));
		primitives1.add(new Primitives((byte) (2 + (byte) x)));
		primitives1.add(new Primitives((byte) (3 + (byte) x)));
		primitives1.add(new Primitives((byte) (4 + (byte) x)));

		primitives2.add(new Primitives((byte) (6 + (byte) x)));
		primitives2.add(new Primitives((byte) (7 + (byte) x)));
		primitives2.add(new Primitives((byte) (8 + (byte) x)));
		primitives2.add(new Primitives((byte) (9 + (byte) x)));
	}

	@JSONConstructor
	public WithCollections(
		@Field("emptyList") final Collection<Integer> emptyList,
		@Field("numbers") final Collection<Integer> numbers,
		@Field("strings") final Collection<String> strings,
		@Field("primitives1") final Collection<Primitives> primitives1,
		@Field("primitives2") final Collection<Primitives> primitives2
	) {
		this.emptyList = new ArrayList<>(emptyList);
		this.numbers = new LinkedList<>(numbers);
		this.strings = new ArrayList<>(strings);
		this.primitives1 = new ArrayList<>(primitives1);
		this.primitives2 = new HashSet<>(primitives2);
	}

	@Override
	public String toString() {
		return "WithMaps:\n" +
			"  " + emptyList.toString() + "\n" +
			"  " + numbers.toString() + "\n" +
			"  " + strings.toString() + "\n" +
			"  " + primitives1.toString() + "\n" +
			"  " + primitives2.toString();
	}

	@Override
	public int hashCode() {
		return 
			this.emptyList.hashCode() + 
			this.numbers.hashCode() + 
			this.strings.hashCode() + 
			this.primitives1.hashCode() + 
			this.primitives2.hashCode();
	}

	@Override
	public boolean equals(final Object other) {
		if (other == null) return false;
		else if (!(other instanceof WithCollections)) return false;
		else {
			final WithCollections o = (WithCollections) other;
			return 
				this.emptyList.equals(o.emptyList) &&
				this.numbers.equals(o.numbers) &&
				this.strings.equals(o.strings) &&
				this.primitives1.equals(o.primitives1) &&
				this.primitives2.equals(o.primitives2);
		}
	}
}


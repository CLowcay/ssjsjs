package ssjsjs.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import ssjsjs.annotations.Field;
import ssjsjs.annotations.JSON;
import ssjsjs.JSONable;

public class NestedCollections implements JSONable {
	private final List<List<Integer>> ll;
	private final List<Set<Integer>> ls;
	private final Optional<Set<List<Integer>>> osl;
	private final Map<String, List<Integer>> ml;
	private final Map<String, Optional<Integer>> mo;
	private final Map<String, Optional<List<Integer>>> mol;

	private static List<Integer> mkList(int n) {
		final List<Integer> r = new ArrayList<>();
		r.add(0 + n);
		r.add(1 + n);
		r.add(2 + n);
		r.add(2 + n);
		r.add(3 + n);
		return r;
	}

	public NestedCollections() {
		this.ll = new ArrayList<>();
		this.ll.add(mkList(0));
		this.ll.add(mkList(1));
		this.ll.add(mkList(2));
		this.ll.add(mkList(2));
		this.ll.add(mkList(3));

		this.ls = new ArrayList<>();
		this.ls.add(new HashSet<>(mkList(0)));
		this.ls.add(new HashSet<>(mkList(1)));
		this.ls.add(new HashSet<>(mkList(2)));
		this.ls.add(new HashSet<>(mkList(2)));
		this.ls.add(new HashSet<>(mkList(3)));

		final Set<List<Integer>> sl = new HashSet<>();
		sl.add(mkList(0));
		sl.add(mkList(1));
		sl.add(mkList(2));
		sl.add(mkList(2));
		sl.add(mkList(3));
		this.osl = Optional.of(sl);

		this.ml = new HashMap<>();
		this.ml.put("one", mkList(1));
		this.ml.put("two", mkList(2));
		this.ml.put("three", mkList(3));

		this.mo = new HashMap<>();
		this.mo.put("one", Optional.of(1));
		this.mo.put("two", Optional.of(2));
		this.mo.put("three", Optional.of(3));

		this.mol = new HashMap<>();
		this.mol.put("one", Optional.of(mkList(1)));
		this.mol.put("two", Optional.of(mkList(2)));
		this.mol.put("three", Optional.of(mkList(3)));
	}

	@JSON
	public NestedCollections(
		@Field("ll") final List<List<Integer>> ll,
		@Field("ls") final List<Set<Integer>> ls,
		@Field("osl") final Optional<Set<List<Integer>>> osl,
		@Field("ml") final Map<String, List<Integer>> ml,
		@Field("mo") final Map<String, Optional<Integer>> mo,
		@Field("mol") final Map<String, Optional<List<Integer>>> mol
	) {
		this.ll = ll;
		this.ls = ls;
		this.osl = osl;
		this.ml = ml;
		this.mo = mo;
		this.mol = mol;
	}

	@Override public boolean equals(final Object other) {
		if (!(other instanceof NestedCollections)) return false;
		else {
			final NestedCollections o = (NestedCollections) other;
			return
				this.ll == null? o.ll == null : this.ll.equals(o.ll) &&
				this.ls == null? o.ls == null : this.ls.equals(o.ls) &&
				this.osl == null? o.osl == null : this.osl.equals(o.osl) &&
				this.ml == null? o.ml == null : this.ml.equals(o.ml) &&
				this.mo == null? o.mo == null : this.mo.equals(o.mo) &&
				this.mol == null? o.mol == null : this.mol.equals(o.mol);
		}
	}

	@Override public int hashCode() {
		return
			(this.ll == null? 0 : ll.hashCode()) +
			(this.ls == null? 0 : ls.hashCode()) +
			(this.osl == null? 0 : osl.hashCode()) +
			(this.ml == null? 0 : ml.hashCode()) +
			(this.mo == null? 0 : mo.hashCode()) +
			(this.mol == null? 0 : mol.hashCode());
	}
}


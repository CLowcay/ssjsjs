package ssjsjs.test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import ssjsjs.annotations.Field;
import ssjsjs.annotations.JSONConstructor;
import ssjsjs.JSONable;

public class WithCollectionTypes implements JSONable {
	private final List<Integer> list;
	private final Set<Integer> set;

	public WithCollectionTypes() {
		this.list = new ArrayList<>();
		this.set = new HashSet<>();

		this.list.add(1);
		this.list.add(2);
		this.list.add(3);
		this.list.add(4);
		this.list.add(4);

		this.set.add(1);
		this.set.add(2);
		this.set.add(3);
		this.set.add(4);
		this.set.add(4);
	}

	@JSONConstructor
	public WithCollectionTypes(
		@Field("list") final List<Integer> list,
		@Field("set") final Set<Integer> set
	) {
		this.list = list;
		this.set = set;
	}

	@Override public int hashCode() {
		return
			(list == null? 0 : list.hashCode()) +
			(set == null? 0 : set.hashCode());
	}

	@Override public boolean equals(final Object other) {
		if (!(other instanceof WithCollectionTypes)) return false;
		else {
			final WithCollectionTypes o = (WithCollectionTypes) other;
			return
				this.list == null? o.list == null : this.list.equals(o.list) &&
				this.set == null? o.set == null : this.set.equals(o.set);
		}
	}
}


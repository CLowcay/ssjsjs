package ssjsjs.test;

import ssjsjs.annotations.As;
import ssjsjs.annotations.Field;
import ssjsjs.annotations.JSON;
import ssjsjs.JSONable;

public class CustomLabels implements JSONable {
	public final String a;
	public final String b;

	public CustomLabels() {
		this.a = "It's a string";
		this.b = "It's another string";
	}

	@JSON
	public CustomLabels(
		@Field("a") final String a,
		@Field("b")@As("custom") final String b
	) {
		this.a = a;
		this.b = b;
	}

	@Override
	public int hashCode() {
		return
			(a == null? 0 : a.hashCode()) +
			(b == null? 0 : b.hashCode());
	}

	@Override
	public boolean equals(final Object other) {
		if (other == null) return false;
		else if (!(other instanceof CustomLabels)) return false;
		else {
			final CustomLabels o = (CustomLabels) other;
			return
				(a == null? o.a == null : a.equals(o.a)) &&
				(b == null? o.b == null : b.equals(o.b));
		}
	}
}


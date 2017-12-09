package ssjsjs.test;

import ssjsjs.annotations.Field;
import ssjsjs.annotations.Implicit;
import ssjsjs.annotations.JSONConstructor;
import ssjsjs.JSONable;

public class ImplicitFields implements JSONable {
	public final String something;
	public final WierdType implicit;

	@JSONConstructor
	public ImplicitFields(
		@Field("something") final String something,
		@Implicit("fromEnv") final WierdType implicit
	) {
		this.something = something;
		this.implicit = implicit;
	}

	@Override
	public int hashCode() {
		return
			(implicit == null? 0 : implicit.hashCode()) +
			(something == null? 0 : something.hashCode());
	}

	@Override
	public boolean equals(final Object other) {
		if (other == null) return false;
		else if (!(other instanceof ImplicitFields)) return false;
		else {
			final ImplicitFields o = (ImplicitFields) other;
			return
				((this.something == null && o.something == null)
					|| (this.something != null && this.something.equals(o.something)))
				&& this.implicit == o.implicit;
		}
	}
}

/**
 * This type cannot be serialized
 * */
class WierdType {
	public final String field;

	WierdType(final String field) {
		this.field = field;
	}
}


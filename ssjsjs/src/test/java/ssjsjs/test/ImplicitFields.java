package ssjsjs.test;

import java.util.Optional;
import ssjsjs.annotations.Field;
import ssjsjs.annotations.Implicit;
import ssjsjs.annotations.JSONConstructor;
import ssjsjs.JSONable;

public class ImplicitFields implements JSONable {
	public final String something;
	public final WierdType implicit;
	public final Optional<ImplicitFields> secondLayer;

	@JSONConstructor
	public ImplicitFields(
		@Field("something") final String something,
		@Implicit("fromEnv") final WierdType implicit,
		@Field("secondLayer") final Optional<ImplicitFields> secondLayer
	) {
		this.something = something;
		this.implicit = implicit;
		this.secondLayer = secondLayer;
	}

	@Override
	public int hashCode() {
		return
			(implicit == null? 0 : implicit.hashCode()) +
			(something == null? 0 : something.hashCode()) +
			(secondLayer == null? 0 : secondLayer.hashCode());
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
				&& this.implicit == o.implicit
				&& this.secondLayer.equals(o.secondLayer);
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


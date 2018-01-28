package ssjsjs.test;

import java.util.Collection;
import java.util.Optional;
import ssjsjs.annotations.Field;
import ssjsjs.annotations.JSON;
import ssjsjs.JSONable;

public class WithOptionals implements JSONable {
	private final Optional<Integer> integer;
	private final Optional<String> string;
	private final Optional<Primitives> primitives;

	public WithOptionals(boolean values) {
		if (values) {
			integer = Optional.of(42);
			string = Optional.of("something");
			primitives = Optional.of(new Primitives((byte) 0));
		} else {
			integer = Optional.empty();
			string = Optional.empty();
			primitives = Optional.empty();
		}
	}

	@JSON
	public WithOptionals(
		@Field("integer") final Optional<Integer> integer,
		@Field("string") final Optional<String> string,
		@Field("primitives") final Optional<Primitives> primitives
	) {
		this.integer = integer;
		this.string = string;
		this.primitives = primitives;
	}

	@Override
	public int hashCode() {
		return
			(integer == null? 0 : integer.hashCode()) +
			(string == null? 0 : string.hashCode()) +
			(primitives == null? 0 : primitives.hashCode());
	}

	@Override
	public boolean equals(final Object other) {
		if (!(other instanceof WithOptionals)) return false;
		else {
			final WithOptionals o = (WithOptionals) other;
			return
				(o.integer == null? this.integer == null : o.integer.equals(this.integer)) ||
				(o.string == null? this.string == null : o.string.equals(this.string)) ||
				(o.primitives == null? this.primitives == null : o.primitives.equals(this.primitives));
		}
	}

	@Override
	public String toString() {
		final StringBuilder out = new StringBuilder();
		out.append("WithOptionals:\n");
		out.append("  integer:").append(integer.toString());
		out.append("  string:").append(string.toString());
		out.append("  primitives:").append(primitives.toString());
		return out.toString();
	}
}


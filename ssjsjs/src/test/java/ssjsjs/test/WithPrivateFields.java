package ssjsjs.test;

import ssjsjs.annotations.Field;
import ssjsjs.annotations.JSON;
import ssjsjs.JSONable;

public class WithPrivateFields implements JSONable {
	private final String example;

	@JSON
	public WithPrivateFields(
		@Field("example") final String example
	) {
		this.example = example;
	}

	@Override
	public int hashCode() {
		return example.hashCode();
	}

	@Override
	public boolean equals(final Object other) {
		if (other == null) return false;
		else if (!(other instanceof WithPrivateFields)) return false;
		else {
			final WithPrivateFields o = (WithPrivateFields) other;
			return this.example == o.example;
		}
	}
}


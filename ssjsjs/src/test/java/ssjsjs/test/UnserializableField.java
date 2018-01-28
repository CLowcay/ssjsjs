package ssjsjs.test;

import ssjsjs.annotations.Field;
import ssjsjs.annotations.JSON;
import ssjsjs.JSONable;

public class UnserializableField implements JSONable {
	final StringBuilder a;

	@JSON
	public UnserializableField(
		@Field("a") final StringBuilder a
	) {
		this.a = a;
	}
}


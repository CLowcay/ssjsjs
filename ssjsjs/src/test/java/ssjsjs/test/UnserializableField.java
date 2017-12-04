package ssjsjs.test;

import ssjsjs.annotations.Field;
import ssjsjs.annotations.JSONConstructor;
import ssjsjs.JSONable;

public class UnserializableField implements JSONable {
	final StringBuilder a;

	@JSONConstructor
	public UnserializableField(
		@Field("a") final StringBuilder a
	) {
		this.a = a;
	}
}


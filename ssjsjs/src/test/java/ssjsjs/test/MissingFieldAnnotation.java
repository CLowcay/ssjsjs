package ssjsjs.test;

import ssjsjs.JSONable;
import ssjsjs.annotations.JSON;
import ssjsjs.annotations.Field;

public class MissingFieldAnnotation implements JSONable {
	private final String a;
	private final String b;

	@JSON
	public MissingFieldAnnotation(
		@Field("a") final String a,
		final String b
	) {
		this.a = a;
		this.b = b;
	}
}


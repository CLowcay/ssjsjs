package ssjsjs.test;

import ssjsjs.annotations.As;
import ssjsjs.annotations.Field;
import ssjsjs.annotations.JSON;
import ssjsjs.JSONable;

public class DuplicateAliases implements JSONable {
	private final String a;
	private final String b;

	@JSON
	public DuplicateAliases(
		@Field("a")@As("a") final String a,
		@Field("b")@As("a") final String b
	) {
		this.a = a;
		this.b = b;
	}
}


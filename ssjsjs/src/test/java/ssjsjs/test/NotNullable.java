package ssjsjs.test;

import ssjsjs.annotations.Field;
import ssjsjs.annotations.JSON;
import ssjsjs.JSONable;

public class NotNullable implements JSONable {
	private final String notNullable;

	public NotNullable() {
		this.notNullable = null;
	}

	@JSON
	public NotNullable(
		@Field("notNullable") final String val
	) {
		this.notNullable = val;
	}
}


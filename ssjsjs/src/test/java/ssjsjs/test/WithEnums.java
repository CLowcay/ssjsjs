package ssjsjs.test;

import ssjsjs.annotations.Field;
import ssjsjs.annotations.JSONConstructor;
import ssjsjs.JSONable;

public class WithEnums implements JSONable {
	enum SomeEnum {
		SOME_VALUE, SOME_OTHER_VALUE
	}

	public final SomeEnum enumField;

	@JSONConstructor
	public WithEnums(
		@Field("enumField") final SomeEnum enumField
	) {
		this.enumField = enumField;
	}

	@Override
	public int hashCode() {
		return enumField.hashCode();
	}

	@Override
	public boolean equals(final Object other) {
		if (other == null) return false;
		else if (!(other instanceof WithEnums)) return false;
		else {
			final WithEnums o = (WithEnums) other;
			return this.enumField == o.enumField;
		}
	}
}


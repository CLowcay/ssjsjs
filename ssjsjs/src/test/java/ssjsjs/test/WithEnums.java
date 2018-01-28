package ssjsjs.test;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ssjsjs.annotations.Field;
import ssjsjs.annotations.JSON;
import ssjsjs.JSONable;

public class WithEnums implements JSONable {
	enum SomeEnum {
		SOME_VALUE, SOME_OTHER_VALUE
	}

	public final SomeEnum enumField;
	public final Collection<SomeEnum> enums;
	public final Map<String, SomeEnum> enumDictionary;
	public final SomeEnum[] arrayEnums;

	public WithEnums(
		final SomeEnum enumField
	) {
		this.enumField = enumField;
		enums = List.of(enumField);
		enumDictionary = new HashMap<>();
		enumDictionary.put("first", enumField);
		arrayEnums = new SomeEnum[] {enumField};
	}

	@JSON
	public WithEnums(
		@Field("enumField") final SomeEnum enumField,
		@Field("enums") final Collection<SomeEnum> enums,
		@Field("enumDictionary") final Map<String, SomeEnum> enumDictionary,
		@Field("arrayEnums") final SomeEnum[] arrayEnums
	) {
		this.enumField = enumField;
		this.enums = enums;
		this.enumDictionary = enumDictionary;
		this.arrayEnums = arrayEnums;
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


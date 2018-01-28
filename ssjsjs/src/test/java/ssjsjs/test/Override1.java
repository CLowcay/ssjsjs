package ssjsjs.test;

import ssjsjs.annotations.Field;
import ssjsjs.annotations.JSON;
import ssjsjs.JSONable;

public class Override1 extends Primitives {
	public Override1(final byte x) {
		super(x);
	}

	@JSON
	public Override1(
		@Field("byteVal") final byte byteVal,
		@Field("charVal") final char charVal,
		@Field("shortVal") final short shortVal,
		@Field("intVal") final int intVal,
		@Field("longVal") final long longVal,
		@Field("floatVal") final float floatVal,
		@Field("doubleVal") final double doubleVal,
		@Field("booleanVal") final boolean booleanVal,
		@Field("stringVal1") final String stringVal1,
		@Field("bbyteVal") final Byte bbyteVal,
		@Field("bcharVal") final Character bcharVal,
		@Field("bshortVal") final Short bshortVal,
		@Field("bintVal") final Integer bintVal,
		@Field("blongVal") final Long blongVal,
		@Field("bfloatVal") final Float bfloatVal,
		@Field("bdoubleVal") final Double bdoubleVal,
		@Field("bbooleanVal") final Boolean bbooleanVal
	) {
		super(
			byteVal,
			charVal,
			shortVal,
			intVal,
			longVal,
			floatVal,
			doubleVal,
			booleanVal,
			stringVal1,
			bbyteVal,
			bcharVal,
			bshortVal,
			bintVal,
			blongVal,
			bfloatVal,
			bdoubleVal,
			bbooleanVal);
	}
}


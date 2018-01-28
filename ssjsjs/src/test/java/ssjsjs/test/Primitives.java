package ssjsjs.test;

import ssjsjs.annotations.Field;
import ssjsjs.annotations.JSONConstructor;
import ssjsjs.JSONable;

public class Primitives implements JSONable {
	public final byte byteVal;
	public final char charVal;
	public final short shortVal;
	public final int intVal;
	public final long longVal;
	public final float floatVal;
	public final double doubleVal;
	public final boolean booleanVal;
	public final String stringVal1;

	public final Byte bbyteVal;
	public final Character bcharVal;
	public final Short bshortVal;
	public final Integer bintVal;
	public final Long blongVal;
	public final Float bfloatVal;
	public final Double bdoubleVal;
	public final Boolean bbooleanVal;

	public Primitives(final byte x) {
		this.byteVal = (byte) (42 + x);
		this.charVal = (char) ('z' + x);
		this.shortVal = (short) ((short) 89 + (short) x);
		this.intVal = 12 * (int) x;
		this.longVal = 98712398412l * (long) x;
		this.floatVal = 0.2342345f * (float) x;
		this.doubleVal = 0.123423d * (double) x;
		this.booleanVal = (x % 2) == 0;
		this.stringVal1 = "It's a string " + x;

		this.bbyteVal = (byte) (this.byteVal + x);
		this.bcharVal = (char) (charVal + x);
		this.bshortVal = (short) (shortVal + x);
		this.bintVal = intVal + (int) x;
		this.blongVal = longVal + (long) x;
		this.bfloatVal = floatVal + (float) x;
		this.bdoubleVal = doubleVal + (double) x;
		this.bbooleanVal = !booleanVal;
	}

	@JSONConstructor
	public Primitives(
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
		this.byteVal = byteVal;
		this.charVal = charVal;
		this.shortVal = shortVal;
		this.intVal = intVal;
		this.longVal = longVal;
		this.floatVal = floatVal;
		this.doubleVal = doubleVal;
		this.booleanVal = booleanVal;
		this.stringVal1 = stringVal1;

		this.bbyteVal = bbyteVal;
		this.bcharVal =  bcharVal;
		this.bshortVal = bshortVal;
		this.bintVal = bintVal;
		this.blongVal = blongVal;
		this.bfloatVal = bfloatVal;
		this.bdoubleVal = bdoubleVal;
		this.bbooleanVal = bbooleanVal;
	}

	@Override
	public String toString() {
		return "Primitives:\n" +
			"  " + byteVal + "\n" +
			"  " + charVal + "\n" +
			"  " + shortVal + "\n" +
			"  " + intVal + "\n" +
			"  " + longVal + "\n" +
			"  " + floatVal + "\n" +
			"  " + doubleVal + "\n" +
			"  " + booleanVal + "\n" +
			"  " + stringVal1 + "\n" +
			"  " + bbyteVal + "\n" +
			"  " + bcharVal + "\n" +
			"  " + bshortVal + "\n" +
			"  " + bintVal + "\n" +
			"  " + blongVal + "\n" +
			"  " + bfloatVal + "\n" +
			"  " + bdoubleVal + "\n" +
			"  " + bbooleanVal;
	}

	@Override
	public int hashCode() {
		return byteVal +
			charVal + shortVal + intVal + (int) longVal +
			(int) floatVal + (int) doubleVal +
			(booleanVal? 1 : 0) +
			(stringVal1 == null? 0 : stringVal1.hashCode());
	}

	@Override
	public boolean equals(final Object other) {
		if (other == null) return false;
		else if (!(other instanceof Primitives)) return false;
		else {
			final Primitives o = (Primitives) other;
			return
				this.byteVal == o.byteVal &&
				this.charVal == o.charVal &&
				this.shortVal == o.shortVal &&
				this.intVal == o.intVal &&
				this.longVal == o.longVal &&
				this.floatVal == o.floatVal &&
				this.doubleVal == o.doubleVal &&
				this.booleanVal == o.booleanVal &&
				(this.stringVal1 == null? o.stringVal1 == null : this.stringVal1.equals(o.stringVal1)) &&
				this.bbyteVal.equals(o.bbyteVal) &&
				this.bcharVal.equals(o.bcharVal) &&
				this.bshortVal.equals(o.bshortVal) &&
				this.bintVal.equals(o.bintVal) &&
				this.blongVal.equals(o.blongVal) &&
				this.bfloatVal.equals(o.bfloatVal) &&
				this.bdoubleVal.equals(o.bdoubleVal) &&
				this.bbooleanVal.equals(o.bbooleanVal);
		}
	}
}


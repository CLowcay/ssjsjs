package ssjsjs.test;

import ssjsjs.annotations.Alias;
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
	public final String stringVal2;

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
		this.stringVal2 =  null;
	}

	@JSONConstructor
	public Primitives(
		@Alias("byteVal") final byte byteVal,
		@Alias("charVal") final char charVal,
		@Alias("shortVal") final short shortVal,
		@Alias("intVal") final int intVal,
		@Alias("longVal") final long longVal,
		@Alias("floatVal") final float floatVal,
		@Alias("doubleVal") final double doubleVal,
		@Alias("booleanVal") final boolean booleanVal,
		@Alias("stringVal1") final String stringVal1,
		@Alias("stringVal2") final String stringVal2
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
		this.stringVal2 = stringVal2;
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
			"  " + stringVal2;
	}

	@Override
	public int hashCode() {
		return byteVal +
			charVal + shortVal + intVal + (int) longVal +
			(int) floatVal + (int) doubleVal +
			(booleanVal? 1 : 0) +
			(stringVal1 == null? 0 : stringVal1.hashCode()) +
			(stringVal2 == null? 0 : stringVal2.hashCode());
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
				(this.stringVal2 == null? o.stringVal2 == null : this.stringVal2.equals(o.stringVal2));
		}
	}
}


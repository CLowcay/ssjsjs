package ssjsjs.test;

import java.util.HashMap;
import java.util.Map;
import ssjsjs.annotations.Implicit;
import ssjsjs.annotations.JSON;
import ssjsjs.JSONable;

public class ImplicitPrimitives implements JSONable {
	public final byte byteVal;
	public final char charVal;
	public final short shortVal;
	public final int intVal;
	public final long longVal;
	public final float floatVal;
	public final double doubleVal;
	public final boolean booleanVal;

	public ImplicitPrimitives() {
		byteVal = 42;
		charVal = 'a';
		shortVal = 42;
		intVal = 42;
		longVal = 42l;
		floatVal = 42.33f;
		doubleVal = 42.33d;
		booleanVal = true;
	}

	@JSON
	public ImplicitPrimitives(
		@Implicit("byteVal") final byte byteVal,
		@Implicit("charVal") final char charVal,
		@Implicit("shortVal") final short shortVal,
		@Implicit("intVal") final int intVal,
		@Implicit("longVal") final long longVal,
		@Implicit("floatVal") final float floatVal,
		@Implicit("doubleVal") final double doubleVal,
		@Implicit("booleanVal") final boolean booleanVal
	) {
		this.byteVal = byteVal;
		this.charVal = charVal;
		this.shortVal = shortVal;
		this.intVal = intVal;
		this.longVal = longVal;
		this.floatVal = floatVal;
		this.doubleVal = doubleVal;
		this.booleanVal = booleanVal;
	}

	public Map<String, Object> getEnvironment() {
		final Map<String, Object> r = new HashMap<>();
		r.put("byteVal", byteVal);
		r.put("charVal", charVal);
		r.put("shortVal", shortVal);
		r.put("intVal", intVal);
		r.put("longVal", longVal);
		r.put("floatVal", floatVal);
		r.put("doubleVal", doubleVal);
		r.put("booleanVal", booleanVal);

		return r;
	}

	@Override public int hashCode() {
		return
			((int) byteVal) +
			((int) charVal) +
			((int) shortVal) +
			(intVal) +
			((int) longVal) +
			((int) floatVal) +
			((int) doubleVal) +
			(booleanVal? 0 : 1);
	}

	@Override public boolean equals(final Object other) {
		if (!(other instanceof ImplicitPrimitives)) return false;
		else {
			final ImplicitPrimitives o = (ImplicitPrimitives) other;

			return 
				this.byteVal == o.byteVal &&
				this.charVal == o.charVal &&
				this.shortVal == o.shortVal &&
				this.intVal == o.intVal &&
				this.longVal == o.longVal &&
				this.floatVal == o.floatVal &&
				this.doubleVal == o.doubleVal &&
				this.booleanVal == o.booleanVal;
		}
	}

	@Override public String toString() {
		final StringBuilder r = new StringBuilder();
		r.append("ImplicitPrimitives:\n");
		r.append("  byteVal: ").append(byteVal);
		r.append("  charVal: ").append(charVal);
		r.append("  shortVal: ").append(shortVal);
		r.append("  intVal: ").append(intVal);
		r.append("  longVal: ").append(longVal);
		r.append("  floatVal: ").append(floatVal);
		r.append("  doubleVal: ").append(doubleVal);
		r.append("  booleanVal: ").append(booleanVal);

		return r.toString();
	}
}


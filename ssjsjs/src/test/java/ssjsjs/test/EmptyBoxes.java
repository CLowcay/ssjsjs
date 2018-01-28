package ssjsjs.test;

import ssjsjs.annotations.Field;
import ssjsjs.annotations.JSONConstructor;
import ssjsjs.annotations.Nullable;
import ssjsjs.JSONable;

public class EmptyBoxes implements JSONable {
	public final Byte byteVal;
	public final Character charVal;
	public final Short shortVal;
	public final Integer intVal;
	public final Long longVal;
	public final Float floatVal;
	public final Double doubleVal;
	public final Boolean booleanVal;

	public EmptyBoxes() {
		byteVal = null;
		charVal = null;
		shortVal = null;
		intVal = null;
		longVal = null;
		floatVal = null;
		doubleVal = null;
		booleanVal = null;
	}

	@JSONConstructor
	public EmptyBoxes(
		@Nullable@Field("byteVal") final Byte byteVal,
		@Nullable@Field("charVal") final Character charVal,
		@Nullable@Field("shortVal") final Short shortVal,
		@Nullable@Field("intVal") final Integer intVal,
		@Nullable@Field("longVal") final Long longVal,
		@Nullable@Field("floatVal") final Float floatVal,
		@Nullable@Field("doubleVal") final Double doubleVal,
		@Nullable@Field("booleanVal") final Boolean booleanVal
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

	@Override
	public int hashCode() {
		return
			(byteVal == null? 0 : byteVal.hashCode()) +
			(charVal == null? 0 : charVal.hashCode() )+
			(shortVal == null? 0 : shortVal.hashCode()) +
			(intVal == null? 0 : intVal.hashCode()) +
			(longVal == null? 0 : longVal.hashCode()) +
			(floatVal == null? 0 : floatVal.hashCode()) +
			(doubleVal == null? 0 : doubleVal.hashCode()) +
			(booleanVal == null? 0 : booleanVal.hashCode());
	}

	@Override
	public boolean equals(final Object other) {
		if (other == null) return false;
		else if (!(other instanceof EmptyBoxes)) return false;
		else {
			final EmptyBoxes o = (EmptyBoxes) other;
			return 
				(byteVal == null? o.byteVal == null : byteVal.equals(o.byteVal)) ||
				(charVal == null? o.charVal == null : charVal.equals(o.charVal)) ||
				(shortVal == null? o.shortVal == null : shortVal.equals(o.shortVal)) ||
				(intVal == null? o.intVal == null : intVal.equals(o.intVal)) ||
				(longVal == null? o.longVal == null : longVal.equals(o.longVal)) ||
				(floatVal == null? o.floatVal == null : floatVal.equals(o.floatVal)) ||
				(doubleVal == null? o.doubleVal == null : doubleVal.equals(o.doubleVal)) ||
				(booleanVal == null? o.booleanVal == null : booleanVal.equals(o.booleanVal));
		}
	}
}


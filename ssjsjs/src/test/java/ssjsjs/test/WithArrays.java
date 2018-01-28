package ssjsjs.test;

import java.util.Arrays;
import ssjsjs.annotations.Field;
import ssjsjs.annotations.JSON;
import ssjsjs.JSONable;

public class WithArrays implements JSONable {
	public final byte[] byteArray;
	public final char[] charArray;
	public final short[] shortArray;
	public final int[] intArray;
	public final long[] longArray;
	public final float[] floatArray;
	public final double[] doubleArray;
	public final boolean[] booleanArray;
	public final String[] StringArray1;
	public final String[] StringArray2;

	public final Byte[] bbyteArray;
	public final Character[] bcharArray;
	public final Short[] bshortArray;
	public final Integer[] bintArray;
	public final Long[] blongArray;
	public final Float[] bfloatArray;
	public final Double[] bdoubleArray;
	public final Boolean[] bbooleanArray;

	public WithArrays() {
		this.byteArray     = new byte[]      {1,2,3};
		this.charArray     = new char[]      {'a'};
		this.shortArray    = new short[]     {4,5};
		this.intArray      = new int[]       {5};
		this.longArray     = new long[]      {42};
		this.floatArray    = new float[]     {1.9f};
		this.doubleArray   = new double[]    {2.4d};
		this.booleanArray  = new boolean[]   {true, false};
		this.StringArray1  = new String[]    {"Something", "", null};
		this.StringArray2  = new String[]    {};
		this.bbyteArray    = new Byte[]      {2, null};
		this.bcharArray    = new Character[] {null, 'a', '☆'};
		this.bshortArray   = new Short[]     {10, null};
		this.bintArray     = new Integer[]   {42};
		this.blongArray    = new Long[]      {43l, 44l};
		this.bfloatArray   = new Float[]     {1.3f};
		this.bdoubleArray  = new Double[]    {2.6d};
		this.bbooleanArray = new Boolean[]   {true, false, null};
	}

	@JSON
	public WithArrays(
		@Field("byteArray") final byte[] byteArray ,
		@Field("charArray") final char[] charArray,
		@Field("shortArray") final short[] shortArray,
		@Field("intArray") final int[] intArray,
		@Field("longArray") final long[] longArray,
		@Field("floatArray") final float[] floatArray,
		@Field("doubleArray") final double[] doubleArray,
		@Field("booleanArray") final boolean[] booleanArray,
		@Field("StringArray1") final String[] StringArray1,
		@Field("StringArray2") final String[] StringArray2,
		@Field("bbyteArray") final Byte[] bbyteArray,
		@Field("bcharArray") final Character[] bcharArray,
		@Field("bshortArray") final Short[] bshortArray,
		@Field("bintArray") final Integer[] bintArray,
		@Field("blongArray") final Long[] blongArray,
		@Field("bfloatArray") final Float[] bfloatArray,
		@Field("bdoubleArray") final Double[] bdoubleArray,
		@Field("bbooleanArray") final Boolean[] bbooleanArray
	) {
		this.byteArray = byteArray;
		this.charArray = charArray;
		this.shortArray = shortArray;
		this.intArray = intArray;
		this.longArray = longArray;
		this.floatArray = floatArray;
		this.doubleArray = doubleArray;
		this.booleanArray = booleanArray;
		this.StringArray1 = StringArray1;
		this.StringArray2 = StringArray2;
		this.bbyteArray = bbyteArray;
		this.bcharArray = bcharArray;
		this.bshortArray = bshortArray;
		this.bintArray = bintArray;
		this.blongArray = blongArray;
		this.bfloatArray = bfloatArray;
		this.bdoubleArray = bdoubleArray;
		this.bbooleanArray = bbooleanArray;
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(byteArray) +
			Arrays.hashCode(charArray) +
			Arrays.hashCode(shortArray) +
			Arrays.hashCode(intArray) +
			Arrays.hashCode(longArray) +
			Arrays.hashCode(floatArray) +
			Arrays.hashCode(doubleArray) +
			Arrays.hashCode(booleanArray) +
			Arrays.hashCode(StringArray1) +
			Arrays.hashCode(StringArray2) +
			Arrays.hashCode(bbyteArray) +
			Arrays.hashCode(bcharArray) +
			Arrays.hashCode(bshortArray) +
			Arrays.hashCode(bintArray) +
			Arrays.hashCode(blongArray) +
			Arrays.hashCode(bfloatArray) +
			Arrays.hashCode(bdoubleArray) +
			Arrays.hashCode(bbooleanArray);
	}

	@Override
	public boolean equals(final Object other) {
		if (other instanceof WithArrays) {
			final WithArrays o = (WithArrays) other;
			return Arrays.equals(this.byteArray, o.byteArray) &&
				Arrays.equals(this.charArray, o.charArray) &&
				Arrays.equals(this.shortArray, o.shortArray) &&
				Arrays.equals(this.intArray, o.intArray) &&
				Arrays.equals(this.longArray, o.longArray) &&
				Arrays.equals(this.floatArray, o.floatArray) &&
				Arrays.equals(this.doubleArray, o.doubleArray) &&
				Arrays.equals(this.booleanArray, o.booleanArray) &&
				Arrays.equals(this.StringArray1, o.StringArray1) &&
				Arrays.equals(this.StringArray2, o.StringArray2) &&
				Arrays.equals(this.bbyteArray, o.bbyteArray) &&
				Arrays.equals(this.bcharArray, o.bcharArray) &&
				Arrays.equals(this.bshortArray, o.bshortArray) &&
				Arrays.equals(this.bintArray, o.bintArray) &&
				Arrays.equals(this.blongArray, o.blongArray) &&
				Arrays.equals(this.bfloatArray, o.bfloatArray) &&
				Arrays.equals(this.bdoubleArray, o.bdoubleArray) &&
				Arrays.equals(this.bbooleanArray, o.bbooleanArray);
		} else return false;
	}
}


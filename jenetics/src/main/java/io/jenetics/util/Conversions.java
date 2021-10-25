package io.jenetics.util;

public final class Conversions {
	private Conversions() {
	}

	public static int[] toIntArray(final double... values) {
		final var result = new int[values.length];
		for (int i = values.length; --i >= 0;) {
			result[i] = (int)values[i];
		}
		return result;
	}

	public static long[] toLongArray(final double... values) {
		final var result = new long[values.length];
		for (int i = values.length; --i >= 0;) {
			result[i] = (long)values[i];
		}
		return result;
	}

	public static Double[] box(final double... values) {
		final Double[] result = new Double[values.length];
		for (int i = values.length; --i >= 0;) {
			result[i] = values[i];
		}
		return result;
	}

	public static double[] unbox(final Double... values) {
		final double[] result = new double[values.length];
		for (int i = values.length; --i >= 0;) {
			result[i] = values[i];
		}
		return result;
	}

}

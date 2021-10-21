package io.jenetics.util;

public final class Conversions {
	private Conversions() {
	}

	public static int[] toIntArray(final double[] values) {
		final var result = new int[values.length];
		for (int i = 0; i < result.length; ++i) {
			result[i] = (int)values[i];
		}

		return result;
	}

	public static long[] toLongArray(final double[] values) {
		final var result = new long[values.length];
		for (int i = 0; i < result.length; ++i) {
			result[i] = (long)values[i];
		}

		return result;
	}

}

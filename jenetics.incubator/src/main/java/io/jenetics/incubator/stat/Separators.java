/*
 * Java Genetic Algorithm Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Author:
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmail.com)
 */
package io.jenetics.incubator.stat;

import java.util.Arrays;
import java.util.Objects;

/**
 * This class represents the bucket <em>separators</em> of a histogram. The
 * graph below shows the separators and the associated buckets.
 * <pre>{@code
 * -Ꝏ     min                                          max    Ꝏ
 *     -----+----+----+----+----+----+----+----+----+----+-----
 *       0  | 1  | 2  | 3  | 4  | 5  | 6  | 7  | 8  | 9  | 10    Buckets
 *     -----+----+----+----+----+----+----+----+----+----+-----
 *          0    1    2    3    4    5    6    7    8    9       Separators
 * }</pre>
 * The number of buckets is the number of separators plus one.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class Separators {
	private final double[] values;
	private final int start;
	private final int end;

	private Separators(final double[] values, final int start, final int end) {
		Objects.checkFromIndexSize(start, end - start, values.length);
		Objects.checkFromToIndex(start, end, values.length);

		this.values = values;
		this.start = start;
		this.end = end;
	}

	/**
	 * Create a new {@code Separators} object from the given {@code separators}.
	 * If no separator is given, this object defines only one bucket.
	 *
	 * @param values the separator values
	 * @throws IllegalArgumentException if the separator values are not finite
	 * or not unique
	 */
	public Separators(final double... values) {
		this(check(values), 0, values.length);
	}

	private static double[] check(final double[] values) {
		for (var separator : values) {
			if (!Double.isFinite(separator)) {
				throw new IllegalArgumentException(
					"All separator values must be finite: %s."
						.formatted(Arrays.toString(values))
				);
			}
		}

		final var result = values.clone();
		Arrays.sort(result);

		for (int i = 1; i < result.length; ++i) {
			if (result[i - 1] == result[i]) {
				throw new IllegalArgumentException(
					"Separators must be unique: %s."
						.formatted(Arrays.toString(result))
				);
			}
		}

		return result;
	}

	/**
	 * Return a copy of the separator values.
	 *
	 * @return a copy of the separator values
	 */
	public double[] toArray() {
		return Arrays.copyOfRange(values, start, end);
	}

	/**
	 * Create a new {@link Separators} object with the values from the given
	 * {@code start} and {@code end} index. Negative {@code end} indexes
	 * indicates that the separator values are trimmed from the <em>end</em>.
	 * {@snippet lang = "java":
	 * final var separators = new Separators(0, 1, 2, 3, 4, 5, 6, 7, 9, 10);
	 * System.out.println(separators.slice(1, -1));
	 * } The snippet above will print
     * <pre>
     * [1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0]
     * </pre>
	 *
	 * <b>Negative array indexes</b>
	 * <pre>{@code
	 *       0    1    2    3    4    5    6    7    8    9     Indexes
	 *     +----+----+----+----+----+----+----+----+----+----+
	 *     | 0  | 1  | 2  | 3  | 4  | 5  | 6  | 7  | 8  | 9  |  Array elements
	 *     +----+----+----+----+----+----+----+----+----+----+
	 *      -10   -9   -8   -7   -6   -5   -4   -3   -2   -1    Negative indexes
	 * }</pre>
     *
     * @param start the start index of the separator values (inclusively)
     * @param end the end index of the separator values (exclusively). If the
     * index is negative, the values will be trimmed from the other side of the
     * array
     * @return a new separators object from the given {@code start} index
     * @throws IndexOutOfBoundsException if the indexes are out of range
     */
    public Separators slice(final int start, final int end) {
	    final var s = start < 0 ? length() + start : start;
	    final var e = end < 0 ? length() + end : end;

        return new Separators(values, s, e);
    }

    /**
     * Return the number of separators.
     *
     * @return the number of separators
     */
    public int length() {
        return end - start;
    }

    /**
     * Return the separator at the given index.
     *
     * @param index the separator index
     * @return the separator at the given index.
     */
    public double at(final int index) {
        Objects.checkIndex(index, length());
        return values[start + index];
    }

    /**
     * Return the bucket index for the given value. A binary search is performed
     * for finding the bucket index. If no separator is defined, this method
     * will return zero, since there is only one big bucket.
     *
     * @param value the value to search
     * @return the bucket index
     */
	public int bucketIndexOf(final double value) {
        if (Double.isNaN(value)) {
            throw new IllegalArgumentException("NaN");
        }
        if (length() == 0) {
            return 0;
        }

        int low = 0;
        int high = length() - 1;

        while (low <= high) {
            if (value < at(low)) {
                return low;
            }
            if (value >= at(high)) {
                return high + 1;
            }

            final int mid = (low + high) >>> 1;
            if (value < at(mid)) {
                high = mid;
            } else if (value >= at(mid)) {
                low = mid + 1;
            }
        }

        throw new AssertionError("This line will never be reached.");
    }

    @Override
    public int hashCode() {
        int result = 1;
        for (int i = start; i < end; ++i) {
            final var element = values[i];
            result = 31 * result + Double.hashCode(element);
        }
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        return obj instanceof Separators sep &&
            Arrays.equals(values, start, end, sep.values, sep.start, sep.end);
    }

    @Override
    public String toString() {
        int max = length() - 1;
        if (max == -1) {
            return "[]";
        }

        final var b = new StringBuilder();
        b.append('[');
        for (int i = 0; ; ++i) {
            b.append(at(i));
            if (i == max) {
                return b.append(']').toString();
            }
            b.append(", ");
        }
    }

    /**
     * Return a new separator object with the given <em>finite</em> {@code min}
     * and {@code max} separator values and given number of classes between
     * minimal and maximal values.
     * <pre>{@code
     * -Ꝏ     min                                          max    Ꝏ
     *     -----+----+----+----+----+----+----+----+----+----+-----
     *       0  | 1  | 2  | 3  | 4  | 5  | 6  | 7  | 8  | 9  | 10    Buckets
     *     -----+----+----+----+----+----+----+----+----+----+-----
     *          0    1    2    3    4    5    6    7    8    9       Separators
     *            |                                        |
     *            +----------------------------------------+
     *                            Classes
     * }</pre>
     * The length of the created {@code Separator} class will be
     * {@code classes + 1} with equally spaced separators of
     * {@code (max - min)/nclasses}. {@code nclasses} will also define
     * {@code classes + 1} buckets.
     *
     * @param min the minimum separator value, inclusively
     * @param max the maximum separator value, exclusively
     * @param classes the number of classes between the {@code min} and
     *        {@code max} values
     * @return a new separator object
     * @throws IllegalArgumentException if {@code min >= max} or {@code min} or
     *        {@code max} are not finite or {@code classes < 1}
     */
	public static Separators of(
        final double min,
        final double max,
        final int classes
    ) {
        if (!Double.isFinite(min) || !Double.isFinite(max) || min >= max) {
            throw new IllegalArgumentException(
                "Invalid border: [min=%f, max=%f].".formatted(min, max)
            );
        }
        if (classes < 1) {
            throw new IllegalArgumentException(
                "Number of classes must at least one: %d."
                    .formatted(classes)
            );
        }

        final var stride = (max - min)/classes;
        final var separators = new double[classes + 1];

        separators[0] = min;
        separators[separators.length - 1] = max;
        for (int i = 1; i < classes; ++i) {
            separators[i] = separators[i - 1] + stride;
        }

        return new Separators(separators);
    }
}

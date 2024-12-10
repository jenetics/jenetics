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
 * Represents the actual frequency data for each bucket of the histogram.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
final class Frequencies {
	private final long[] values;
	private final int start;
	private final int end;

	private Frequencies(final long[] values, final int start, final int end) {
		Objects.checkFromIndexSize(start, end - start, values.length);
		Objects.checkFromToIndex(start, end, values.length);

		this.values = values;
		this.start = start;
		this.end = end;
	}

	/**
	 * Create a new frequency object from the given {@code frequency} values.
	 *
	 * @param values the frequency values
	 * @throws IllegalArgumentException if the given array has less than one
	 * element
	 */
	public Frequencies(final long... values) {
		this(values.clone(), 0, values.length);

		if (values.length < 1) {
			throw new IllegalArgumentException(
				"Frequency array length must be at least one, but was %d."
					.formatted(values.length)
			);
		}
	}

	/**
	 * Calculates the sample counts by summing the frequency values.
	 *
	 * @return the sample count
	 */
	public long sampleCount() {
		long sum = 0;
		for (var value : values) {
			sum += value;
		}
		return sum;
	}

	public long[] values() {
		return Arrays.copyOfRange(values, start, end);
	}

	/**
	 * Return the length of the frequency array.
	 *
	 * @return the length of the frequency array
	 */
	public int length() {
		return end - start;
	}

	/**
	 * Returns the frequency value at the given {@code index}.
	 *
	 * @param index the frequency index
	 * @return the frequency value at the given {@code index}
	 */
	public long at(final int index) {
		return values[start + index];
	}

	/**
	 * Create a new {@link Frequencies} object with the values from the given
	 * {@code start} index.
	 *
	 * @param start the start index of the frequency values
	 * @return a new frequency object from the given {@code start} index
	 * @throws IndexOutOfBoundsException if the indexes are out of range
	 */
	public Frequencies slice(final int start) {
		return new Frequencies(values, start, end);
	}

	/**
	 * Create a new {@link Frequencies} object with the values from the given
	 * {@code start} and {@code end} index. Negative {@code end} indexes
	 * indicates that the frequency values are trimmed from the <em>end</em>.
	 * {@snippet lang = "java":
	 * final var frequencies = new Frequencies(0, 1, 2, 3, 4, 5, 6, 7, 9, 10);
	 * System.out.println(frequencies.slice(1, -1));
	 * } The snippet above will print
     * <pre>
     * [1, 2, 3, 4, 5, 6, 7, 8, 9]
     * </pre>
     *
     * @param start the start index of the separator values (inclusively)
     * @param end the end index of the separator values (exclusively). If the
     * index is negative, the values will be trimmed from the other side of the
     * array
     * @return a new separators object from the given {@code start} index
     * @throws IndexOutOfBoundsException if the indexes are out of range
     */
    public Frequencies slice(final int start, final int end) {
        return new Frequencies(values, start, end < 0 ? this.end + end : end);
    }

    @Override
    public int hashCode() {
        int result = 1;
        for (int i = start; i < end; ++i) {
            final var element = values[i];
            result = 31 * result + Long.hashCode(element);
        }
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        return obj instanceof Frequencies f &&
            Arrays.equals(values, start, end, f.values, f.start, f.end);
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
}

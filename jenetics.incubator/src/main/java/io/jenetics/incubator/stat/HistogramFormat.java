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

import java.io.PrintStream;
import java.util.Arrays;

/**
 * Prints a graphical representation of the histogram to the given
 * {@code output}.
 * {@snippet lang="java":
 * final var builder = Histogram.Builder.of(0, 10, 20);
 * final var random = RandomGenerator.getDefault();
 * for (int i = 0; i < 10_000; ++i) {
 *     builder.accept(random.nextGaussian(5, 2));
 * }
 *
 * final Histogram observation = builder.build();
 * observation.print(System.out);
 * }
 * <p>
 * The code snippet above will lead to the following output.
 * <p>
 * <pre>{@code
 *      ┌───────────────────────────────────────────────────────────┐
 * 1020 ┤                            ██ ██                          │
 *  952 ┤                         ██ ██ ██ ██                       │
 *  884 ┤                      ██ ██ ██ ██ ██                       │
 *  816 ┤                      ██ ██ ██ ██ ██                       │
 *  748 ┤                      ██ ██ ██ ██ ██ ██                    │
 *  680 ┤                   ██ ██ ██ ██ ██ ██ ██ ██                 │
 *  612 ┤                   ██ ██ ██ ██ ██ ██ ██ ██                 │
 *  544 ┤                ██ ██ ██ ██ ██ ██ ██ ██ ██                 │
 *  476 ┤                ██ ██ ██ ██ ██ ██ ██ ██ ██ ██              │
 *  408 ┤             ██ ██ ██ ██ ██ ██ ██ ██ ██ ██ ██              │
 *  340 ┤             ██ ██ ██ ██ ██ ██ ██ ██ ██ ██ ██ ██           │
 *  272 ┤          ██ ██ ██ ██ ██ ██ ██ ██ ██ ██ ██ ██ ██ ██        │
 *  204 ┤       ██ ██ ██ ██ ██ ██ ██ ██ ██ ██ ██ ██ ██ ██ ██ ██     │
 *  136 ┤       ██ ██ ██ ██ ██ ██ ██ ██ ██ ██ ██ ██ ██ ██ ██ ██ ██  │
 *   68 ┤ ██ ██ ██ ██ ██ ██ ██ ██ ██ ██ ██ ██ ██ ██ ██ ██ ██ ██ ██  │
 *      └───┬──┬──┬──┬──┬──┬──┬──┬──┬──┬──┬──┬──┬──┬──┬──┬──┬──┬──┬─┘
 *     0.0                                                         10.0
 * }</pre>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
final class HistogramFormat {
	private static final String FULL = "██ ";
	private static final String EMPTY = "   ";

	private final int _frequencyStepCount;

	HistogramFormat(final int frequencyStepCount) {
		_frequencyStepCount = frequencyStepCount;
	}

	void print(PrintStream out, Histogram histogram) {
        /*
        final long[] values = histogram.frequencies().slice(1, -1).values();
        final long max = LongStream.of(values).max().orElse(0);
        final var stepSize = round(max/(double)_frequencyStepCount);

        final var maxStringLength = (int)Math.ceil(Math.log10(stepSize*_frequencyStepCount));
        final var formatString = "%" + maxStringLength + "d ┤ ";

        final int margin = maxStringLength + 1;
        final int length = values.length*FULL.length() + 4;

        // Print histogram
        out.print(" ".repeat(maxStringLength + 1));
        out.print("┌");
        for (int i = 0; i < values.length; i++) {
            out.print("───");
        }
        out.println("──┐");

        for (int i = _frequencyStepCount - 1; i >= 0; --i) {
            out.format(formatString, (i + 1)*stepSize);

for (long value : values) {
if (value - 0.5*stepSize >= i*stepSize) {
out.print(FULL);
} else {
out.print(EMPTY);
}
}
            out.println(" │");
        }

        out.print(" ".repeat(maxStringLength + 1));
        out.print("└──");
        for (int i = 0; i < values.length; i++) {
            out.print("─┬─");
        }
        out.println("┘");

        out.print(" ".repeat(maxStringLength));
        out.print(histogram.separators().at(0));

        final var spaces =
            maxStringLength +
            EMPTY.length()*(histogram.bucketCount() - 2) -
            Double.toString(histogram.separators().min()).length() -
            Double.toString(histogram.separators().max()).length();

        out.print(" ".repeat(spaces));
        out.print(histogram.separators().at(histogram.separators().length() - 1));
        out.println();

        // Print statistics.
        table(out, margin, length, new String[][] {
            {
                " N=%d".formatted(histogram.moments.count()),
                " ∧=%.3f".formatted(histogram.moments.min()),
                " ∨=%.3f".formatted(histogram.moments.max())
            },
            {
                " μ=%.4f".formatted(histogram.moments.mean()),
                " s²=%.4f".formatted(histogram.moments.variance()),
                " S=%.4f".formatted(histogram.moments.skewness())
            }
        });
         */
	}

	private static long round(final double value) {
		final long size = (long) Math.ceil(value / 100.0);
		final long count = (long) Math.ceil(value / size);
		return size * count;
	}

	private static void table(
		PrintStream out,
		int margin,
		int length,
		String[][] table
	) {
		if (table.length >= 1) {
			final int cols = table[0].length;
			top(out, margin, length, cols);

			for (int i = 0; i < table.length; ++i) {
				final String[] line = table[i];
				line(out, margin, length, line);

				if (i < table.length - 1) {
					middle(out, margin, length, cols);
				} else {
					bottom(out, margin, length, cols);
				}
			}
		}
	}

	private static void top(PrintStream out, int margin, int length, int cols) {
		final int[] cellSizes = partition(length - cols - 1, cols);

		out.print(" ".repeat(margin));
		out.print("┌");
		for (int i = 0; i < cols - 1; ++i) {
			out.print("─".repeat(cellSizes[i]));
			out.print("┬");
		}
		out.print("─".repeat(cellSizes[cols - 1]));
		out.println("┐");
	}

	private static void middle(PrintStream out, int margin, int length, int cols) {
		final int[] cellSizes = partition(length - cols - 1, cols);

		out.print(" ".repeat(margin));
		out.print("├");
		for (int i = 0; i < cols - 1; ++i) {
			out.print("─".repeat(cellSizes[i]));
			out.print("┼");
		}
		out.print("─".repeat(cellSizes[cols - 1]));
		out.println("┤");
	}

	private static void line(PrintStream out, int margin, int length, String... cols) {
		final int[] cellSizes = partition(length - cols.length - 1, cols.length);

		out.print(" ".repeat(margin));
		out.print("│");
		for (int i = 0; i < cols.length - 1; ++i) {
			out.printf("%-" + cellSizes[i] + "s", cols[i]);
			out.print("│");
		}
		out.printf("%-" + cellSizes[cols.length - 1] + "s", cols[cols.length - 1]);
		out.println("│");
	}

	private static void bottom(PrintStream out, int margin, int length, int cols) {
		final int[] cellSizes = partition(length - cols - 1, cols);

		out.print(" ".repeat(margin));
		out.print("└");
		for (int i = 0; i < cols - 1; ++i) {
			out.print("─".repeat(cellSizes[i]));
			out.print("┴");
		}
		out.print("─".repeat(cellSizes[cols - 1]));
		out.println("┘");
	}

	private static int[] partition(final int size, final int parts) {
		final int[] partition = new int[parts];

		final int bulk = size/parts;
		final int rest = size%parts;

		Arrays.fill(partition, bulk);
		for (int i = 0; i < rest; ++i) {
			++partition[i];
		}

		return partition;
	}

}

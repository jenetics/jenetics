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
package io.jenetics.distassert.internal.screen;

import static io.jenetics.distassert.internal.screen.DrawChars.LIGHT_DOWN_AND_HORIZONTAL;
import static io.jenetics.distassert.internal.screen.DrawChars.LIGHT_DOWN_AND_LEFT;
import static io.jenetics.distassert.internal.screen.DrawChars.LIGHT_HORIZONTAL;
import static io.jenetics.distassert.internal.screen.DrawChars.LIGHT_LEFT;
import static io.jenetics.distassert.internal.screen.DrawChars.LIGHT_UP_AND_RIGHT;
import static io.jenetics.distassert.internal.screen.DrawChars.LIGHT_VERTICAL;
import static io.jenetics.distassert.internal.screen.DrawChars.LIGHT_VERTICAL_AND_LEFT;
import static java.util.Objects.requireNonNull;

import java.io.PrintStream;
import java.util.Arrays;

import io.jenetics.distassert.observation.Histogram;

/**
 * Console renderer for histogram objects.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 9.2
 * @since 9.2
 */
public final class HistogramRenderer {
	private static final int DEFAULT_WIDTH = 70;
	private static final int DEFAULT_HEIGHT = 18;
	private static final int MAX_SLOT_WIDTH = 3;

	private HistogramRenderer() {
	}

	public static void print(final Histogram histogram, final PrintStream out) {
		print(histogram, out, DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}

	public static void print(
		final Histogram histogram,
		final PrintStream out,
		final int width,
		final int height
	) {
		render(histogram, width, height).print(requireNonNull(out));
	}

	static Screen render(
		final Histogram histogram,
		final int width,
		final int height
	) {
		requireNonNull(histogram);
		if (height < 4) {
			throw new IllegalArgumentException(
				"Histogram height must be at least four: %d.".formatted(height)
			);
		}

		final long[] frequencies = histogram.buckets().frequencies();
		final long maximum = Arrays.stream(frequencies).max().orElse(0);
		final AxisScale scale = AxisScale.of(maximum, height - 2);
		final int labelWidth = Long.toString(scale.maximum()).length();
		final int axisX = labelWidth + 3;
		final String minLabel = Double.toString(histogram.interval().min());
		final String maxLabel = Double.toString(histogram.interval().max());
		final int maxLabelExtension = maxLabel.length() - maxLabel.length()/2;
		final int availablePlotWidth = width - axisX - maxLabelExtension;
		final int slotWidth = Math.min(
			MAX_SLOT_WIDTH,
			availablePlotWidth/frequencies.length
		);

		if (slotWidth < 1) {
			throw new IllegalArgumentException(
				"Histogram width %d is too small for %d buckets."
					.formatted(width, frequencies.length)
			);
		}
		final int plotWidth = slotWidth*frequencies.length;
		final int axisBottom = scale.height();
		final int actualHeight = axisBottom + 2;

		final int axisRight = axisX + plotWidth;
		final int minLabelX = axisX - minLabel.length()/2;
		final int maxLabelX = axisRight - maxLabel.length()/2;
		final int actualWidth = Math.max(
			axisRight + 1,
			maxLabelX + maxLabel.length()
		);
		if (minLabelX < 0 || actualWidth > width) {
			throw new IllegalArgumentException(
				"Histogram width %d is too small for the interval labels."
					.formatted(width)
			);
		}

		final var screen = new Screen(actualWidth, actualHeight);
		drawAxes(screen, scale, axisX, axisRight, axisBottom);

		drawBars(screen, frequencies, scale, axisX, slotWidth, axisBottom);
		drawBucketTicks(screen, frequencies.length, axisX, slotWidth, axisBottom);
		screen.draw(new Text(minLabelX, actualHeight - 1, minLabel));
		screen.draw(new Text(maxLabelX, actualHeight - 1, maxLabel));

		return screen;
	}

	private static void drawAxes(
		final Screen screen,
		final AxisScale scale,
		final int axisX,
		final int axisRight,
		final int axisBottom
	) {
		for (int y = 0; y < axisBottom; ++y) {
			screen.set(axisX, y, LIGHT_VERTICAL);
		}
		screen.set(axisX, axisBottom, LIGHT_UP_AND_RIGHT);
		for (int x = axisX + 1; x < axisRight; ++x) {
			screen.set(x, axisBottom, LIGHT_HORIZONTAL);
		}
		screen.set(axisRight, axisBottom, LIGHT_LEFT);

		if (scale.maximum() == 0) {
			drawYLabel(screen, 0, axisX, axisBottom - 1);
		} else {
			for (int y = 0; y < scale.height(); y += scale.labelSpacing()) {
				drawYLabel(
					screen,
					scale.maximum() - y*scale.unitsPerRow(),
					axisX,
					y
				);
			}
		}
	}

	private static void drawBars(
		final Screen screen,
		final long[] frequencies,
		final AxisScale scale,
		final int axisX,
		final int slotWidth,
		final int axisBottom
	) {
		final int start = axisX + 1;
		final int barWidth = Math.max(1, slotWidth - 1);

		for (int i = 0; i < frequencies.length; ++i) {
			final BarHeight height = scale(
				frequencies[i],
				scale.unitsPerRow(),
				scale.height()
			);
			screen.draw(new Bar(
				start + i*slotWidth,
				axisBottom - 1,
				barWidth,
				height.full(),
				height.fraction()
			));
		}
	}

	private static BarHeight scale(
		final long value,
		final long unitsPerRow,
		final int maximumHeight
	) {
		if (value == 0) {
			return new BarHeight(0, 0);
		}

		final int full = Math.min(maximumHeight, (int)(value/unitsPerRow));
		final double fraction = full == maximumHeight
			? 0
			: value%unitsPerRow/(double)unitsPerRow;

		return new BarHeight(full, fraction);
	}

	private static void drawYLabel(
		final Screen screen,
		final long value,
		final int axisX,
		final int y
	) {
		final String label = Long.toString(value);
		screen.draw(new Text(axisX - label.length() - 1, y, label));
		screen.set(
			axisX,
			y,
			y == 0 ? LIGHT_DOWN_AND_LEFT : LIGHT_VERTICAL_AND_LEFT
		);
	}

	private static void drawBucketTicks(
		final Screen screen,
		final int bucketCount,
		final int axisX,
		final int slotWidth,
		final int axisBottom
	) {
		for (int i = 1; i < bucketCount; ++i) {
			final int x = axisX + i*slotWidth;
			screen.set(x, axisBottom, LIGHT_DOWN_AND_HORIZONTAL);
		}
	}

	private record BarHeight(int full, double fraction) {
	}

	private record AxisScale(
		long maximum,
		long unitsPerRow,
		int height,
		int labelSpacing
	) {
		static AxisScale of(final long maximum, final int height) {
			if (maximum == 0) {
				return new AxisScale(0, 1, 1, 1);
			}

			final long desiredMaximum = niceMaximum(maximum);
			final long unitsPerRow = niceStep(desiredMaximum, height);
			final long niceMaximum = roundUp(desiredMaximum, unitsPerRow);
			final int actualHeight = (int)(niceMaximum/unitsPerRow);

			return new AxisScale(
				niceMaximum,
				unitsPerRow,
				actualHeight,
				labelSpacing(actualHeight, unitsPerRow)
			);
		}

		private static long roundUp(final long value, final long step) {
			final long quotient = value/step + (value%step == 0 ? 0 : 1);
			return quotient > Long.MAX_VALUE/step
				? Long.MAX_VALUE
				: quotient*step;
		}

		private static int labelSpacing(final int height, final long unitsPerRow) {
			for (int spacing = 1; spacing <= height; ++spacing) {
				if (height%spacing == 0 &&
					height/spacing <= 6 &&
					unitsPerRow <= Long.MAX_VALUE/spacing &&
					isNice(unitsPerRow*spacing))
				{
					return spacing;
				}
			}
			return 1;
		}

		private static boolean isNice(final long value) {
			long normalized = value;
			while (normalized%10 == 0) {
				normalized /= 10;
			}
			return normalized == 1 || normalized == 2 || normalized == 5;
		}

		private static long niceMaximum(final long value) {
			if (value <= 10) {
				return value;
			}

			final long quantum = value < 100
				? 5
				: powerOfTen(Long.toString(value).length() - 2);
			final long quotient = value/quantum + (value%quantum == 0 ? 0 : 1);
			return quotient > Long.MAX_VALUE/quantum
				? Long.MAX_VALUE
				: quotient*quantum;
		}

		private static long niceStep(final long maximum, final int height) {
			if (height == 1) {
				return maximum;
			}

			final double raw = maximum/(double)height;
			if (raw <= 1) {
				return 1;
			}

			final double magnitude = Math.pow(10, Math.floor(Math.log10(raw)));
			final double fraction = raw/magnitude;
			final double niceFraction = fraction <= 1
				? 1
				: fraction <= 2
					? 2
					: fraction <= 5 ? 5 : 10;
			final double result = niceFraction*magnitude;

			return result >= Long.MAX_VALUE ? Long.MAX_VALUE : (long)result;
		}

		private static long powerOfTen(final int exponent) {
			long result = 1;
			for (int i = 0; i < exponent; ++i) {
				result *= 10;
			}
			return result;
		}
	}
}

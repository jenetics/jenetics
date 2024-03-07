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
package io.jenetics.testfixtures.stat;

import static java.util.Objects.requireNonNull;

import java.util.Arrays;
import java.util.function.DoubleConsumer;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public final class Hist implements DoubleConsumer {
	private final double[] _bins;
	private final long[] _table;

	private long _count = 0;

	private Hist(final double... bins) {
		_bins = bins;
		_table = new long[bins.length + 1];
	}

	public int classes() {
		return _bins.length - 1;
	}

	public long count() {
		return _count;
	}

	@Override
	public void accept(final double value) {
		++_table[index(value)];
		++_count;
	}

	private int index(final double value) {
		int low = 0;
		int high = _bins.length - 1;

		while (low <= high) {
			if (value < _bins[low]) {
				return low;
			}
			if (value >= _bins[high]) {
				return high + 1;
			}

			final int mid = (low + high) >>> 1;
			if (value < _bins[mid]) {
				high = mid;
			} else if (value >= _bins[mid]) {
				low = mid + 1;
			}
		}

		throw new AssertionError("This line will never be reached.");
	}

	public double chi2(final Cdf cdf) {
		requireNonNull(cdf);

		double chi2 = 0;
		for (int i = 0; i < classes(); ++i) {
			final var e = p_i(i, cdf)*_count;
			final var o2 = _table[i + 1]*_table[i + 1];
			chi2 += o2/e;
		}

		return chi2 - _count;
	}

	private double p_i(final int i, final Cdf cdf) {
		return cdf.apply(_bins[i + 1]) - cdf.apply(_bins[i]);
	}

	@Override
	public String toString() {
		return Arrays.toString(_bins) + "\n" +
			Arrays.toString(_table);
	}

	public static Hist of(
		final double min,
		final double max,
		final int classes
	) {
		if (!Double.isFinite(min) || !Double.isFinite(max) || min >= max) {
			throw new IllegalArgumentException();
		}
		if (classes < 1) {
			throw new IllegalArgumentException();
		}

		final double stride = (max - min)/classes;
		final double[] bins = new double[classes + 1];

		bins[0] = min;
		bins[bins.length - 1] = max;
		for (int i = 1; i < classes; ++i) {
			bins[i] = bins[i - 1] + stride;
		}

		return new Hist(bins);
	}


	public static void main(String[] args) {
		final var hist = Hist.of(2, 3, 10);
		hist.accept(1.9);
		hist.accept(2.05);
		hist.accept(2.15);
		hist.accept(2.25);
		hist.accept(2.35);
		hist.accept(2.45);
		hist.accept(2.55);
		hist.accept(2.65);
		hist.accept(2.75);
		hist.accept(2.85);
		hist.accept(2.95);
		hist.accept(3);
		System.out.println(hist);
	}

}

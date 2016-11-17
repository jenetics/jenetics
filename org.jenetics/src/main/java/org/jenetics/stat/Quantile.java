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
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 */
package org.jenetics.stat;

import static java.lang.Double.compare;
import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static org.jenetics.internal.util.Equality.eq;

import java.util.Arrays;
import java.util.function.DoubleConsumer;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collector;

/**
 * Implementation of the quantile estimation algorithm published by
 * <p>
 * <strong>Raj JAIN and Imrich CHLAMTAC</strong>:
 * <em>
 *     The P<sup>2</sup> Algorithm for Dynamic Calculation of Quantiles and
 *     Histograms Without Storing Observations
 * </em>
 * <br>
 * [<a href="http://www.cse.wustl.edu/~jain/papers/ftp/psqr.pdf">Communications
 * of the ACM; October 1985, Volume 28, Number 10</a>]
 * <p>
 * This class is designed to work with (though does not require) streams. For
 * example, you can compute the quantile with:
 * <pre>{@code
 * final DoubleStream stream = ...
 * final Quantile quantile = stream.collect(
 *         () -> new Quantile(0.23),
 *         Quantile::accept,
 *         Quantile::combine
 *     );
 * }</pre>
 *
 * <p>
 * <b>Implementation note:</b>
 * <i>This implementation is not thread safe. However, it is safe to use on a
 * parallel stream, because the parallel implementation of
 * {@link java.util.stream.Stream#collect Stream.collect()}provides the
 * necessary partitioning, isolation, and merging of results for safe and
 * efficient parallel execution.</i>
 * <br>
 * <i>Using this class in the {@code collect} method of an parallel stream can
 * lead to an reduced accuracy of the quantile value. Since this implementation
 * is an estimation algorithm, combining the estimations will only work for
 * large streams ({@code size >> 1000}).</i>
 *
 * @see <a href="http://en.wikipedia.org/wiki/Quantile">Wikipedia: Quantile</a>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 3.7
 */
public class Quantile implements DoubleConsumer {

	private long _samples = 0;

	// The desired quantile.
	private final double _quantile;

	// Marker heights.
	private final double[] _q = {0, 0, 0, 0, 0};

	// Marker positions.
	private final double[] _n = {0, 0, 0, 0, 0};

	// Desired marker positions.
	private final double[] _nn = {0, 0, 0};

	// Desired marker position increments.
	private final double[] _dn = {0, 0, 0};

	private boolean _initialized;

	/**
	 * Create a new quantile accumulator with the given value.
	 *
	 * @param quantile the wished quantile value.
	 * @throws IllegalArgumentException if the {@code quantile} is not in the
	 *         range {@code [0, 1]}.
	 */
	public Quantile(final double quantile) {
		_quantile = quantile;
		init(quantile);
	}

	private void init(final double quantile) {
		check(quantile);

		Arrays.fill(_q, 0);
		Arrays.fill(_n, 0);
		Arrays.fill(_nn, 0);
		Arrays.fill(_dn, 0);

		_n[0] = -1.0;
		_q[2] = 0.0;
		_initialized =
			compare(quantile, 0.0) == 0 ||
			compare(quantile, 1.0) == 0;

		_samples = 0;
	}

	private static void check(final double quantile) {
		if (quantile < 0.0 || quantile > 1) {
			throw new IllegalArgumentException(format(
				"Quantile (%s) not in the valid range of [0, 1]", quantile
			));
		}
	}

	/**
	 * Reset this object to its initial state.
	 */
	public void reset() {
		init(_quantile);
	}

	/**
	 * Return the <em>quantile</em> {@code this} object has been parametrized
	 * with.
	 *
	 * @since 3.1
	 *
	 * @return the <em>quantile</em> {@code this} object has been parametrized
	 *         with
	 */
	public double getQuantile() {
		return _quantile;
	}

	/**
	 * Return the computed quantile value.
	 *
	 * @return the quantile value.
	 */
	public double getValue() {
		return _q[2];
	}

	/**
	 * Return the number of samples the quantile value  was calculated of.
	 *
	 *
	 * @return the number of samples the quantile value  was calculated of
	 */
	public long getSamples() {
		return _samples;
	}

	@Override
	public void accept(final double value) {
		if (!_initialized) {
			initialize(value);
		} else {
			update(value);
		}

		++_samples;
	}

	/**
	 * Combine two {@code Quantile} objects.
	 *
	 * @since 3.1
	 *
	 * @param other the other {@code Quantile} object to combine
	 * @return {@code this}
	 * @throws java.lang.NullPointerException if the {@code other} object is
	 *         {@code null}.
	 * @throws java.lang.IllegalArgumentException if the {@link #getQuantile}
	 *         of the {@code other} object differs from {@code this} one.
	 */
	public Quantile combine(final Quantile other) {
		if (_quantile != other._quantile) {
			throw new IllegalArgumentException(format(
				"Can't perform combine, the quantile are not equal: %s != %s",
				_quantile, other._quantile
			));
		}

		_samples += other._samples;

		if (_quantile == 0.0) {
			_q[2] = Math.min(_q[2], other._q[2]);
		} else if (_quantile == 1.0) {
			_q[2] = Math.max(_q[2], other._q[2]);
		} else {
			// Combine the marker positions.
			_n[1] += other._n[1];
			_n[2] += other._n[2];
			_n[3] += other._n[3];
			_n[4] += other._n[4];

			// Combine the marker height.
			_q[0] = Math.min(_q[0], other._q[0]);
			_q[1] = (_q[1] + other._q[1])*0.5;
			_q[2] = (_q[2] + other._q[2])*0.5;
			_q[3] = (_q[3] + other._q[3])*0.5;
			_q[4] = Math.max(_q[4], other._q[4]);

			// Combine position of markers.
			_nn[0] += other._nn[0];
			_nn[1] += other._nn[1];
			_nn[2] += other._nn[2];

			adjustMarkerHeights();
		}

		return this;
	}

	private void initialize(double value) {
		if (_n[0] < 0.0) {
			_n[0] = 0.0;
			_q[0] = value;
		} else if (_n[1] == 0.0) {
			_n[1] = 1.0;
			_q[1] = value;
		} else if (_n[2] == 0.0) {
			_n[2] = 2.0;
			_q[2] = value;
		} else if (_n[3] == 0.0) {
			_n[3] = 3.0;
			_q[3] = value;
		} else if (_n[4] == 0.0) {
			_n[4] = 4.0;
			_q[4] = value;
		}

		if (_n[4] != 0.0) {
			Arrays.sort(_q);

			_nn[0] = 2.0*_quantile;
			_nn[1] = 4.0*_quantile;
			_nn[2] = 2.0*_quantile + 2.0;

			_dn[0] = _quantile/2.0;
			_dn[1] = _quantile;
			_dn[2] = (1.0 + _quantile)/2.0;

			_initialized = true;
		}
	}

	private void update(double value) {
		assert _initialized;

		// If min or max, handle as special case; otherwise, ...
		if (_quantile == 0.0) {
			if (value < _q[2]) {
				_q[2] = value;
			}
		} else if (_quantile == 1.0) {
			if (value > _q[2]) {
				_q[2] = value;
			}
		} else {
			// Increment marker locations and update min and max.
			if (value < _q[0]) {
				++_n[1]; ++_n[2]; ++_n[3]; ++_n[4]; _q[0] = value;
			} else if (value < _q[1]) {
				++_n[1]; ++_n[2]; ++_n[3]; ++_n[4];
			} else if (value < _q[2]) {
				++_n[2]; ++_n[3]; ++_n[4];
			} else if (value < _q[3]) {
				++_n[3]; ++_n[4];
			} else if (value < _q[4]) {
				++_n[4];
			} else {
				++_n[4]; _q[4] = value;
			}

			// Increment positions of markers k + 1
			_nn[0] += _dn[0];
			_nn[1] += _dn[1];
			_nn[2] += _dn[2];

			adjustMarkerHeights();
		}
	}

	// Adjust heights of markers 0 to 2 if necessary
	private void adjustMarkerHeights() {
		double mm = _n[1] - 1.0;
		double mp = _n[1] + 1.0;
		if (_nn[0] >= mp && _n[2] > mp) {
			_q[1] = qPlus(mp, _n[0], _n[1], _n[2], _q[0], _q[1], _q[2]);
			_n[1] = mp;
		} else if (_nn[0] <= mm && _n[0] < mm) {
			_q[1] = qMinus(mm, _n[0], _n[1], _n[2], _q[0], _q[1], _q[2]);
			_n[1] = mm;
		}

		mm = _n[2] - 1.0;
		mp = _n[2] + 1.0;
		if (_nn[1] >= mp && _n[3] > mp) {
			_q[2] = qPlus(mp, _n[1], _n[2], _n[3], _q[1], _q[2], _q[3]);
			_n[2] = mp;
		} else if (_nn[1] <= mm && _n[1] < mm) {
			_q[2] = qMinus(mm, _n[1], _n[2], _n[3], _q[1], _q[2], _q[3]);
			_n[2] = mm;
		}

		mm = _n[3] - 1.0;
		mp = _n[3] + 1.0;
		if (_nn[2] >= mp && _n[4] > mp) {
			_q[3] = qPlus(mp, _n[2], _n[3], _n[4], _q[2], _q[3], _q[4]);
			_n[3] = mp;
		} else if (_nn[2] <= mm && _n[2] < mm) {
			_q[3] = qMinus(mm, _n[2], _n[3], _n[4], _q[2], _q[3], _q[4]);
			_n[3] = mm;
		}
	}

	private static double qPlus(
		final double mp,
		final double m0,
		final double m1,
		final double m2,
		final double q0,
		final double q1,
		final double q2
	) {
		double result = q1 +
					((mp - m0)*(q2 - q1)/(m2 - m1) +
					(m2 - mp)*(q1 - q0)/(m1 - m0))/(m2 - m0);

		if (result > q2) {
			result = q1 + (q2 - q1)/(m2 - m1);
		}

		return result;
	}

	private static double qMinus(
		final double mm,
		final double m0,
		final double m1,
		final double m2,
		final double q0,
		final double q1,
		final double q2
	) {
		double result = q1 -
					((mm - m0)*(q2 - q1)/(m2 - m1) +
					(m2 - mm)*(q1 - q0)/(m1 - m0))/(m2 - m0);

		if (q0 > result) {
			result = q1 + (q0 - q1)/(m0 - m1);
		}

		return result;
	}

	/**
	 * Compares the state of two {@code Quantile} objects. This is
	 * a replacement for the {@link #equals(Object)} which is not advisable to
	 * implement for this mutable object. If two object have the same state, it
	 * has still the same state when updated with the same value.
	 * <pre>{@code
	 * final Quantile q1 = ...;
	 * final Quantile q2 = ...;
	 *
	 * if (q1.sameState(q2)) {
	 *     final double value = random.nextDouble();
	 *     q1.accept(value);
	 *     q2.accept(value);
	 *
	 *     assert q1.sameState(q2);
	 *     assert q2.sameState(q1);
	 *     assert q1.sameState(q1);
	 * }
	 * }</pre>
	 *
	 * @since 3.7
	 *
	 * @param other the other object for the test
	 * @return {@code true} the {@code this} and the {@code other} objects have
	 *         the same state, {@code false} otherwise
	 */
	public boolean sameState(final Quantile other) {
		return eq(_quantile, other._quantile) &&
			eq(_dn, other._dn) &&
			eq(_n, other._n) &&
			eq(_nn, other._nn) &&
			eq(_q, other._q);
	}

	@Override
	public String toString() {
		return format(
			"%s[samples=%d, quantile=%f]",
			getClass().getSimpleName(), getSamples(), getValue()
		);
	}


	static Quantile median() {
		return new Quantile(0.5);
	}

	/**
	 * Return a {@code Collector} which applies an double-producing mapping
	 * function to each input element, and returns quantiles for the resulting
	 * values.
	 *
	 * <pre>{@code
	 * final Stream<SomeObject> stream = ...
	 * final Quantile quantile = stream
	 *     .collect(toQuantile(0.25, v -> v.doubleValue()));
	 * }</pre>
	 *
	 * @param quantile the wished quantile value.
	 * @param mapper a mapping function to apply to each element
	 * @param <T> the type of the input elements
	 * @return a {@code Collector} implementing the quantiles reduction
	 * @throws java.lang.NullPointerException if the given {@code mapper} is
	 *         {@code null}
	 * @throws IllegalArgumentException if the {@code quantile} is not in the
	 *         range {@code [0, 1]}.
	 */
	public static <T> Collector<T, ?, Quantile> toQuantile(
		final double quantile,
		final ToDoubleFunction<? super T> mapper
	) {
		check(quantile);
		requireNonNull(mapper);

		return Collector.of(
			() -> new Quantile(quantile),
			(r, t) -> r.accept(mapper.applyAsDouble(t)),
			Quantile::combine
		);
	}

	/**
	 * Return a {@code Collector} which applies an double-producing mapping
	 * function to each input element, and returns the median for the resulting
	 * values.
	 *
	 * <pre>{@code
	 * final Stream<SomeObject> stream = ...
	 * final Quantile median = stream.collect(toMedian(v -> v.doubleValue()));
	 * }</pre>
	 *
	 * @param mapper a mapping function to apply to each element
	 * @param <T> the type of the input elements
	 * @return a {@code Collector} implementing the quantiles reduction
	 * @throws java.lang.NullPointerException if the given {@code mapper} is
	 *         {@code null}
	 */
	public static <T> Collector<T, ?, Quantile> toMedian(
		final ToDoubleFunction<? super T> mapper
	) {
		requireNonNull(mapper);

		return Collector.of(
			Quantile::median,
			(r, t) -> r.accept(mapper.applyAsDouble(t)),
			Quantile::combine
		);
	}

}

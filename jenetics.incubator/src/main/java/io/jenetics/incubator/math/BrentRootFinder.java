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
package io.jenetics.incubator.math;

import static java.lang.Math.abs;
import static java.util.Objects.requireNonNull;

import java.util.function.DoubleUnaryOperator;

import io.jenetics.incubator.stat.Interval;

/**
 * Brent root finding method.
 *
 * @param epsilon the <em>relative</em> tolerance value
 * @param maxIterations the maximal number of iterations
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 8.2
 * @since 8.2
 */
public record BrentRootFinder(double epsilon, int maxIterations)
	implements RootFinder
{

	/**
	 * Represents the found root value plus error estimation and performed
	 * iterations.
	 *
	 * @param value the found root value
	 * @param error the error estimate
	 * @param iterations the performed iterations
	 */
	public record Root(double value, double error, int iterations) {
	}

	/**
	 * Default Brent's solver.
	 */
	public static final BrentRootFinder DEFAULT = new BrentRootFinder();

	public BrentRootFinder {
		if (epsilon <= 0) {
			throw new IllegalArgumentException(
				"Epsilon must be positive: " + epsilon
			);
		}
		if (maxIterations <= 0) {
			throw new IllegalArgumentException(
				"Maximal iterations must be positive: " + maxIterations
			);
		}
	}

	public BrentRootFinder(final double epsilon) {
		this(epsilon, 1_000_000);
	}

	public BrentRootFinder() {
		this(0x1.0p-52);
	}

	@Override
	public double solve(final DoubleUnaryOperator fn, final Interval interval) {
		return root(fn, interval).value();
	}

	public Root root(final DoubleUnaryOperator fn, final Interval interval) {
		final double m = 0.5*interval.min() + 0.5*interval.max();

		final var fm = fn.applyAsDouble(m);
		if (abs(fm) < epsilon) {
			return new Root(m, abs(fm), 0);
		}

		final var fa = fn.applyAsDouble(interval.min());
		if (abs(fa) < epsilon) {
			return new Root(interval.min(), abs(fa), 0);
		}
		if (Double.compare(fm*fa, 0.0) < 0) {
			return brent(fn, new Interval(interval.min(), m));
		}

		final var fb = fn.applyAsDouble(interval.max());
		if (abs(fb) < epsilon) {
			return new Root(interval.max(), abs(fb), 0);
		}
		if (Double.compare(fm*fb, 0.0) < 0) {
			return brent(fn, new Interval(m, interval.max()));
		}

		throw new ArithmeticException("No root between interval " + interval);
	}

	private Root brent(final DoubleUnaryOperator fn, final Interval interval) {
		requireNonNull(fn);
		requireNonNull(interval);

		// Set up aliases to match Brent's notation.
		double a = interval.min();
		double b = interval.max();
		double t = epsilon;

		// Implementation and notation based on Chapter 4 in "Algorithms for
		// Minimization without Derivatives" by Richard Brent.
		double c, d, e, fa, fb, fc, tol, m, p, q, r, s, err;

		fa = fn.applyAsDouble(a);
		fb = fn.applyAsDouble(b);

		c = a; fc = fa; d = e = b - a;

		int iteration = 0;
		while (true) {
			++iteration;

			if (abs(fc) < abs(fb)) {
				a  =  b;  b =  c;  c =  a;
				fa = fb; fb = fc; fc = fa;
			}

			tol = 2.0*t*abs(b) + t;
			err = m = 0.5*(c - b);

			// Exact comparison with 0 is OK here.
			if (abs(m) > tol && nonZero(fb)) {
				// See if bisection is forced.
				if (abs(e) < tol || abs(fa) <= abs(fb)) {
					d = e = m;
				} else {
					s = fb/fa;
					if (a == c) {
						// Linear interpolation.
						p = 2.0*m*s;
						q = 1.0 - s;
					} else {
						// Inverse quadratic interpolation.
						q = fa/fc;
						r = fb/fc;
						p = s*(2.0*m*q*(q - r) - (b - a)*(r - 1.0));
						q = (q - 1.0)*(r - 1.0)*(s - 1.0);
					}
					if (p > 0.0) {
						q = -q;
					} else {
						p = -p;
					}
					s = e;
					e = d;
					if (2.0*p < 3.0*m*q - abs(tol*q) && p < abs(0.5*s*q)) {
						d = p/q;
					} else {
						d = e = m;
					}
				}
				a = b;
				fa = fb;

				if (abs(d) > tol) {
					b += d;
				} else if (m > 0.0) {
					b += tol;
				} else {
					b -= tol;
				}
				if (iteration == maxIterations) {
					return new Root(b, err, iteration);
				}

				fb = fn.applyAsDouble(b);
				if ((fb > 0.0 && fc > 0.0) ||
					(fb <= 0.0 && fc <= 0.0))
				{
					c = a; fc = fa; d = e = b - a;
				}
			} else {
				return new Root(b, err, iteration);
			}
		}
	}

	private static boolean nonZero(final double value) {
		return abs(value) > Double.MIN_VALUE;
	}

}

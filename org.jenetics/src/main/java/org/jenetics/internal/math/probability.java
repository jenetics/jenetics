/*
 * Java Genetic Algorithm Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * Author:
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 *
 */
package org.jenetics.internal.math;

import static org.jenetics.util.math.pow;

import org.jenetics.util.StaticObject;

/**
 * Mathematical functions regarding probabilities.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since @__new_version__@
 * @version @__new_version__@ &mdash; <em>$Date$</em>
 */
public final class probability extends StaticObject {
	private probability() {}

	static final long INT_RANGE = pow(2, 32) - 1;

	/**
	 * Maps the probability, given in the range {@code [0, 1]}, to an
	 * integer in the range {@code [Integer.MIN_VALUE, Integer.MAX_VALUE]}.
	 *
	 * @see {@link #toInt(double)}
	 * @see {@link #toFloat(int)}
	 *
	 * @param probability the probability to widen.
	 * @return the widened probability.
	 */
	public static int toInt(final float probability) {
		return Math.round(INT_RANGE*probability + Integer.MIN_VALUE);
	}

	/**
	 * Maps the probability, given in the range {@code [0, 1]}, to an
	 * integer in the range {@code [Integer.MIN_VALUE, Integer.MAX_VALUE]}.
	 *
	 * @see {@link #toInt(float)}
	 * @see {@link #toFloat(int)}
	 *
	 * @param probability the probability to widen.
	 * @return the widened probability.
	 */
	public static int toInt(final double probability) {
		return (int)(Math.round(INT_RANGE*probability) + Integer.MIN_VALUE);
	}

	/**
	 * Maps the <i>integer</i> probability, within the range
	 * {@code [Integer.MIN_VALUE, Integer.MAX_VALUE]} back to a float
	 * probability within the range {@code [0, 1]}.
	 *
	 * @see {@link #toInt(float)}
	 * @see {@link #toInt(double)}
	 *
	 * @param probability the <i>integer</i> probability to map.
	 * @return the mapped probability within the range {@code [0, 1]}.
	 */
	public static float toFloat(final int probability) {
		final long value = (long)probability + Integer.MAX_VALUE;
		return (float)(value/(double)INT_RANGE);
	}
}

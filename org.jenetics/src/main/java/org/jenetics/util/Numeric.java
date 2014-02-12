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
package org.jenetics.util;

import java.util.Comparator;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version @__version__@ &mdash; <em>$Date: 2014-02-12 $</em>
 * @since @__version__@
 */
public interface Numeric<N> extends Comparator<N> {

	public N abs(final N x);

	public N negate(final N x);

	public N plus(final N x, final N y);

	public N minus(final N x, final N y);

	public N times(final N x, final N y);

	public N div(final N x, final N y);

	public N mean(final N x, final N y);

	public static interface Conversion<T> {
		public T fromNumber(final Number value);
		public Number toNumber(final T value);
	}

	public static final Numeric<Double> DOUBLE = new DoubleNumeric();

	public static final Numeric<Long> LONG = new LongNumeric();

}

final class DoubleNumeric implements Numeric<Double> {

	@Override
	public Double abs(final Double x) {
		return Math.abs(x);
	}

	@Override
	public Double negate(final Double x) {
		return -x;
	}

	@Override
	public Double plus(final Double x, final Double y) {
		return x + y;
	}

	@Override
	public Double minus(final Double x, final Double y) {
		return x - y;
	}

	@Override
	public Double times(final Double x, final Double y) {
		return x*y;
	}

	@Override
	public Double div(final Double x, final Double y) {
		return x/y;
	}

	@Override
	public Double mean(final Double x, final Double y) {
		return x + (y - x)/2.0;
	}

	@Override
	public int compare(final Double x, final Double y) {
		return x.compareTo(y);
	}

}

final class LongNumeric implements Numeric<Long> {

	@Override
	public Long abs(final Long x) {
		return Math.abs(x);
	}

	@Override
	public Long negate(final Long x) {
		return -x;
	}

	@Override
	public Long plus(final Long x, final Long y) {
		return x + y;
	}

	@Override
	public Long minus(final Long x, final Long y) {
		return x - y;
	}

	@Override
	public Long times(final Long x, final Long y) {
		return x*y;
	}

	@Override
	public Long div(final Long x, final Long y) {
		return x/y;
	}

	@Override
	public Long mean(final Long x, final Long y) {
		return x + (y - x)/2;
	}

	@Override
	public int compare(final Long x, final Long y) {
		return x.compareTo(y);
	}

}

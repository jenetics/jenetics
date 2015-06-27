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

import static java.lang.Double.doubleToLongBits;

import java.io.Serializable;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class DoubleRange implements Serializable {

	private static final long serialVersionUID = 1L;

	private final double _min;
	private final double _max;

	private DoubleRange(final double min, final double max) {
		_min = min;
		_max = max;
	}

	public double getMin() {
		return _min;
	}

	public double getMax() {
		return _max;
	}

	public static DoubleRange of(final double min, final double max) {
		return new DoubleRange(min, max);
	}

	@Override
	public int hashCode() {
		return (int)(doubleToLongBits(_min) + 31*doubleToLongBits(_max));
	}

	@Override
	public boolean equals(final Object other) {
		return other instanceof DoubleRange &&
			Double.compare(_min, ((DoubleRange)other)._min) == 0 &&
			Double.compare(_max, ((DoubleRange)other)._max) == 0;
	}

	@Override
	public String toString() {
		return "[" + _min + ", " + _max + "]";
	}

}

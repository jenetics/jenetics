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

import java.io.Serializable;

import org.jenetics.internal.util.Equality;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class LongRange implements Serializable {

	private static final long serialVersionUID = 1L;

	private final long _min;
	private final long _max;

	private LongRange(final long min, final long max) {
		_min = min;
		_max = max;
	}

	public long getMin() {
		return _min;
	}

	public long getMax() {
		return _max;
	}

	public static LongRange of(final long min, final long max) {
		return new LongRange(min, max);
	}

	@Override
	public int hashCode() {
		return (int)(_min + 31*_max);
	}

	@Override
	public boolean equals(final Object other) {
		return Equality.of(this, other).test(range ->
			_min == range._min && _max == range._max
		);
	}

	@Override
	public String toString() {
		return "[" + _min + ", " + _max + "]";
	}

}

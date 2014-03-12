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

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version 2.0 &mdash; <em>$Date: 2014-03-12 $</em>
 * @since 2.0
 */
public final class Duration implements Comparable<Duration>, Serializable {
	private static final long serialVersionUID = 1L;

	private final long _nanos;

	private Duration(final long nanos) {
		_nanos = nanos;
	}

	public long toNanos() {
		return _nanos;
	}

	public long toMillis() {
		return _nanos/1_000_000;
	}

	public double toSeconds() {
		return (double)_nanos/1_000_000_000.0;
	}

	@Override
	public int compareTo(final Duration other) {
		int result = 0;
		if (_nanos < other._nanos) {
			result = -1;
		} else if (_nanos > other._nanos) {
			result = 1;
		}
		return result;
	}

	@Override
	public int hashCode() {
		return (int)_nanos;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof Duration)) {
			return false;
		}

		final Duration duration = (Duration)obj;
		return duration._nanos == _nanos;
	}

	@Override
	public String toString() {
		return String.format("%11.11f s", toSeconds());
	}

	public static Duration ofNanos(final long nanos) {
		return new Duration(nanos);
	}

	public static Duration ofSeconds(final double seconds) {
		return new Duration((long)(seconds*1_000_000_000));
	}

}

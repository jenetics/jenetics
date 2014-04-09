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
 * This class models a quantity or amount of time, such as '34.5 seconds'.
 * <p>
 * <i>It is an (temporary) replacement for the the {@code Measurable<Duration>}
 * class in the remove JScience library. Will be removed (and be replaced by the
 * JDK version) when updated to Java 8.
 * </i>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version 2.0 &mdash; <em>$Date: 2014-04-02 $</em>
 * @since 2.0
 */
public final class Duration implements Comparable<Duration>, Serializable {
	private static final long serialVersionUID = 1L;

	private final long _nanos;

	private Duration(final long nanos) {
		_nanos = nanos;
	}

	/**
	 * Return the amount of this duration in nano seconds.
	 *
	 * @return the amount of this duration in nano seconds.
	 */
	public long toNanos() {
		return _nanos;
	}

	/**
	 * Return the amount of this duration in milli seconds.
	 *
	 * @return the amount of this duration in milli seconds.
	 */
	public long toMillis() {
		return _nanos/1_000_000;
	}

	/**
	 * Return the amount of this duration in seconds.
	 *
	 * @return the amount of this duration in seconds.
	 */
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

	/**
	 * Create a new duration object from the given nano seconds.
	 *
	 * @param nanos the amount of the new duration in nano seconds.
	 * @return a new duration object from the given nano seconds
	 */
	public static Duration ofNanos(final long nanos) {
		return new Duration(nanos);
	}

	/**
	 * Create a new duration object from the given milli seconds.
	 *
	 * @param millis the amount of the new duration in milli seconds.
	 * @return a new duration object from the given milli seconds
	 */
	public static Duration ofMillis(final long millis) {
		return new Duration(millis*1_000_000);
	}

	/**
	 * Create a new duration object from the given seconds.
	 *
	 * @param seconds the amount of the new duration in seconds.
	 * @return a new duration object from the given seconds
	 */
	public static Duration ofSeconds(final double seconds) {
		return new Duration((long)(seconds*1_000_000_000));
	}

}

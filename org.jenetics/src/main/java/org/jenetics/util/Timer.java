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

import static org.jenetics.util.object.eq;
import static org.jenetics.util.object.hashCodeOf;
import static org.jenetics.util.object.nonNull;

import java.io.Serializable;

import javax.measure.Measurable;
import javax.measure.Measure;
import javax.measure.quantity.Duration;
import javax.measure.unit.SI;

import javolution.lang.Reusable;

/**
 * Timer for measure the performance of the GA. The timer uses nano second
 * precision (by using {@link System#nanoTime()}). This timer is not synchronized.
 * It's up to the user to ensure thread safety.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 2.0 &mdash; <em>$Date: 2013-05-25 $</em>
 */
public final class Timer
	implements
		Comparable<Timer>,
		Reusable,
		Serializable,
		Cloneable
{
	private static final long serialVersionUID = 1L;
	private static final String DEFAULT_LABEL = "Timer";

	private String _label;
	/*private[test]*/ long _start = 0;
	/*private[test]*/ long _stop = 0;
	/*private[test]*/ long _sum = 0;

	private transient Accumulator<? super Long> _accumulator = accumulators.NULL;

	/**
	 * Create a new time with the given label. The label is use in the
	 * {@link #toString()} method.
	 *
	 * @param label the timer label.
	 * @throws NullPointerException if the {@code label} is {@code null}.
	 */
	public Timer(final String label) {
		_label = nonNull(label, "Time label");
	}

	/**
	 * Create a new Timer object.
	 */
	public Timer() {
		this(DEFAULT_LABEL);
	}

	/**
	 * Set the accumulator for the interim results.
	 *
	 * [code]
	 * final Mean<Long> variance = new Mean<>();
	 * final Timer timer = new Timer();
	 * timer.setAccumulator(variance);
	 *
	 * for (int i = 0; i < 100; ++I) {
	 *     timer.start();
	 *     ... // Do some measurable task.
	 *     timer.stop();
	 * }
	 *
	 * // Print the average time used for the 'measurable' task.
	 * System.out.println(mean.getMean());
	 * [/code]
	 *
	 * @param accumulator the accumulator used for the interim results.
	 * @throws NullPointerException if the {@code accumulator} is {@code null}.
	 */
	public void setAccumulator(final Accumulator<? super Long> accumulator) {
		_accumulator = nonNull(accumulator, "Accumulator");
	}

	/**
	 * Start the timer.
	 */
	public void start() {
		_start = System.nanoTime();
	}

	/**
	 * Stop the timer.
	 */
	public void stop() {
		_stop = System.nanoTime();
		final long time = _stop - _start;
		_accumulator.accumulate(time);
		_sum += time;
	}

	/**
	 * Reset the timer.
	 */
	@Override
	public void reset() {
		_sum = 0;
		_start = 0;
		_stop = 0;
	}

	/**
	 * Return the overall time of this timer. The following code snippet would
	 * return a measured time of 10 s (theoretically).
	 * [code]
	 * final Timer timer = new Timer();
	 * for (int i = 0; i < 10; ++i) {
	 *     timer.start();
	 *     Thread.sleep(1000);
	 *     timer.stop();
	 * }
	 * [/code]
	 *
	 * @return the measured time so far.
	 */
	public Measurable<Duration> getTime() {
		return Measure.valueOf(_sum, SI.NANO(SI.SECOND));
	}

	/**
	 * Return the time between two successive calls of {@link #start()} and
	 * {@link #stop()}.
	 *
	 * @return the interim time measured.
	 */
	public Measurable<Duration> getInterimTime() {
		return Measure.valueOf(_stop - _start, SI.NANO(SI.SECOND));
	}

	/**
	 * Return the timer label.
	 *
	 * @return the timer label.
	 */
	public String getLabel() {
		return _label;
	}

	/**
	 * Set the timer label.
	 *
	 * @param label the new timer label
	 */
	public void setLabel(final String label) {
		_label = nonNull(label, "Timer label");
	}

	@Override
	public int compareTo(final Timer timer) {
		nonNull(timer, "Timer");

		long diff = _sum - timer._sum;
		int comp = 0;
		if (diff < 0) {
			comp = -1;
		} else if (diff > 0) {
			comp = 1;
		}
		return comp;
	}

	@Override
	public int hashCode() {
		return hashCodeOf(getClass()).
				and(_label).
				and(_start).
				and(_stop).
				and(_sum).value();
	}

	@Override
	public boolean equals(final Object object) {
		if (object == this) {
			return true;
		}
		if (!(object instanceof Timer)) {
			return false;
		}

		final Timer timer = (Timer)object;
		return eq(_start, timer._start) &&
				eq(_stop, timer._stop) &&
				eq(_sum, timer._sum) &&
				eq(_label, timer._label);
	}

	@Override
	public Timer clone() {
		try {
			return (Timer)super.clone();
		} catch (CloneNotSupportedException e) {
			throw new AssertionError(e);
		}
	}

	@Override
	public String toString() {
		return String.format(
					"%s: %11.11f s", _label,
					getTime().doubleValue(SI.SECOND)
				);
	}

}




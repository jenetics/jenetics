/*
 * Java Genetic Algorithm Library (@!identifier!@).
 * Copyright (c) @!year!@ Franz Wilhelmstötter
 *  
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * Author:
 *     Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 *     
 */
package org.jenetics.util;

import javax.measure.Measurable;
import javax.measure.Measure;
import javax.measure.quantity.Duration;
import javax.measure.unit.SI;

/**
 * Timer for measure the performance of the GA. The timer uses nano second
 * precision (by using {@link System#nanoTime()}).
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: Timer.java,v 1.6 2010-01-15 10:38:22 fwilhelm Exp $
 */
public class Timer {
	private final String _label;
	private long _start = 0;
	private long _stop = 0;
	private long _sum = 0;
	
	/**
	 * Create a new time with the given label. The label is use in the 
	 * {@link #toString()} method.
	 * 
	 * @param label the timer label.
	 * @throws NullPointerException if the {@code label} is {@code null}.
	 */
	public Timer(final String label) {
		Validator.notNull(label, "Time label");
		_label = label;
	}
	
	/**
	 * Create a new Timer object with label 'Timer'.
	 */
	public Timer() {
		this("Timer");
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
		_sum += _stop - _start;
	}
	
	/**
	 * Reset the timer.
	 */
	public void reset() {
		_sum = 0;
		_start = 0;
	}
	
	/**
	 * Return the overall time of this timer. The following code snippet would
	 * return a measured time of 10 s (theoretically).
	 * [code]
	 *     final Timer timer = new Timer();
	 *     for (int i = 0; i < 10) {
	 *         timer.start();
	 *         Thread.sleep(1000);
	 *         timer.stop();
	 *     }
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
	
	@Override
	public String toString() {
		return String.format("%s: %11.11f", _label, getTime().doubleValue(SI.SECOND));
	}
	
}




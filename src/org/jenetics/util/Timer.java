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
 * Timer for measure the performance of the GA.
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: Timer.java,v 1.3 2009-02-17 21:29:14 fwilhelm Exp $
 */
public class Timer {
	private final String _label;
	private long _start = 0;
	private long _sum = 0;
	
	public Timer(final String lable) {
		_label = lable;
	}
	
	public void start() {
		_start = System.currentTimeMillis();
	}
	
	public void stop() {
		_sum += System.currentTimeMillis() - _start;
	}
	
	public void reset() {
		_sum = 0;
		_start = 0;
	}
	
	public Measurable<Duration> getTime() {
		return Measure.valueOf(_sum, SI.MILLI(SI.SECOND));
	}
	
	@Override
	public String toString() {
		return String.format("%15s: %10s", _label, getTime());
	}
	
}

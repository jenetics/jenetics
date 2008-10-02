package org.jenetics.util;

import javax.measure.Measurable;
import javax.measure.Measure;
import javax.measure.quantity.Duration;
import javax.measure.unit.SI;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: Timer.java,v 1.2 2008-10-02 19:40:17 fwilhelm Exp $
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
	}
	
	public Measurable<Duration> getTime() {
		return Measure.valueOf(_sum, SI.MILLI(SI.SECOND));
	}
	
	@Override
	public String toString() {
		return String.format("%15s: %10s", _label, getTime());
	}
	
}

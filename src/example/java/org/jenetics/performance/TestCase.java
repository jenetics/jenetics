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
package org.jenetics.performance;

import org.jenetics.stat.Variance;
import org.jenetics.util.Accumulators.MinMax;
import org.jenetics.util.Accumulator;
import org.jenetics.util.Timer;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public abstract class TestCase implements Runnable {

	private final String _name;
	protected Timer _timer;
	private Variance<Long> _variance;
	private MinMax<Long> _minmax;
	
	public TestCase(
		final String name
	) {
		_name = name;
		init();
	}
	
	private void init() {
		_timer = new Timer(_name);
		_variance = new Variance<Long>();
		_minmax = new MinMax<Long>();
		
		_timer.setAccumulator(new Accumulator<Long>() {
			@Override public void accumulate(final Long value) {
				_variance.accumulate(value);
				_minmax.accumulate(value);
			}
		});
	}
	
	@Override
	public final void run() {
		init();
		test();
	}
	
	protected abstract void test();
	
	public Timer getTimer() {
		return _timer;
	}
	
	public Variance<Long> getVariance() {
		return _variance;
	}
	
	public MinMax<Long> getMinMax() {
		return _minmax;
	}
	
}

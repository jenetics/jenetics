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

import javolution.lang.Reusable;

import org.jenetics.stat.Variance;
import org.jenetics.util.accumulators.MinMax;
import org.jenetics.util.Accumulator;
import org.jenetics.util.Timer;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public abstract class TestCase 
	implements 
		Runnable, 
		Reusable, 
		Comparable<TestCase> 
{

	private final String _name;
	private final int _loops;
	private final int _size;
	
	private int _ordinal = 1;
	
	protected Timer _timer;
	private Variance<Long> _variance;
	private MinMax<Long> _minmax;
	
	public TestCase(final String name, final int loops, final int size) {
		_name = name;
		_loops = loops;
		_size = size;
		
		reset();
	}
	
	public TestCase(final String name, final int loops) {
		this(name, loops, 1);
	}
	
	public TestCase(final String name) {
		this(name, 1000);
	}
	
	@Override
	public void reset() {
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
		reset();
		for (int i = 0; i < _loops; ++i) {
			beforeTest();
			_timer.start();
			test();
			_timer.stop();
			afterTest();
		}
	}
	
	protected void beforeTest() {
	}
	
	protected abstract void test();
	
	protected void afterTest() {
	}
	
	public void setOrdinal(final int ordinal) {
		_ordinal = ordinal;
	}
	
	public int getOrdinal() {
		return _ordinal;
	}
	
	public int getSize() {
		return _size;
	}
	
	public Timer getTimer() {
		return _timer;
	}
	
	public Variance<Long> getVariance() {
		return _variance;
	}
	
	public MinMax<Long> getMinMax() {
		return _minmax;
	}
	
	@Override
	public int compareTo(final TestCase test) {
		return _ordinal - test._ordinal;
	}
	
}

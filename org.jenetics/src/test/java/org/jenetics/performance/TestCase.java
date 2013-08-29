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
package org.jenetics.performance;

import javolution.lang.Reusable;

import org.jenetics.stat.Variance;
import org.jenetics.util.accumulators.MinMax;
import org.jenetics.util.Accumulator;
import org.jenetics.util.Timer;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date: 2013-04-27 $</em>
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
		_variance = new Variance<>();
		_minmax = new MinMax<>();

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

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

import static org.jenetics.util.accumulator.accumulate;

import java.util.Iterator;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public class AccumulatorsTest {
	
	static final class IntegerIterator implements Iterator<Integer> {
		private final int _length;
		private int _pos = 0;
		
		IntegerIterator(final int length) {
			_length = length;
		}
		
		@Override
		public boolean hasNext() {
			return _pos < _length;
		}

		@Override
		public Integer next() {
			return _pos++;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
	
	static final class IntegerIterable implements Iterable<Integer> {
		private final int _length;
		
		IntegerIterable(final int length) {
			_length = length;
		}
		
		@Override
		public Iterator<Integer> iterator() {
			return new IntegerIterator(_length);
		}
		
	}
	
	@Test
	public void callSpeed() {
		final Accumulator<Integer> accumulator = new AbstractAccumulator<Integer>() {};
		Timer timer = new Timer();
		timer.start();
		for (long i = 0, n = 100000000L; i < n; ++i) {
			accumulator.accumulate(null);
		}
		timer.stop();
		System.out.println(accumulator);
		System.out.println(timer);
	}	
	

	
	@Test
	public void accumulate1() {
		final int SAMPLES = 1000;
		final AbstractAccumulator<Integer> accumulator = new AbstractAccumulator<Integer>(){};
		accumulate(new IntegerIterator(SAMPLES), accumulator);
		
		Assert.assertEquals(accumulator.getSamples(), SAMPLES);
	}
	
	@Test
	public void accumulate2() {
		final int SAMPLES = 1000;
		final AbstractAccumulator<Integer> accumulator = new AbstractAccumulator<Integer>(){};
		accumulate(new IntegerIterable(SAMPLES), accumulator);
		
		Assert.assertEquals(accumulator.getSamples(), SAMPLES);
	}
	
	@Test
	public void accumulate3() {
		final int SAMPLES = 1000;
		final AbstractAccumulator<Integer> accumulator1 = new AbstractAccumulator<Integer>(){};
		final AbstractAccumulator<Integer> accumulator2 = new AbstractAccumulator<Integer>(){};
		
		accumulate(
				new IntegerIterable(SAMPLES), 
				accumulator1,
				accumulator2
			);
		
		Assert.assertEquals(accumulator1.getSamples(), SAMPLES);
		Assert.assertEquals(accumulator2.getSamples(), SAMPLES);
	}
	
	@Test
	public void accumulate4() {
		final int SAMPLES = 1000;
		final AbstractAccumulator<Integer> accumulator1 = new AbstractAccumulator<Integer>(){};
		final AbstractAccumulator<Integer> accumulator2 = new AbstractAccumulator<Integer>(){};
		final AbstractAccumulator<Integer> accumulator3 = new AbstractAccumulator<Integer>(){};
		
		accumulate(
				new IntegerIterable(SAMPLES), 
				accumulator1,
				accumulator2,
				accumulator3
			);
		
		Assert.assertEquals(accumulator1.getSamples(), SAMPLES);
		Assert.assertEquals(accumulator2.getSamples(), SAMPLES);
		Assert.assertEquals(accumulator3.getSamples(), SAMPLES);
	}
	
	@Test
	public void accumulate5() {
		final int SAMPLES = 1000;
		final AbstractAccumulator<Integer> accumulator1 = new AbstractAccumulator<Integer>(){};
		final AbstractAccumulator<Integer> accumulator2 = new AbstractAccumulator<Integer>(){};
		final AbstractAccumulator<Integer> accumulator3 = new AbstractAccumulator<Integer>(){};
		final AbstractAccumulator<Integer> accumulator4 = new AbstractAccumulator<Integer>(){};
		
		accumulate(
				new IntegerIterable(SAMPLES), 
				accumulator1,
				accumulator2,
				accumulator3,
				accumulator4
			);
		
		Assert.assertEquals(accumulator1.getSamples(), SAMPLES);
		Assert.assertEquals(accumulator2.getSamples(), SAMPLES);
		Assert.assertEquals(accumulator3.getSamples(), SAMPLES);
		Assert.assertEquals(accumulator4.getSamples(), SAMPLES);
	}
	
	@Test
	public void accumulate6() {
		final int SAMPLES = 1000;
		final AbstractAccumulator<Integer> accumulator1 = new AbstractAccumulator<Integer>(){};
		final AbstractAccumulator<Integer> accumulator2 = new AbstractAccumulator<Integer>(){};
		final AbstractAccumulator<Integer> accumulator3 = new AbstractAccumulator<Integer>(){};
		final AbstractAccumulator<Integer> accumulator4 = new AbstractAccumulator<Integer>(){};
		final AbstractAccumulator<Integer> accumulator5 = new AbstractAccumulator<Integer>(){};
		
		accumulate(
				new IntegerIterable(SAMPLES), 
				accumulator1,
				accumulator2,
				accumulator3,
				accumulator4,
				accumulator5
			);
		
		Assert.assertEquals(accumulator1.getSamples(), SAMPLES);
		Assert.assertEquals(accumulator2.getSamples(), SAMPLES);
		Assert.assertEquals(accumulator3.getSamples(), SAMPLES);
		Assert.assertEquals(accumulator4.getSamples(), SAMPLES);
		Assert.assertEquals(accumulator5.getSamples(), SAMPLES);
	}
	
	@Test
	public void accumulateN() {
		final int SAMPLES = 1000;
		final Array<AbstractAccumulator<Integer>> accumulators = new Array<AbstractAccumulator<Integer>>(10);
		for (int i = 0; i < accumulators.length(); ++i) {
			accumulators.set(i, new AbstractAccumulator<Integer>(){});
		}
		
		accumulate(
				new IntegerIterable(SAMPLES), 
				accumulators
			);
		
		for (AbstractAccumulator<Integer> accumulator : accumulators) {
			Assert.assertEquals(accumulator.getSamples(), SAMPLES);
		}
	}
	
//	@Test
//	public void sum() {
//		final Integer64[] array = new Integer64[20];
//		for (int i = 0; i < array.length; ++i) {
//			array[i] = Integer64.valueOf(i);
//		}
//		
//		final Accumulators.Sum<Integer64> sum = new Accumulators.Sum<Integer64>();
//		Accumulators.accumulate(Arrays.asList(array), sum);
//		Assert.assertEquals(sum.getSum(), Integer64.valueOf((20*19/2)));
//	}
	
}









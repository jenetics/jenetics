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

import java.util.Iterator;
import java.util.ListIterator;

import org.jenetics.util.Array;
import org.jenetics.util.Factory;
import org.jenetics.util.Predicate;
import org.jenetics.util.Timer;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public class ArrayTest extends PerfTest {

	private int N = 1000000;
	private final int LOOPS = 1000;
	private final Array<Integer> _array = new Array<Integer>(N);
	
	public ArrayTest() {
		super("Array");
	}
	
	@Override
	protected int calls() {
		return N;
	}
	
	private void init() {
		for (int j = N; --j >= 0;) {
			_array.set(j, 23);
			_array.get(j);
		}
		for (int j = N; --j >= 0;) {
			_array.set(j, 2);
			_array.get(j);
		}
	}
	
	private void forLoopGetter() {
		final Timer timer = newTimer("for-loop (getter)");
		
		for (int i = LOOPS; --i >= 0;) {
			timer.start();
			for (int j = N; --j >= 0;) {
				_array.get(j);
			}
			timer.stop();
		}
	}
	
	private void foreachLoopGetter() {
		final Timer timer = newTimer("foreach");
		
		final Predicate<Integer> getter = new Predicate<Integer>() {
			@Override public boolean evaluate(Integer object) {
				@SuppressWarnings("unused") final Integer i = object;
				return true;
			}
		};
		
		for (int i = LOOPS; --i >= 0;) {
			timer.start();
			_array.foreach(getter);
			timer.stop();
		}
	}
	
	private void forLoopSetter() {
		final Timer timer = newTimer("for-loop (setter)");
		
		for (int i = LOOPS; --i >= 0;) {
			timer.start();
			for (int j = N; --j >= 0;) {
				_array.set(j, 1);
			}
			timer.stop();
		}
	}
	
	private void fill() {
		final Timer timer = newTimer("fill(Integer)");
		
		for (int i = LOOPS; --i >= 0;) {
			timer.start();
			_array.fill(1);
			timer.stop();
		}
	}
	
	private void fillFactory() {
		final Timer timer = newTimer("fill(Factory)");
		
		final Factory<Integer> factory = new Factory<Integer>() {
			@Override
			public Integer newInstance() {
				return 1;
			}
		};
		
		for (int i = LOOPS; --i >= 0;) {
			timer.start();
			_array.fill(factory);
			timer.stop();
		}
	}
	
	private void iterator() {
		final Timer timer = newTimer("iterator");
		
		for (int i = LOOPS; --i >= 0;) {
			timer.start();
			for (Iterator<Integer> it = _array.iterator(); it.hasNext();) {
				it.next();
			}
			timer.stop();
		}
	}
	
	private void listIterator() {
		final Timer timer = newTimer("listIterator");
		
		for (int i = LOOPS; --i >= 0;) {
			timer.start();
			for (ListIterator<Integer> it = _array.listIterator(); it.hasNext();) {
				it.next();
				it.set(1);
			}
			timer.stop();
		}
	}
	
	@Override
	protected ArrayTest measure() {
		init();
		
		foreachLoopGetter();
		forLoopGetter();
		forLoopSetter();
		iterator();
		listIterator();
		fill();
		fillFactory();
		
		return this;
	}
	
}

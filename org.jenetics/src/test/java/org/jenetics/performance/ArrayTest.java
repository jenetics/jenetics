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

import java.util.Comparator;
import java.util.Iterator;
import java.util.ListIterator;

import org.jenetics.util.Array;
import org.jenetics.util.arrays;
import org.jenetics.util.Factory;
import org.jenetics.util.Function;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date$</em>
 */
@Suite("Array")
public class ArrayTest {

	private static final Function<Integer, Boolean>
	GETTER = new Function<Integer, Boolean>() {
		@Override
		public Boolean apply(final Integer value) {
			return Boolean.TRUE;
		}
	};

	private static final Factory<Integer> INTEGER_FACTORY = new Factory<Integer>() {
		@Override
		public Integer newInstance() {
			return 1;
		}
	};

	private static final int LOOPS = 20;
	private static int SIZE = 1000000;

	private final Array<Integer> _array = new Array<>(SIZE);

	public ArrayTest() {
	}

	@Test(1)
	public final TestCase forLoopGetter = new TestCase("for-loop (getter)", LOOPS, SIZE) {
		{
			_array.fill(INTEGER_FACTORY);
			for (int i = _array.length(); --i >= 0;) {
				_array.get(i);
			}
		}

		@Override
		protected void test() {
			for (int i = _array.length(); --i >= 0;) {
				_array.get(i);
			}
		}
	};

	@Test(2)
	public final TestCase foreachLoopGetter = new TestCase("foreach(GETTER)", LOOPS, SIZE) {
		@Override
		protected void test() {
			_array.foreach(GETTER);
		}
	};

	@Test(3)
	public final TestCase foreachLoopSetter = new TestCase("for-loop (setter)", LOOPS, SIZE) {
		@Override
		protected void test() {
			for (int i = _array.length(); --i >= 0;) {
				_array.set(i, 1);
			}
		}
	};

	@Test(4)
	public final TestCase fill = new TestCase("fill(1)", LOOPS, SIZE) {
		@Override
		protected void test() {
			_array.setAll(1);
		}
	};

	@Test(5)
	public final TestCase fillFactory = new TestCase("fill(Factory)", LOOPS, SIZE) {
		@Override
		protected void test() {
			_array.fill(INTEGER_FACTORY);
		}
	};

	@Test(6)
	public final TestCase iterator = new TestCase("iterator()", LOOPS, SIZE) {
		@Override
		protected void test() {
			for (Iterator<Integer> it = _array.iterator(); it.hasNext();) {
				it.next();
			}
		}
	};

	@Test(7)
	public final TestCase listIterator = new TestCase("listIterator()", LOOPS, SIZE) {
		@Override
		protected void test() {
			for (ListIterator<Integer> it = _array.listIterator(); it.hasNext();) {
				it.next();
				it.set(1);
			}
		}
	};

	@Test(8)
	public final TestCase sort = new TestCase("sort()", 50, SIZE) {
		private final Comparator<Integer> _comparator = new Comparator<Integer>() {
			@Override
			public int compare(Integer o1, Integer o2) {
				return o1.compareTo(o2);
			}
		};

		@Override
		protected void beforeTest() {
			for (int i = _array.length(); --i >= 0;) {
				_array.set(i, i);
			}
			arrays.shuffle(_array);
		}

		@Override
		protected void test() {
			_array.sort(_comparator);
		}

		@Override
		protected void afterTest() {
			if (!arrays.isSorted(_array)) {
				throw new IllegalArgumentException("Error: array not sorted");
			}
		}
	};

	@Test(9)
	public final TestCase quicksort = new TestCase("quicksort()", 50, SIZE) {
		@Override
		protected void beforeTest() {
			for (int i = _array.length(); --i >= 0;) {
				_array.set(i, i);
			}
			arrays.shuffle(_array);
		}

		@Override
		protected void test() {
			_array.sort();
		}

		@Override
		protected void afterTest() {
			if (!arrays.isSorted(_array)) {
				throw new IllegalArgumentException("Error: array not sorted");
			}
		}
	};

	@Test(10)
	public final TestCase copy = new TestCase("copy()", LOOPS, SIZE) {
		@Override
		protected void test() {
			_array.copy();
		}
	};

}

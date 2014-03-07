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
package org.jenetics.internal.util;

import java.util.Random;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import org.jenetics.util.math;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date: 2014-02-15 $</em>
 */
public abstract class ArrayProxyTestBase<T> {

	public abstract ArrayProxy<T> newArrayProxy(final int length);

	public abstract T newArrayProxyElement(final Random random);

	public T newArrayProxyElement() {
		return newArrayProxyElement(new Random(math.random.seed()));
	}

	@Test
	public void uncheckedOffsetGetSet() {
		final long seed = math.random.seed();
		final Random random = new Random(seed);

		final ArrayProxy<T> proxy = newArrayProxy(10_000);
		for (int i = 0; i < proxy._length; ++i) {
			proxy.__set(i, newArrayProxyElement(random));
		}

		random.setSeed(seed);
		for (int i = 0; i < proxy._length; ++i) {
			final T actual = proxy.__get(i);
			final T expected = newArrayProxyElement(random);

			Assert.assertEquals(actual, expected);
		}
	}

	@Test
	public void uncheckedGetSet() {
		final long seed = math.random.seed();
		final Random random = new Random(seed);

		final ArrayProxy<T> proxy = newArrayProxy(10_000);
		for (int i = 0; i < proxy._length; ++i) {
			proxy.uncheckedSet(i, newArrayProxyElement(random));
		}

		random.setSeed(seed);
		for (int i = 0; i < proxy._length; ++i) {
			final T actual = proxy.uncheckedGet(i);
			final T expected = newArrayProxyElement(random);

			Assert.assertEquals(actual, expected);
		}
	}

	@Test
	public void getSet() {
		final long seed = math.random.seed();
		final Random random = new Random(seed);

		final ArrayProxy<T> proxy = newArrayProxy(10_000);
		for (int i = 0; i < proxy._length; ++i) {
			proxy.set(i, newArrayProxyElement(random));
		}

		random.setSeed(seed);
		for (int i = 0; i < proxy._length; ++i) {
			final T actual = proxy.get(i);
			final T expected = newArrayProxyElement(random);

			Assert.assertEquals(actual, expected);
		}
	}

	@Test(
		dataProvider = "outofboundsdata",
		expectedExceptions = IndexOutOfBoundsException.class
	)
	public void outOfBoundsGet(final Integer length, final Integer index) {
		final ArrayProxy<T> proxy = newArrayProxy(length);
		proxy.get(index);
	}

	@Test(
		dataProvider = "outofboundsdata",
		expectedExceptions = IndexOutOfBoundsException.class
	)
	public void outOfBoundsSet(final Integer length, final Integer index) {
		final ArrayProxy<T> proxy = newArrayProxy(length);
		proxy.get(index);
	}

	@DataProvider(name = "outofboundsdata")
	public Object[][] getOutOfBoundsData() {
		return new Object[][] {
			{3, -1},
			{3, 3},
			{10, -234},
			{10, 233},
			{100, -292929},
			{100, 200}
		};
	}

	@Test(dataProvider = "subintintdata")
	public void subIntInt(
		final Integer length,
		final Integer start,
		final Integer end
	) {
		final long seed = math.random.seed();
		final Random random = new Random(seed);

		final ArrayProxy<T> proxy = newArrayProxy(length);
		for (int i = 0; i < proxy._length; ++i) {
			proxy.set(i, newArrayProxyElement(random));
		}

		random.setSeed(seed);
		for (int i = 0; i < start; ++i) {
			final T actual = proxy.get(i);
			final T expected = newArrayProxyElement(random);

			Assert.assertEquals(actual, expected);
		}

		final ArrayProxy<T> sub = proxy.slice(start, end);
		for (int i = 0; i < (end - start); ++i) {
			final T expected = newArrayProxyElement(random);

			Assert.assertEquals(sub.get(i), expected);
			Assert.assertEquals(proxy.get(i + start), expected);

			Assert.assertEquals(sub.uncheckedGet(i), expected);
			Assert.assertEquals(proxy.uncheckedGet(i + start), expected);

			Assert.assertEquals(
				sub.__get(i + start + proxy._start),
				expected
			);
			Assert.assertEquals(
				proxy.__get(i + start + proxy._start),
				expected
			);
		}
	}

	@Test(dataProvider = "subintintdata")
	public void subSubIntInt(
		final Integer length,
		final Integer start,
		final Integer end
	) {
		final long seed = math.random.seed();
		final Random random = new Random(seed);

		final ArrayProxy<T> proxy = newArrayProxy(length + 10).slice(5, length + 5);
		for (int i = 0; i < proxy._length; ++i) {
			proxy.set(i, newArrayProxyElement(random));
		}

		random.setSeed(seed);
		for (int i = 0; i < start; ++i) {
			final T actual = proxy.get(i);
			final T expected = newArrayProxyElement(random);

			Assert.assertEquals(actual, expected);
		}

		final ArrayProxy<T> sub = proxy.slice(start, end);
		for (int i = 0; i < (end - start); ++i) {
			final T expected = newArrayProxyElement(random);

			Assert.assertEquals(sub.get(i), expected);
			Assert.assertEquals(proxy.get(i + start), expected);

			Assert.assertEquals(sub.uncheckedGet(i), expected);
			Assert.assertEquals(proxy.uncheckedGet(i + start), expected);

			Assert.assertEquals(
				sub.__get(i + start + proxy._start),
				expected
			);
			Assert.assertEquals(
				proxy.__get(i + start + proxy._start),
				expected
			);
		}
	}

	@Test(dataProvider = "subintintdata")
	public void subSubSubIntInt(
		final Integer length,
		final Integer start,
		final Integer end
	) {
		final long seed = math.random.seed();
		final Random random = new Random(seed);

		final ArrayProxy<T> proxy = newArrayProxy(length + 20)
										.slice(5, length + 15)
										.slice(5, length + 10);
		for (int i = 0; i < proxy._length; ++i) {
			proxy.set(i, newArrayProxyElement(random));
		}

		random.setSeed(seed);
		for (int i = 0; i < start; ++i) {
			final T actual = proxy.get(i);
			final T expected = newArrayProxyElement(random);

			Assert.assertEquals(actual, expected);
		}

		final ArrayProxy<T> sub = proxy.slice(start, end);
		for (int i = 0; i < (end - start); ++i) {
			final T expected = newArrayProxyElement(random);

			Assert.assertEquals(sub.get(i), expected);
			Assert.assertEquals(proxy.get(i + start), expected);

			Assert.assertEquals(sub.uncheckedGet(i), expected);
			Assert.assertEquals(proxy.uncheckedGet(i + start), expected);

			Assert.assertEquals(
				sub.__get(i + start + proxy._start),
				expected
			);
			Assert.assertEquals(
				proxy.__get(i + start + proxy._start),
				expected
			);
		}
	}

	@DataProvider(name = "subintintdata")
	public Object[][] getSubIntIntData() {
		return new Object[][] {
			{1000, 0, 1000},
			{1000, 2, 1000},
			{1000, 100, 900},
			{1000, 5, 600},
			{10000, 8800, 9400},
			{10000, 4, 9800}
		};
	}


	@Test(dataProvider = "swapparameter")
	public void swapIntIntArrayProxyInt(
		final Integer length, final Integer start, final Integer end,
		final Integer otherLength, final Integer otherStart
	) {
		final long thatSeed = math.random.seed();
		final long otherSeed = math.random.seed();
		final Random thatRandom = new Random(thatSeed);
		final Random otherRandom = new Random(otherSeed);

		final ArrayProxy<T> that = newArrayProxy(length);
		final ArrayProxy<T> other = newArrayProxy(otherLength);
		final ArrayProxy<T> thatCopy = newArrayProxy(length);
		final ArrayProxy<T> otherCopy = newArrayProxy(otherLength);

		for (int i = 0; i < length; ++i) {
			that.set(i, newArrayProxyElement(thatRandom));
		}
		for (int i = 0; i < otherLength; ++i) {
			other.set(i, newArrayProxyElement(otherRandom));
		}

		thatRandom.setSeed(thatSeed);
		otherRandom.setSeed(otherSeed);
		for (int i = 0; i < length; ++i) {
			thatCopy.set(i, newArrayProxyElement(thatRandom));
		}
		for (int i = 0; i < otherLength; ++i) {
			otherCopy.set(i, newArrayProxyElement(otherRandom));
		}


		that.swap(start, end, other, otherStart);

		for (int j = start; j < end; ++j) {
			Assert.assertEquals(that.get(j), otherCopy.get(j + otherStart - start));
		}
		for (int j = 0; j < (end - start); ++j) {
			Assert.assertEquals(other.get(j + otherStart), thatCopy.get(j + start));
		}

	}

	@DataProvider(name = "swapparameter")
	public Object[][] getSwapParameter() {
		return new Object[][] {
			{10, 1, 5, 15, 4},
			{10, 5, 7, 3, 1},
			{100, 23, 56, 60, 21},
			{1000, 231, 561, 601, 211},
			{10001, 2310, 5610, 6010, 2113}
		};
	}

	@Test(
		dataProvider = "swapparameteroutofbounds",
		expectedExceptions = IndexOutOfBoundsException.class
	)
	public void swapIntIntArrayProxyIntIndexOutOfBounds(
		final Integer length, final Integer start, final Integer end,
		final Integer otherLength, final Integer otherStart
	) {
		final long thatSeed = math.random.seed();
		final long otherSeed = math.random.seed();
		final Random thatRandom = new Random(thatSeed);
		final Random otherRandom = new Random(otherSeed);

		final ArrayProxy<T> that = newArrayProxy(length);
		final ArrayProxy<T> other = newArrayProxy(otherLength);
		final ArrayProxy<T> thatCopy = newArrayProxy(length);
		final ArrayProxy<T> otherCopy = newArrayProxy(otherLength);

		for (int i = 0; i < length; ++i) {
			that.set(i, newArrayProxyElement(thatRandom));
		}
		for (int i = 0; i < otherLength; ++i) {
			other.set(i, newArrayProxyElement(otherRandom));
		}

		thatRandom.setSeed(thatSeed);
		otherRandom.setSeed(otherSeed);
		for (int i = 0; i < length; ++i) {
			thatCopy.set(i, newArrayProxyElement(thatRandom));
		}
		for (int i = 0; i < otherLength; ++i) {
			otherCopy.set(i, newArrayProxyElement(otherRandom));
		}


		try {
			that.swap(start, end, other, otherStart);
		} catch (IndexOutOfBoundsException e) {
			// The arrays should not be changed.
			for (int i = 0; i < length; ++i) {
				Assert.assertEquals(that.get(i), thatCopy.get(i));
			}
			for (int i = 0; i < length; ++i) {
				Assert.assertEquals(other.get(i), otherCopy.get(i));
			}

			throw e;
		}

	}

	@DataProvider(name = "swapparameteroutofbounds")
	public Object[][] getSwapParameterOutOfBounds() {
		return new Object[][] {
			{10, -1, 5, 15, 4},
			{10, 1, -5, 15, 4},
			{10, 0, 5, 15, -3},
			{10, 2, 7, 3, 1},
			{100, 2, 100, 34, 3}
		};
	}

	@Test(dataProvider = "copyproxylength")
	public void copy(final Integer length, final Integer offset) {
		final long seed = math.random.seed();
		final Random random = new Random(seed);

		final ArrayProxy<T> that = newArrayProxy(length + offset).slice(offset);
		for (int i = 0; i < length; ++i) {
			that.set(i, newArrayProxyElement(random));
		}

		final ArrayProxy<T> copy = that.copy();
		Assert.assertEquals(copy._length, length.intValue());

		for (int i = 0; i < length; ++i) {
			that.set(i, newArrayProxyElement(random));
		}

		random.setSeed(seed);
		for (int i = 0; i < length; ++i) {
			Assert.assertEquals(copy.get(i), newArrayProxyElement(random));
		}
	}

	@DataProvider(name = "copyproxylength")
	public Object[][] getCopyProxyLengthOffset() {
		return new Object[][] {
			{2, 1},
			{0, 0},
			{1, 0},
			{234, 23},
			{12203, 433},
			{122300, 1433}
		};
	}

}

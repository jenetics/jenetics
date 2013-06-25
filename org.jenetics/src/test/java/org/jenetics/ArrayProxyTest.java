/*
 * Java Genetic Algorithm Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
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
package org.jenetics;

import java.util.concurrent.atomic.AtomicInteger;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import org.jenetics.util.Function;
import org.jenetics.util.ISeq;
import org.jenetics.util.Seq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date: 2013-06-25 $</em>
 */
public class ArrayProxyTest {

	static ArrayProxy<Integer> newArrayProxy(final int length) {
		return new ArrayProxy<Integer>(0, length) {
			private final int[] _array = new int[length];
			{for (int i = 0; i < length; ++i) {
				_array[i] = i;
			}}

			@Override
			Integer uncheckedOffsetGet(int absoluteIndex) {
				return _array[absoluteIndex];
			}
		};
	}

	static ISeq<Integer> newISeq(final int length) {
		return new ArrayProxyISeq<>(newArrayProxy(length));
	}

	private static Function<Integer, Boolean> ValueOf(final int value) {
		return new Function<Integer, Boolean>() {
			@Override
			public Boolean apply(final Integer v) {
				return v == value;
			}
		};
	}

	@Test(dataProvider = "sequences")
	public void contains(final Seq<Integer> seq) {
		for (int i = 0; i < seq.length(); ++i) {
			Assert.assertTrue(seq.contains(i));
		}

		for (int i = seq.length(); i < 2*seq.length(); ++i) {
			Assert.assertFalse(seq.contains(i));
		}
	}

	@Test(dataProvider = "sequences")
	public void indexOf(final Seq<Integer> seq) {
		for (int i = 0; i < seq.length(); ++i) {
			final int value = seq.get(i);
			Assert.assertEquals(seq.indexOf(value), i);
		}
	}

	@Test(dataProvider = "sequences")
	public void indexOfInt(final Seq<Integer> seq) {
		for (int start = 0; start < seq.length(); ++start) {
			for (int i = 0; i < seq.length(); ++i) {
				final int value = seq.get(i);
				final int index = seq.indexOf(value, start);

				if (i >= start) {
					Assert.assertEquals(index, i);
				} else {
					Assert.assertEquals(index, -1);
				}
			}
		}
	}

	@Test(dataProvider = "sequences")
	public void indexOfIntInt(final Seq<Integer> seq) {
		for (int start = 0; start < seq.length(); ++start) {
			for (int end = start; end < seq.length(); ++end) {
				for (int i = 0; i < seq.length(); ++i) {
					final int value = seq.get(i);
					final int index = seq.indexOf(value, start, end);

					if (i >= start && i < end) {
						Assert.assertEquals(index, i);
					} else {
						Assert.assertEquals(index, -1);
					}
				}
			}
		}
	}

	@Test(dataProvider = "sequences")
	public void indexWhere(final Seq<Integer> seq) {
		for (int i = 0; i < seq.length(); ++i) {
			final int value = seq.get(i);
			Assert.assertEquals(seq.indexWhere(ValueOf(value)), i);
		}
	}

	@Test(dataProvider = "sequences")
	public void indexWhereInt(final Seq<Integer> seq) {
		for (int start = 0; start < seq.length(); ++start) {
			for (int i = 0; i < seq.length(); ++i) {
				final int value = seq.get(i);
				final int index = seq.indexWhere(ValueOf(value), start);

				if (i >= start) {
					Assert.assertEquals(index, i);
				} else {
					Assert.assertEquals(index, -1);
				}
			}
		}
	}

	@Test(dataProvider = "sequences")
	public void indexWhereIntInt(final Seq<Integer> seq) {
		for (int start = 0; start < seq.length(); ++start) {
			for (int end = start; end < seq.length(); ++end) {
				for (int i = 0; i < seq.length(); ++i) {
					final int value = seq.get(i);
					final int index = seq.indexWhere(ValueOf(value), start, end);

					if (i >= start && i < end) {
						Assert.assertEquals(index, i);
					} else {
						Assert.assertEquals(index, -1);
					}
				}
			}
		}
	}

	@Test(dataProvider = "sequences")
	public void forEach(final Seq<Integer> seq) {
		final AtomicInteger counter = new AtomicInteger();
		final AtomicInteger lastValue = new AtomicInteger(-1);

		seq.forEach(new Function<Integer, Void>() {
			@Override public Void apply(final Integer value) {
				Assert.assertTrue(lastValue.get() < value);

				lastValue.set(value);
				counter.incrementAndGet();
				return null;
			}
		});

		Assert.assertEquals(counter.get(), seq.length());
	}

	@Test(dataProvider = "sequences")
	public void forAll(final Seq<Integer> seq) {
		final AtomicInteger counter = new AtomicInteger();

		seq.forAll(new Function<Integer, Boolean>() {
			@Override public Boolean apply(final Integer value) {
				counter.incrementAndGet();
				return true;
			}
		});

		Assert.assertEquals(counter.get(), seq.length());
	}

	@Test(dataProvider = "sequences")
	public void forAll2(final Seq<Integer> seq) {
		final AtomicInteger counter = new AtomicInteger();

		seq.forAll(new Function<Integer, Boolean>() {
			@Override public Boolean apply(final Integer value) {
				return counter.incrementAndGet() < seq.length()/2;
			}
		});

		Assert.assertEquals(counter.get(), seq.length()/2);
	}

	@Test(dataProvider = "sequences")
	public void get(final Seq<Integer> seq) {
		int lastValue = -1;
		for (int i = 0; i < seq.length(); ++i) {
			Assert.assertTrue(lastValue < seq.get(i));
			lastValue = seq.get(i);
		}
	}

	@DataProvider
	public Object[][] sequences() {
		return new Object[][] {
			{newISeq(33)}
		};
	}




}

















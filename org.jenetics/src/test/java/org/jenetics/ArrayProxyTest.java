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

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import org.jenetics.util.Function;
import org.jenetics.util.ISeq;
import org.jenetics.util.Seq;
import org.jenetics.util.functions;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date: 2013-06-25 $</em>
 */
public class ArrayProxyTest {

	static ArrayProxy<Integer> newArrayProxy(final int length) {
		final ObjectArrayProxy<Integer> proxy = new ObjectArrayProxy<>(length);
		for (int i = 0; i < length; ++i) {
			proxy._array[i] = i;
		}
		return proxy;
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
			final Integer value = seq.get(i);
			Assert.assertTrue(seq.contains(value));
		}

		for (int i = seq.length(); i < 2*seq.length(); ++i) {
			Assert.assertFalse(seq.contains(Integer.toString(i)));
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
	public void lastIndexOf(final Seq<Integer> seq) {
		for (int i = 0; i < seq.length(); ++i) {
			final int value = seq.get(i);
			Assert.assertEquals(seq.lastIndexOf(value), i);
		}
	}

	@Test(dataProvider = "sequences")
	public void lastIndexOfInt(final Seq<Integer> seq) {
		for (int end = seq.length(); end <= 0; --end) {
			for (int i = 0; i < seq.length(); ++i) {
				final int value = seq.get(i);
				final int index = seq.lastIndexOf(value, end);

				if (i < end) {
					Assert.assertEquals(index, i);
				} else {
					Assert.assertEquals(index, -1);
				}
			}
		}
	}

	@Test(dataProvider = "sequences")
	public void lastIndexOfIntInt(final Seq<Integer> seq) {
		for (int start = 0; start < seq.length(); ++start) {
			for (int end = start; end < seq.length(); ++end) {
				for (int i = 0; i < seq.length(); ++i) {
					final int value = seq.get(i);
					final int index = seq.lastIndexOf(value, start, end);

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
	public void lastIndexWhere(final Seq<Integer> seq) {
		for (int i = 0; i < seq.length(); ++i) {
			final int value = seq.get(i);
			Assert.assertEquals(seq.lastIndexWhere(ValueOf(value)), i);
		}
	}

	@Test(dataProvider = "sequences")
	public void lastIndexWhereInt(final Seq<Integer> seq) {
		for (int end = seq.length(); end <= 0; --end) {
			for (int i = 0; i < seq.length(); ++i) {
				final int value = seq.get(i);
				final int index = seq.lastIndexWhere(ValueOf(value), end);

				if (i < end) {
					Assert.assertEquals(index, i);
				} else {
					Assert.assertEquals(index, -1);
				}
			}
		}
	}

	@Test(dataProvider = "sequences")
	public void lastIndexWhereIntInt(final Seq<Integer> seq) {
		for (int start = 0; start < seq.length(); ++start) {
			for (int end = start; end < seq.length(); ++end) {
				for (int i = 0; i < seq.length(); ++i) {
					final int value = seq.get(i);
					final int index = seq.lastIndexWhere(ValueOf(value), start, end);

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
	public void iterator(final Seq<Integer> seq) {
		int count = 0;
		final Iterator<Integer> it = seq.iterator();
		while (it.hasNext()) {
			it.next();
			++count;
		}

		Assert.assertEquals(count, seq.length());
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

	@Test(dataProvider = "sequences")
	public void map(final Seq<Integer> seq) {
		final Seq<String> sseq = seq.map(functions.ObjectToString);
		Assert.assertEquals(sseq.length(), seq.length());

		for (int i = 0; i < seq.length(); ++i) {
			Assert.assertEquals(sseq.get(i), seq.get(i).toString());
		}
	}

	@Test(dataProvider = "sequences")
	public void toArrayObject(final Seq<Integer> seq) {
		final Object[] array = seq.toArray();
		Assert.assertEquals(array.length, seq.length());

		for (int i = 0; i < seq.length(); ++i) {
			Assert.assertEquals(array[i], seq.get(i));
		}
	}

	@Test(dataProvider = "sequences")
	public void toArrayInteger(final Seq<Integer> seq) {
		final Integer[] array = seq.toArray(new Integer[0]);
		Assert.assertEquals(array.length, seq.length());

		for (int i = 0; i < seq.length(); ++i) {
			Assert.assertEquals(array[i], seq.get(i));
		}
	}

	@Test(dataProvider = "sequences")
	public void toArrayInteger2(final Seq<Integer> seq) {
		final Integer[] array = seq.toArray(new Integer[seq.length()]);
		Assert.assertEquals(array.length, seq.length());

		for (int i = 0; i < seq.length(); ++i) {
			Assert.assertEquals(array[i], seq.get(i));
		}
	}

	@Test(dataProvider = "sequences")
	public void toArrayInteger3(final Seq<Integer> seq) {
		final Integer[] array = seq.toArray(new Integer[seq.length() + 5]);
		Assert.assertEquals(array.length, seq.length() + 5);

		for (int i = 0; i < seq.length(); ++i) {
			Assert.assertEquals(array[i], seq.get(i));
		}
	}

	@Test(dataProvider = "sequences")
	public void subSeqInt(final Seq<Integer> seq) {
		for (int start = 0; start < seq.length(); ++start) {
			final Seq<Integer> sub = seq.subSeq(start);

			Assert.assertEquals(sub.length(), seq.length() - start);
			for (int i = 0; i < sub.length(); ++i) {
				Assert.assertEquals(
					new Integer(sub.get(i)),
					new Integer(seq.get(i + start))
				);
			}
		}
	}

	@Test(dataProvider = "sequences")
	public void subSeqIntInt(final Seq<Integer> seq) {
		for (int start = 0; start < seq.length(); ++start) {
			for (int end = start; end < seq.length(); ++end) {
				final Seq<Integer> sub = seq.subSeq(start, end);

				Assert.assertEquals(sub.length(), end - start);
				for (int i = 0; i < sub.length(); ++i) {
					Assert.assertEquals(sub.get(i), seq.get(i + start));
				}
			}
		}
	}

	@DataProvider
	public Object[][] sequences() {
		return new Object[][] {
			{newISeq(33)},
			{newISeq(35).subSeq(5)} ,
			{newISeq(33).subSeq(8, 23)},
			{newISeq(50).subSeq(5, 43).subSeq(10)}
		};
	}




}

















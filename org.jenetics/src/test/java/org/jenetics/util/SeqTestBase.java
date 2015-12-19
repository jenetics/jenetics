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
package org.jenetics.util;

import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import org.jenetics.internal.util.IntRef;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public abstract class SeqTestBase {

	protected abstract Seq<Integer> newSeq(final int length);

	private static Predicate<Integer> ValueOf(final int value) {
		return i -> i == value;
	}

	@Test
	public void length() {
		for (int i = 0; i < 100; ++i) {
			final Seq<Integer> seq = newSeq(i);
			for (int j = 0; j < i; ++j) {
				Assert.assertEquals(seq.get(j).intValue(), j);
			}
			Assert.assertEquals(seq.length(), i);
		}
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
			final Integer value = it.next();
			Assert.assertTrue(seq.contains(value));
			++count;
		}

		Assert.assertEquals(count, seq.length());
	}

	@Test(dataProvider = "sequences")
	public void stream(final Seq<Integer> seq) {
		final IntRef count = new IntRef();
		seq.stream().forEach(value -> {
			Assert.assertTrue(seq.contains(value));
			++count.value;
		});

		Assert.assertEquals(count.value, seq.length());
	}

	@Test(dataProvider = "sequences")
	public void forEach(final Seq<Integer> seq) {
		final AtomicInteger counter = new AtomicInteger();
		final AtomicInteger lastValue = new AtomicInteger(-1);

		seq.forEach(value -> {
			Assert.assertTrue(lastValue.get() < value);
			Assert.assertTrue(seq.contains(value));

			lastValue.set(value);
			counter.incrementAndGet();
		});

		Assert.assertEquals(counter.get(), seq.length());
	}

	@Test(dataProvider = "sequences")
	public void forAll(final Seq<Integer> seq) {
		final AtomicInteger counter = new AtomicInteger();

		seq.forAll(value -> {
			Assert.assertTrue(seq.contains(value));
			counter.incrementAndGet();
			return true;
		});

		Assert.assertEquals(counter.get(), seq.length());
	}

	@Test(dataProvider = "sequences")
	public void forAll2(final Seq<Integer> seq) {
		final AtomicInteger counter = new AtomicInteger();

		seq.forAll(value ->  counter.incrementAndGet() < seq.length()/2);
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

	@Test(
		dataProvider = "sequences",
		expectedExceptions = IndexOutOfBoundsException.class
	)
	public void indexOutOfBoundsGet(final Seq<Integer> seq) {
		seq.get(seq.length());
	}

	@Test(
		dataProvider = "sequences",
		expectedExceptions = IndexOutOfBoundsException.class
	)
	public void negativeIndexGet(final Seq<Integer> seq) {
		seq.get(-1);
	}

	@Test(dataProvider = "sequences")
	public void map(final Seq<Integer> seq) {
		final Seq<String> sseq = seq.map(Objects::toString);
		Assert.assertEquals(sseq.length(), seq.length());

		for (int i = 0; i < seq.length(); ++i) {
			Assert.assertEquals(sseq.get(i), seq.get(i).toString());
		}
	}

	@Test(dataProvider = "sequences")
	public void append(final Seq<Integer> seq) {
		final Seq<Integer> appended = seq.append(-1000, -5000);

		Assert.assertEquals(appended.length(), seq.length() + 2);
		Assert.assertEquals(appended.get(appended.length() - 2).intValue(), -1000);
		Assert.assertEquals(appended.get(appended.length() - 1).intValue(), -5000);
	}

	@Test(dataProvider = "sequences")
	public void prepend(final Seq<Integer> seq) {
		final Seq<Integer> prepended = seq.prepend(-1000, -5000);

		Assert.assertEquals(prepended.length(), seq.length() + 2);
		Assert.assertEquals(prepended.get(0).intValue(), -1000);
		Assert.assertEquals(prepended.get(1).intValue(), -5000);
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
				Assert.assertEquals(sub.get(i), seq.get(i + start));
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
			{newSeq(33)},
			{newSeq(35).subSeq(5)} ,
			{newSeq(33).subSeq(8, 23)},
			{newSeq(50).subSeq(5, 43).subSeq(10)},
			{newSeq(100).subSeq(1, 95).subSeq(1, 80).subSeq(1, 75)},
			{newSeq(100).subSeq(1, 95).subSeq(1, 80).subSeq(1, 75).subSeq(1, 70)},
			{newSeq(100).subSeq(0, 95).subSeq(1, 80).subSeq(1).subSeq(1, 70)},
			{newSeq(100).subSeq(0, 95).subSeq(1, 80).subSeq(1).subSeq(1, 70).subSeq(0)}
		};
	}

}

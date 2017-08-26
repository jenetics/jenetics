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

import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.util.function.Supplier;

import org.testng.Assert;
import org.testng.annotations.Test;

import org.jenetics.internal.math.random;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public abstract class MSeqTestBase extends SeqTestBase {

	@Override
	protected abstract MSeq<Integer> newSeq(final int length);

	private Supplier<Integer> RandomInt(final Random random) {
		return random::nextInt;
	}

	@Test
	public void asList() {
		final long seed = random.seed();
		final Random random = new Random(seed);

		final MSeq<Integer> seq = newSeq(1000);
		for (int i = 0; i < seq.length(); ++i) {
			seq.set(i, random.nextInt());
		}

		final List<Integer> list = seq.asList();
		random.setSeed(seed);
		for (int i = 0; i < seq.length(); ++i) {
			list.set(i, random.nextInt());
		}

		random.setSeed(seed);
		for (int i = 0; i < seq.length(); ++i) {
			Assert.assertEquals(list.get(i).intValue(), random.nextInt());
		}

		random.setSeed(seed);
		for (int i = 0; i < seq.length(); ++i) {
			Assert.assertEquals(seq.get(i).intValue(), random.nextInt());
		}
	}

	@Test(dataProvider = "sequences")
	public void reverse(final MSeq<Integer> seq) {
		final ISeq<Integer> original = seq.toISeq();

		seq.reverse();
		Assert.assertNotEquals(seq, original);
		for (int i = 0; i < seq.length(); ++i) {
			Assert.assertEquals(seq.get(i), original.get(seq.length() - i - 1));
		}

		Assert.assertEquals(original, seq.reverse());
	}

	@Test(dataProvider = "sequences")
	public void sort(final MSeq<Integer> seq) {
		seq.shuffle(new Random(23));
		Assert.assertFalse(seq.isSorted());

		seq.sort();
		Assert.assertTrue(seq.isSorted());
	}

	@Test(dataProvider = "sequences")
	public void sortWithComparator(final MSeq<Integer> seq) {
		seq.shuffle(new Random(23));
		Assert.assertFalse(seq.isSorted());

		seq.sort((a, b) -> b.compareTo(a));
		Assert.assertTrue(seq.isSorted((a, b) -> b.compareTo(a)));
		Assert.assertTrue(seq.reverse().isSorted());
	}

	@Test(dataProvider = "sequences")
	public void sortWithStart(final MSeq<Integer> seq) {
		seq.shuffle(new Random(23));
		Assert.assertFalse(seq.isSorted());

		seq.sort(2);
		Assert.assertTrue(seq.subSeq(2).isSorted());
		Assert.assertFalse(seq.isSorted());
	}

	@Test(dataProvider = "sequences")
	public void sortWithEnd(final MSeq<Integer> seq) {
		seq.shuffle(new Random(23));
		Assert.assertFalse(seq.isSorted());

		seq.sort(0, seq.length() - 2);
		Assert.assertTrue(seq.subSeq(0, seq.length() - 2).isSorted());
		Assert.assertFalse(seq.isSorted());
	}

	@Test(dataProvider = "sequences")
	public void fill(final MSeq<Integer> seq) {
		final long seed = random.seed();
		final Random random = new Random(seed);

		seq.fill(RandomInt(random));

		random.setSeed(seed);
		for (int i = 0; i < seq.length(); ++i) {
			Assert.assertEquals(seq.get(i).intValue(), random.nextInt());
		}
	}

	@Test(dataProvider = "sequences")
	public void set(final MSeq<Integer> seq) {
		final long seed = random.seed();
		final Random random = new Random(seed);

		for (int i = 0; i < seq.length(); ++i) {
			seq.set(i, random.nextInt());
		}

		random.setSeed(seed);
		for (int i = 0; i < seq.length(); ++i) {
			Assert.assertEquals(seq.get(i).intValue(), random.nextInt());
		}
	}

	@Test(dataProvider = "sequences")
	public void setAll(final MSeq<Integer> seq) {
		final long seed = random.seed();
		final Random random = new Random(seed);

		final Integer v = random.nextInt();
		seq.fill(() -> v);

		random.setSeed(seed);
		final Integer value = random.nextInt();
		for (int i = 0; i < seq.length(); ++i) {
			Assert.assertEquals(seq.get(i), value);
		}
	}

	@Test(dataProvider = "sequences")
	public void setAllArray(final MSeq<Integer> seq) {
		final long seed = random.seed();
		final Random random = new Random(seed);

		final Integer[] array = new Integer[seq.length()];
		for (int i = 0; i < array.length; ++i) {
			array[i] = random.nextInt();
		}
		seq.setAll(array);

		random.setSeed(seed);
		for (int i = 0; i < seq.length(); ++i) {
			Assert.assertEquals(seq.get(i).intValue(), random.nextInt());
		}
	}

	@Test(dataProvider = "sequences")
	public void setAllIterable(final MSeq<Integer> seq) {
		final long seed = random.seed();
		final Random random = new Random(seed);

		final Integer[] array = new Integer[seq.length()];
		for (int i = 0; i < array.length; ++i) {
			array[i] = random.nextInt();
		}
		seq.setAll(Arrays.asList(array));

		random.setSeed(seed);
		for (int i = 0; i < seq.length(); ++i) {
			Assert.assertEquals(seq.get(i).intValue(), random.nextInt());
		}
	}

	@Test(dataProvider = "sequences")
	public void setAllIterator(final MSeq<Integer> seq) {
		final long seed = random.seed();
		final Random random = new Random(seed);

		final Integer[] array = new Integer[seq.length()];
		for (int i = 0; i < array.length; ++i) {
			array[i] = random.nextInt();
		}
		seq.setAll(Arrays.asList(array).iterator());

		random.setSeed(seed);
		for (int i = 0; i < seq.length(); ++i) {
			Assert.assertEquals(seq.get(i).intValue(), random.nextInt());
		}
	}

	@Test(dataProvider = "sequences")
	public void swapIntInt(final MSeq<Integer> seq) {
		for (int i = 0; i < seq.length() - 3; ++i) {
			final Integer[] copy = seq.toArray(new Integer[0]);
			final int j = i + 2;
			final Integer vi = seq.get(i);
			final Integer vj = seq.get(j);

			seq.swap(i, j);

			Assert.assertEquals(seq.get(i), vj);
			Assert.assertEquals(seq.get(j), vi);
			for (int k = 0; k < seq.length(); ++k) {
				if (k != i && k != j) {
					Assert.assertEquals(seq.get(k), copy[k]);
				}
			}
		}
	}

	@Test(dataProvider = "sequences")
	public void swapIntIntMSeqInt(final MSeq<Integer> seq) {
		for (int start = 0; start < seq.length() - 3; ++start) {
			final long seed = random.seed();
			final Random random = new Random(seed);
			final MSeq<Integer> other = newSeq(seq.length());
			final MSeq<Integer> otherCopy = newSeq(seq.length());
			for (int j = 0; j < other.length(); ++j) {
				other.set(j, random.nextInt());
				otherCopy.set(j, other.get(j));
			}

			final Integer[] copy = seq.toArray(new Integer[0]);
			final int end = start + 2;
			final int otherStart = 1;

			seq.swap(start, end, other, otherStart);

			for (int j = start; j < end; ++j) {
				Assert.assertEquals(seq.get(j), otherCopy.get(j + otherStart - start));
			}
			for (int j = 0; j < (end - start); ++j) {
				Assert.assertEquals(other.get(j + otherStart), copy[j + start]);
			}
		}
	}

	@Test(dataProvider = "sequences")
	public void toISeq(final MSeq<Integer> seq) {
		final ISeq<Integer> iseq = seq.toISeq();
		final Integer[] copy = seq.toArray(new Integer[0]);

		final long seed = random.seed();
		final Random random = new Random(seed);
		for (int i = 0; i < seq.length(); ++i) {
			seq.set(i, random.nextInt());
		}

		random.setSeed(seed);
		for (int i = 0; i < seq.length(); ++i) {
			Assert.assertEquals(seq.get(i).intValue(), random.nextInt());
		}

		for (int i = 0; i < seq.length(); ++i) {
			Assert.assertEquals(iseq.get(i), copy[i]);
		}
	}

	@Test
	public void toSeqMSeqChange() {
		final MSeq<Integer> mseq = newSeq(20);
		final ISeq<Integer> iseq1 = mseq.toISeq();

		mseq.shuffle();

		final ISeq<Integer> iseq2 = mseq.toISeq();
		Assert.assertNotEquals(iseq1, iseq2, "ISeq instances must not be equal.");
	}

	@Test
	public void toListIteratorChange() {
		final MSeq<Integer> mseq = newSeq(20);
		final ISeq<Integer> iseq1 = mseq.toISeq();

		final ListIterator<Integer> it = mseq.listIterator();
		it.next();
		it.set(300);

		final ISeq<Integer> iseq2 = mseq.toISeq();
		Assert.assertNotEquals(iseq1, iseq2, "ISeq instances must not be equal.");
	}

	@Test
	public void toListChange() {
		final MSeq<Integer> mseq = newSeq(20);
		final ISeq<Integer> iseq1 = mseq.toISeq();

		final List<Integer> list = mseq.asList();
		list.set(0, 300);

		final ISeq<Integer> iseq2 = mseq.toISeq();
		Assert.assertNotEquals(iseq1, iseq2, "ISeq instances must not be equal.");
	}

}

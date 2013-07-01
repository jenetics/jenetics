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
package org.jenetics.util;

import java.util.Arrays;
import java.util.Random;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date$</em>
 */
public abstract class MSeqTestBase extends SeqTestBase {

	@Override
	protected abstract MSeq<Integer> newSeq(final int length);

	private Factory<Integer> RandomInt(final Random random) {
		return new Factory<Integer>() {
			@Override
			public Integer newInstance() {
				return random.nextInt();
			}
		};
	}

	@Test(dataProvider = "sequences")
	public void fill(final MSeq<Integer> seq) {
		final long seed = math.random.seed();
		final Random random = new Random(seed);

		seq.fill(RandomInt(random));

		random.setSeed(seed);
		for (int i = 0; i < seq.length(); ++i) {
			Assert.assertEquals(seq.get(i).intValue(), random.nextInt());
		}
	}

	@Test(dataProvider = "sequences")
	public void set(final MSeq<Integer> seq) {
		final long seed = math.random.seed();
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
		final long seed = math.random.seed();
		final Random random = new Random(seed);

		seq.setAll(random.nextInt());

		random.setSeed(seed);
		final Integer value = random.nextInt();
		for (int i = 0; i < seq.length(); ++i) {
			Assert.assertEquals(seq.get(i), value);
		}
	}

	@Test(dataProvider = "sequences")
	public void setAllArray(final MSeq<Integer> seq) {
		final long seed = math.random.seed();
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
		final long seed = math.random.seed();
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
		final long seed = math.random.seed();
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
			final long seed = math.random.seed();
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

		final long seed = math.random.seed();
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

}








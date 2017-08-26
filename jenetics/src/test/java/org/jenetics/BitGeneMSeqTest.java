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
package org.jenetics;

import java.util.Random;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import org.jenetics.internal.collection.Array;
import org.jenetics.internal.math.random;

import org.jenetics.util.MSeq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public class BitGeneMSeqTest  {

	public MSeq<BitGene> newSeq(final int length) {
		return BitGeneMSeq.of(Array.of(BitGeneStore.ofLength(length)));
	}

	@Test(dataProvider = "sequences")
	public void swapIntInt(final MSeq<BitGene> seq) {
		for (int i = 0; i < seq.length() - 3; ++i) {
			final BitGene[] copy = seq.toArray(new BitGene[0]);
			final int j = i + 2;
			final BitGene vi = seq.get(i);
			final BitGene vj = seq.get(j);

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
	public void swapIntIntMSeqInt(final MSeq<BitGene> seq) {
		for (int start = 0; start < seq.length() - 3; ++start) {
			final long seed = random.seed();
			final Random random = new Random(seed);
			final MSeq<BitGene> other = newSeq(seq.length());
			final MSeq<BitGene> otherCopy = newSeq(seq.length());
			for (int j = 0; j < other.length(); ++j) {
				other.set(j, BitGene.of(random.nextBoolean()));
				otherCopy.set(j, other.get(j));
			}

			final BitGene[] copy = seq.toArray(new BitGene[0]);
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

	@DataProvider
	public Object[][] sequences() {
		return new Object[][] {
			{newSeq(330)},
			{newSeq(350).subSeq(50)} ,
			{newSeq(330).subSeq(80, 230)},
			{newSeq(500).subSeq(50, 430).subSeq(100)}
		};
	}

}

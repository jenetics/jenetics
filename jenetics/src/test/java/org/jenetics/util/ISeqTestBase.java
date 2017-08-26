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

import java.util.List;
import java.util.Random;

import org.testng.Assert;
import org.testng.annotations.Test;

import org.jenetics.internal.math.random;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public abstract class ISeqTestBase extends SeqTestBase {

	@Override
	protected abstract ISeq<Integer> newSeq(final int length);

	@Test(expectedExceptions = UnsupportedOperationException.class)
	public void asList() {
		final ISeq<Integer> seq = newSeq(1000);
		final List<Integer> list = seq.asList();

		list.set(3, 3);
	}

	@Test(dataProvider = "sequences")
	public void copy(final ISeq<Integer> seq) {
		final MSeq<Integer> copy = seq.copy();
		Assert.assertEquals(copy, seq);

		final Integer[] mcopy = copy.toArray(new Integer[0]);
		for (int i = 0; i < mcopy.length; ++i) {
			Assert.assertEquals(mcopy[i], seq.get(i));
		}

		final long seed = random.seed();
		final Random random = new Random(seed);
		for (int i = 0; i < copy.length(); ++i) {
			copy.set(i, random.nextInt());
		}

		for (int i = 0; i < mcopy.length; ++i) {
			Assert.assertEquals(mcopy[i], seq.get(i));
		}

		random.setSeed(seed);
		for (int i = 0; i < copy.length(); ++i) {
			Assert.assertEquals(copy.get(i).intValue(), random.nextInt());
		}
	}
}

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

import static org.jenetics.util.MSeq.toMSeq;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import org.jenetics.internal.util.Named;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date: 2014-05-14 $</em>
 */
public class MSeqTest {

	@Test
	public void collector() {
		final int size = 10_000;
		final Random random = RandomRegistry.getRandom();

		final List<Double> list = new ArrayList<>(size);
		for (int i = 0; i < size; ++i) {
			list.add(random.nextDouble());
		}

		final MSeq<Double> seq = list.stream().collect(toMSeq());
		Assert.assertEquals(list, seq.asList());
	}

	@Test(dataProvider = "subSequences")
	public void immutable(final Named<MSeq<Integer>> parameter) {
		final MSeq<Integer> seq = parameter.value;
		final Integer second = seq.get(1);

		final ISeq<Integer> iseq = seq.toISeq();
		Assert.assertEquals(iseq.get(1), second);

		final Integer newSecond = -22;
		seq.set(1, newSecond);
		Assert.assertEquals(seq.get(1), newSecond);
		Assert.assertEquals(iseq.get(1), second);
	}

	@Test(dataProvider = "subSequences")
	public void subSeqImmutable(final Named<MSeq<Integer>> parameter) {
		final MSeq<Integer> seq = parameter.value;
		final Integer second = seq.get(1);

		final MSeq<Integer> slice = seq.subSeq(1);
		Assert.assertEquals(slice.get(0), second);

		final ISeq<Integer> islice = slice.toISeq();
		Assert.assertEquals(islice.get(0), second);

		final Integer newSecond = -22;
		seq.set(1, newSecond);
		Assert.assertEquals(slice.get(0), newSecond);
		Assert.assertEquals(islice.get(0), second);
	}

	@Test(dataProvider = "subSequences")
	public void subSubSeqImmutable(final Named<MSeq<Integer>> parameter) {
		final MSeq<Integer> seq = parameter.value;
		final Integer second = seq.get(1);
		final Integer third = seq.get(2);

		final MSeq<Integer> slice = seq.subSeq(1);
		Assert.assertEquals(slice.get(0), second);

		final MSeq<Integer> sliceSlice = slice.subSeq(1);
		Assert.assertEquals(sliceSlice.get(0), third);

		final ISeq<Integer> islice = slice.toISeq();
		Assert.assertEquals(islice.get(0), second);

		final ISeq<Integer> isliceSlice = sliceSlice.toISeq();
		Assert.assertEquals(isliceSlice.get(0), third);

		final Integer newSecond = -22;
		seq.set(1, newSecond);
		Assert.assertEquals(slice.get(0), newSecond);
		Assert.assertEquals(islice.get(0), second);

		final Integer newThird = -333;
		seq.set(2, newThird);
		Assert.assertEquals(sliceSlice.get(0), newThird);
		Assert.assertEquals(isliceSlice.get(0), third);
	}

	@DataProvider(name = "subSequences")
	public Object[][] subSequences() {
		final MSeq<Integer> mseq = MSeq.ofLength(10);
		for (int i = 0; i < mseq.length(); ++i) {
			mseq.set(i, i + 1);
		}


		final List<MSeq<Integer>> sequences = new ArrayList<>();
		MSeq<Integer> next = mseq;
		for (int i = 2; i < mseq.length(); ++i) {
			sequences.add(next);
			next = next.subSeq(1);
		}

		return sequences.stream()
			.map(s -> new Object[]{Named.of(s.toString(","), s)})
			.toArray(Object[][]::new);
	}

}

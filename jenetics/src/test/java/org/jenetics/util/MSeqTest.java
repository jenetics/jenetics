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
		final Integer fourth = seq.get(3);

		final MSeq<Integer> slice = seq.subSeq(1);
		Assert.assertEquals(slice.get(0), second);

		final MSeq<Integer> sliceSlice = slice.subSeq(1);
		Assert.assertEquals(sliceSlice.get(0), third);

		final ISeq<Integer> islice = slice.toISeq();
		Assert.assertEquals(islice.get(0), second);

		final ISeq<Integer> isliceSlice = sliceSlice.toISeq();
		final ISeq<Integer> isliceSlice2 = islice.subSeq(1);
		Assert.assertEquals(isliceSlice.get(0), third);
		Assert.assertEquals(isliceSlice2.get(0), third);

		final Integer newSecond = -22;
		seq.set(1, newSecond);
		Assert.assertEquals(slice.get(0), newSecond);
		Assert.assertEquals(islice.get(0), second);

		final Integer newThird = -333;
		seq.set(2, newThird);
		Assert.assertEquals(sliceSlice.get(0), newThird);
		Assert.assertEquals(isliceSlice.get(0), third);
		Assert.assertEquals(isliceSlice2.get(0), third);

		final Integer newFourth = -4444;
		seq.set(3, newFourth);
		Assert.assertEquals(sliceSlice.get(1), newFourth);
		Assert.assertEquals(isliceSlice2.subSeq(1).get(0), fourth);
	}

	@Test(dataProvider = "subSequences")
	public void subSubSeqImmutable2(final Named<MSeq<Integer>> parameter) {
		final MSeq<Integer> seq = parameter.value;
		final Integer[] values = seq.toArray(new Integer[seq.length()]);
		final Integer[] newValues = seq
			.map(i -> -i)
			.toArray(new Integer[seq.length()]);
		final Integer[] newValues2 = seq
			.map(i -> -i - 1000)
			.toArray(new Integer[seq.length()]);

		ISeq<Integer> iseq = seq.toISeq();
		MSeq<Integer> mseq = seq;
		for (int i = 0; i < seq.length(); ++i) {
			Assert.assertEquals(seq.get(i), values[i]);
			Assert.assertEquals(mseq.get(0), values[i]);
			Assert.assertEquals(iseq.get(0), values[i]);

			seq.set(i, newValues[i]);
			Assert.assertEquals(seq.get(i), newValues[i]);
			Assert.assertEquals(mseq.get(0), newValues[i]);
			Assert.assertEquals(iseq.get(0), values[i]);

			mseq.set(0, newValues2[i]);
			Assert.assertEquals(seq.get(i), newValues2[i]);
			Assert.assertEquals(mseq.get(0), newValues2[i]);
			Assert.assertEquals(iseq.get(0), values[i]);

			iseq = iseq.subSeq(1);
			mseq = mseq.subSeq(1);
		}
	}

	@DataProvider(name = "subSequences")
	public Object[][] subSequences() {
		final MSeq<Integer> mseq = MSeq.ofLength(20);
		for (int i = 0; i < mseq.length(); ++i) {
			mseq.set(i, i + 1);
		}


		final List<MSeq<Integer>> sequences = new ArrayList<>();
		MSeq<Integer> next = mseq;
		for (int i = 3; i < mseq.length(); ++i) {
			sequences.add(next);
			next = next.subSeq(1);
		}

		return sequences.stream()
			.map(s -> new Object[]{Named.of(s.toString(","), s)})
			.toArray(Object[][]::new);
	}

	@Test
	public void empty() {
		Assert.assertNotNull(MSeq.EMPTY);
		Assert.assertNotNull(MSeq.empty());
		Assert.assertSame(MSeq.EMPTY, MSeq.empty());
		Assert.assertEquals(MSeq.EMPTY.length(), 0);
		Assert.assertEquals(MSeq.empty().asList().size(), 0);
	}

	@Test
	public void zeroLengthSameAsEmpty() {
		Assert.assertSame(MSeq.of(), MSeq.empty());
	}

	@Test
	public void isEmpty() {
		Assert.assertTrue(MSeq.empty().isEmpty());
		Assert.assertEquals(MSeq.empty().length(), 0);
	}

	@Test
	public void subSeqEmptyMSeq() {
		Assert.assertSame(MSeq.of(1, 2, 3).subSeq(3), MSeq.empty());
		Assert.assertSame(MSeq.of(1, 2, 3).subSeq(3, 3), MSeq.empty());
		Assert.assertSame(MSeq.of(1, 2, 3).subSeq(2, 2), MSeq.empty());
		Assert.assertSame(MSeq.of(1, 2, 3).subSeq(1, 1), MSeq.empty());
		Assert.assertSame(MSeq.of(1, 2, 3).subSeq(0, 0), MSeq.empty());
	}

	@Test
	public void emptySeqAppend() {
		final MSeq<Integer> empty = MSeq.empty();
		final MSeq<Integer> seq = MSeq.of(1, 2, 3, 4);
		final MSeq<Integer> aseq = empty.append(seq);

		Assert.assertEquals(aseq, seq);
	}

	@Test
	public void emptySeqPrepend() {
		final MSeq<Integer> empty = MSeq.empty();
		final MSeq<Integer> seq = MSeq.of(1, 2, 3, 4);
		final MSeq<Integer> aseq = empty.prepend(seq);

		Assert.assertEquals(aseq, seq);
	}

	@Test(expectedExceptions = ArrayIndexOutOfBoundsException.class)
	public void subSeqOutOtBounds1() {
		MSeq.of(1, 2, 3).subSeq(5);
	}

	@Test(expectedExceptions = ArrayIndexOutOfBoundsException.class)
	public void subSeqOutOtBounds2() {
		MSeq.of(1, 2, 3).subSeq(-5);
	}

	@Test(expectedExceptions = ArrayIndexOutOfBoundsException.class)
	public void subSeqOutOtBounds4() {
		MSeq.of(1, 2, 3).subSeq(0, 10);
	}

	@Test(expectedExceptions = ArrayIndexOutOfBoundsException.class)
	public void subSeqOutOtBounds5() {
		MSeq.of(1, 2, 3).subSeq(-5, 2);
	}

	@Test
	public void mapEmptyMSeq() {
		final MSeq<Integer> integers = MSeq.empty();
		final MSeq<String> strings = integers.map(Object::toString);

		Assert.assertSame(integers, strings);
		Assert.assertSame(strings, MSeq.empty());
	}

	@Test
	public void copyEmptyMSeq() {
		Assert.assertSame(MSeq.empty().copy(), MSeq.empty());
	}

	@Test
	public void toISeqEmptyMSeq() {
		Assert.assertSame(MSeq.empty().toISeq(), ISeq.empty());
	}

}

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

import static org.jenetics.util.Seq.toSeq;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public class SeqTest {

	@Test
	public void collector() {
		final int size = 10_000;
		final Random random = RandomRegistry.getRandom();

		final List<Double> list = new ArrayList<>(size);
		for (int i = 0; i < size; ++i) {
			list.add(random.nextDouble());
		}

		final Seq<Double> seq = list.stream().collect(toSeq());
		Assert.assertEquals(list, seq.asList());
	}

	@Test
	public void empty() {
		Assert.assertNotNull(Seq.EMPTY);
		Assert.assertNotNull(Seq.empty());
		Assert.assertSame(Seq.EMPTY, Seq.empty());
		Assert.assertEquals(Seq.EMPTY.length(), 0);
		Assert.assertEquals(Seq.empty().asList().size(), 0);
	}

	@Test
	public void zeroLengthSameAsEmpty() {
		Assert.assertSame(Seq.of(), Seq.empty());
	}

	@Test
	public void isEmpty() {
		Assert.assertTrue(Seq.empty().isEmpty());
		Assert.assertEquals(Seq.empty().length(), 0);
	}

	@Test
	public void subSeqEmptySeq() {
		Assert.assertSame(Seq.of(1, 2, 3).subSeq(3), Seq.empty());
		Assert.assertSame(Seq.of(1, 2, 3).subSeq(3, 3), Seq.empty());
		Assert.assertSame(Seq.of(1, 2, 3).subSeq(2, 2), Seq.empty());
		Assert.assertSame(Seq.of(1, 2, 3).subSeq(1, 1), Seq.empty());
		Assert.assertSame(Seq.of(1, 2, 3).subSeq(0, 0), Seq.empty());
	}

	@Test
	public void emptySeqAppend() {
		final Seq<Integer> empty = Seq.empty();
		final Seq<Integer> seq = Seq.of(1, 2, 3, 4);
		final Seq<Integer> aseq = empty.append(seq);

		Assert.assertEquals(aseq, seq);
		Assert.assertSame(aseq, seq);
	}

	@Test
	public void emptySeqPrepend() {
		final Seq<Integer> empty = Seq.empty();
		final Seq<Integer> seq = Seq.of(1, 2, 3, 4);
		final Seq<Integer> aseq = empty.prepend(seq);

		Assert.assertEquals(aseq, seq);
		Assert.assertSame(aseq, seq);
	}

	@Test(expectedExceptions = ArrayIndexOutOfBoundsException.class)
	public void subSeqOutOtBounds1() {
		Seq.of(1, 2, 3).subSeq(5);
	}

	@Test(expectedExceptions = ArrayIndexOutOfBoundsException.class)
	public void subSeqOutOtBounds2() {
		Seq.of(1, 2, 3).subSeq(-5);
	}

	@Test(expectedExceptions = ArrayIndexOutOfBoundsException.class)
	public void subSeqOutOtBounds4() {
		Seq.of(1, 2, 3).subSeq(0, 10);
	}

	@Test(expectedExceptions = ArrayIndexOutOfBoundsException.class)
	public void subSeqOutOtBounds5() {
		Seq.of(1, 2, 3).subSeq(-5, 2);
	}

	@Test
	public void mapEmptyMSeq() {
		final Seq<Integer> integers = Seq.empty();
		final Seq<String> strings = integers.map(Object::toString);

		Assert.assertSame(integers, strings);
		Assert.assertSame(strings, Seq.empty());
	}

}

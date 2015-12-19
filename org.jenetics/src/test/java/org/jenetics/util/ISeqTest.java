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

import static org.jenetics.util.ISeq.toISeq;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public class ISeqTest {

	@Test
	public void collector() {
		final int size = 10_000;
		final Random random = RandomRegistry.getRandom();

		final List<Double> list = new ArrayList<>(size);
		for (int i = 0; i < size; ++i) {
			list.add(random.nextDouble());
		}

		final ISeq<Double> seq = list.stream().collect(toISeq());
		Assert.assertEquals(list, seq.asList());
	}

	@Test
	public void empty() {
		Assert.assertNotNull(ISeq.EMPTY);
		Assert.assertNotNull(ISeq.empty());
		Assert.assertSame(ISeq.EMPTY, ISeq.empty());
		Assert.assertEquals(ISeq.EMPTY.length(), 0);
		Assert.assertEquals(ISeq.empty().asList().size(), 0);
	}

	@Test
	public void zeroLengthSameAsEmpty() {
		Assert.assertSame(ISeq.of(), ISeq.empty());
	}

	@Test
	public void isEmpty() {
		Assert.assertTrue(ISeq.empty().isEmpty());
		Assert.assertEquals(ISeq.empty().length(), 0);
	}

	@Test
	public void subSeqEmptyISeq() {
		Assert.assertSame(ISeq.of(1, 2, 3).subSeq(3), ISeq.empty());
		Assert.assertSame(ISeq.of(1, 2, 3).subSeq(3, 3), ISeq.empty());
		Assert.assertSame(ISeq.of(1, 2, 3).subSeq(2, 2), ISeq.empty());
		Assert.assertSame(ISeq.of(1, 2, 3).subSeq(1, 1), ISeq.empty());
		Assert.assertSame(ISeq.of(1, 2, 3).subSeq(0, 0), ISeq.empty());
	}

	@Test(expectedExceptions = ArrayIndexOutOfBoundsException.class)
	public void subSeqOutOtBounds1() {
		ISeq.of(1, 2, 3).subSeq(5);
	}

	@Test(expectedExceptions = ArrayIndexOutOfBoundsException.class)
	public void subSeqOutOtBounds2() {
		ISeq.of(1, 2, 3).subSeq(-5);
	}

	@Test(expectedExceptions = ArrayIndexOutOfBoundsException.class)
	public void subSeqOutOtBounds4() {
		ISeq.of(1, 2, 3).subSeq(0, 10);
	}

	@Test(expectedExceptions = ArrayIndexOutOfBoundsException.class)
	public void subSeqOutOtBounds5() {
		ISeq.of(1, 2, 3).subSeq(-5, 2);
	}

	@Test
	public void mapEmptyISeq() {
		final ISeq<Integer> integers = ISeq.empty();
		final ISeq<String> strings = integers.map(Object::toString);

		Assert.assertSame(integers, strings);
		Assert.assertSame(strings, ISeq.empty());
	}

	@Test
	public void copyEmptyISeq() {
		Assert.assertSame(ISeq.empty().copy(), MSeq.empty());
	}

}

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
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmail.com)
 */
package io.jenetics.internal.collection;

import java.util.Random;

import org.testng.Assert;
import org.testng.annotations.Test;

import io.jenetics.util.ISeq;
import io.jenetics.util.ISeqTestBase;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
@Test
public class LazyISeqTest extends ISeqTestBase {

	@Override
	protected ISeq<Integer> newSeq(final int length) {
		final int[] values = new int[length];
		for (int i = 0; i < length; ++i) {
			values[i] = i;
		}

		return LazyISeq.of(i -> values[i], length);
	}

	@Test
	public void readThrough() {
		final int length = 100;
		final Random random = new Random();
		final int[] values = new int[length];
		for (int i = 0; i < length; ++i) {
			values[i] = random.nextInt();
		}

		final ISeq<Integer> lazy = LazyISeq.of(i -> values[i], length);
		for (int i = 0; i < length; ++i) {
			Assert.assertEquals(lazy.get(i).intValue(), values[i]);
		}

		for (int i = 0; i < length; ++i) {
			values[i] = random.nextInt();
		}
		for (int i = 0; i < length; ++i) {
			Assert.assertEquals(lazy.get(i).intValue(), values[i]);
		}
	}
}

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
package io.jenetics.util;

import java.util.Random;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class BaseSeqArrayViewTest extends SeqTestBase {

	@Override
	protected Seq<Integer> newSeq(final int length) {
		final int[] array = new int[length];
		for (int i = 0; i < length; ++i) {
			array[i] = i;
		}

		return wrap(array);
	}

	private static SeqView<Integer> wrap(final int[] array) {
		return new SeqView<>(new BaseSeqList<>(new BaseSeq<>() {
			@Override
			public Integer get(final int index) {
				return array[index];
			}
			@Override
			public int length() {
				return array.length;
			}
		}));
	}

	@Test
	public void readThrough() {
		final int length = 100;
		final Random random = new Random();
		final int[] array = new int[length];
		for (int i = 0; i < length; ++i) {
			array[i] = random.nextInt();
		}

		final SeqView<Integer> view = wrap(array);
		for (int i = 0; i < length; ++i) {
			Assert.assertEquals(view.get(i).intValue(), array[i]);
		}

		for (int i = 0; i < length; ++i) {
			array[i] = random.nextInt();
		}
		for (int i = 0; i < length; ++i) {
			Assert.assertEquals(view.get(i).intValue(), array[i]);
		}
	}

}

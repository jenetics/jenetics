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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
@Test
public class SeqViewTest extends SeqTestBase {

	@Override
	protected Seq<Integer> newSeq(final int length) {
		final List<Integer> list = new ArrayList<>(length);
		for (int i = 0; i < length; ++i) {
			list.add(i);
		}
		return new SeqView<>(list);
	}

	@Test
	public void readThrough() {
		final int length = 100;
		final Random random = new Random();
		final List<Integer> list = new ArrayList<>(length);
		for (int i = 0; i < length; ++i) {
			list.add(random.nextInt());
		}

		final SeqView<Integer> view = new SeqView<>(list);
		for (int i = 0; i < length; ++i) {
			Assert.assertEquals(view.get(i), list.get(i));
		}

		for (int i = 0; i < length; ++i) {
			list.set(i, random.nextInt());
		}
		for (int i = 0; i < length; ++i) {
			Assert.assertEquals(view.get(i), list.get(i));
		}
	}

}

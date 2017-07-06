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

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public class requireTest {

	@Test
	public void validPredicate() {
		final MSeq<Verifiable> array = MSeq.ofLength(100);
		for (int i = 0; i < array.length(); ++i) {
			array.set(i, () -> true);
		}
		Assert.assertEquals(array.indexWhere(o -> !o.isValid()), -1);

		array.set(77, () -> false);
		Assert.assertEquals(array.indexWhere(o -> !o.isValid()), 77);
	}

}

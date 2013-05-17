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
package org.jenetix.util;

import org.jscience.mathematics.number.LargeInteger;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public class LargeIntegerRandomTest {

	@Test
	public void random() {
		final NumberRandom<LargeInteger> random = new LargeIntegerRandom();
		final LargeInteger min = LargeInteger.valueOf(0);
		final LargeInteger max = LargeInteger.ONE.times2pow(256).minus(-1);
		//final LargeInteger max = LargeInteger.valueOf("100000000999999999990000000000000000000000000");

		for (int i = 0; i < 10; ++i) {
			System.out.println(random.next(min, max));
		}
	}

}

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
package org.jenetics.internal.math;

import java.util.Random;

import org.testng.Assert;
import org.testng.annotations.Test;

import org.jenetics.util.RandomRegistry;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public class probabilityTest {

	@Test
	public void toIntToFloat() {
		final Random random = RandomRegistry.getRandom();

		for (int i = 0; i < 100000; ++i) {
			final float p = random.nextFloat();

			final int ip = probability.toInt(p);
			final float fip = probability.toFloat(ip);
			Assert.assertEquals(fip, p, 0.000001F);
		}
	}

	@Test
	public void probabilityToInt() {
		Assert.assertEquals(probability.toInt(0), Integer.MIN_VALUE);
		Assert.assertEquals(probability.toInt(1), Integer.MAX_VALUE);
		Assert.assertEquals(probability.toInt(0.5), 0);
		Assert.assertEquals(probability.toInt(0.25), Integer.MIN_VALUE/2);
		Assert.assertEquals(probability.toInt(0.75), Integer.MAX_VALUE/2);
	}

}

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
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public class LCG64ShiftRandomCompatibilityTest {

	@Test(dataProvider = "data")
	public void random(final TestData data) {
		final String[] parameters = data.getParameters();
		final long seed = Long.parseLong(parameters[0]);
		final int splitp = Integer.parseInt(parameters[1]);
		final int splits = Integer.parseInt(parameters[2]);
		final long jump = Long.parseLong(parameters[3]);
		final int jump2 = Integer.parseInt(parameters[4]);

		final LCG64ShiftRandom random = new LCG64ShiftRandom(seed);
		random.split(splitp, splits);
		random.jump(jump);
		random.jump2(jump2);

		for (final String[] value : data) {
			final long expected = Long.parseLong(value[0]);
			Assert.assertEquals(random.nextLong(), expected);
		}
	}

	@Test(dataProvider = "data")
	public void threadSafeRandom(final TestData data) {
		final String[] parameters = data.getParameters();
		final long seed = Long.parseLong(parameters[0]);
		final int splitp = Integer.parseInt(parameters[1]);
		final int splits = Integer.parseInt(parameters[2]);
		final long jump = Long.parseLong(parameters[3]);
		final int jump2 = Integer.parseInt(parameters[4]);

		final LCG64ShiftRandom random = new LCG64ShiftRandom.ThreadSafe(seed);
		random.split(splitp, splits);
		random.jump(jump);
		random.jump2(jump2);

		for (final String[] value : data) {
			final long expected = Long.parseLong(value[0]);
			Assert.assertEquals(random.nextLong(), expected);
		}
	}

	@DataProvider(name = "data")
	public Object[][] data() {
		return TestData.list("/org/jenetics/util/LCG64ShiftRandom")
			.map(data -> new Object[]{data})
			.toArray(Object[][]::new);
	}

}

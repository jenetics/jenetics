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
 * @version <em>$Date: 2014-01-20 $</em>
 */
public class LCG64ShiftRandomCompatibilityTest {

	private final static String TEST_RESOURCE =
			"/org/jenetics/util/LCG64ShiftRandom.dat/%d-%d-%d-%d-%d";

	private static TestData testData(
		final Long seed,
		final Integer splitp,
		final Integer splits,
		final Long jump,
		final Integer jump2
	) {
		final String resource = String.format(
			TEST_RESOURCE, seed, splitp, splits, jump, jump2
		);
		return new TestData(resource);
	}

	@Test(dataProvider = "parameters")
	public void random(
		final Long seed,
		final Integer splitp,
		final Integer splits,
		final Long jump,
		final Integer jump2
	) {
		final LCG64ShiftRandom random = new LCG64ShiftRandom(seed);
		random.split(splitp, splits);
		random.jump(jump);
		random.jump2(jump2);

		for (final String[] value : testData(seed, splitp, splits, jump, jump2)) {
			final long expected = Long.parseLong(value[0]);
			Assert.assertEquals(random.nextLong(), expected);
		}
	}

	@Test(dataProvider = "parameters")
	public void threadSafeRandom(
		final Long seed,
		final Integer splitp,
		final Integer splits,
		final Long jump,
		final Integer jump2
	) {
		final LCG64ShiftRandom random = new LCG64ShiftRandom.ThreadSafe(seed);
		random.split(splitp, splits);
		random.jump(jump);
		random.jump2(jump2);

		for (final String[] value : testData(seed, splitp, splits, jump, jump2)) {
			final long expected = Long.parseLong(value[0]);
			Assert.assertEquals(random.nextLong(), expected);
		}
	}

	@DataProvider(name = "parameters")
	public Object[][] parameters() {
		return new Object[][] {
			{0L, 5, 0, 0L, 0},
			{0L, 5, 0, 0L, 23},
			{0L, 5, 0, 0L, 46},
			{0L, 5, 0, 948392782247324L, 0},
			{0L, 5, 0, 948392782247324L, 23},
			{0L, 5, 0, 948392782247324L, 46},
			{0L, 5, 2, 0L, 0},
			{0L, 5, 2, 0L, 23},
			{0L, 5, 2, 0L, 46},
			{0L, 5, 2, 948392782247324L, 0},
			{0L, 5, 2, 948392782247324L, 23},
			{0L, 5, 2, 948392782247324L, 46},
			{0L, 5, 4, 0L, 0},
			{0L, 5, 4, 0L, 23},
			{0L, 5, 4, 0L, 46},
			{0L, 5, 4, 948392782247324L, 0},
			{0L, 5, 4, 948392782247324L, 23},
			{0L, 5, 4, 948392782247324L, 46},
			{0L, 8, 0, 0L, 0},
			{0L, 8, 0, 0L, 23},
			{0L, 8, 0, 0L, 46},
			{0L, 8, 0, 948392782247324L, 0},
			{0L, 8, 0, 948392782247324L, 23},
			{0L, 8, 0, 948392782247324L, 46},
			{0L, 8, 2, 0L, 0},
			{0L, 8, 2, 0L, 23},
			{0L, 8, 2, 0L, 46},
			{0L, 8, 2, 948392782247324L, 0},
			{0L, 8, 2, 948392782247324L, 23},
			{0L, 8, 2, 948392782247324L, 46},
			{0L, 8, 4, 0L, 0},
			{0L, 8, 4, 0L, 23},
			{0L, 8, 4, 0L, 46},
			{0L, 8, 4, 948392782247324L, 0},
			{0L, 8, 4, 948392782247324L, 23},
			{0L, 8, 4, 948392782247324L, 46},
			{0L, 8, 6, 0L, 0},
			{0L, 8, 6, 0L, 23},
			{0L, 8, 6, 0L, 46},
			{0L, 8, 6, 948392782247324L, 0},
			{0L, 8, 6, 948392782247324L, 23},
			{0L, 8, 6, 948392782247324L, 46},
			{74236788222246L, 5, 0, 0L, 0},
			{74236788222246L, 5, 0, 0L, 23},
			{74236788222246L, 5, 0, 0L, 46},
			{74236788222246L, 5, 0, 948392782247324L, 0},
			{74236788222246L, 5, 0, 948392782247324L, 23},
			{74236788222246L, 5, 0, 948392782247324L, 46},
			{74236788222246L, 5, 2, 0L, 0},
			{74236788222246L, 5, 2, 0L, 23},
			{74236788222246L, 5, 2, 0L, 46},
			{74236788222246L, 5, 2, 948392782247324L, 0},
			{74236788222246L, 5, 2, 948392782247324L, 23},
			{74236788222246L, 5, 2, 948392782247324L, 46},
			{74236788222246L, 5, 4, 0L, 0},
			{74236788222246L, 5, 4, 0L, 23},
			{74236788222246L, 5, 4, 0L, 46},
			{74236788222246L, 5, 4, 948392782247324L, 0},
			{74236788222246L, 5, 4, 948392782247324L, 23},
			{74236788222246L, 5, 4, 948392782247324L, 46},
			{74236788222246L, 8, 0, 0L, 0},
			{74236788222246L, 8, 0, 0L, 23},
			{74236788222246L, 8, 0, 0L, 46},
			{74236788222246L, 8, 0, 948392782247324L, 0},
			{74236788222246L, 8, 0, 948392782247324L, 23},
			{74236788222246L, 8, 0, 948392782247324L, 46},
			{74236788222246L, 8, 2, 0L, 0},
			{74236788222246L, 8, 2, 0L, 23},
			{74236788222246L, 8, 2, 0L, 46},
			{74236788222246L, 8, 2, 948392782247324L, 0},
			{74236788222246L, 8, 2, 948392782247324L, 23},
			{74236788222246L, 8, 2, 948392782247324L, 46},
			{74236788222246L, 8, 4, 0L, 0},
			{74236788222246L, 8, 4, 0L, 23},
			{74236788222246L, 8, 4, 0L, 46},
			{74236788222246L, 8, 4, 948392782247324L, 0},
			{74236788222246L, 8, 4, 948392782247324L, 23},
			{74236788222246L, 8, 4, 948392782247324L, 46},
			{74236788222246L, 8, 6, 0L, 0},
			{74236788222246L, 8, 6, 0L, 23},
			{74236788222246L, 8, 6, 0L, 46},
			{74236788222246L, 8, 6, 948392782247324L, 0},
			{74236788222246L, 8, 6, 948392782247324L, 23},
			{74236788222246L, 8, 6, 948392782247324L, 46}
		};

	}
}








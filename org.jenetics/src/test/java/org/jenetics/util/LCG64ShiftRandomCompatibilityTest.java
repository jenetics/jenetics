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
 * @version <em>$Date: 2013-04-27 $</em>
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
			{new Long(0L), new Integer(5), new Integer(0), new Long(0L), new Integer(0)},
			{new Long(0L), new Integer(5), new Integer(0), new Long(0L), new Integer(23)},
			{new Long(0L), new Integer(5), new Integer(0), new Long(0L), new Integer(46)},
			{new Long(0L), new Integer(5), new Integer(0), new Long(948392782247324L), new Integer(0)},
			{new Long(0L), new Integer(5), new Integer(0), new Long(948392782247324L), new Integer(23)},
			{new Long(0L), new Integer(5), new Integer(0), new Long(948392782247324L), new Integer(46)},
			{new Long(0L), new Integer(5), new Integer(2), new Long(0L), new Integer(0)},
			{new Long(0L), new Integer(5), new Integer(2), new Long(0L), new Integer(23)},
			{new Long(0L), new Integer(5), new Integer(2), new Long(0L), new Integer(46)},
			{new Long(0L), new Integer(5), new Integer(2), new Long(948392782247324L), new Integer(0)},
			{new Long(0L), new Integer(5), new Integer(2), new Long(948392782247324L), new Integer(23)},
			{new Long(0L), new Integer(5), new Integer(2), new Long(948392782247324L), new Integer(46)},
			{new Long(0L), new Integer(5), new Integer(4), new Long(0L), new Integer(0)},
			{new Long(0L), new Integer(5), new Integer(4), new Long(0L), new Integer(23)},
			{new Long(0L), new Integer(5), new Integer(4), new Long(0L), new Integer(46)},
			{new Long(0L), new Integer(5), new Integer(4), new Long(948392782247324L), new Integer(0)},
			{new Long(0L), new Integer(5), new Integer(4), new Long(948392782247324L), new Integer(23)},
			{new Long(0L), new Integer(5), new Integer(4), new Long(948392782247324L), new Integer(46)},
			{new Long(0L), new Integer(8), new Integer(0), new Long(0L), new Integer(0)},
			{new Long(0L), new Integer(8), new Integer(0), new Long(0L), new Integer(23)},
			{new Long(0L), new Integer(8), new Integer(0), new Long(0L), new Integer(46)},
			{new Long(0L), new Integer(8), new Integer(0), new Long(948392782247324L), new Integer(0)},
			{new Long(0L), new Integer(8), new Integer(0), new Long(948392782247324L), new Integer(23)},
			{new Long(0L), new Integer(8), new Integer(0), new Long(948392782247324L), new Integer(46)},
			{new Long(0L), new Integer(8), new Integer(2), new Long(0L), new Integer(0)},
			{new Long(0L), new Integer(8), new Integer(2), new Long(0L), new Integer(23)},
			{new Long(0L), new Integer(8), new Integer(2), new Long(0L), new Integer(46)},
			{new Long(0L), new Integer(8), new Integer(2), new Long(948392782247324L), new Integer(0)},
			{new Long(0L), new Integer(8), new Integer(2), new Long(948392782247324L), new Integer(23)},
			{new Long(0L), new Integer(8), new Integer(2), new Long(948392782247324L), new Integer(46)},
			{new Long(0L), new Integer(8), new Integer(4), new Long(0L), new Integer(0)},
			{new Long(0L), new Integer(8), new Integer(4), new Long(0L), new Integer(23)},
			{new Long(0L), new Integer(8), new Integer(4), new Long(0L), new Integer(46)},
			{new Long(0L), new Integer(8), new Integer(4), new Long(948392782247324L), new Integer(0)},
			{new Long(0L), new Integer(8), new Integer(4), new Long(948392782247324L), new Integer(23)},
			{new Long(0L), new Integer(8), new Integer(4), new Long(948392782247324L), new Integer(46)},
			{new Long(0L), new Integer(8), new Integer(6), new Long(0L), new Integer(0)},
			{new Long(0L), new Integer(8), new Integer(6), new Long(0L), new Integer(23)},
			{new Long(0L), new Integer(8), new Integer(6), new Long(0L), new Integer(46)},
			{new Long(0L), new Integer(8), new Integer(6), new Long(948392782247324L), new Integer(0)},
			{new Long(0L), new Integer(8), new Integer(6), new Long(948392782247324L), new Integer(23)},
			{new Long(0L), new Integer(8), new Integer(6), new Long(948392782247324L), new Integer(46)},
			{new Long(74236788222246L), new Integer(5), new Integer(0), new Long(0L), new Integer(0)},
			{new Long(74236788222246L), new Integer(5), new Integer(0), new Long(0L), new Integer(23)},
			{new Long(74236788222246L), new Integer(5), new Integer(0), new Long(0L), new Integer(46)},
			{new Long(74236788222246L), new Integer(5), new Integer(0), new Long(948392782247324L), new Integer(0)},
			{new Long(74236788222246L), new Integer(5), new Integer(0), new Long(948392782247324L), new Integer(23)},
			{new Long(74236788222246L), new Integer(5), new Integer(0), new Long(948392782247324L), new Integer(46)},
			{new Long(74236788222246L), new Integer(5), new Integer(2), new Long(0L), new Integer(0)},
			{new Long(74236788222246L), new Integer(5), new Integer(2), new Long(0L), new Integer(23)},
			{new Long(74236788222246L), new Integer(5), new Integer(2), new Long(0L), new Integer(46)},
			{new Long(74236788222246L), new Integer(5), new Integer(2), new Long(948392782247324L), new Integer(0)},
			{new Long(74236788222246L), new Integer(5), new Integer(2), new Long(948392782247324L), new Integer(23)},
			{new Long(74236788222246L), new Integer(5), new Integer(2), new Long(948392782247324L), new Integer(46)},
			{new Long(74236788222246L), new Integer(5), new Integer(4), new Long(0L), new Integer(0)},
			{new Long(74236788222246L), new Integer(5), new Integer(4), new Long(0L), new Integer(23)},
			{new Long(74236788222246L), new Integer(5), new Integer(4), new Long(0L), new Integer(46)},
			{new Long(74236788222246L), new Integer(5), new Integer(4), new Long(948392782247324L), new Integer(0)},
			{new Long(74236788222246L), new Integer(5), new Integer(4), new Long(948392782247324L), new Integer(23)},
			{new Long(74236788222246L), new Integer(5), new Integer(4), new Long(948392782247324L), new Integer(46)},
			{new Long(74236788222246L), new Integer(8), new Integer(0), new Long(0L), new Integer(0)},
			{new Long(74236788222246L), new Integer(8), new Integer(0), new Long(0L), new Integer(23)},
			{new Long(74236788222246L), new Integer(8), new Integer(0), new Long(0L), new Integer(46)},
			{new Long(74236788222246L), new Integer(8), new Integer(0), new Long(948392782247324L), new Integer(0)},
			{new Long(74236788222246L), new Integer(8), new Integer(0), new Long(948392782247324L), new Integer(23)},
			{new Long(74236788222246L), new Integer(8), new Integer(0), new Long(948392782247324L), new Integer(46)},
			{new Long(74236788222246L), new Integer(8), new Integer(2), new Long(0L), new Integer(0)},
			{new Long(74236788222246L), new Integer(8), new Integer(2), new Long(0L), new Integer(23)},
			{new Long(74236788222246L), new Integer(8), new Integer(2), new Long(0L), new Integer(46)},
			{new Long(74236788222246L), new Integer(8), new Integer(2), new Long(948392782247324L), new Integer(0)},
			{new Long(74236788222246L), new Integer(8), new Integer(2), new Long(948392782247324L), new Integer(23)},
			{new Long(74236788222246L), new Integer(8), new Integer(2), new Long(948392782247324L), new Integer(46)},
			{new Long(74236788222246L), new Integer(8), new Integer(4), new Long(0L), new Integer(0)},
			{new Long(74236788222246L), new Integer(8), new Integer(4), new Long(0L), new Integer(23)},
			{new Long(74236788222246L), new Integer(8), new Integer(4), new Long(0L), new Integer(46)},
			{new Long(74236788222246L), new Integer(8), new Integer(4), new Long(948392782247324L), new Integer(0)},
			{new Long(74236788222246L), new Integer(8), new Integer(4), new Long(948392782247324L), new Integer(23)},
			{new Long(74236788222246L), new Integer(8), new Integer(4), new Long(948392782247324L), new Integer(46)},
			{new Long(74236788222246L), new Integer(8), new Integer(6), new Long(0L), new Integer(0)},
			{new Long(74236788222246L), new Integer(8), new Integer(6), new Long(0L), new Integer(23)},
			{new Long(74236788222246L), new Integer(8), new Integer(6), new Long(0L), new Integer(46)},
			{new Long(74236788222246L), new Integer(8), new Integer(6), new Long(948392782247324L), new Integer(0)},
			{new Long(74236788222246L), new Integer(8), new Integer(6), new Long(948392782247324L), new Integer(23)},
			{new Long(74236788222246L), new Integer(8), new Integer(6), new Long(948392782247324L), new Integer(46)}
		};

	}
}








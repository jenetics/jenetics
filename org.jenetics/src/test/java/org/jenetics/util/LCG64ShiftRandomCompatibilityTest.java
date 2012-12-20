/*
 * Java Genetic Algorithm Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * Author:
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 *
 */
package org.jenetics.util;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date: 2012-12-20 $</em>
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








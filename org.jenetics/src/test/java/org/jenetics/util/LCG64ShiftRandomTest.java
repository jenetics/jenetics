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

import org.testng.annotations.DataProvider;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date: 2012-12-20 $</em>
 */
public class LCG64ShiftRandomTest extends RandomTestBase {

	@DataProvider(name = "PRNG2")
	Object[][] getPRNG2() {
		return new Object[][]{
			{new LCG64ShiftRandom(666)},
			{new LCG64ShiftRandom.ThreadSafe(666)}
		};
	}

	@Override @DataProvider(name = "PRNG22")
	protected Object[][] getPRNG22() {
		final long seed = math.random.seed();
		return new Object[][]{
			{new LCG64ShiftRandom(seed), new LCG64ShiftRandom(seed)},
			{new LCG64ShiftRandom.ThreadSafe(seed), new LCG64ShiftRandom.ThreadSafe(seed)}
		};
	}

	@Override @DataProvider(name = "PRNG3")
	protected Object[][] getPRNG3() {
		final long seed = math.random.seed();
		return new Object[][]{
			{new LCG64ShiftRandom(seed)},
			{new LCG64ShiftRandom.ThreadSafe(seed)},
			{new LCG64ShiftRandom.ThreadLocal().get()}
		};
	}

}




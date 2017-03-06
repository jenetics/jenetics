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
package org.jenetics.random;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
@Test
public class LCG64ShiftRandomTest extends Random64TestBase {

	@Test
	public void create() {
		new LCG64ShiftRandom();
	}

	@Test
	public void createThreadSafe() {
		new LCG64ShiftRandom.ThreadSafe();
	}

	@Test
	public void createThreadLocal() {
		new LCG64ShiftRandom.ThreadLocal().get();
	}

	@Override
	@DataProvider(name = "seededPRNGPair")
	protected Object[][] getSeededPRNGPair() {
		final long seed = PRNG.seed();
		return new Object[][] {
			{new LCG64ShiftRandom(seed), new LCG64ShiftRandom(seed)},
			{new LCG64ShiftRandom.ThreadSafe(seed), new LCG64ShiftRandom(seed)},
			{new LCG64ShiftRandom.ThreadSafe(seed), new LCG64ShiftRandom.ThreadSafe(seed)}
		};
	}

	@Override
	@DataProvider(name = "PRNG")
	protected Object[][] getPRNG() {
		final long seed = PRNG.seed();
		return new Object[][] {
			{new LCG64ShiftRandom(seed)},
			{new LCG64ShiftRandom.ThreadSafe(seed)},
			{new LCG64ShiftRandom.ThreadLocal().get()}
		};
	}

}

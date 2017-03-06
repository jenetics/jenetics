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

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import org.jenetics.random.XOR32ShiftRandom.Param;
import org.jenetics.random.XOR32ShiftRandom.ParamSelector;
import org.jenetics.random.XOR32ShiftRandom.Shift;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public class XOR32ShiftRandomTest extends Random32TestBase {

	@Test
	public void create() {
		new XOR32ShiftRandom();
	}

	@Test
	public void createThreadSafe() {
		new XOR32ShiftRandom.ThreadSafe();
	}

	@Test
	public void createThreadLocal() {
		new XOR32ShiftRandom.ThreadLocal().get();
	}

	@Override
	@DataProvider(name = "seededPRNGPair")
	protected Object[][] getSeededPRNGPair() {
		final byte[] seed = XOR32ShiftRandom.seedBytes();

		return new Object[][] {
			{new XOR32ShiftRandom(seed), new XOR32ShiftRandom(seed)},
			{new XOR32ShiftRandom.ThreadSafe(seed), new XOR32ShiftRandom(seed)},
			{new XOR32ShiftRandom.ThreadSafe(seed), new XOR32ShiftRandom.ThreadSafe(seed)}
		};
	}

	@Override
	@DataProvider(name = "PRNG")
	protected Object[][] getPRNG() {
		final byte[] seed = XOR32ShiftRandom.seedBytes();

		return new Object[][] {
			{new XOR32ShiftRandom(seed)},
			{new XOR32ShiftRandom.ThreadSafe(seed)},
			{new XOR32ShiftRandom.ThreadLocal().get()}
		};
	}

	@Test
	public void paramSelectorShiftParam() {
		final List<Shift> shifts = singletonList(Shift.DEFAULT);
		final List<Param> params = singletonList(Param.DEFAULT);

		final ParamSelector selector = new ParamSelector(shifts, params);
		final byte[] seed = selector.seed();

		Assert.assertSame(selector.shift(), shifts.get(0));
		Assert.assertSame(selector.param(), params.get(0));

		for (int i = 0; i < 100; ++i) {
			selector.next();
			Assert.assertFalse(Arrays.equals(seed, selector.seed()));
			Assert.assertSame(selector.shift(), shifts.get(0));
			Assert.assertSame(selector.param(), params.get(0));
		}
	}

	@Test
	public void paramSelectorShift() {
		final List<Shift> shifts = singletonList(Shift.DEFAULT);
		final List<Param> params = Param.PARAMS;

		final ParamSelector selector = new ParamSelector(shifts, params);
		final byte[] seed = selector.seed();

		for (Param param : params) {
			Assert.assertTrue(Arrays.equals(seed, selector.seed()));
			Assert.assertSame(selector.shift(), shifts.get(0));
			Assert.assertSame(selector.param(), param);

			selector.next();
		}

		for (Param param : params) {
			Assert.assertFalse(Arrays.equals(seed, selector.seed()));
			Assert.assertSame(selector.shift(), shifts.get(0));
			Assert.assertSame(selector.param(), param);

			selector.next();
		}
	}

	@Test
	public void paramSelector() {
		final List<Shift> shifts = asList(Shift.values());
		final List<Param> params = Param.PARAMS;

		final ParamSelector selector = new ParamSelector(shifts, params);
		final byte[] seed = selector.seed();

		for (Shift shift : shifts) {
			for (Param param : params) {
				Assert.assertTrue(Arrays.equals(seed, selector.seed()));
				Assert.assertSame(selector.shift(), shift);
				Assert.assertSame(selector.param(), param);

				selector.next();
			}
		}

		for (Shift shift : shifts) {
			for (Param param : params) {
				Assert.assertFalse(Arrays.equals(seed, selector.seed()));
				Assert.assertSame(selector.shift(), shift);
				Assert.assertSame(selector.param(), param);

				selector.next();
			}
		}
	}

}

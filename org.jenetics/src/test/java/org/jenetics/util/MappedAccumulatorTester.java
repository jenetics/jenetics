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

import java.util.Random;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date$</em>
 */
public abstract class MappedAccumulatorTester<A extends MappedAccumulator<Double>>
	extends ObjectTester<A>
{

	@Test
	public void accumulatedSamples() {
		final int SAMPLES = 12345;
		final Random random = new Random(123456);
		final MappedAccumulator<Double> accu = getFactory().newInstance();

		final long samples = accu.getSamples();

		for (int i = 0; i < SAMPLES; ++i) {
			accu.accumulate(random.nextDouble()*6);
		}

		Assert.assertEquals(accu._samples, SAMPLES + samples);
		Assert.assertEquals(accu.getSamples(), SAMPLES + samples);
	}

	@Test
	public void testClone() {
		MappedAccumulator<Double> accu1 = getFactory().newInstance();
		for (int i = 0; i < 1000; ++i) {
			accu1.accumulate(Double.valueOf(i));
		}

		Accumulator<Double> accu2 = accu1.clone();

		Assert.assertNotSame(accu1, accu2);
		Assert.assertEquals(accu1.hashCode(), accu2.hashCode());
		Assert.assertEquals(accu1, accu2);

		accu1.accumulate(4.5);
		Assert.assertFalse(accu1.equals(accu2));
		Assert.assertFalse(accu1.hashCode() == accu2.hashCode());

		accu2.accumulate(4.5);
		Assert.assertEquals(accu1.hashCode(), accu2.hashCode());
		Assert.assertEquals(accu1, accu2);

		accu1 = getFactory().newInstance();
		accu2 = accu1.clone();

		Assert.assertNotSame(accu1, accu2);
		Assert.assertEquals(accu1.hashCode(), accu2.hashCode());
		Assert.assertEquals(accu1, accu2);
	}

}

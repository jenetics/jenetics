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

import java.util.Random;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public abstract class Random32TestBase extends RandomTestBase {

//	@Test(dataProvider = "seededPRNGPair")
//	public void sameByteIntValueSequence(final Random rand1, final Random rand2) {
//		final byte[] bytes = new byte[4];
//		for (int i = 0; i < 1234; ++i) {
//			rand1.nextBytes(bytes);
//			bit.reverse(bytes);
//
//			Assert.assertEquals(bit.toInt(bytes), rand2.nextInt());
//		}
//	}

}

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

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date: 2014-01-27 $</em>
 */
public class RandomUtils {

	public RandomUtils() {
	}

	public String nextString(final int length) {
		final Random random = RandomRegistry.getRandom();

		final StringBuilder chars = new StringBuilder(length);
		for (int i = 0; i < length; ++i) {
			chars.append((char)random.nextInt(Short.MAX_VALUE));
		}

		return chars.toString();
	}

	public short nextShort() {
		return (short)RandomRegistry.getRandom().nextInt(Short.MAX_VALUE);
	}

	public static final Factory<Short> SFact = lambda.factory(
		new RandomUtils(), "nextShort"
	);

	public static final Factory<Integer> IFact = lambda.factory(
		RandomRegistry.getRandom(), "nextInt"
	);

	public static final Factory<Long> LFact = lambda.factory(
		RandomRegistry.getRandom(), "nextLong"
	);

	public static final Factory<Float> FFact = lambda.factory(
		RandomRegistry.getRandom(), "nextFloat"
	);

	public static final Factory<Double> DFact = lambda.factory(
		RandomRegistry.getRandom(), "nextDouble"
	);

}

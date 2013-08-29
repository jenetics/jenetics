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
package org.jenetics;

import java.util.Random;

import org.jenetics.util.Factory;
import org.jenetics.util.ObjectTester;
import org.jenetics.util.RandomRegistry;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date: 2013-04-27 $</em>
 */
public class ExponentialScalerTest extends ObjectTester<ExponentialScaler> {

	final Factory<ExponentialScaler> _factory = new Factory<ExponentialScaler>() {
		@Override
		public ExponentialScaler newInstance() {
			final Random random = RandomRegistry.getRandom();
			final double a = random.nextInt(100) + 10;
			final double b = random.nextInt(100) + 10;
			final double c = random.nextInt(100) + 10;

			return new ExponentialScaler(a, b, c);
		}
	};
	@Override
	protected Factory<ExponentialScaler> getFactory() {
		return _factory;
	}

}

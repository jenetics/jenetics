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

import org.jenetics.Statistics.Time;
import org.jenetics.util.Duration;
import org.jenetics.util.Factory;
import org.jenetics.util.ObjectTester;
import org.jenetics.util.RandomRegistry;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date: 2014-03-12 $</em>
 */
public class StatisticsTimeTest extends ObjectTester<Time> {

	final Factory<Time> _factory = new Factory<Time>() {
		@Override
		public Time newInstance() {
			final Random random = RandomRegistry.getRandom();

			final Time time = new Time();
			time.alter.set(Duration.ofSeconds(random.nextDouble() * 1000));
			time.combine.set(Duration.ofSeconds(random.nextDouble()*1000));
			time.evaluation.set(Duration.ofSeconds(random.nextDouble()*1000));
			time.execution.set(Duration.ofSeconds(random.nextDouble()*1000));
			time.selection.set(Duration.ofSeconds(random.nextDouble()*1000));
			time.statistics.set(Duration.ofSeconds(random.nextDouble()*1000));

			return time;
		}
	};
	@Override
	protected Factory<Time> getFactory() {
		return _factory;
	}

}

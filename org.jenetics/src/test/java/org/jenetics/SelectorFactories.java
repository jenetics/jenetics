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
import org.jenetics.util.RandomRegistry;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date: 2014-02-15 $</em>
 */
public class SelectorFactories {

	private SelectorFactories() {
	}

	public static Factory<BoltzmannSelector<DoubleGene, Double>>
	BoltzmannSelector = new Factory<BoltzmannSelector<DoubleGene, Double>>() {
		@Override
		public BoltzmannSelector<DoubleGene, Double> newInstance() {
			final Random random = RandomRegistry.getRandom();
			return new BoltzmannSelector<>(random.nextDouble());
		}
	};

	public static Factory<ExponentialRankSelector<DoubleGene, Double>>
	ExponentialRankSelector = new Factory<ExponentialRankSelector<DoubleGene, Double>>() {
		@Override
		public ExponentialRankSelector<DoubleGene, Double> newInstance() {
			final Random random = RandomRegistry.getRandom();
			return new ExponentialRankSelector<>(random.nextDouble());
		}
	};

	public static Factory<LinearRankSelector<DoubleGene, Double>>
	LinearRankSelector = new Factory<LinearRankSelector<DoubleGene, Double>>() {
		@Override
		public LinearRankSelector<DoubleGene, Double> newInstance() {
			final Random random = RandomRegistry.getRandom();
			return new LinearRankSelector<>(random.nextDouble());
		}
	};

	public static Factory<RouletteWheelSelector<DoubleGene, Double>>
	RouletteWheelSelector = new Factory<RouletteWheelSelector<DoubleGene, Double>>() {
		@Override
		public RouletteWheelSelector<DoubleGene, Double> newInstance() {
			return new RouletteWheelSelector<>();
		}
	};

}

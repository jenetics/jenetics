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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * Author:
 *     Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 *
 */
package org.jenetics;

import java.util.Random;

import org.jscience.mathematics.number.Float64;

import org.jenetics.util.Factory;
import org.jenetics.util.RandomRegistry;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public class SelectorFactories {

	private SelectorFactories() {
	}

	public static Factory<BoltzmannSelector<Float64Gene, Float64>>
	BoltzmannSelector = new Factory<BoltzmannSelector<Float64Gene, Float64>>() {
		@Override
		public BoltzmannSelector<Float64Gene, Float64> newInstance() {
			final Random random = RandomRegistry.getRandom();
			return new BoltzmannSelector<>(random.nextDouble());
		}
	};

	public static Factory<ExponentialRankSelector<Float64Gene, Float64>>
	ExponentialRankSelector = new Factory<ExponentialRankSelector<Float64Gene, Float64>>() {
		@Override
		public ExponentialRankSelector<Float64Gene, Float64> newInstance() {
			final Random random = RandomRegistry.getRandom();
			return new ExponentialRankSelector<>(random.nextDouble());
		}
	};

	public static Factory<LinearRankSelector<Float64Gene, Float64>>
	LinearRankSelector = new Factory<LinearRankSelector<Float64Gene, Float64>>() {
		@Override
		public LinearRankSelector<Float64Gene, Float64> newInstance() {
			final Random random = RandomRegistry.getRandom();
			return new LinearRankSelector<>(random.nextDouble());
		}
	};

	public static Factory<RouletteWheelSelector<Float64Gene, Float64>>
	RouletteWheelSelector = new Factory<RouletteWheelSelector<Float64Gene, Float64>>() {
		@Override
		public RouletteWheelSelector<Float64Gene, Float64> newInstance() {
			return new RouletteWheelSelector<>();
		}
	};

}






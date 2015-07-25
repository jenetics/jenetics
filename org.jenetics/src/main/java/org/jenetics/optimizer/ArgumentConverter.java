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
package org.jenetics.optimizer;

import java.util.function.DoubleFunction;
import java.util.function.Function;

import org.jenetics.DoubleGene;
import org.jenetics.Gene;
import org.jenetics.MultiPointCrossover;
import org.jenetics.RouletteWheelSelector;
import org.jenetics.Selector;
import org.jenetics.util.DoubleRange;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public abstract class ArgumentConverter {

	public abstract Object arg(final int index, final double value);

	@SafeVarargs
	final <G extends Gene<?, G>, C extends Comparable<? super C>>
	Function<double[], Selector<G, C>>
	selector(final Class<? extends Selector> type, DoubleFunction<Object>... args) {

		return null;
	}


	public void foo() {
		final Function<double[], Selector<DoubleGene, Double>> s = selector(
			RouletteWheelSelector.class,
				d -> "",
				d -> ""
			);
	}

}

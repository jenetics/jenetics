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

import java.util.Optional;
import java.util.function.Function;

import org.jenetics.Alterer;
import org.jenetics.DoubleChromosome;
import org.jenetics.DoubleGene;
import org.jenetics.GaussianMutator;
import org.jenetics.Genotype;
import org.jenetics.NumericGene;
import org.jenetics.engine.Codec;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class GaussianMutatorProxy<
	G extends NumericGene<?, G>,
	C extends Comparable<? super C>
>
	implements Proxy<Alterer<G, C>>
{

	private final double _probability;

	public GaussianMutatorProxy(final double probability) {
		_probability = probability;
	}

	@Override
	public Function<double[], Optional<Alterer<G, C>>> factory() {
		return args -> args[0] < _probability
			? Optional.of(new GaussianMutator<>(args[1]))
			: Optional.empty();
	}

	@Override
	public int argsLength() {
		return 2;
	}


	public static <G extends NumericGene<?, G>, C extends Comparable<? super C>>
	Codec<GaussianMutator<G, C>, DoubleGene> of() {
		return Codec.of(
			Genotype.of(DoubleChromosome.of(1, 0)),
			gt -> new GaussianMutator<G, C>(gt.getGene().getAllele())
		);
	}

}

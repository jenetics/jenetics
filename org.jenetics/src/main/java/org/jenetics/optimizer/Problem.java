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

import java.util.function.Function;

import org.jenetics.Gene;
import org.jenetics.Genotype;
import org.jenetics.engine.Codec;
import org.jenetics.util.Factory;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public interface Problem<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
{

	public Factory<Genotype<G>> genotype();

	public Function<Genotype<G>, C> function();


	/**
	 *
	 * @param genotype
	 * @param function
	 * @param <G>
	 * @param <C>
	 * @return
	 */
	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	Problem<G, C> of(
		final Factory<Genotype<G>> genotype,
		final Function<Genotype<G>, C> function
	) {
		return new Problem<G, C>() {
			@Override
			public Factory<Genotype<G>> genotype() {
				return genotype;
			}

			@Override
			public Function<Genotype<G>, C> function() {
				return function;
			}
		};
	}

	public static <G extends Gene<?, G>, C extends Comparable<? super C>, S>
	Problem<G, C> of(
		final Factory<Genotype<G>> genotype,
		final Function<S, C> function,
		final Function<Genotype<G>, S> decoder
	) {
		return of(
			genotype,
			function.compose(decoder)
		);
	}

	public static <G extends Gene<?, G>, C extends Comparable<? super C>, S>
	Problem<G, C> of(
		final Function<S, C> function,
		final Codec<S, G> codec
	) {
		return of(
			codec.encoding(),
			function.compose(codec.decoder())
		);
	}

}

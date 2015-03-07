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
package org.jenetics.engine;

import static java.util.Objects.requireNonNull;

import java.util.function.Function;

import org.jenetics.Gene;
import org.jenetics.Genotype;
import org.jenetics.Optimize;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class Optimizer<ARG_TYPE> {

	private final class Inner<G extends Gene<?, G>> {
		private final Codec<G, ARG_TYPE> _codec;

		Inner(final Codec<G, ARG_TYPE> codec) {
			_codec = requireNonNull(codec);
		}

		<R extends Comparable<? super R>> ARG_TYPE
		argmin(final Function<ARG_TYPE, R> function) {
			final Engine<G, R> engine = Engine
				.builder(function.compose(_codec.decoder()), _codec.encoding())
				.optimize(Optimize.MINIMUM)
				.build();

			final Genotype<G> bgt = engine.stream()
				.limit(limit.bySteadyFitness(30))
				.collect(EvolutionResult.toBestGenotype());

			return _codec.decoder().apply(bgt);
		}
	}

	private Inner<?> _inner;

	private Optimizer() {
	}

	public <R extends Comparable<? super R>> ARG_TYPE
	argmin(final Function<ARG_TYPE, R> function) {
		return _inner.argmin(function);
	}


	public static <G extends Gene<?, G>, S> Optimizer<S>
	of(final Codec<G, S> codec) {
		final Optimizer<S> optimizer = new Optimizer<>();
		optimizer._inner = optimizer.new Inner<>(codec);

		return optimizer;
	}

	public static Optimizer<Double> ofDouble(
		final double min,
		final double max
	) {
		return of(Codec.ofDouble(min, max));
	}

	public static void main(final String[] args) {
		final double result = Optimizer.ofDouble(0, 100).argmin(i -> i);

		System.out.println(result);
	}
}

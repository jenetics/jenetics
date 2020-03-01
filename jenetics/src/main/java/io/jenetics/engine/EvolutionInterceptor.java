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
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmail.com)
 */
package io.jenetics.engine;

import java.util.function.Function;

import io.jenetics.Gene;

/**
 *
 * @param <G>
 * @param <C>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since !__version__!
 * @version !__version__!
 */
public interface EvolutionInterceptor<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
> {

	EvolutionStart<G, C> before(final EvolutionStart<G, C> start);

	EvolutionResult<G, C> after(final EvolutionResult<G, C> result);


	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	EvolutionInterceptor<G, C> of(
		final Function<? super EvolutionStart<G, C>, EvolutionStart<G, C>> before,
		final Function<? super EvolutionResult<G, C>, EvolutionResult<G, C>> after
	) {
		return new EvolutionInterceptor<G, C>() {
			@Override
			public EvolutionStart<G, C> before(final EvolutionStart<G, C> start) {
				return before.apply(start);
			}

			@Override
			public EvolutionResult<G, C> after(final EvolutionResult<G, C> result) {
				return after.apply(result);
			}
		};
	}

	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	EvolutionInterceptor<G, C>
	ofBefore(final Function<? super EvolutionStart<G, C>, EvolutionStart<G, C>> before) {
		return of(before, Function.identity());
	}

	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	EvolutionInterceptor<G, C>
	ofAfter(final Function<? super EvolutionResult<G, C>, EvolutionResult<G, C>> after) {
		return of(Function.identity(), after);
	}


	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	EvolutionInterceptor<G, C> identity() {
		return of(Function.identity(), Function.identity());
	}
}

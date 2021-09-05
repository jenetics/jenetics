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

import static java.util.Objects.requireNonNull;

import java.util.function.Function;

import io.jenetics.Gene;

/**
 * The evolution interceptor allows to update the {@link EvolutionStart} object,
 * <em>before</em> the evolution start, and update the {@link EvolutionResult}
 * object <em>after</em> the evolution.
 *
 * @see EvolutionResult#toUniquePopulation()
 * @see Engine.Builder#interceptor(EvolutionInterceptor)
 *
 * @param <G> the gene type
 * @param <C> the fitness result type
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 6.0
 * @version 6.0
 */
public interface EvolutionInterceptor<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
> {

	/**
	 * This method is called right before the evaluation of a generation is
	 * started.
	 *
	 * @param start the evolution start object
	 * @return the possible <em>update</em> evolution start object
	 * @throws NullPointerException if the evolution {@code start} object is
	 *         {@code null}
	 */
	default EvolutionStart<G, C> before(final EvolutionStart<G, C> start) {
		return start;
	}

	/**
	 * This method is called after the evaluation of a generation. If this
	 * method alters the evolution result object, the population within this
	 * result object is re-evaluated.
	 *
	 * @param result the evolution result object to update
	 * @return the possible <em>updated</em> evolution result object
	 * @throws NullPointerException if the evolution {@code result} object is
	 *         {@code null}
	 */
	default EvolutionResult<G, C> after(final EvolutionResult<G, C> result) {
		return result;
	}

	/**
	 * Composes {@code this} interceptor with the {@code other} one. The
	 * {@link #before(EvolutionStart)} of {@code this} interceptor is called
	 * <em>after</em> the {@code before} method of the {@code other} interceptor.
	 * And the {@link #after(EvolutionResult)} of {@code this} interceptor is
	 * called <em>before</em> the {@code after} method of the {@code other}
	 * interceptor.
	 *
	 * @param other the other, composing interceptor
	 * @return a new, composed interceptor
	 * @throws NullPointerException if the {@code other} interceptor is
	 *         {@code null}
	 */
	default EvolutionInterceptor<G, C>
	compose(final EvolutionInterceptor<G, C> other) {
		requireNonNull(other);
		return EvolutionInterceptor.of(
			start -> before(other.before(start)),
			result -> other.after(after(result))
		);
	}

	/**
	 * Create a new interceptor instance with the given {@code before} and
	 * {@code after} functions.
	 *
	 * @param before the function executed before each evolution step
	 * @param after the function executed after each evolution step
	 * @param <G> the gene type
	 * @param <C> the fitness result type
	 * @return a new interceptor instance with the given interceptor functions
	 * @throws NullPointerException if one of the functions is {@code null}
	 */
	static <G extends Gene<?, G>, C extends Comparable<? super C>>
	EvolutionInterceptor<G, C> of(
		final Function<? super EvolutionStart<G, C>, EvolutionStart<G, C>> before,
		final Function<? super EvolutionResult<G, C>, EvolutionResult<G, C>> after
	) {
		requireNonNull(before);
		requireNonNull(after);

		return new EvolutionInterceptor<>() {
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

	/**
	 * Create a new interceptor instance with the given {@code before} function.
	 *
	 * @param before the function executed before each evolution step
	 * @param <G> the gene type
	 * @param <C> the fitness result type
	 * @return a new interceptor instance with the given interceptor function
	 * @throws NullPointerException if the function is {@code null}
	 */
	static <G extends Gene<?, G>, C extends Comparable<? super C>>
	EvolutionInterceptor<G, C>
	ofBefore(final Function<? super EvolutionStart<G, C>, EvolutionStart<G, C>> before) {
		return EvolutionInterceptor.of(before, Function.identity());
	}

	/**
	 * Create a new interceptor instance with the given {@code after} function.
	 *
	 * @param after the function executed after each evolution step
	 * @param <G> the gene type
	 * @param <C> the fitness result type
	 * @return a new interceptor instance with the given interceptor function
	 * @throws NullPointerException if the function is {@code null}
	 */
	static <G extends Gene<?, G>, C extends Comparable<? super C>>
	EvolutionInterceptor<G, C>
	ofAfter(final Function<? super EvolutionResult<G, C>, EvolutionResult<G, C>> after) {
		return EvolutionInterceptor.of(Function.identity(), after);
	}

	/**
	 * Return an interceptor object which does nothing.
	 *
	 * @param <G> the gene type
	 * @param <C> the fitness result type
	 * @return a new <em>identity</em> interceptor
	 */
	static <G extends Gene<?, G>, C extends Comparable<? super C>>
	EvolutionInterceptor<G, C> identity() {
		return EvolutionInterceptor.of(Function.identity(), Function.identity());
	}

}

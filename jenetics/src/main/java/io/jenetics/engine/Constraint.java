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

import io.jenetics.Gene;
import io.jenetics.Phenotype;

/**
 * This interface allows you to define constraints on single phenotypes. In some
 * sense it is a more advanced version of the {@link Phenotype#isValid()}
 * method, which checks the validity of the underlying genotypes and/or
 * chromosomes. Additionally it is possible to <em>repair</em> invalid
 * individuals.
 *
 * @see Engine.Builder#constraint(Constraint)
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public interface Constraint<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
> {

	/**
	 * Checks the validity of the given {@code individual}.
	 *
	 * @param individual the phenotype to check
	 * @return {@code true} if the given {@code individual} is valid,
	 *         {@code false} otherwise
	 * @throws NullPointerException if the given {@code individual} is
	 *         {@code null}
	 */
	public boolean test(final Phenotype<G, C> individual);

	/**
	 * Tries to repair the given phenotype. This method is called by the
	 * evolution {@link Engine} if the {@link #test(Phenotype)} method returned
	 * {@code false}.
	 *
	 * @param individual the phenotype to repair
	 * @return a newly created, valid phenotype. The implementation is free to
	 *         use the given invalid {@code individual} as a starting point for
	 *         the created phenotype.
	 * @throws NullPointerException if the given {@code individual} is
	 *         {@code null}
	 */
	public Phenotype<G, C> repair(final Phenotype<G, C> individual);

}

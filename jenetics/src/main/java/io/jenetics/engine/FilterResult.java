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
package io.jenetics.engine;

import static java.util.Objects.requireNonNull;

import io.jenetics.Gene;
import io.jenetics.Phenotype;
import io.jenetics.util.ISeq;

/**
 * Represent the result of the validation/filtering step.
 *
 * @param <G> the gene type
 * @param <C> the fitness type
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 3.0
 * @version 4.0
 */
final class FilterResult<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
> {

	final ISeq<Phenotype<G, C>> population;
	final int killCount;
	final int invalidCount;

	FilterResult(
		final ISeq<Phenotype<G, C>> population,
		final int killCount,
		final int invalidCount
	) {
		this.population = requireNonNull(population);
		this.killCount = killCount;
		this.invalidCount = invalidCount;
	}

}

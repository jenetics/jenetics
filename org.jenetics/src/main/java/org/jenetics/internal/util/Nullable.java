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
package org.jenetics.internal.util;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import org.jenetics.DoubleGene;
import org.jenetics.Phenotype;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since !__version__!
 * @version !__version__! &mdash; <em>$Date: 2014-06-25 $</em>
 */
public final class Nullable<A> {

	private final Optional<A> _value;

	private Nullable(final Optional<A> value) {
		_value = Objects.requireNonNull(value);
	}

	public <B> Nullable<B> map(final Function<A, B> mapper) {
		return flatMap(v -> Optional.ofNullable(mapper.apply(v)));
	}

	public <B> Nullable<B> flatMap(final Function<A, Optional<B>> mapper) {
		return new Nullable<>(_value.flatMap(mapper));
	}

	public <B> Optional<B> yield(final Function<A, B> mapper) {
		return _value.flatMap(v -> Optional.ofNullable(mapper.apply(v)));
	}

	public Optional<A> yield() {
		return _value;
	}

	public A value() {
		return _value.orElse(null);
	}

	public static <A> Nullable<A> of(final A value) {
		return new Nullable<>(Optional.ofNullable(value));
	}

	public static void main(final String[] args) {
		Phenotype<DoubleGene, Double> value = null;
		final Nullable<Phenotype<DoubleGene, Double>> start = Nullable.of(value);

		final Optional<DoubleGene> gene = Nullable.of(value)
			.map(p -> p.getGenotype())
			.map(gt -> gt.getChromosome())
			.map(c -> c.getGene())
			.yield();

		Optional.ofNullable(value)
			.flatMap(p -> Optional.ofNullable(p.getGenotype()))
			.flatMap(gt -> Optional.ofNullable(gt.getChromosome()))
			.flatMap(c -> Optional.ofNullable(c.getGene()));

		/*
		for {
			gt <- Optional.ofNullable(pt.getGenotype())
			c <- Optional.ofNullable(gt.getChromosome())
			g <- Optional.ofNullable(c.getGene())
		} yield g.getAllele()
		*/
	}

}

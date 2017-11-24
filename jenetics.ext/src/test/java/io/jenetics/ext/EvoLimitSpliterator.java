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
package io.jenetics.ext;

import static java.util.Objects.requireNonNull;

import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;

import io.jenetics.IntegerGene;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.internal.util.LimitSpliterator;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class EvoLimitSpliterator
	implements LimitSpliterator<EvolutionResult<IntegerGene, Integer>>
{

	private final Spliterator<EvolutionResult<IntegerGene, Integer>> _spliterator;

	public EvoLimitSpliterator(
		final Spliterator<EvolutionResult<IntegerGene, Integer>> spliterator
	) {
		_spliterator = requireNonNull(spliterator);
	}

	@Override
	public LimitSpliterator<EvolutionResult<IntegerGene, Integer>>
	limit(final Predicate<? super EvolutionResult<IntegerGene, Integer>> proceed) {
		return this;
	}

	@Override
	public boolean
	tryAdvance(final Consumer<? super EvolutionResult<IntegerGene, Integer>> action) {
		return _spliterator.tryAdvance(action);
	}

	@Override
	public Spliterator<EvolutionResult<IntegerGene, Integer>> trySplit() {
		return _spliterator.trySplit();
	}

	@Override
	public long estimateSize() {
		return _spliterator.estimateSize();
	}

	@Override
	public int characteristics() {
		return _spliterator.characteristics();
	}
}

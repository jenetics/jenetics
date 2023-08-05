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
package io.jenetics.internal.concurrent;

import static java.util.Objects.requireNonNull;

import java.util.concurrent.Executor;
import java.util.function.Function;

import io.jenetics.Gene;
import io.jenetics.Genotype;
import io.jenetics.util.Seq;

/**
 * Default phenotype evaluation strategy. It uses the configured {@link Executor}
 * for the fitness evaluation.
 *
 * @param <G> the gene type
 * @param <C> the fitness result type
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since 4.2
 */
public final class ExecutorEvaluator<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
	extends AbstractEvaluator<G, C>
{

	private final Executor _executor;

	public ExecutorEvaluator(
		final Function<? super Genotype<G>, ? extends C> function,
		final Executor executor
	) {
		super(function);
		_executor = requireNonNull(executor);
	}

	public ExecutorEvaluator<G, C> with(final Executor executor) {
		return new ExecutorEvaluator<>(_function, executor);
	}

	@Override
	protected void execute(final Seq<? extends Runnable> tasks) {
		try (var c = BatchExecutor.with(_executor)) {
			c.execute(tasks);
		}
	}

}

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

import java.util.function.Predicate;
import java.util.stream.Stream;

import io.jenetics.Gene;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.engine.EvolutionStream;
import io.jenetics.internal.util.StreamProxy;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class EvolutionStreamAdapter<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
	extends StreamProxy<EvolutionResult<G, C>>
	implements EvolutionStream<G, C>
{

	public EvolutionStreamAdapter(final Stream<EvolutionResult<G, C>> self) {
		super(self);
	}

	@Override
	public EvolutionStream<G, C>
	limit(final Predicate<? super EvolutionResult<G, C>> proceed) {
		throw new UnsupportedOperationException();
	}
}

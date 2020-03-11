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

import java.util.concurrent.SubmissionPublisher;
import java.util.concurrent.atomic.AtomicBoolean;

import io.jenetics.Gene;

/**
 *
 *
 * @param <G> the gene type
 * @param <C> the fitness result type
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class EvolutionPublisher<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
	extends SubmissionPublisher<EvolutionResult<G, C>>
{

	private final AtomicBoolean _proceed = new AtomicBoolean(true);

	private EvolutionStream<G, C> _stream;

	public EvolutionPublisher() {
	}

	public EvolutionPublisher(final EvolutionStream<G, C> stream) {
		attach(stream);
	}

	public void attach(final EvolutionStream<G, C> stream) {
		_stream = stream.limit(er -> _proceed.get());
		_stream.forEach(this::submit);
	}

	@Override
	public void close() {
		_proceed.set(false);
		super.close();
	}

}

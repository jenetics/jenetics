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

import java.util.concurrent.CancellationException;
import java.util.concurrent.Executor;
import java.util.concurrent.SubmissionPublisher;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

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
	private Thread _thread;

	public EvolutionPublisher(
		final Executor executor,
		final int maxBufferCapacity,
		final EvolutionStream<G, C> stream
	) {
		super(executor, maxBufferCapacity);
		attach(stream);
	}

	public EvolutionPublisher(final EvolutionStream<G, C> stream) {
		attach(stream);
	}

	public EvolutionPublisher() {
	}

	public synchronized void attach(final EvolutionStream<G, C> stream) {
		requireNonNull(stream);
		if (_stream != null) {
			throw new IllegalStateException("Already attached evolution stream.");
		}

		_stream = stream.limit(er -> _proceed.get());
		//_thread = new Thread(new Evolve<>(_stream, this::submit));
		_thread = new Thread(() -> {
			try {
				_stream.forEach(this::submit);
			} catch(CancellationException e) {
				Thread.currentThread().interrupt();
			}
		});
		_thread.start();
	}

	@Override
	public void close() {
		_proceed.set(false);
		_thread.interrupt();
		super.close();
	}

	private static final class Evolve<
		G extends Gene<?, G>,
		C extends Comparable<? super C>
	>
		implements Runnable
	{
		private final EvolutionStream<G, C> _stream;
		private final Consumer<? super EvolutionResult<G, C>> _consumer;

		private Evolve(
			final EvolutionStream<G, C> stream,
			final Consumer<? super EvolutionResult<G, C>> consumer
		) {
			_stream = requireNonNull(stream);
			_consumer = requireNonNull(consumer);
		}

		@Override
		public void run() {
			try {
				_stream.forEach(_consumer);
			} catch(CancellationException e) {
				Thread.currentThread().interrupt();
			}
		}
	}

}

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
import java.util.stream.Stream;

/**
 *
 *
 * @param <T> the element type of the publisher
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class StreamPublisher<T> extends SubmissionPublisher<T> {

	private final Object _lock = new Object(){};

	private final AtomicBoolean _proceed = new AtomicBoolean(true);

	private Stream<? extends T> _stream;
	private Thread _thread;

	public StreamPublisher(
		final Executor executor,
		final int maxBufferCapacity,
		final Stream<? extends T> stream
	) {
		super(executor, maxBufferCapacity);
		attach(stream);
	}

	public StreamPublisher(final Stream<? extends T> stream) {
		attach(stream);
	}

	public StreamPublisher() {
	}

	public synchronized void attach(final Stream<? extends T> stream) {
		requireNonNull(stream);

		synchronized (_lock) {
			if (_stream != null) {
				throw new IllegalStateException(
					"Already attached evolution stream."
				);
			}

			_stream = stream.takeWhile(e -> _proceed.get());
			_thread = new Thread(() -> {
				try {
					_stream.forEach(this::submit);
				} catch(CancellationException e) {
					Thread.currentThread().interrupt();
				} finally {
					_thread = null;
					close();
				}
			});
			_thread.start();
		}
	}

	@Override
	public void close() {
		synchronized (_lock) {
			_proceed.set(false);
			if (_thread != null) {
				_thread.interrupt();
			}
		}
		super.close();
	}

}

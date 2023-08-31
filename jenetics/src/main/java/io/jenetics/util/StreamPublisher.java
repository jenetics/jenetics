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
package io.jenetics.util;

import static java.util.Objects.requireNonNull;

import java.util.concurrent.CancellationException;
import java.util.concurrent.Executor;
import java.util.concurrent.Flow;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.SubmissionPublisher;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

import io.jenetics.internal.util.Lifecycle.ExtendedCloseable;

/**
 * This class allows creating a reactive {@link Flow.Publisher} from a given
 * Java {@link Stream}.
 *
 * {@snippet lang="java":
 * final Stream<Long> stream = engine.stream()
 *     .limit(33)
 *     .map(EvolutionResult::generation);
 *
 * try (var publisher = new StreamPublisher<Long>()) {
 *     publisher.subscribe(new Subscriber<>() {
 *         private Subscription subscription;
 *         \@Override
 *         public void onSubscribe(final Subscription subscription) {
 *             (this.subscription = subscription).request(1);
 *         }
 *         \@Override
 *         public void onNext(final Long g) {
 *             System.out.println("Got new generation: " + g);
 *             subscription.request(1);
 *         }
 *         \@Override
 *         public void onError(final Throwable throwable) {
 *         }
 *         \@Override
 *         public void onComplete() {
 *             System.out.println("Evolution completed.");
 *         }
 *     });
 *
 *     // Attaching the stream, starts the element publishing.
 *     publisher.attach(stream);
 *
 *     ...
 * }
 * }
 *
 * @param <T> the element type of the publisher
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 6.0
 * @since 6.0
 */
public class StreamPublisher<T> extends SubmissionPublisher<T> {

	private final Object _lock = new Object(){};

	private final AtomicBoolean _proceed = new AtomicBoolean(true);

	private Stream<? extends T> _stream;
	private Thread _thread;

	/**
	 * Creates a new {@code StreamPublisher} using the given {@code Executor}
	 * for async delivery to subscribers, with the given maximum buffer size for
	 * each subscriber.
	 *
	 * @param executor the executor to use for async delivery, supporting
	 *        creation of at least one independent thread
	 * @param maxBufferCapacity the maximum capacity for each subscriber's buffer
	 * @param handler if non-null, procedure to invoke upon exception thrown in
	 *        method {@code onNext}
	 * @throws NullPointerException if one of the arguments is {@code null}
	 * @throws IllegalArgumentException if maxBufferCapacity not positive
	 */
	public StreamPublisher(
		final Executor executor,
		final int maxBufferCapacity,
		final BiConsumer<? super Subscriber<? super T>, ? super Throwable> handler
	) {
		super(executor, maxBufferCapacity, handler);
	}

	/**
	 * Creates a new {@code StreamPublisher} using the given {@code Executor}
	 * for async delivery to subscribers, with the given maximum buffer size for
	 * each subscriber, and no handler for Subscriber exceptions in method
	 * {@link java.util.concurrent.Flow.Subscriber#onNext(Object)}.
	 *
	 * @param executor the executor to use for async delivery, supporting
	 *        creation of at least one independent thread
	 * @param maxBufferCapacity the maximum capacity for each subscriber's buffer
	 * @throws NullPointerException if the given {@code executor} is {@code null}
	 * @throws IllegalArgumentException if maxBufferCapacity not positive
	 */
	public StreamPublisher(final Executor executor, final int maxBufferCapacity) {
		super(executor, maxBufferCapacity);
	}

	/**
	 * Creates a new publisher using the {@code ForkJoinPool.commonPool()} for
	 * async delivery to subscribers (unless it does not support a parallelism
	 * level of at least two, in which case, a new Thread is created to run each
	 * task), with maximum buffer capacity of {@code Flow.defaultBufferSize()},
	 * and no handler for Subscriber exceptions in method onNext.
	 */
	public StreamPublisher() {
	}

	/**
	 * Attaches the given stream to the publisher. This method automatically
	 * starts the publishing of the elements read from the stream. The attached
	 * {@code stream} is closed, when {@code this} publisher is closed.
	 *
	 * @param stream the {@code stream} to attach
	 * @throws NullPointerException if the given {@code stream} is {@code null}
	 * @throws IllegalStateException if a stream is already attached to this
	 *         publisher
	 */
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
					close();
				} catch(CancellationException e) {
					Thread.currentThread().interrupt();
					close();
				} catch (Throwable e) {
					closeExceptionally(e);
				}
			});
			_thread.start();
		}
	}

	/**
	 * Unless already closed, issues {@code onComplete} signals to current
	 * subscribers, and disallows subsequent attempts to publish. Upon return,
	 * this method does NOT guarantee that all subscribers have already completed.
	 */
	@Override
	public void close() {
		synchronized (_lock) {
			final var closeable = ExtendedCloseable.of(
				() -> { if (_thread != null) _thread.interrupt(); },
				() -> { if (_stream != null) _stream.close(); }
			);

			_proceed.set(false);
			closeable.silentClose();
		}
		super.close();
	}

}

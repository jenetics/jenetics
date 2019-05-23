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
package io.jenetics.ext.internal;

import java.util.List;
import java.util.Spliterator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class UpdatableSpliteratorTest {

	private static final int MAX_ELEMENTS = 10;

	private final Lock _lock = new ReentrantLock();
	private final Condition _change = _lock.newCondition();
	private final Condition _consume = _lock.newCondition();

	private int _counter = 0;

	@Test
	public void foo() throws InterruptedException {
		final UpdatableSpliterator<String> spliterator = new UpdatableSpliterator<>(
			newSpliterator("0", 0),
			this::transform
		);

		final Runnable updater = () -> {
			try {
				int i = 0;
				while (!Thread.currentThread().isInterrupted()) {
					_lock.lock();
					try {
						while (_counter < MAX_ELEMENTS) {
							_change.await();
						}

						spliterator.update(newSpliterator("" + ++i, 0));
						_counter = 0;
						_consume.signal();
					} finally {
						_lock.unlock();
					}
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}

		};
		final Thread thread = new Thread(updater);
		thread.start();

		StreamSupport.stream(spliterator, false)
			//.parallel()
			.limit(150)
			.forEach(this::consume);

		thread.interrupt();
		thread.join();
	}

	private void consume(final String value) {
		_lock.lock();
		try {
			while (_counter >= MAX_ELEMENTS) {
				_consume.await();
			}
			_counter++;
			_change.signal();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		} finally {
			_lock.unlock();
		}

		System.out.println(value);
	}

	private String transform(final String value) {
		return value + ": updated";
	}

	private Spliterator<String> newSpliterator(final String prefix, final int b) {
		final AtomicInteger count = new AtomicInteger(b);
		final List<String> values = Stream
			.generate(() -> prefix + "_" + count.getAndIncrement())
			.limit(MAX_ELEMENTS+1)
			.collect(Collectors.toList());

		return values.spliterator();
	}

}

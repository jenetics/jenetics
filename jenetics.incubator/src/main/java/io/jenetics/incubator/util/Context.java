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
package io.jenetics.incubator.util;

import static java.util.Objects.requireNonNull;

import java.util.concurrent.atomic.AtomicReference;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class Context<T> {

	private static final class Entry<T> {
		T value;
		Entry(final T value) {
			this.value = value;
		}
	}

	public record Scope<T>(
		Context<T> context,
		Scope<T> parent,
		ScopedValue.Carrier carrier
	) {
		T get() {
			return carrier.get(context.key).value;
		}
	}

	private final ScopedValue<Entry<T>> key = ScopedValue.newInstance();

	private final AtomicReference<Entry<T>> dflt = new AtomicReference<>();

	public Context(final T value) {
		dflt.set(new Entry<>(value));
	}

	private Entry<T> current() {
		return key.orElse(dflt.get());
	}

	public T get() {
		return current().value;
	}

	public void set(final T value) {
		current().value = value;
	}

	public void run(T value, Runnable task) {
		ScopedValue.where(key, new Entry<>(value)).run(task);
	}

	public void run(Runnable task) {
		ScopedValue.where(key, new Entry<>(get())).run(task);
	}

	public Scope<T> scope() {
		final var carrier = ScopedValue.where(key, new Entry<>(get()));
		return new Scope<>(this, null, carrier);
	}

}

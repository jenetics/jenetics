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
package io.jenetics.incubator.restful;

import static java.util.Objects.requireNonNull;

import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicBoolean;

import io.jenetics.incubator.restful.client.Client;

/**
 * Wraps the default client into a caller which returns {@link Mono} objects
 * instead of {@link java.util.concurrent.Flow.Publisher} objects.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 8.2
 * @version 8.2
 */
public final class MonoCaller<T> implements Caller<T, Mono<Response.Success<T>>> {
	private final Client client;

	private MonoCaller(final Client client) {
		this.client = requireNonNull(client);
	}

	@Override
	public Mono<Response.Success<T>> call(final Resource<? extends T> resource) {
		return Mono.create(sink -> {
			final var result = client.call(resource);
			final var cancelled = new AtomicBoolean(false);

			sink.onCancel(() -> {
				result.cancel(true);
				cancelled.set(true);
			});

			result
				.whenComplete((value, error) -> {
					if (error != null) {
						if (!cancelled.get()) {
							sink.error(error);
						}
					} else if (value != null) {
						@SuppressWarnings("unchecked")
						final var success = (Response.Success<T>)value;
						sink.success(success);
					} else {
						sink.success();
					}
				});
			}
		);
	}

	public static <T> MonoCaller<T> of(final Client client) {
		return new MonoCaller<>(client);
	}

}

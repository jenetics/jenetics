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

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.http.HttpResponse;
import java.util.function.Function;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 8.2
 * @version 8.2
 */
final class ServerBodyHandler<T>
	implements HttpResponse.BodyHandler<ServerResponse<T>>
{
	private final Reader reader;
	private final Class<T> type;

	@SuppressWarnings("unchecked")
	ServerBodyHandler(final Reader reader, final Class<? extends T> type) {
		this.reader = requireNonNull(reader);
		this.type = (Class<T>)requireNonNull(type);
	}

	@Override
	public HttpResponse.BodySubscriber<ServerResponse<T>>
	apply(final HttpResponse.ResponseInfo info) {
		if (info.statusCode() == 200) {
			return subscriber(type, ServerResponse.OK::new);
		} else {
			return subscriber(ProblemDetail.class, ServerResponse.NOK::new);
		}
	}

	<A, B> HttpResponse.BodySubscriber<B>
	subscriber(final Class<A> type, final Function<? super A, ? extends B> fn) {
		requireNonNull(type);

		return HttpResponse.BodySubscribers.mapping(
			HttpResponse.BodySubscribers.ofInputStream(),
			in -> {
				try {
					return fn.apply(reader.read(in, type));
				} catch (IOException e) {
					throw new UncheckedIOException(e);
				}
			}
		);
	}

}

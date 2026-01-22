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
package io.jenetics.incubator.http;

import java.net.http.HttpResponse;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 8.2
 * @version 8.2
 */
sealed interface ServerResult<T> {

	record OK<T> (T value) implements ServerResult<T> {}

	record NOK<T> (ProblemDetail detail) implements ServerResult<T> {}

	default Response<T> toResult(
		final Request<? extends T> request,
		final HttpResponse<ServerResult<T>> result
	) {
		return switch (this) {
			case ServerResult.OK(var body) -> new Response.Success<>(
				request,
				new Headers(result.headers().map()),
				result.statusCode(),
				request.type().cast(body)
			);
			case ServerResult.NOK(var detail) -> new Response.ServerError<>(
				request,
				new Headers(result.headers().map()),
				result.statusCode(),
				detail
			);
		};
	}

}

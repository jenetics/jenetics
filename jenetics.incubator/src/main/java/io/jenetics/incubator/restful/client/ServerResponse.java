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
package io.jenetics.incubator.restful.client;

import java.net.http.HttpResponse;

import io.jenetics.incubator.restful.ProblemDetail;
import io.jenetics.incubator.restful.Resource;
import io.jenetics.incubator.restful.Response;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 8.2
 * @version 8.2
 */
sealed interface ServerResponse<T> {
	record OK<T> (T value) implements ServerResponse<T> {}

	record NOK<T> (ProblemDetail detail) implements ServerResponse<T> {}

	default Response<T> toResponse(
		final Resource<? extends T> resource,
		final HttpResponse<ServerResponse<T>> result
	) {
		return switch (this) {
			case ServerResponse.OK(var body) -> new Response.Success<>(
				resource,
				result.headers(),
				result.statusCode(),
				resource.type().cast(body)
			);
			case ServerResponse.NOK(var detail) -> new Response.ServerError<>(
				resource,
				result.headers(),
				result.statusCode(),
				detail
			);
		};
	}

}

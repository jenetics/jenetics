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

import io.jenetics.incubator.restful.api.Method;
import io.jenetics.incubator.restful.api.Path;
import io.jenetics.incubator.restful.client.DefaultClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 8.2
 * @version 8.2
 */
public final class MuseumApi {
	private MuseumApi() {
	}

	interface MuseumHours extends Path<String> {
		@Override
		default Resource<String> resource() {
			return Resource.of("/museum-hours", String.class);
		}
		Method.Get<String> get(LocalDate startDate, int page, int limit);
		Method.Put<String> get(LocalDate startDate, LocalDate endDate, int page, int limit);
	}

	public static void main(String[] args) throws Throwable {
		var client = new DefaultClient("", null, null);

		Response<String> response = path(MuseumHours.class)
			.get(LocalDate.now(), 1, 10)
			.call(client.sync());

		var foo = switch (response) {
			case Response.Success<String> s -> "";
			case Response.Failure<String> e -> switch (e) {
				case Response.ClientError<String> ce -> "";
				case Response.ServerError<String> se -> "";
			};
		};

		Response<String> result2 = response.flatMap(r ->
			path(MuseumHours.class)
				.get(LocalDate.now(), 2, 10)
				.call(client.sync())
		);
	}

	/*
	public Method.Get<String> get(LocalDate startDate, int page, int limit) {
		final var resource = MUSEUM_HOURS
			.params(Parameter.query("startDate", startDate.toString()))
			.params(Parameter.query("page", Integer.toString(page)))
			.params(Parameter.query("limit", Integer.toString(limit)));

		return resource::GET;
	}
	 */

	static <T> T path(Class<T> path) {
		return null;
	}

}

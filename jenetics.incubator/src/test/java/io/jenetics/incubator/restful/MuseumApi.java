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

import java.time.LocalDate;
import java.time.LocalTime;

import io.jenetics.incubator.restful.api.ApiPath;
import io.jenetics.incubator.restful.api.ApiProxy;
import io.jenetics.incubator.restful.api.Method;
import io.jenetics.incubator.restful.client.DefaultClient;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 8.2
 * @version 8.2
 */
public final class MuseumApi {
	private MuseumApi() { }

	record Hours(String name, LocalTime start, LocalTime end) {}

	interface MuseumHours extends ApiPath<Hours> {
		Resource<Hours> PATH = Resource.of("/museum-hours", Hours.class);

		Method.Get<Hours> get(LocalDate startDate, int page, int limit);
		Method.Put<Hours> get(LocalDate startDate, LocalDate endDate, int page, int limit);
	}

	public static void main(String[] args) throws Throwable {
		var client = new DefaultClient("", null, null);

		Result<Hours> response = ApiProxy.of(MuseumHours.class)
			.get(LocalDate.now(), 1, 10)
			.call(client.sync());

		var result = switch (response) {
			case Result.Success<Hours> s -> "";
			case Result.ClientError<Hours> ce -> "";
			case Result.ServerError<Hours> se -> "";
		};

		System.out.println(result);
	}

}

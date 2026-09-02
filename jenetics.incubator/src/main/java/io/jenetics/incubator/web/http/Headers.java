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
package io.jenetics.incubator.web.http;

import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A read-only view of a set of HTTP headers.
 *
 * @param values the header values
 */
public record Headers(Map<String, List<String>> values) {

	public Headers {
		final var copy = new HashMap<String, List<String>>();
		values.forEach((name, vals) -> copy.put(name, List.copyOf(vals)));
		values = Map.copyOf(copy);
	}

	void addTo(HttpRequest.Builder builder) {
		values.forEach((name, values) ->
			values.forEach(value ->
				builder.header(name, value)
			)
		);
	}

	static Headers of(HttpHeaders headers) {
		return new Headers(headers.map());
	}

}

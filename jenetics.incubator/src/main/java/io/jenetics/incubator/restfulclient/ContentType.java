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
package io.jenetics.incubator.restfulclient;

import static java.util.Objects.requireNonNull;

import java.net.http.HttpHeaders;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Content type header parameters.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 8.2
 * @version 8.2
 */
public enum ContentType implements Parameter.Header {
	JSON("application/json"),
	XML("application/xml"),
	JSON_PROBLEM_DETAILS("application/problem+json"),
	XML_PROBLEM_DETAILS("application/problem+json");

	private static final String CONTENT_TYPE_KEY = "Content-Type";

	private final String value;

	ContentType(final String value) {
		this.value = requireNonNull(value);
	}

	@Override
	public String key() {
		return CONTENT_TYPE_KEY;
	}

	@Override
	public String value() {
		return value;
	}

	static Optional<ContentType> of(final String name) {
		return Stream.of(values())
			.filter(ct -> ct.key().equals(name))
			.findFirst();
	}

	static Optional<ContentType> of(final HttpHeaders headers) {
		return headers.firstValue(CONTENT_TYPE_KEY)
			.flatMap(ContentType::of);
	}

}

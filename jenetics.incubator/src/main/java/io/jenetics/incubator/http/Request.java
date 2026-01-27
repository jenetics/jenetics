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

import static java.util.Objects.requireNonNull;

import java.net.URI;
import java.util.Map;
import java.util.Optional;

/**
 * The HTTP request object.
 *
 * @param <T> the response body type of the request
 */
public sealed interface Request<T> {

	/**
	 * Return the request's response type.
	 *
	 * @return the request's response type
	 */
	Class<T> type();

	/**
	 * Return the request headers.
	 *
	 * @return the request headers
	 */
	Headers headers();

	/**
	 * Return the request URI.
	 *
	 * @return the request URI
	 */
	URI uri();

	/**
	 * Return a new request of the same type, but with the given {@code uri}.
	 *
	 * @param uri the new request uri
	 * @return a new request of the same type
	 */
	default Request<T> withUri(URI uri) {
		requireNonNull(uri);

		return switch (this) {
			case GET<T> _ -> new GET<>(type(), uri, headers());
			case PUT<T> r -> new PUT<>(type(), uri, headers(), r.body());
			case POST<T> r -> new POST<>(type(), uri, headers(), r.body());
			case DELETE<T> _ -> new DELETE<>(type(), uri, headers());
		};
	}

	/**
	 * <em>GET</em> request.
	 *
	 * @param type the response body type
	 * @param uri the request's {@code URI}
	 * @param headers the request headers
	 * @param <T> the response body type of the request
	 */
	record GET<T>(Class<T> type, URI uri, Headers headers) implements Request<T> {

		public GET {
			requireNonNull(type);
			requireNonNull(uri);
			requireNonNull(headers);
		}

		/**
		 * <em>GET</em> request.
		 *
		 * @param type the response body type
		 * @param uri the request's {@code URI}
		 */
		public GET(Class<T> type, URI uri) {
			this(type, uri, new Headers(Map.of()));
		}

	}

	/**
	 * <em>PUT</em> request.
	 *
	 * @param type the response body type
	 * @param uri the request's {@code URI}
	 * @param headers the request headers
	 * @param body the request body
	 * @param <T> the response body type of the request
	 */
	record PUT<T>(Class<T> type, URI uri, Headers headers, Optional<Object> body)
		implements Request<T>
	{

		public PUT {
			requireNonNull(type);
			requireNonNull(uri);
			requireNonNull(headers);
			requireNonNull(body);
		}

		/**
		 * <em>PUT</em> request.
		 *
		 * @param type the response body type
		 * @param uri the request's {@code URI}
		 * @param body the request body, may be {@code null}
		 */
		public PUT(Class<T> type, URI uri, Object body) {
			this(type, uri, new Headers(Map.of()), Optional.ofNullable(body));
		}

	}

	/**
	 * <em>POST</em> request.
	 *
	 * @param type the response body type
	 * @param uri the request's {@code URI}
	 * @param headers the request headers
	 * @param body the request body
	 * @param <T> the response body type of the request
	 */
	record POST<T>(Class<T> type, URI uri, Headers headers, Optional<Object> body)
		implements Request<T>
	{

		public POST {
			requireNonNull(type);
			requireNonNull(uri);
			requireNonNull(headers);
			requireNonNull(body);
		}

		/**
		 * <em>POST</em> request.
		 *
		 * @param type the response body type
		 * @param uri the request's {@code URI}
		 * @param body the request body, may be {@code null}
		 */
		public POST(Class<T> type, URI uri, Object body) {
			this(type, uri, new Headers(Map.of()), Optional.ofNullable(body));
		}

	}

	/**
	 * <em>DELETE</em> request.
	 *
	 * @param type the response body type
	 * @param uri the request's {@code URI}
	 * @param headers the request headers
	 * @param <T> the response body type of the request
	 */
	record DELETE<T>(Class<T> type, URI uri, Headers headers) implements Request<T> {

		public DELETE {
			requireNonNull(type);
			requireNonNull(uri);
			requireNonNull(headers);
		}

		/**
		 * <em>DELETE</em> request.
		 *
		 * @param type the response body type
		 * @param uri the request's {@code URI}
		 */
		public DELETE(Class<T> type, URI uri) {
			this(type, uri, new Headers(Map.of()));
		}

	}

}

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
	 * Interface for HTTP <em>GET</em> request.
	 *
	 * @param <T> the response body type of the request.
	 */
	non-sealed interface GET<T> extends Request<T> {

		/**
		 * Create a new <em>GET</em> request for the given response {@code type}
		 * and request {@code headers}.
		 *
		 * @param type the response body type
		 * @param headers the request headers
		 * @return a new <em>GET</em> request
		 * @param <T> the response body type of the request
		 */
		static <T> GET<T> of(Class<T> type, URI uri, Headers headers) {
			record GETRecord<T>(Class<T> type, URI uri, Headers headers)
				implements GET<T> { }

			return new GETRecord<>(type, uri, headers);
		}

		/**
		 * Create a new <em>GET</em> request for the given response {@code type}.
		 *
		 * @param type the response body type
		 * @return a new <em>GET</em> request
		 * @param <T> the response body type of the request
		 */
		static <T> GET<T> of(Class<T> type, URI uri) {
			return of(type, uri, new Headers(Map.of()));
		}
	}

	/**
	 * Interface for HTTP <em>PUT</em> request.
	 *
	 * @param <T> the response body type of the request.
	 */
	non-sealed interface PUT<T> extends Request<T> {

		/**
		 * Return the request body of the request, if any.
		 *
		 * @return the request body
		 */
		Optional<Object> body();

		/**
		 * Create a new <em>PUT</em> request for the given response {@code type},
		 * request {@code body}, maybe {@code null} and request {@code headers}.
		 *
		 * @param type the response body type
		 * @param body the request body, maybe {@code null}
		 * @param headers the request headers
		 * @return a new <em>PUT</em> request
		 * @param <T> the response body type of the request
		 */
		static <T> PUT<T> of(Class<T> type, URI uri, Headers headers, Object body) {
			record PUTRecord<T>(
				Class<T> type,
				URI uri,
				Headers headers,
				Optional<Object> body
			)
				implements PUT<T> { }

			return new PUTRecord<>(type, uri, headers, Optional.ofNullable(body));
		}

		/**
		 * Create a new <em>PUT</em> request for the given response {@code type},
		 * and request {@code body}, maybe {@code null}.
		 *
		 * @param type the response body type
		 * @param body the request body, maybe {@code null}
		 * @return a new <em>PUT</em> request
		 * @param <T> the response body type of the request
		 */
		static <T> PUT<T> of(Class<T> type, URI uri, Object body) {
			return of(type, uri, new Headers(Map.of()), Optional.ofNullable(body));
		}
	}

	/**
	 * Interface for HTTP <em>POST</em> request.
	 *
	 * @param <T> the response body type of the request.
	 */
	non-sealed interface POST<T> extends Request<T> {

		/**
		 * Return the request body of the request, if any.
		 *
		 * @return the request body
		 */
		Optional<Object> body();

		/**
		 * Create a new <em>POST</em> request for the given response {@code type},
		 * request {@code body}, maybe {@code null} and request {@code headers}.
		 *
		 * @param type the response body type
		 * @param body the request body, maybe {@code null}
		 * @param headers the request headers
		 * @return a new <em>POST</em> request
		 * @param <T> the response body type of the request
		 */
		static <T> POST<T> of(Class<T> type, URI uri, Headers headers, Object body) {
			record POSTRecord<T>(
				Class<T> type,
				URI uri,
				Headers headers,
				Optional<Object> body
			)
				implements POST<T> { }

			return new POSTRecord<>(type, uri, headers, Optional.ofNullable(body));
		}

		/**
		 * Create a new <em>POST</em> request for the given response {@code type}
		 * and request {@code body}, maybe {@code null}.
		 *
		 * @param type the response body type
		 * @param body the request body, maybe {@code null}
		 * @return a new <em>POST</em> request
		 * @param <T> the response body type of the request
		 */
		static <T> POST<T> of(Class<T> type, URI uri, Object body) {
			return of(type, uri, new Headers(Map.of()), Optional.ofNullable(body));
		}
	}

	/**
	 * Interface for HTTP <em>DELETE</em> request.
	 *
	 * @param <T> the response body type of the request.
	 */
	non-sealed interface DELETE<T> extends Request<T> {

		/**
		 * Create a new <em>DELETE</em> request for the given response {@code type}
		 * and request {@code headers}.
		 *
		 * @param type the response body type
		 * @param headers the request headers
		 * @return a new <em>DELETE</em> request
		 * @param <T> the response body type of the request
		 */
		static <T> DELETE<T> of(Class<T> type, URI uri, Headers headers) {
			record DELETERecord<T>(Class<T> type, URI uri, Headers headers)
				implements DELETE<T> { }

			return new DELETERecord<>(type, uri, headers);
		}

		/**
		 * Create a new <em>DELETE</em> request for the given response {@code type}.
		 *
		 * @param type the response body type
		 * @return a new <em>DELETE</em> request
		 * @param <T> the response body type of the request
		 */
		static <T> DELETE<T> of(Class<T> type, URI uri) {
			return of(type, uri, new Headers(Map.of()));
		}
	}

}

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

/**
 * This class wraps a failure object into an exception. This exception is used
 * for asynchronous calls, which returns {@link java.util.concurrent.CompletableFuture}
 * objects. Such calls will transport the error state via exceptions.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 8.2
 * @version 8.2
 */
public final class ResponseException extends RuntimeException {

	private final Response.Failure<?> failure;

	public ResponseException(final Response.Failure<?> failure) {
		this.failure = requireNonNull(failure);
	}

	public ResponseException(final String message, final Response.Failure<?> failure) {
		super(message);
		this.failure = requireNonNull(failure);
	}

	/**
	 * Return the wrapped failure response.
	 *
	 * @return the wrapped failure response
	 */
	public Response.Failure<?> failure() {
		return failure;
	}

}

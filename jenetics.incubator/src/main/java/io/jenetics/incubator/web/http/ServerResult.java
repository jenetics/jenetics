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

/**
 * The result object returned by the {@link Client#send(Request)} method.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 8.2
 * @version 8.2
 */
public sealed interface ServerResult<T> {

	/**
	 * Return the original resource object. The type of the original resource
	 * is not known, since the response type might have been changed by one
	 * of the mapping methods.
	 *
	 * @return the original resource object
	 */
	Request<T> request();

	/**
	 * The HTTP status code of the response.
	 *
	 * @return the HTTP status code
	 */
	int status();

	/**
	 * The response headers.
	 *
	 * @return response headers
	 */
	Headers headers();

	/**
	 * The server success response.
	 *
	 * @param request the original request object
	 * @param headers the response headers
	 * @param status the response status
	 * @param body the response body
	 * @param <T> the body type
	 */
	record OK<T>(Request<T> request, Headers headers, int status, T body)
		implements ServerResult<T>
	{
	}

	/**
	 * The server error response
	 *
	 * @param request the original request object
	 * @param headers the response headers
	 * @param status the response status
	 * @param detail the error details as string
	 * @param <T> the body type
	 */
	record NOK<T> (Request<T> request, Headers headers, int status, String detail)
		implements ServerResult<T>
	{
	}

}

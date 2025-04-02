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

/**
 * REST methods.
 *
 * @see <a href="https://de.wikipedia.org/wiki/Representational_State_Transfer">REST</a>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 8.2
 * @version 8.2
 */
public interface Rest<T> {

	/**
	 * Executes the HTTP GET method.
	 *
	 * @param caller the resource caller
	 * @return the GET caller result
	 * @param <C> the caller result type
	 */
	<C> C GET(final Caller<? super T, ? extends C> caller);

	/**
	 * Executes the HTTP PUT method.
	 *
	 * @param body the request body
	 * @param caller the resource caller
	 * @return the PUT caller result
	 * @param <C> the caller result type
	 */
	<C> C PUT(final Object body, final Caller<? super T, ? extends C> caller);

	/**
	 * Executes the HTTP POST method.
	 *
	 * @param body the request body
	 * @param caller the resource caller
	 * @return the POST caller result
	 * @param <C> the caller result type
	 */
	<C> C POST(final Object body, final Caller<? super T, ? extends C> caller);

	/**
	 * Executes the HTTP DELETE method.
	 *
	 * @param caller the resource caller
	 * @return the DELETE caller result
	 * @param <C> the caller result type
	 */
	<C> C DELETE(final Caller<? super T, ? extends C> caller);

}

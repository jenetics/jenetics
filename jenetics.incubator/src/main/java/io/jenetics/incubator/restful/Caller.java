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

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow;

/**
 * This interface is responsible for calling the given {@code resource} and
 * return a result object. This interface is not meant to be implemented directly.
 * The usual <em>implementation</em> will be a method reference from a client
 * implementation.
 *
 * @param <T> the <em>main</em> result type of the {@code resource}
 * @param <C> the result type returned by the caller
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 8.2
 * @version 8.2
 */
@FunctionalInterface
public interface Caller<T, C> {

	/**
	 * Caller specialization for synchronous REST calls.
	 *
	 * @param <T> the result type
	 */
	interface Sync<T> extends Caller<T, Response<T>> {}

	/**
	 * Caller specialization for asynchronous REST calls.
	 *
	 * @param <T> the result type
	 */
	interface Async<T> extends Caller<T, CompletableFuture<Response.Success<T>>> {}

	/**
	 * Caller specialization for reactive REST calls.
	 *
	 * @param <T> the result type
	 */
	interface Reactive<T> extends Caller<T, Flow.Publisher<Response.Success<T>>> {}

	/**
	 * Calls the given {@code resource} and returns its result.
	 *
	 * @param resource the resource
	 * @return the call result
	 */
	C call(Resource<? extends T> resource);

}

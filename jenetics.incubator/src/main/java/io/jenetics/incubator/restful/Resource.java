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

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import io.jenetics.incubator.http.Caller;
import io.jenetics.incubator.http.Request.DELETE;
import io.jenetics.incubator.http.Request.GET;
import io.jenetics.incubator.http.Request.POST;
import io.jenetics.incubator.http.Request.PUT;

/**
 * This object represents a REST endpoint. It is immutable and can be reused
 * and shared between different threads.
 *
 * @param <T> the resource return type
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 8.2
 * @version 8.2
 */
public final class Resource<T> {
	private final Class<? extends T> type;
	private final Path path;
	private final List<Parameter> parameters;

	private Resource(
		final Class<? extends T> type,
		final Path path,
		final Collection<Parameter> parameters
	) {
		this.type = requireNonNull(type);
		this.path = requireNonNull(path);
		this.parameters = List.copyOf(parameters);
	}

	/**
	 * Return the resource result type.
	 *
	 * @return the resource result type
	 */
	@SuppressWarnings("unchecked")
	public Class<T> type() {
		return (Class<T>)type;
	}

	/**
	 * Return the resource path.
	 *
	 * @return the resource path
	 */
	public Path path() {
		return path;
	}

	/**
	 * Return the parameter list associated with this resource.
	 *
	 * @return the parameter list
	 */
	public List<Parameter> parameters() {
		return parameters;
	}

	/**
	 * Set the resource parameters: path, query, and header.
	 *
	 * @param parameters the resource parameters
	 * @return a new resource
	 */
	public Resource<T> add(Parameter... parameters) {
		final var params = Stream
			.concat(Stream.of(parameters), this.parameters.stream())
			.toList();

		return new Resource<>(type, path, params);
	}

	/**
	 * Executes the HTTP GET method.
	 *
	 * @return the GET caller result
	 */
	public <C> C GET(final Caller<T, C> caller) {
		return caller.call(new GET<>(type(), path.toURI()));
	}

	/**
	 * Executes the HTTP PUT method.
	 *
	 * @param body the request body
	 * @param caller the resource caller
	 * @return the PUT caller result
	 * @param <C> the caller result type
	 */
	public <C> C PUT(final Object body, final Caller<T, C> caller) {
		return caller.call(new PUT<>(type(), path.toURI(), body));
	}

	/**
	 * Executes the HTTP PUT method.
	 *
	 * @param caller the resource caller
	 * @return the PUT caller result
	 * @param <C> the caller result type
	 */
	public <C> C PUT(final Caller<T, C> caller) {
		return caller.call(new PUT<>(type(), path.toURI(), null));
	}

	/**
	 * Executes the HTTP POST method.
	 *
	 * @param body the request body
	 * @param caller the resource caller
	 * @return the POST caller result
	 * @param <C> the caller result type
	 */
	public <C> C POST(final Object body, final Caller<T, C> caller) {
		return caller.call(new POST<>(type(), path.toURI(), body));
	}

	/**
	 * Executes the HTTP POST method.
	 *
	 * @param caller the resource caller
	 * @return the POST caller result
	 * @param <C> the caller result type
	 */
	public <C> C POST(final Caller<T, C> caller) {
		return caller.call(new POST<>(type(), path.toURI(), null));
	}

	/**
	 * Executes the HTTP DELETE method.
	 *
	 * @param caller the resource caller
	 * @return the DELETE caller result
	 * @param <C> the caller result type
	 */
	public <C> C DELETE(final Caller<T, C> caller) {
		return caller.call(new DELETE<>(type(), path.toURI()));
	}

	@Override
	public int hashCode() {
		return Objects.hash(type, path, parameters);
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof Resource<?> rsc &&
			Objects.equals(type, rsc.type) &&
			Objects.equals(path, rsc.path) &&
			Objects.equals(parameters, rsc.parameters);
	}

	@Override
	public String toString() {
		return "Resource[type=%s, path=%s, parameters=%s]"
			.formatted(type, path, parameters);
	}

	/**
	 * Create a new resource object from the given {@code path} a return
	 * {@code type}.
	 *
	 * @param path the (local) resource path
	 * @param type the resource return type
	 * @return a new resource
	 * @param <T> the resource type
	 */
	public static <T> Resource<T> of(Path path, Class<? extends T> type) {
		return new Resource<>(type, path, List.of());
	}

	/**
	 * Create a new resource object from the given {@code path} a return
	 * {@code type}.
	 *
	 * @param path the (local) resource path
	 * @param type the resource return type
	 * @return a new resource
	 * @param <T> the resource type
	 */
	public static <T> Resource<T> of(String path, Class<? extends T> type) {
		return new Resource<>(type, Path.of(path), List.of());
	}

}

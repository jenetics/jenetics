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
//	private final Class<? extends T> type;
//	private final Path path;
//	private final List<Parameter> parameters;
//	private final Object body;
//	private final MethodType method;
//
//	private Resource(
//		final Class<? extends T> type,
//		final Path path,
//		final Collection<Parameter> parameters,
//		final Object body,
//		final MethodType method
//	) {
//		this.type = requireNonNull(type);
//		this.path = requireNonNull(path);
//		this.parameters = List.copyOf(parameters);
//		this.body = body;
//		this.method = requireNonNull(method);
//	}
//
//	/**
//	 * Return the resource result type.
//	 *
//	 * @return the resource result type
//	 */
//	@SuppressWarnings("unchecked")
//	public Class<T> type() {
//		return (Class<T>)type;
//	}
//
//	/**
//	 * Return the resource path.
//	 *
//	 * @return the resource path
//	 */
//	public String path() {
//		return path.path();
//	}
//
//	/**
//	 * Return the resolved resource path. The path parameter will be replaced by
//	 * the real values, if defined.
//	 *
//	 * @return the resolved path
//	 */
//	public String resolvedPath() {
//		final var result = parameters.stream()
//			.filter(p -> p instanceof Parameter.Path)
//			.map(Parameter.Path.class::cast)
//			.reduce(path, Path::resolve,
//				(a, b) -> {
//					throw new AssertionError("No parallel resolve allowed.");
//				}
//			);
//
//		final var unresolved = result.paramNames();
//		if (!unresolved.isEmpty()) {
//			throw new IllegalStateException("Unresolved parameters: " + unresolved);
//		}
//
//		final var queries = parameters.stream()
//			.filter(p -> p instanceof Parameter.Query)
//			.map(Parameter.Query.class::cast)
//			.map(Resource::encodeQuery)
//			.collect(Collectors.joining("&", "?", ""));
//
//		return result.path() + queries;
//	}
//
//	private static String encodeQuery(final Parameter.Query query) {
//		return URLEncoder.encode(query.key(), UTF_8) + "=" +
//			URLEncoder.encode(query.value(), UTF_8);
//	}
//
//	/**
//	 * Return the parameter list associated with this resource.
//	 *
//	 * @return the parameter list
//	 */
//	public List<Parameter> parameters() {
//		return parameters;
//	}
//
//	/**
//	 * Return the resource body. The body will be set by the
//	 * {@link #PUT(Object, Endpoint)} and {@link #POST(Object, Endpoint)} methods
//	 * and is then available for the {@link Endpoint}.
//	 *
//	 * @return the resource body
//	 */
//	public Optional<?> body() {
//		return Optional.ofNullable(body);
//	}
//
//	/**
//	 * Return the resource method. The method will be set by the
//	 * {@link #GET(Endpoint)}, {@link #PUT(Object, Endpoint)},
//	 * {@link #POST(Object, Endpoint)} and {@link #DELETE(Endpoint)} methods
//	 * and is then available for the {@link Endpoint}.
//	 *
//	 * @return the resource method
//	 */
//	public MethodType method() {
//		return method;
//	}
//
//	/**
//	 * Set the resource parameters: path, query, and header.
//	 *
//	 * @param parameters the resource parameters
//	 * @return a new resource
//	 */
//	public Resource<T> params(Parameter... parameters) {
//		final var params = parameters().stream()
//			.collect(Collectors.toMap(Parameter::key, Function.identity()));
//		for (var parameter : parameters) {
//			params.put(parameter.key(),parameter);
//		}
//
//		return new Resource<>(type, path, params.values(), body, method);
//	}
//
//	/**
//	 * Executes the HTTP GET method.
//	 *
//	 * @param caller the resource caller
//	 * @return the GET caller result
//	 * @param <C> the caller result type
//	 */
//	public <C> C GET(final Endpoint<? super T, ? extends C> caller) {
//		var rsc = new Resource<T>(type, path, parameters, null, GET);
//		return caller.call(rsc);
//	}
//
//	/**
//	 * Executes the HTTP PUT method.
//	 *
//	 * @param body the request body
//	 * @param caller the resource caller
//	 * @return the PUT caller result
//	 * @param <C> the caller result type
//	 */
//	public <C> C PUT(final Object body, final Endpoint<? super T, ? extends C> caller) {
//		var rsc = new Resource<T>(type, path, parameters, body, PUT);
//		return caller.call(rsc);
//	}
//
//	/**
//	 * Executes the HTTP POST method.
//	 *
//	 * @param body the request body
//	 * @param caller the resource caller
//	 * @return the POST caller result
//	 * @param <C> the caller result type
//	 */
//	public <C> C POST(final Object body, final Endpoint<? super T, ? extends C> caller) {
//		var rsc = new Resource<T>(type, path, parameters, body, POST);
//		return caller.call(rsc);
//	}
//
//	/**
//	 * Executes the HTTP DELETE method.
//	 *
//	 * @param caller the resource caller
//	 * @return the DELETE caller result
//	 * @param <C> the caller result type
//	 */
//	public <C> C DELETE(final Endpoint<? super T, ? extends C> caller) {
//		var rsc = new Resource<T>(type, path, parameters, null, DELETE);
//		return caller.call(rsc);
//	}
//
//	@Override
//	public int hashCode() {
//		return Objects.hash(type, path, parameters, body, method);
//	}
//
//	@Override
//	public boolean equals(final Object obj) {
//		return obj instanceof Resource<?> rsc &&
//			Objects.equals(type, rsc.type) &&
//			Objects.equals(path, rsc.path) &&
//			Objects.equals(parameters, rsc.parameters) &&
//			Objects.equals(body, rsc.body) &&
//			Objects.equals(method, rsc.method);
//	}
//
//	@Override
//	public String toString() {
//		return "Resource[type=%s, path=%s, parameters=%s]"
//			.formatted(type, path, parameters);
//	}
//
//	/**
//	 * Create a new resource object from the given {@code path} a return
//	 * {@code type}.
//	 *
//	 * @param path the (local) resource path
//	 * @param type the resource return type
//	 * @return a new resource
//	 * @param <T> the resource type
//	 */
//	public static <T> Resource<T> of(String path, Class<? extends T> type) {
//		return new Resource<>(type, Path.of(path), List.of(), null, GET);
//	}

}

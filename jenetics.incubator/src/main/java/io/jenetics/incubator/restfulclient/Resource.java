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

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.requireNonNull;
import static io.jenetics.incubator.restfulclient.Method.DELETE;
import static io.jenetics.incubator.restfulclient.Method.GET;
import static io.jenetics.incubator.restfulclient.Method.POST;
import static io.jenetics.incubator.restfulclient.Method.PUT;

import java.net.URLEncoder;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 8.2
 * @version 8.2
 */
public final class Resource<T> implements Rest<T> {
	private final Class<? extends T> type;
	private final Path path;
	private final List<Parameter> parameters;
	private final Object body;
	private final Method method;

	private Resource(
		final Class<? extends T> type,
		final Path path,
		final Collection<Parameter> parameters,
		final Object body,
		final Method method
	) {
		this.type = requireNonNull(type);
		this.path = requireNonNull(path);
		this.parameters = List.copyOf(parameters);
		this.body = body;
		this.method = requireNonNull(method);
	}

	@SuppressWarnings("unchecked")
	public Class<T> type() {
		return (Class<T>)type;
	}

	public String path() {
		return path.path();
	}

	public String resolvedPath() {
		final var result = parameters.stream()
			.filter(p -> p instanceof Parameter.Path)
			.map(Parameter.Path.class::cast)
			.reduce(path, Path::resolve,
				(a, b) -> {
					throw new AssertionError("No parallel resolve allowed.");
				}
			);

		final var unresolved = result.paramNames();
		if (!unresolved.isEmpty()) {
			throw new IllegalStateException("Unresolved parameters: " + unresolved);
		}

		final var queries = parameters.stream()
			.filter(p -> p instanceof Parameter.Query)
			.map(Parameter.Query.class::cast)
			.map(Resource::encodeQuery)
			.collect(Collectors.joining("&", "?", ""));

		return result.path() + queries;
	}

	private static String encodeQuery(final Parameter.Query query) {
		return URLEncoder.encode(query.key(), UTF_8) + "=" +
			URLEncoder.encode(query.value(), UTF_8);
	}

	public List<Parameter> parameters() {
		return parameters;
	}

	public Optional<?> body() {
		return Optional.ofNullable(body);
	}

	public Method method() {
		return method;
	}

	public Resource<T> params(Parameter... parameters) {
		final var params = parameters().stream()
			.collect(Collectors.toMap(Parameter::key, Function.identity()));
		for (var parameter : parameters) {
			params.put(parameter.key(),parameter);
		}

		return new Resource<>(type, path, params.values(), body, method);
	}

	@Override
	public <C> C GET(final Caller<? super T, ? extends C> caller) {
		var rsc = new Resource<T>(type, path, parameters, null, GET);
		return caller.call(rsc);
	}

	@Override
	public <C> C PUT(final Object body, final Caller<? super T, ? extends C> caller) {
		var rsc = new Resource<T>(type, path, parameters, body, PUT);
		return caller.call(rsc);
	}

	@Override
	public <C> C POST(final Object body, final Caller<? super T, ? extends C> caller) {
		var rsc = new Resource<T>(type, path, parameters, body, POST);
		return caller.call(rsc);
	}

	@Override
	public <C> C DELETE(final Caller<? super T, ? extends C> caller) {
		var rsc = new Resource<T>(type, path, parameters, null, DELETE);
		return caller.call(rsc);
	}

	public Rest<T> rest() {
		return new Rest<>() {
			@Override
			public <C> C GET(Caller<? super T, ? extends C> caller) {
				return Resource.this.GET(caller);
			}
			@Override
			public <C> C PUT(Object body, Caller<? super T, ? extends C> caller) {
				return Resource.this.PUT(body, caller);
			}
			@Override
			public <C> C POST(Object body, Caller<? super T, ? extends C> caller) {
				return Resource.this.POST(body, caller);
			}
			@Override
			public <C> C DELETE(Caller<? super T, ? extends C> caller) {
				return Resource.this.DELETE(caller);
			}
		};
	}

	public static <T> Resource<T> of(String path, Class<? extends T> type) {
		return new Resource<>(type, Path.of(path), List.of(), null, GET);
	}

}

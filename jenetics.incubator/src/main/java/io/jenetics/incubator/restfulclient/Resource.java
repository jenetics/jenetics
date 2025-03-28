package io.jenetics.incubator.restfulclient;

import static java.util.Objects.requireNonNull;

import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class Resource<T> {

	private final Class<T> type;
	private final String path;
	private final List<Parameter> parameters;
	private final Object body;
	private final Method method;

	private Resource(
		final Class<T> type,
		final String path,
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

	public Class<T> type() {
		return type;
	}

	public String path() {
		return path;
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

	private Resource<T> method(final Method method) {
		return new Resource<>(type, path, parameters, body, method);
	}

	public Resource<T> params(Parameter... parameters) {
		final var params = parameters().stream()
			.collect(Collectors.toMap(Parameter::key, Function.identity()));
		for (var parameter : parameters) {
			params.put(parameter.key(),parameter);
		}

		return new Resource<>(type, path, params.values(), body, method);
	};

	private Resource<T> body(Object body) {
		return new Resource<>(type, path, parameters, body, method);
	}

	public URI toURI() {
		return null;
	}

	public <C> C GET(final Caller<T, C> caller) {
		return method(Method.GET).call(caller);
	}

	public <C> C PUT(final Object body, final Caller<T, C> caller) {
		return method(Method.PUT).body(body).call(caller);
	}

	public <C> C POST(final Object body, final Caller<T, C> caller) {
		return method(Method.POST).body(body).call(caller);
	}

	public <C> C DELETE(final Caller<T, C> caller) {
		return method(Method.DELETE).call(caller);
	}

	private <C> C call(Caller<T, C> caller) {
		return caller.call(this);
	}

	public static <T> Resource<T> of(String path, Class<T> type) {
		return new Resource<>(type, path, List.of(), null, Method.GET);
	}

}

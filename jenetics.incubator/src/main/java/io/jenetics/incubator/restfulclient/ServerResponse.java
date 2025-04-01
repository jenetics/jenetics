package io.jenetics.incubator.restfulclient;

import java.net.http.HttpResponse;

sealed interface ServerResponse<T> {
	record OK<T> (T value) implements ServerResponse<T> {}
	record NOK<T> (ProblemDetail detail) implements ServerResponse<T> {}

	default Response<T> toResponse(
		Resource<? extends T> resource,
		final HttpResponse<ServerResponse<T>> result
	) {
		return switch (this) {
			case ServerResponse.OK(var body) -> new Response.Success<>(
				resource,
				result.headers(),
				result.statusCode(),
				resource.type().cast(body)
			);
			case ServerResponse.NOK(var detail) -> new Response.ServerError<>(
				resource,
				result.headers(),
				result.statusCode(),
				detail
			);
		};
	}

}

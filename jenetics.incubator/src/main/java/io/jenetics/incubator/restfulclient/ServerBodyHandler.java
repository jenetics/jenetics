package io.jenetics.incubator.restfulclient;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.http.HttpResponse;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

final class ServerBodyHandler<T>
	implements HttpResponse.BodyHandler<ServerResponse<T>>
{
	private final Reader reader;
	private final Class<T> type;

	@SuppressWarnings("unchecked")
	ServerBodyHandler(final Reader reader, final Class<? extends T> type) {
		this.reader = requireNonNull(reader);
		this.type = (Class<T>)requireNonNull(type);
	}

	@Override
	public HttpResponse.BodySubscriber<ServerResponse<T>>
	apply(final HttpResponse.ResponseInfo info) {
		if (info.statusCode() == 200) {
			return subscriber(type, ServerResponse.OK::new);
		} else {
			return subscriber(ProblemDetail.class, ServerResponse.NOK::new);
		}
	}

	<A, B> HttpResponse.BodySubscriber<B>
	subscriber(final Class<A> type, final Function<? super A, ? extends B> fn) {
		requireNonNull(type);

		return HttpResponse.BodySubscribers.mapping(
			HttpResponse.BodySubscribers.ofInputStream(),
			in -> {
				try {
					return fn.apply(reader.read(in, type));
				} catch (IOException e) {
					throw new UncheckedIOException(e);
				}
			}
		);
	}

}

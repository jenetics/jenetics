package io.jenetics.incubator.restfulclient;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.http.HttpResponse;

final class BodySubscribers {
	private BodySubscribers() {
	}

	static <T> HttpResponse.BodySubscriber<T> jackson(
		final ObjectMapper mapper,
		final Class<? extends T> type
	) {
		return HttpResponse.BodySubscribers.mapping(
			HttpResponse.BodySubscribers.ofInputStream(),
			in -> {
				try {
					return mapper.readValue(in, type);
				} catch (IOException e) {
					throw new UncheckedIOException(e);
				}
			}
		);
	}

}

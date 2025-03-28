package io.jenetics.incubator.restfulclient;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.http.HttpResponse;

final class BodyHandlers {
	private BodyHandlers() {
	}

	static HttpResponse.BodyHandler<?> jackson(
		final ObjectMapper mapper,
		final Class<?> type
	) {
		return info -> {
			if (info.statusCode() == 200) {
				return BodySubscribers.jackson(mapper, type);
			} else {
				return BodySubscribers.jackson(mapper, ErrorInfo.class);
			}
		};
	}

}

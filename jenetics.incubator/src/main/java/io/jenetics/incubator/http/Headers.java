package io.jenetics.incubator.http;

import java.net.http.HttpRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record Headers(Map<String, List<String>> values) {

	public Headers {
		final var copy = new HashMap<String, List<String>>();
		values.forEach((name, vals) -> copy.put(name, List.copyOf(vals)));
		values = Map.copyOf(copy);
	}

	void addTo(HttpRequest.Builder builder) {
		values.forEach((name, values) ->
			values.forEach(value ->
				builder.header(name, value)
			)
		);
	}

}

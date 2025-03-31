package io.jenetics.incubator.restfulclient;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.UncheckedIOException;
import java.net.http.HttpRequest;

final class BodyPublishers {
	private BodyPublishers() {
	}

	static HttpRequest.BodyPublisher jackson(
		final Object body,
		final ObjectMapper mapper
	) {
		return HttpRequest.BodyPublishers.ofInputStream(() -> {
			if (body == null) {
				return new ByteArrayInputStream(new byte[0]);
			}

			var out = new PipedOutputStream();
			try {
				var in = new PipedInputStream(out);
				Thread.ofVirtual().start(() -> {
					try {
						mapper.writeValue(out, body);
						out.close();
					} catch (IOException e) {
						throw new UncheckedIOException(e);
					}
				});
				return in;
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		});
	}

}

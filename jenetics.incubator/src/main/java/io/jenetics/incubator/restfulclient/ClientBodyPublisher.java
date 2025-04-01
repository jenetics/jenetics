package io.jenetics.incubator.restfulclient;

import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.http.HttpRequest;
import java.nio.ByteBuffer;
import java.util.concurrent.Flow;

import static java.util.Objects.requireNonNull;

final class ClientBodyPublisher implements HttpRequest.BodyPublisher{

	private final HttpRequest.BodyPublisher delegate;

	ClientBodyPublisher(final Writer writer, final Object body) {
		requireNonNull(writer);

		if (body == null) {
			this.delegate = HttpRequest.BodyPublishers.noBody();
		} else {
			this.delegate = HttpRequest.BodyPublishers.ofInputStream(() -> {
				final var in = new RestPipedInputStream(new PipedInputStream());
				final var thread = Thread.ofVirtual().factory().newThread(() -> {
					try (var out = new PipedOutputStream()) {
						in.connect(out);
						writer.write(out, body);
					} catch (Throwable e) {
						in.error(e);
					}
				});

				try {
					return in;
				} finally {
					thread.start();
				}
			});
		}
	}

	@Override
	public void subscribe(final Flow.Subscriber<? super ByteBuffer> subscriber) {
		delegate.subscribe(subscriber);
	}

	@Override
	public long contentLength() {
		return delegate.contentLength();
	}
}

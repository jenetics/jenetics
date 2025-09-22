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
package io.jenetics.incubator.restful.client;

import static java.util.Objects.requireNonNull;

import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.http.HttpRequest;
import java.nio.ByteBuffer;
import java.util.concurrent.Flow;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 8.2
 * @version 8.2
 */
final class ClientBodyPublisher implements HttpRequest.BodyPublisher {

	private final HttpRequest.BodyPublisher delegate;

	ClientBodyPublisher(final Writer writer, final Object body) {
		requireNonNull(writer);

		if (body == null) {
			this.delegate = HttpRequest.BodyPublishers.noBody();
		} else {
			this.delegate = HttpRequest.BodyPublishers.ofInputStream(() -> {
				final var in = new ErrorPropagatingPipedInputStream(new PipedInputStream());
				Thread.ofVirtual().start(() -> {
					try (var out = new PipedOutputStream()) {
						in.connect(out);
						writer.write(out, body);
					} catch (Throwable e) {
						in.error(e);
					}
				});

				return in;
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

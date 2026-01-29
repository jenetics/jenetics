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
package io.jenetics.incubator.web.http;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Combines a {@link RequestBodyWriter} and {@link ResponseBodyReader} into one
 * interface.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since !__version__!
 * @version !__version__!
 */
public interface BodyMarshaling extends RequestBodyWriter, ResponseBodyReader {

	/**
	 * Return a body marshaling from the given {@code writer} and {@code reader}.
	 *
	 * @param writer the request body writer
	 * @param reader the response body reader
	 * @return aa body marshaling
	 */
	static BodyMarshaling of(RequestBodyWriter writer, ResponseBodyReader reader) {
		requireNonNull(writer);
		requireNonNull(reader);

		return new BodyMarshaling() {
			@Override
			public void write(OutputStream sink, Object value) throws IOException {
				writer.write(sink, value);
			}
			@Override
			public <T> T read(InputStream input, Class<T> type) throws IOException {
				return reader.read(input, type);
			}
		};
	}

}

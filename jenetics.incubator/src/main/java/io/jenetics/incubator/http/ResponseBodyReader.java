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
package io.jenetics.incubator.http;

import java.io.IOException;
import java.io.InputStream;

/**
 * Reader interface for reading values from a given input stream.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 8.2
 * @version 8.2
 */
@FunctionalInterface
public interface ResponseBodyReader {

	/**
	 * Reads a value, of type {@code T}, from the given {@code input} stream.
	 *
	 * @param input the input stream the value is read from
	 * @param type the type of the read object
	 * @return the read (deserialized) value
	 * @param <T> the value type
	 * @throws IOException if reading the value fails
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	<T> T read(final InputStream input, Class<T> type) throws IOException;

}

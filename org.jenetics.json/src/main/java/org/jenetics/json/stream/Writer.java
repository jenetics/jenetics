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
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 */
package org.jenetics.json.stream;

import static java.util.Objects.requireNonNull;

import java.io.IOException;

import com.google.gson.stream.JsonWriter;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
@FunctionalInterface
public interface Writer<T> {

	public void write(final T value, final JsonWriter json) throws IOException;


	public static <T> Writer<T> obj(final String name, final Writer<? super T>... children) {
		requireNonNull(name);
		requireNonNull(children);

		return (data, json) -> {
			if (data != null) {
				json.beginObject();
				json.name(name);
				for (Writer<? super T> child : children) {
					child.write(data, json);
				}
				json.endObject();
			}
		};
	}

}

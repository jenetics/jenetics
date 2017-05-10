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
import java.util.function.Function;

import com.google.gson.stream.JsonWriter;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
@FunctionalInterface
public interface Writer<T> {

	public void write(final JsonWriter json, final T value) throws IOException;

	public default <B> Writer<B> map(final Function<? super B, ? extends T> mapper) {
		return (json, data) -> {
			if (data != null) {
				final T value = mapper.apply(data);
				if (value != null) {
					write(json, value);
				}
			}
		};
	}

	public static <T> Writer<T> obj(final Writer<? super T>... children) {
		requireNonNull(children);

		return (json, data) -> {
			if (data != null) {
				json.beginObject();
				for (Writer<? super T> child : children) {
					child.write(json, data);
				}
				json.endObject();
			}
		};
	}


	public static Writer<String> text(final String name) {
		return (json, data) -> {
			json.name(name).value(data);
		};
	}

	public static <T> Writer<T> text(final String name, final String value) {
		return (json, data) -> {
			json.name(name).value(value);
		};
	}

	public static Writer<Number> number(final String name) {
		return (json, data) -> {
			if (data != null) {
				json.name(name).value(data);
			}
		};
	}

	public static Writer<Number> number() {
		return (json, data) -> {
			if (data != null) {
				json.value(data);
			}
		};
	}

	public static Writer<Boolean> bool(final String name) {
		return (json, data) -> {
			if (data != null) {
				json.name(name).value(data);
			}
		};
	}

	public static <T> Writer<Iterable<T>> array(final String name, final Writer<? super T> writer) {
		return (json, data) -> {
			if (data != null) {
				json.name(name);
				json.beginArray();
				for (T value : data) {
					writer.write(json, value);
				}
				json.endArray();
			}
		};
	}

}

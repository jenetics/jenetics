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
package io.jenetics.incubator.metamodel;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toMap;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;
import java.util.stream.Stream;

import io.jenetics.incubator.metamodel.internal.Props;
import io.jenetics.internal.util.Lazy;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 8.3
 * @since 8.3
 */
public class Model {

	private final PathValue<?> value;
	private final List<Model> children = new CopyOnWriteArrayList<>();

	private final Supplier<Map<Path, PathValue<?>>> fields;

	public Model(final PathValue<?> value) {
		this.value = requireNonNull(value);

		this.fields = Lazy.of(() ->
			Props.list(this.value)
				.collect(toMap(PathValue::path, a -> a, (a, b) -> a, TreeMap::new))
		);
	}

	public void add(final Model model) {
		children.add(requireNonNull(model));
	}

	public Stream<PathValue<?>> fields() {
		return Stream.concat(
			fields.get().values().stream(),
			children.stream().flatMap(Model::fields)
		);
	}

	public Stream<PathValue<?>> fields(final Matcher<? super Object> matcher) {
		requireNonNull(matcher);
		return fields().filter(matcher::matches);
	}

	@SuppressWarnings("unchecked")
	public <T> Stream<PathValue<T>> fieldsOfType(final Class<? extends T> type) {
		requireNonNull(type);

		return fields()
			.filter(pv -> type.isInstance(pv.value()))
			.map(pv -> (PathValue<T>)pv);
	}

	public <T> Stream<PathValue<T>> fieldsOfType(
		final Class<? extends T> type,
		final Matcher<? super T> matcher
	) {
		@SuppressWarnings("unchecked")
		final var typ = (Class<T>)requireNonNull(type);
		return fieldsOfType(typ).filter(matcher::matches);
	}

}


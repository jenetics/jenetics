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
package io.jenetics.incubator.property;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.stream.Stream;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
final class PropertyExtractor implements Extractor<PathObject, Property> {

	static final PropertyExtractor DEFAULT =
		new PropertyExtractor(PropertyDescriptionExtractor::extract);

	private final Extractor<? super Class<?>, ? extends PropertyDescription> descriptions;

	PropertyExtractor(
		final Extractor<
			? super Class<?>,
			? extends PropertyDescription> descriptions
	) {
		this.descriptions = requireNonNull(descriptions);
	}

	@Override
	public Stream<Property> extract(final PathObject object) {
		requireNonNull(object);

		if (object.value() != null) {
			return descriptions
				.extract(object.value().getClass())
				.map(desc -> {
					final var enclosing = object.value();
					final var path = object.path().append(desc.name());
					final var type = desc.type();
					final var value = desc.read(object.value());

					if (type.isArray() &&
						!type.getComponentType().isPrimitive())
					{
						return new ArrayProperty(desc, enclosing, path, value);
					} else if (List.class.isAssignableFrom(type)) {
						return new ListProperty(desc, enclosing, path, value);
					} else {
						return new SimpleProperty(desc, enclosing, path, value);
					}
				});
		} else {
			return Stream.empty();
		}
	}

	public Stream<Property> properties(final Object value) {
		final var data = value instanceof PathObject object
			? object
			: new PathObject(value);

		return extract(data);
	}

}

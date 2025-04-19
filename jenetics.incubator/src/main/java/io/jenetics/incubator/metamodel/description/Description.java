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
package io.jenetics.incubator.metamodel.description;

import static java.util.Objects.requireNonNull;

import java.lang.reflect.Type;

import io.jenetics.incubator.metamodel.Path;
import io.jenetics.incubator.metamodel.type.MetaModelType;

/**
 * Adds path information to a {@link MetaModelType}.
 *
 * @param path the path for a given model type
 * @param type the actual property type
 * @param enclosure the enclosing type for the model property
 * @param model the model type
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 7.2
 * @since 7.2
 */
public record Description(
	Path path,
	Type type,
	Type enclosure,
	MetaModelType model
)  {

	public Description {
		requireNonNull(path);
		requireNonNull(type);
		requireNonNull(enclosure);
		requireNonNull(model);
	}

	@Override
	public String toString() {
		return "Description[path=%s, type=%s, enclosure=%s]".formatted(
			path,
			type.getTypeName(),
			enclosure.getTypeName()
		);
	}

}

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
package io.jenetics.incubator.beans.internal;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import io.jenetics.incubator.beans.PathEntry;
import io.jenetics.incubator.beans.description.Description;
import io.jenetics.incubator.beans.property.Property;
import io.jenetics.incubator.beans.property.SimpleProperty;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class Filters {
	private Filters() {
	}

	public static final Predicate<? super PathEntry<? extends Type>>
		STANDARD_SOURCE_DESCRIPTION_FILTER =
		type -> {
			final var cls = type.value() instanceof ParameterizedType pt
				? (Class<?>)pt.getRawType()
				: (Class<?>)type.value();

			final var name = cls.getName();

			return
				// Allow native Java arrays, except byte[] arrays.
				(name.startsWith("[") && !name.endsWith("[B")) ||
					// Allow Java collection classes.
					Collection.class.isAssignableFrom(cls) ||
					(
						!name.startsWith("java") &&
							!name.startsWith("com.sun") &&
							!name.startsWith("sun") &&
							!name.startsWith("jdk")
					);
		};

	public static final Predicate<? super Description>
		STANDARD_TARGET_DESCRIPTION_FILTER =
		prop -> !(prop.value() instanceof Description.Value.Single &&
			prop.value().enclosure().getName().startsWith("java"));


	public static final Predicate<? super PathEntry<?>>
		STANDARD_SOURCE_FILTER =
		object -> {
			final var type = object.value() != null
				? object.value().getClass()
				: Object.class;

			return Filters.STANDARD_SOURCE_DESCRIPTION_FILTER
				.test(PathEntry.of(object.path(), type));
		};

	public static final Predicate<? super Property> STANDARD_TARGET_FILTER = prop ->
		!(prop instanceof SimpleProperty &&
			prop.value().enclosure().getClass().getName().startsWith("java"));

	public static Pattern toPattern(final String glob) {
		return Pattern.compile(
			"^" +
				Pattern.quote(glob)
					.replace("*", "\\E.*\\Q")
					.replace("?", "\\E.\\Q") +
				"$"
		);
	}

	public static Predicate<? super PathEntry<?>>
	toFilter(final Pattern pattern) {
		return object -> pattern
			.matcher(object.value() != null
				? object.value().getClass().getName()
				: "-")
			.matches();
	}

}

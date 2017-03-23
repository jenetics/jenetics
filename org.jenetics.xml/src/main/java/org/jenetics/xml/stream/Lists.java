/*
 * Java GPX Library (@__identifier__@).
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
package org.jenetics.xml.stream;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Helper methods for handling lists. All method handles null values correctly.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 1.0
 * @since 1.0
 */
public final class Lists {

	private static final Class<?> IMMUTABLE = Collections
		.unmodifiableList(new LinkedList<Object>())
		.getClass();

	private static final Class<?> IMMUTABLE_RANDOM_ACCESS = Collections
		.unmodifiableList(new ArrayList<Object>())
		.getClass();

	private Lists() {
	}

	public static <T> List<T> immutable(final List<T> list) {
		List<T> result = list;
		if (result == null) {
			result = Collections.emptyList();
		} else if (list.isEmpty()) {
			result = Collections.emptyList();
		} else if (isMutable(list)) {
			result = Collections.unmodifiableList(new ArrayList<>(list));
		}

		return result;
	}

	static boolean isImmutable(final List<?> list) {
		return
			list == null ||
			list.getClass() == IMMUTABLE ||
			list.getClass() == IMMUTABLE_RANDOM_ACCESS ||
			list.isEmpty();
	}

	static boolean isMutable(final List<?> list) {
		return !isImmutable(list);
	}

	static <T> List<T> copy(final List<T> list) {
		return list != null
			? list.isEmpty()
				? Collections.emptyList()
				: new ArrayList<T>(list)
			: Collections.emptyList();
	}

	static <T> List<T> mutable(final List<T> list) {
		List<T> result = list;
		if (result == null) {
			result = new ArrayList<>();
		} else if (isImmutable(list)) {
			result = new ArrayList<T>(list);
		}

		return result;
	}

	static <T> void copy(final List<T> source, final List<T> target) {
		requireNonNull(target);
		if (source != null) {
			source.forEach(Objects::requireNonNull);
		}

		target.clear();
		if (source != null) {
			target.addAll(source);
		}
	}

}

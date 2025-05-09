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
package io.jenetics.incubator.metamodel.property;

import java.util.Iterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import io.jenetics.incubator.metamodel.type.CollectionType;

/**
 * Base class for properties which consists of 0 to n objects.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 8.3
 * @since 8.3
 */
public sealed interface CollectionProperty
	extends EnclosingProperty, Iterable<Object>
	permits IndexedProperty, SetProperty, MapProperty
{

	default int size() {
		return ((CollectionType)type()).size().of(read()).get();
	}

	@Override
	default Iterator<Object> iterator() {
		return ((CollectionType)type()).iterable().of(read()).iterator();
	}

	default Stream<Object> stream() {
		return StreamSupport.stream(spliterator(), false);
	}

}

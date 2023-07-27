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
package io.jenetics.incubator.beans.statical;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.RecordComponent;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

/**
 * Methods for extracting <em>static</em> property {@link Description} objects,
 * directly accessible from a given data type.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class DescriptionExtractor {
	private DescriptionExtractor() {
	}

	public static Stream<Description> extract(final Class<?> type) {
		if (type == null) {
			return Stream.empty();
		}

		final var descriptions = new ArrayList<Description>();

		if (type.isArray() && !type.getComponentType().isPrimitive()) {
			descriptions.add(
				new IndexedDescription(
					"",
					type.getComponentType(), type,
					object -> object,
					Array::getLength, Array::get, Array::set
				)
			);
		} else if (List.class.isAssignableFrom(type)) {
			descriptions.add(
				new IndexedDescription(
					"",
					Object.class, List.class,
					object -> object,
					Lists::size, Lists::get, Lists::set
				)
			);
		} else if (type.isRecord()) {
			for (var component : type.getRecordComponents()) {
				descriptions.add(toDescription(component));
			}
		} else {
			try {
				final PropertyDescriptor[] descriptors = Introspector
					.getBeanInfo(type)
					.getPropertyDescriptors();

				for (var descriptor : descriptors) {
					if (descriptor.getReadMethod() != null &&
						!"class".equals(descriptor.getName()) &&
						!"declaringClass".equals(descriptor.getName()))
					{
						descriptions.add(toDescription(descriptor));
					}
				}
			} catch (IntrospectionException e) {
				throw new IllegalArgumentException(
					"Can't introspect class '%s'.".formatted(type.getName()),
					e
				);
			}
		}

		descriptions.sort(Comparator.naturalOrder());
		return descriptions.stream();
	}

	private static SimpleDescription
	toDescription(final RecordComponent component) {
		return new SimpleDescription(
			component.getName(),
			component.getType(),
			Methods.toGetter(component.getAccessor()),
			null
		);
	}

	private static Description
	toDescription(final PropertyDescriptor descriptor) {
		final Type returnType = descriptor.getReadMethod().getGenericReturnType();
		if (returnType instanceof ParameterizedType ptype) {
			if (ptype.getRawType() instanceof Class<?> cls) {
				if (List.class.isAssignableFrom(cls)) {
					final var atype = ptype.getActualTypeArguments();
					if (atype.length == 1) {
						if (atype[0] instanceof Class<?> acls) {
							return new IndexedDescription(
								descriptor.getName(),
								acls, List.class,
								Methods.toGetter(descriptor.getReadMethod()),
								Lists::size, Lists::get, Lists::set
							);
						}
					}

				}
			}
		}

		return new SimpleDescription(
			descriptor.getName(),
			descriptor.getPropertyType(),
			Methods.toGetter(descriptor.getReadMethod()),
			Methods.toSetter(descriptor.getWriteMethod())
		);
	}

}

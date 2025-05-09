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
package io.jenetics.incubator.metamodel.type;

import static java.util.Objects.requireNonNull;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import io.jenetics.incubator.metamodel.internal.Reflect;

/**
 * Trait which represents a bean type.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 8.0
 * @since 8.0
 */
public final class BeanType implements StructType, ConcreteType {
	private final Class<?> type;

	private List<ComponentType> components = null;

	BeanType(final Class<?> type) {
		this.type = requireNonNull(type);
	}

	@Override
	public Class<?> type() {
		return type;
	}

	@Override
	public synchronized List<ComponentType> components() {
		if (components == null) {
			components = components0();
		}
		return components;
	}

	private List<ComponentType> components0() {
		final PropertyDescriptor[] descriptors;
		try {
			descriptors = Introspector.getBeanInfo(type).getPropertyDescriptors();
		} catch (IntrospectionException e) {
			throw new IllegalArgumentException(
				"Can't introspect class '%s'.".formatted(type),
				e
			);
		}

		return Stream.of(descriptors)
			.filter(pd -> pd.getReadMethod() != null)
			.filter(pd -> !pd.getReadMethod().getName().equals("getClass"))
			.map(pd ->
				new ComponentType(
					pd.getName(),
					this,
					pd.getReadMethod().getGenericReturnType(),
					pd.getReadMethod(),
					pd.getWriteMethod(),
					Reflect.getAnnotations(pd).toList()
				)
			)
			.toList();
	}

	@Override
	public int hashCode() {
		return Objects.hash(type);
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof BeanType bt &&
			type.equals(bt.type);
	}

	@Override
	public String toString() {
		return "BeanType[type=%s]".formatted(type.getName());
	}


}

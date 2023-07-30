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
package io.jenetics.incubator.beans;

import static java.util.Objects.requireNonNull;
import static io.jenetics.incubator.beans.internal.Types.isIdentityType;

import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.jenetics.incubator.beans.property.Properties;
import io.jenetics.incubator.beans.property.Property;

/**
 * Wrapper class for extending objects graphs with additional navigation methods.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class ModelProperties implements Iterable<Property> {

    private final Object model;

	private volatile Data data;

	private static final class Data {
		final List<Property> properties;
		final Map<Path, Property> paths;
		final Map<Object, Path> objects;

		private Data(final List<Property> properties) {
			this.properties = requireNonNull(properties);

			paths = properties.stream()
				.collect(Collectors.toMap(
					Property::path,
					Function.identity()
				));

			objects = properties.stream()
				.filter(prop -> isIdentityType(prop.value()))
				.collect(Collectors.toMap(
					Property::value,
					Property::path,
					(p1, p2) -> p1,
					IdentityHashMap::new
				));
		}
	}

	/**
	 * Create a new metaobject wrapper for the given {@code object}.
	 *
	 * @param model the object to wrap
	 * @throws NullPointerException if the argument is {@code null}
	 */
    public ModelProperties(Object model) {
        this.model = requireNonNull(model);
    }

	private Data data() {
		Data val = data;
		if (val == null) {
			synchronized (this) {
				val = data;
				if (val == null) {
					data = val = new Data(Properties.walk(model).toList());
				}
			}
		}

		return val;
	}

	/**
	 * Return the wrapped model object.
	 *
	 * @return the wrapped model object
	 */
	public Object model() {
		return model;
	}

	/**
	 * Return the number of properties, the wrapped object graph consists of.
	 *
	 * @return the number of properties
	 */
	public int size() {
		return data().properties.size();
	}

	/**
	 * Return the property at the given {@code path}.
	 *
	 * @param path the path of the property
	 * @return the property with the given path
	 */
	public Optional<Property> get(final Path path) {
		return Optional.ofNullable(data().paths.get(path));
	}

	/**
	 * Return the path of the given {@code value}, if it is part of the object
	 * graph and an <em>identity</em> object.
	 *
	 * @param value the value to lookup in the object graph
	 * @return the object path if its part of the object graph
	 */
	public Optional<Path> pathOf(final Object value) {
		return Optional.ofNullable(data().objects.get(value));
	}

	/**
	 * Return the parent object of the given {@code value}.
	 *
	 * @param value the child value
	 * @return the parent value
	 */
	public Optional<Property> parentOf(final Object value) {
		return pathOf(value)
			.flatMap(Path::parent)
			.flatMap(this::get);
	}

	@Override
	public Iterator<Property> iterator() {
		return data().properties.iterator();
	}

	/**
	 * Return a <em>flattened</em> stream of all properties of the wrapped
	 * object.
	 *
	 * @return all properties
	 */
	public Stream<Property> stream() {
		return data().properties.stream();
	}

	@Override
	public int hashCode() {
		return model.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		return obj == this ||
			obj instanceof ModelProperties mo &&
			model.equals(mo.model);
	}

    @Override
    public String toString() {
        return "MetaObject[" + "object=" + model + ']';
    }

}

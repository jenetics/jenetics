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

import java.lang.constant.Constable;
import java.time.temporal.TemporalAccessor;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.jenetics.incubator.beans.property.Properties;
import io.jenetics.incubator.beans.property.Property;
import io.jenetics.incubator.beans.property.Property.Path;

/**
 * Wrapper class for extending objects graphs with additional navigation methods.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class MetaObject {

    private final Object object;

	private volatile List<Property> properties;
	private volatile Map<Path, Property> paths;
	private volatile Map<Object, Path> objects;

    public MetaObject(Object object) {
        this.object = requireNonNull(object);
    }

	public Object object() {
		return object;
	}

	private void init() {
		if (properties == null) {
			synchronized (this) {
				if (properties == null) {
					properties = Properties.walk(object).toList();

					paths = properties.stream()
						.collect(Collectors.toMap(
							Property::path,
							Function.identity()
						));

					objects = properties.stream()
						.filter(p -> isIdentityType(p.value()))
						.collect(Collectors.toMap(
							Property::value,
							Property::path,
							(p1, p2) -> p1,
							IdentityHashMap::new
						));
				}
			}
		}
	}

	private static boolean isIdentityType(final Object object) {
		return
			object != null &&
			!(object instanceof Constable) &&
			!(object instanceof TemporalAccessor) &&
			!(object instanceof Number);
	}

	/**
	 * Return a <em>flattened</em> stream of all properties of the wrapped
	 * object.
	 *
	 * @return all properties
	 */
    public Stream<Property> properties() {
	    init();
		return properties.stream();
    }

	/**
	 * Return the path of the given {@code value}, if it is part of the object
	 * graph and an <em>identity</em> object.
	 *
	 * @param value the value to lookup in the object graph
	 * @return the object path if part of the object graph
	 */
	public Optional<Path> pathOf(final Object value) {
		init();
		return Optional.ofNullable(objects.get(value));
	}

	/**
	 * Return the property at the given {@code path}.
	 *
	 * @param path the path of the property
	 * @return the property with the given path
	 */
	public Optional<Property> propertyAt(final Path path) {
		init();
		return Optional.ofNullable(paths.get(path));
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
			.flatMap(this::propertyAt);
	}

    @Override
    public String toString() {
        return "MetaObject[" + "object=" + object + ']';
    }

}

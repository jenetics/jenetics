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

import java.util.Collections;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.jenetics.incubator.metamodel.property.Properties;
import io.jenetics.incubator.metamodel.property.Property;

/**
 * Wrapper class for extending model graphs with additional navigation methods.
 * The bean properties of the wrapped model are evaluated lazily.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 7.2
 * @since 7.2
 */
public final class MetaModel implements Iterable<Property> {

    private final Object model;

	private volatile Data data;

	private static final class Data {
		final List<Property> properties;
		final NavigableSet<Path> paths;

		final NavigableMap<Path, Property> pathProperties;
		final Map<Object, Path> objectPaths;

		private Data(final List<Property> properties) {
			this.properties = requireNonNull(properties);

			this.paths = Collections.unmodifiableNavigableSet(
				new TreeSet<>(
					properties.stream()
						.map(Property::path)
						.toList()
				)
			);

			this.pathProperties = properties.stream()
				.collect(Collectors.toMap(
					Property::path,
					Function.identity(),
					(a, b) -> a,
					TreeMap::new
				));

			this.objectPaths = properties.stream()
				.collect(Collectors.toMap(
					p -> unwrap(p.value()),
					Property::path,
					(p1, p2) -> p1,
					IdentityHashMap::new
				));
		}

		private static Object unwrap(final Object object) {
			return object instanceof Optional<?> optional
				? optional.orElse(null)
				: object;
		}
	}

	/**
	 * Create a new metaobject wrapper for the given {@code object}.
	 *
	 * @param model the object to wrap
	 * @throws NullPointerException if the argument is {@code null}
	 */
    private MetaModel(Object model) {
        this.model = requireNonNull(model);
    }

	private Data data() {
		Data value = data;
		if (value == null) {
			synchronized (this) {
				value = data;
				if (value == null) {
					data = value = new Data(
						Properties.walk(model)
							.sorted(Comparator.comparing(Property::path))
							.toList()
					);
				}
			}
		}

		return value;
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
	 * @see #at(Path)
	 *
	 * @param path the path of the property
	 * @return the property with the given path
	 */
	public Optional<Property> get(final Path path) {
		return Optional.ofNullable(data().pathProperties.get(path));
	}

	/**
	 * Return the property with the given {@code path}.
	 *
	 * @see #get(Path)
	 *
	 * @param path the property path
	 * @return the property with the given {@code path}
	 * @throws java.util.NoSuchElementException if the property at the given
	 *         {@code path} doesn't exist
	 */
	public Property at(final Path path) {
		return get(path).orElseThrow();
	}

	/**
	 * Return the property at the given {@code path}.
	 *
	 * @see #at(String)
	 *
	 * @param path the path of the property
	 * @return the property with the given path
	 */
	public Optional<Property> get(final String path) {
		return get(Path.of(path));
	}

	/**
	 * Return the property with the given {@code path}.
	 *
	 * @see #get(String)
	 *
	 * @param path the property path
	 * @return the property with the given {@code path}
	 * @throws java.util.NoSuchElementException if the property at the given
	 *         {@code path} doesn't exist
	 */
	public Property at(final String path) {
		return get(path).orElseThrow();
	}

	/**
	 * Return the path of the given {@code value}, if it is part of the object
	 * graph and an <em>identity</em> object.
	 *
	 * @param value the value to lookup in the object graph
	 * @return the object path if its part of the object graph
	 */
	public Optional<Path> pathOf(final Object value) {
		return Optional.ofNullable(data().objectPaths.get(value));
	}

	/**
	 * Return the parent object of the given {@code value}.
	 *
	 * @param value the child value
	 * @return the parent value
	 */
	public Optional<Property> parentOf(final Object value) {
		return pathOf(value)
			.flatMap(p -> Optional.ofNullable(p.parent()))
			.flatMap(this::get);
	}

	/**
	 * Performs the given action for each entry of this model until all entries
	 * have been processed or the action throws an exception.
	 *
	 * @param action The action to be performed for each entry
	 */
	public void forEach(final BiConsumer<? super Path, ? super Property> action) {
		requireNonNull(action);

		iterator().forEachRemaining(property ->
			action.accept(property.path(), property)
		);
	}

	/**
	 * Returns a {@link Set} of the paths of this model.
	 *
	 * @return a {@link Set} of the paths of this model
	 */
	public NavigableSet<Path> paths() {
		return data().paths;
	}

	@Override
	public Iterator<Property> iterator() {
		return data().properties.iterator();
	}

	/**
	 * Return a property stream of the wrapped model object.
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
		return obj instanceof MetaModel mo &&
			model.equals(mo.model);
	}

	@Override
	public String toString() {
		return "ModelBean[" + "model=" + model + ']';
	}


	/**
	 * Return a new model-bean for the given {@code model} object.
	 *
	 * @param model the model object
	 * @return a new model-bean for the given {@code model} object
	 * @throws NullPointerException if the given {@code model} is {@code null}
	 */
	public static MetaModel of(final Object model) {
		return new MetaModel(model);
	}

}

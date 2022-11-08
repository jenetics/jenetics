package io.jenetics.incubator.property;

import static java.util.Objects.requireNonNull;
import static java.util.Spliterators.spliteratorUnknownSize;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Spliterator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import io.jenetics.incubator.property.Property.Path;

public class RecursivePropertyExtractor implements Extractor<Object, Property> {

	private final Extractor<? super DataObject, ? extends Property> properties;
	private final Extractor<? super Property, ?> flattener;

	public RecursivePropertyExtractor(
		final Extractor<DataObject, Property> properties,
		final Extractor<? super Property, ?> flattener
	) {
		this.properties = requireNonNull(properties);
		this.flattener = requireNonNull(flattener);
	}

	public RecursivePropertyExtractor(
		final Extractor<DataObject, Property> properties
	) {
		this(properties, PropertyFlattener.INSTANCE);
	}

	public RecursivePropertyExtractor() {
		this(PropertyExtractor.DEFAULT);
	}

	@Override
	public Stream<Property> extract(final Object source) {
		final Map<Object, Object> visited = new IdentityHashMap<>();
		return stream(new DataObject(Path.EMPTY, source), visited);
	}

	private Stream<Property> stream(
		final DataObject object,
		final Map<Object, Object> visited
	) {
		if (object == null) {
			return Stream.empty();
		}

		final boolean exists;
		synchronized(visited) {
			if (!(exists = visited.containsKey(object))) {
				visited.put(object, "");
			}
		}

		if (exists) {
			return Stream.empty();
		} else {
			final var it = new PropertyPreOrderIterator(object, properties);
			final var sp = spliteratorUnknownSize(it, Spliterator.SIZED);

			return StreamSupport.stream(sp, false)
				.flatMap(prop ->
					Stream.concat(
						Stream.of(prop),
						flatten(prop, visited)
					)
				);
		}
	}

	private Stream<Property> flatten(
		final Property property,
		final Map<Object, Object> visited
	) {
		final var index = new AtomicInteger();

		return flattener.extract(property)
			.flatMap(ele -> {
				final Path path = property.path()
					.indexed(index.getAndIncrement());

				final Property parent = null;
				/*
				new ReadonlyPropertyRecord(
					property.value(),
					path,
					ele != null ? ele.getClass() : Object.class,
					ele
				);

				 */

				return Stream.concat(
					Stream.of(parent),
					stream(
						new DataObject(path, ele),
						visited
					)
				);
			});
	}

}

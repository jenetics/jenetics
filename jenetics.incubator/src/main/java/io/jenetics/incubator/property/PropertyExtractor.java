package io.jenetics.incubator.property;

import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public class PropertyExtractor implements Extractor<DataObject, Property> {

	public static final PropertyExtractor DEFAULT =
		new PropertyExtractor(PropertyDescriptionExtractor.INSTANCE);

	private final Extractor<? super Class<?>, ? extends PropertyDescription> descriptions;

	public PropertyExtractor(
		final Extractor<
			? super Class<?>,
			? extends PropertyDescription> descriptions
	) {
		this.descriptions = requireNonNull(descriptions);
	}

	@Override
	public Stream<Property> extract(final DataObject object) {
		requireNonNull(object);

		if (object.value() != null) {
			return descriptions
				.extract(object.value().getClass())
				.map(desc -> {
					final var enclosing = object.value();
					final var path = object.path().append(desc.name());
					final var type = desc.type();
					final var value = desc.read(object.value());

					if (type.isArray() &&
						!type.getComponentType().isPrimitive())
					{
						return new ArrayProperty(desc, enclosing, path, value);
					}
					if (List.class.isAssignableFrom(type)) {
						return new ListProperty(desc, enclosing, path, value);
					}
					if (Set.class.isAssignableFrom(type)) {
						return new SetProperty(desc, enclosing, path, value);
					}
					if (Collection.class.isAssignableFrom(type)) {
						return new CollectionProperty(desc, enclosing, path, value);
					}
					if (Iterable.class.isAssignableFrom(type)) {
						return new IterableProperty(desc, enclosing, path, value);
					}
					if (Map.class.isAssignableFrom(type)) {
						return new MapProperty(desc, enclosing, path, value);
					}

					return new SimpleProperty(desc, enclosing, path, value);
				});
		} else {
			return Stream.empty();
		}
	}

	public Stream<Property> properties(final Object value) {
		final var data = value instanceof DataObject object
			? object
			: new DataObject(value);

		return extract(data);
	}

}

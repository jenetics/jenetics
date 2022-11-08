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
						final var array = (Object[])value;
						return new ArrayProperty(enclosing, path, type, array);
					}
					if (List.class.isAssignableFrom(type)) {
						final var list = (List<?>)value;
						return new ListProperty(enclosing, path, type, list);
					}
					if (Set.class.isAssignableFrom(type)) {
						final var set = (Set<?>)value;
						return new SetProperty(enclosing, path, type, set);
					}
					if (Collection.class.isAssignableFrom(type)) {
						final var coll = (Collection<?>)value;
						return new CollectionProperty(enclosing, path, type, coll);
					}
					if (Map.class.isAssignableFrom(type)) {
						final var map = (Map<?, ?>)value;
						return new MapProperty(enclosing, path, type, map);
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

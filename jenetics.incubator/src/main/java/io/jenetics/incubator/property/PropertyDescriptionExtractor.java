package io.jenetics.incubator.property;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Stream;

public enum PropertyDescriptionExtractor
	implements Extractor<Class<?>, PropertyDescription>
{
	INSTANCE;

	@Override
	public Stream<PropertyDescription> extract(final Class<?> type) {
		final var descriptions = new ArrayList<PropertyDescription>();

		if (type.isRecord()) {
			for (var component : type.getRecordComponents()) {
				descriptions.add(
					new PropertyDescription(
						component.getName(),
						component.getType(),
						component.getAccessor(),
						null
					)
				);
			}
		} else {
			try {
				final PropertyDescriptor[] descriptors = Introspector
					.getBeanInfo(type)
					.getPropertyDescriptors();

				for (var descriptor : descriptors) {
					if (descriptor.getReadMethod() != null &&
						!"class".equals(descriptor.getName()))
					{
						descriptions.add(
							new PropertyDescription(
								descriptor.getName(),
								descriptor.getPropertyType(),
								descriptor.getReadMethod(),
								descriptor.getWriteMethod()
							)
						);
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

}

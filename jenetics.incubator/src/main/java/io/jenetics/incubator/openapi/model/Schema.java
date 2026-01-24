package io.jenetics.incubator.openapi.model;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

import java.util.List;
import java.util.Map;
import java.util.Set;

public sealed interface Schema {

	Type type();

	record Primitive(String name, Type type) implements Schema {
	}

	sealed interface Complex extends Schema {
		Set<Property> properties();

		@Override
		default Type.Obj type() {
			return new Type.Obj();
		}
	}

	record Inline(Set<Property> properties) implements Complex {

		public Inline {
			properties = Set.copyOf(properties);
		}

		public Named withName(String name) {
			return new Named(name, properties);
		}
	}

	record Named(String name, Set<Property> properties) implements Complex {
		public Named {
			properties = Set.copyOf(properties);
		}
	}

	record AllOf(Set<Complex> schemas, Set<Property> properties) implements Complex {
		public AllOf {
			properties = Set.copyOf(properties);
		}
	}

	static Inline allOf(Set<? extends Complex> schemas) {
		final List<Property> properties = schemas.stream()
			.flatMap(schema -> schema.properties().stream())
			.toList();

		final List<String> duplicates = properties.stream()
			.collect(groupingBy(Property::name, counting()))
			.entrySet().stream()
			.filter(entry -> entry.getValue() > 1)
			.map(Map.Entry::getKey)
			.toList();

		if (!duplicates.isEmpty()) {
			throw new IllegalArgumentException(
				"Duplicate properties: " + duplicates
			);
		}

		return new Inline(Set.copyOf(properties));
	}

}

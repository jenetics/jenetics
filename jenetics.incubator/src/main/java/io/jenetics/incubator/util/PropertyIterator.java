package io.jenetics.incubator.util;

import static java.util.Objects.requireNonNull;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import io.jenetics.incubator.util.PropertyIterator.Property;

final class PropertyIterator implements Iterator<Property> {

	public record Property(String path, Object value) {}

	private final Deque<Iterator<Property>> _deque = new ArrayDeque<>();
	private final Map<Object, Object> _visited = new IdentityHashMap<>();

	PropertyIterator(final Object root) {
		requireNonNull(root);
		_deque.push(properties("", root));
	}

	@Override
	public boolean hasNext() {
		final Iterator<Property> peek = _deque.peek();
		return peek != null && peek.hasNext();
	}

	@Override
	public Property next() {
		final Iterator<Property> it = _deque.peek();
		if (it == null) {
			throw new NoSuchElementException("No next element.");
		}

		final Property node = it.next();
		if (!it.hasNext()) {
			_deque.pop();
		}

		final Iterator<Property> children = properties(node.path(), node.value());
		if (children.hasNext()) {
			_deque.push(children);
		}

		return node;
	}

	private Iterator<Property> properties(final String path, final Object root) {
		final var properties = new ArrayList<Property>();
		collect(path, root, properties, _visited);
		return properties.iterator();
	}

	private static <T> void collect(
		final String path,
		final Object object,
		final List<Property> properties,
		final Map<Object, Object> visited
	) {
		try {
			if (object != null && !visited.containsKey(object)) {
				visited.put(object, "");

				properties.add(new Property(path, object));

				// Collect the objects with the correct type.
				descriptors(object.getClass())
					.flatMap(d -> getFlattenedValues(object, path, d))
					.filter(d -> !visited.containsKey(d.value()))
					.peek(v -> visited.put(v.value(), ""))
					.forEach(properties::add);
			}
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

	private static Stream<Property> getFlattenedValues(
		final Object object,
		final String path,
		final PropertyDescriptor desc
	) {
		try {
			final var newPath = path.isEmpty()
				? desc.getName()
				: path + "." + desc.getName();

			final var result = desc.getReadMethod().invoke(object);
			if (result instanceof Collection<?> col) {
				return col.stream()
					.map(v -> new Property(newPath, v));
			} else if (result != null &&
						result.getClass().isArray() &&
						!result.getClass().getComponentType().isPrimitive())
			{
				return IntStream.range(0, Array.getLength(result))
					.mapToObj(i -> Array.get(result, i))
					.map(v -> new Property(newPath, v));
			} else if (result != null) {
				return Stream.of(new Property(newPath, result));
			} else {
				return Stream.empty();
			}
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

	private static Stream<PropertyDescriptor> descriptors(final Class<?> type) {
		try {
			return Stream.of(Introspector.getBeanInfo(type).getPropertyDescriptors())
				.filter(pd -> pd.getReadMethod() != null);
		} catch (IntrospectionException e) {
			throw new IllegalArgumentException("Can't introspect Object.");
		}
	}

	public static void main(final String[] args) {
		record Foo(String a, int b, double c) {}


		final var it = new PropertyIterator(new Foo("asdf", 1, 4.44));
		while (it.hasNext()) {
			System.out.println(it.next());
		}
	}

}

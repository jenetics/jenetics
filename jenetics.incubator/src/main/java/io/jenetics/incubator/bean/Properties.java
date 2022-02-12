package io.jenetics.incubator.bean;

import static java.util.Spliterators.spliteratorUnknownSize;

import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Spliterator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Contains helper method for getting all properties of a given root bean.
 */
public final class Properties {
    private Properties() {
    }

	/**
	 * Return a Stream that is lazily populated with bean properties by walking
	 * the object graph rooted at a given starting object. The object tree is
	 * traversed in pre-order.
	 *
	 * @param root the root of the object tree
	 * @param filter the <em>bean</em> class filter
	 * @param options the visit options
	 * @return the property stream, containing all transitive properties of the
	 *         given root object
	 */
	public static Stream<Property> walk(
		final Object root,
		final Predicate<? super Class<?>> filter,
		final VisitOption... options
	) {
		final Map<Object, Object> visited = new IdentityHashMap<>();
		return walk(null, root, filter, visited, options);
	}

    private static Stream<Property> walk(
        final String basePath,
        final Object root,
		final Predicate<? super Class<?>> filter,
		final Map<Object, Object> visited,
        final VisitOption... options
    ) {
        if (root != null && filter.test(root.getClass())) {
            final var it = new PropertyIterator(basePath, root, Beans::properties);
            final var sp = spliteratorUnknownSize(it, Spliterator.SIZED);

            final var result = StreamSupport.stream(sp, false)
                .filter(p -> p.type() != Class.class)
                .filter(p -> {
                    synchronized (visited) {
                        final var visit = visited.containsKey(p.object());
                        visited.put(p.object(), "");
                        return visit;
                    }
                });

            return shouldFlatten(options)
                ? result.flatMap(p -> Properties.flatten(p, filter, visited, options))
                : result.map(Property.class::cast);
        } else {
            return Stream.empty();
        }
    }

    private static boolean shouldFlatten(final VisitOption... options) {
        for (var option : options) {
            if (option == VisitOption.UNPACK_COLLECTIONS) {
                return true;
            }
        }

        return false;
    }

    private static Stream<Property> flatten(
		final Property property,
		final Predicate<? super Class<?>> filter,
		final Map<Object, Object> visited,
		final VisitOption... options
	) {
        if (property.value() instanceof final Collection<?> coll) {
			final var index = new AtomicInteger();
            final var flattened = coll.stream()
                .flatMap(ele ->
					walk(
						indexed(property.path(), index),
						ele,
						filter,
						visited,
						options
					)
				);

            return Stream.concat(
                Stream.of(property),
                flattened
            );
        } else {
            return Stream.of(property);
        }
    }

    private static String indexed(final String basePath, final AtomicInteger index) {
        return String.format("%s[%s]", basePath, index.getAndIncrement());
    }

}

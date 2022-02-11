package io.jenetics.incubator.bean;

import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Spliterator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.Spliterators.spliteratorUnknownSize;

/**
 * Contains helper method for getting all properties of a given root bean.
 */
public final class Properties {
    private Properties() {
    }

	public static Stream<Property> walk(
		final Object root,
		final Predicate<? super Class<?>> filter,
		final VisitOption... options
	) {
		return walk(null, root, filter, options);
	}

    private static Stream<Property> walk(
        final String basePath,
        final Object root,
		final Predicate<? super Class<?>> filter,
        final VisitOption... options
    ) {
        final Map<Object, Object> visited = new IdentityHashMap<>();

        if (root != null && filter.test(root.getClass())) {
            final var it = new PropertyIterator(basePath, root, filter) {
				@Override
				Iterator<Property> next(final String basePath, final Object parent) {
					return Beans.properties(basePath, parent).iterator();
				}
			};
            final var sp = spliteratorUnknownSize(it, Spliterator.SIZED);

            final var result = StreamSupport.stream(sp, false)
                .filter(p -> p.type() != Class.class)
                .filter(p -> {
                    synchronized (visited) {
                        final var visit = visited.containsKey(p.parent());
                        visited.put(p.parent(), "");
                        return visit;
                    }
                });

            return shouldFlatten(options)
                ? result.flatMap(p -> Properties.flatten(p, filter, options))
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
		final VisitOption... options
	) {
        if (property.value() instanceof final Collection<?> coll) {
			final var index = new AtomicInteger();
            final var flattened = coll.stream()
                .flatMap(ele -> walk(
					indexed(property.path(), index),
					ele,
					filter,
					options)
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

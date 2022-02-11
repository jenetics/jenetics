package io.jenetics.incubator.bean;

import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Spliterator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.Spliterators.spliteratorUnknownSize;

public final class Properties {
    private Properties() {
    }

    private static Stream<Property> stream(
        final String basePath,
        final Object root,
        final VisitOption... options
    ) {
        final Map<Object, Object> visited = new IdentityHashMap<>();

        if (root != null) {
            final var it = new PropertyIterator(basePath, root);
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
                ? result.flatMap(p -> Properties.flatten(p, options))
                : result.map(Property.class::cast);
        } else {
            return Stream.empty();
        }
    }

    public static Stream<Property> stream(
		final Object root,
		final VisitOption... options
	) {
        return stream(null, root, options);
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
		final VisitOption... options
	) {
        if (property.value() instanceof final Collection<?> coll) {
			final var index = new AtomicInteger();
            final var flattened = coll.stream()
                .flatMap(ele -> stream(indexed(property.path(), index), ele, options));

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

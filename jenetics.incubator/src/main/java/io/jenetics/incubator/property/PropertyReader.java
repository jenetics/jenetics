package io.jenetics.incubator.property;

import java.util.function.Predicate;
import java.util.stream.Stream;

import io.jenetics.incubator.property.Property.Path;

/**
 * This interface is responsible for reading the properties of a given
 * {@code object}.
 */
@FunctionalInterface
interface PropertyReader {

	/**
	 * The default property reader, using the bean introspector class.
	 */
	PropertyReader DEFAULT = Properties::stream;

	/**
	 * Reads the properties from the given {@code object}. The
	 * {@code basePath} is needed for building the <em>full</em> path of
	 * the read properties. Both arguments may be {@code null}.
	 *
	 * @param basePath the base path of the read properties
	 * @param object   the object from where to read its properties
	 * @return the object's properties
	 */
	Stream<Property> read(final Path basePath, final Object object);

	/**
	 * Create a new reader which filters specific object from the property
	 * read.
	 *
	 * @param filter the object filter applied to the reader
	 * @return a new reader with the applied filter
	 */
	default PropertyReader filter(final Predicate<? super Object> filter) {
		return (basePath, object) -> {
			if (filter.test(object)) {
				return read(basePath, object);
			} else {
				return Stream.empty();
			}
		};
	}

	/**
	 * Create a new reader which reads the properties only from the given
	 * packages.
	 *
	 * @param includes the base packages of the object where the properties
	 *                 are read from
	 * @return a new reader which reads the properties only from the given
	 * packages
	 */
	default PropertyReader filterPackages(final String... includes) {
		return filter(object -> {
			if (object != null) {
				if (includes.length == 0) {
					return true;
				}

				final var pkg = object.getClass().getPackage().getName();
				for (var p : includes) {
					if (pkg.startsWith(p)) {
						return true;
					}
				}
			}

			return false;
		});
	}
}

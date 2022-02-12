package io.jenetics.incubator.bean;

import java.util.stream.Stream;

/**
 * Represents an object's property. A property might be defined as usual
 * <em>bean</em> property, with getter and setter, or as record component.
 */
public interface Property {

	/**
	 * This interface is responsible for reading the properties of a given
	 * {@code object}.
	 */
	@FunctionalInterface
	interface Reader {

		/**
		 * Reads the properties from the given {@code object}. The
		 * {@code basePath} is needed for building the <em>full</em> path of
		 * the read properties. Both arguments may be {@code null}.
		 *
		 * @param basePath the base path of the read properties
		 * @param object the object from where to read its properties
		 * @return the object's properties
		 */
		Stream<Property> read(final String basePath, final Object object);
	}

	@FunctionalInterface
	interface Flattener {
		Stream<Property> flatten(final Property property);
	}

	record Path() {
		public boolean matches(final String pattern) {
			return false;
		}
	}


	/**
	 * Returns the object which contains {@code this} property.
	 *
	 * @return the object which contains {@code this} property
	 */
	Object object();

	/**
	 * The full path, separated with dots '.', of {@code this} property from
	 * the <em>root</em> object.
	 *
	 * @return the full property path
	 */
	String path();

	/**
	 * The property type.
	 *
	 * @return the property type
	 */
	Class<?> type();

	/**
	 * The name of {@code this} property
	 *
	 * @return the property name
	 */
	String name();

	/**
	 * The initial, cached property value, might be {@code null}.
	 *
	 * @return the initial, cached property value
	 */
	Object value();

	/**
	 * Read the current property value. Might differ from {@link #value()} if
	 * the underlying (mutable) object has been changed.
	 *
	 * @return the current property value
	 */
	Object read();

	/**
	 * Changes the property value.
	 *
	 * @param value the new property value
	 * @return {@code true} if the value has been changed successfully,
	 *         {@code false} if the property is not mutable
	 */
	boolean write(final Object value);



	/* *************************************************************************
	 * Helper implementations
	 * ************************************************************************/

}

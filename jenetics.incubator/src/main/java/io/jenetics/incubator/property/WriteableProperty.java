package io.jenetics.incubator.property;

public non-sealed interface WriteableProperty extends Property {

	/**
	 * Read the current property value. Might differ from {@link #value()} if
	 * the underlying (mutable) object has been changed.
	 *
	 * @return the current property value
	 */
	default Object read() {
		return value();
	}

	/**
	 * Changes the property value.
	 *
	 * @param value the new property value
	 * @return {@code true} if the value has been changed successfully,
	 *         {@code false} if the property value couldn't be changed
	 */
	default boolean write(final Object value) {
		return false;
	}

}

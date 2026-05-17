package io.jenetics.incubator.csv;

public interface StringFormat {

	/**
	 * Returns the string representation of the given {@code value}. The returned
	 * string must be parsable to the original {@code value} with the
	 * {@link #parse(String, Class)} method.
	 *
	 * @param value the value to <em>format</em>
	 * @return the string format of the given value
	 */
	String format(Object value);

	default String[] format(Object[] components) {
		final String[] values = new String[components.length];
		for (int i = 0; i < components.length; ++i) {
			values[i] = format(components[i]);
		}
		return values;
	}

	/**
	 * Convert the given string {@code value} to the desired {@code type}. If
	 * the given input {@code value} is {@code null}, the parser method
	 * also returns {@code null}.
	 *
	 * @param value the string value to convert
	 * @param type the target type
	 * @return the converted string value
	 * @param <T> the target type
	 * @throws UnsupportedOperationException if the conversion target {@code type}
	 *         is not supported
	 * @throws RuntimeException if the {@code value} can't be converted. This is
	 *         the exception thrown by the registered parser function.
	 */
	<T> T parse(String value, Class<T> type);

}

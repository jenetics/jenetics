package io.jenetics.incubator.csv;

@FunctionalInterface
public interface StringParser {

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

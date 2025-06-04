package io.jenetics.distassert.assertion;

import static java.lang.Math.abs;

/**
 * Encapsulates the context settings which describe certain rules for numerical
 * operations.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since !__version__!
 * @version !__version__!
 */
final class NumericalContext {

	static final NumericalContext CONTEXT = new NumericalContext(Math.pow(10, -3));

	private final double epsilon;

	/**
	 * Create a new numerical context with the given epsilon.
	 *
	 * @param epsilon the {@code epsilon} of this context
	 */
	public NumericalContext(double epsilon) {
		this.epsilon = abs(epsilon);
	}

	/**
	 * Return the epsilon value used in this numerical context.
	 *
	 * @return the epsilon value used in this numerical context
	 */
	public double epsilon() {
		return epsilon;
	}

	/**
	 * Checks if the given two {@code double} values are equal, obeying the
	 * defined {@link #epsilon()}.
	 *
	 * @param a the first value to compare
	 * @param b the second value to compare
	 * @return {@code true} if the given values are equal, modulo the given
	 *         {@link #epsilon()}, {@code false} otherwise
	 */
	public boolean equals(double a, double b) {
		return Double.compare(a, b) == 0 || abs(a - b) <= epsilon();
	}

	/**
	 * Tests whether the given value {@code a} is greater than zero.
	 *
	 * @param a the value to test
	 * @return {@code true} if the given value is greater than zero, {@code false}
	 *         otherwise
	 */
	public boolean isGreaterZero(double a) {
		return abs(a) > epsilon() && Double.compare(a, 0.0) > 0;
	}

	/**
	 * Tests whether the given value {@code a} is smaller than zero.
	 *
	 * @param a the value to test
	 * @return {@code true} if the given value is smaller than zero, {@code false}
	 *         otherwise
	 */
	public boolean isSmallerZero(double a) {
		return abs(a) > epsilon() && Double.compare(a, 0.0) < 0;
	}

	/**
	 * Tests whether the given double value is zero, according to the defined
	 * {@link #epsilon()}.
	 *
	 * @param a the value to test
	 * @return {@code true} if the given value is (near) zero, {@code false}
	 *         otherwise
	 */
	public boolean isZero(double a) {
		return equals(a, 0);
	}

	/**
	 * Tests whether the given double value is not zero, according to the defined
	 * {@link #epsilon()}.
	 *
	 * @param a the value to test
	 * @return {@code true} if the given value is not (near) zero, {@code false}
	 *         otherwise
	 */
	public boolean isNotZero(double a) {
		return !isZero(a);
	}

	/**
	 * Tests whether the given double value is one, according to the defined
	 * {@link #epsilon()}.
	 *
	 * @param a the value to test
	 * @return {@code true} if the given value is (near) one, {@code false}
	 *         otherwise
	 */
	public boolean isOne(double a) {
		return equals(a, 1);
	}


	@Override
	public String toString() {
		return "NumericalContext[epsilon=%f]".formatted(epsilon);
	}

}

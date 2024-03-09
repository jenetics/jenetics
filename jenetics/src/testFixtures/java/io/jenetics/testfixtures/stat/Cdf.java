package io.jenetics.testfixtures.stat;

/**
 * The cumulative distribution function.
 */
@FunctionalInterface
public interface Cdf {
	double apply(double value);
}
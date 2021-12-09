package io.jenetics.incubator.grammar;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.random.RandomGenerator;
import java.util.stream.IntStream;

public class TrackingCodons implements SymbolIndex {

	private final IntStream.Builder _values;

	private final RandomGenerator _random;
	private final AtomicInteger _pos = new AtomicInteger(0);

	public TrackingCodons(final RandomGenerator random) {
		_random = random;
		_values = IntStream.builder();
	}

	public int[] values() {
		return _values.build().toArray();
	}

	@Override
	public int next(final int bound) {
		if (bound < 1) {
			throw new IllegalArgumentException(
				"Bound is smaller than 1: " + bound
			);
		}

		final int value = _random.nextInt(256);
		_values.accept(value);

		return value%bound;
	}

}

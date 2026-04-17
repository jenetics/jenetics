package io.jenetics.incubator.randomizer;

import java.util.concurrent.atomic.AtomicLong;

@FunctionalInterface
public interface Randomizer<T> {

	T next(long seed);

	default T next() {
		record Holder() {
			static final AtomicLong SEED = new AtomicLong(System.nanoTime());
		}
		return next(Holder.SEED.incrementAndGet());
	}

	static <T> Randomizer<T> of(Class<T> type) {
		return RandomizerSupplierProvider.get(type).get(type);
	}

}

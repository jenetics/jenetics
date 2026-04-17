package io.jenetics.incubator.randomizer;

import org.instancio.Instancio;

final class InstancioProvider extends RandomizerSupplierProvider {

	private static final InstancioSupplier SUPPLIER = new InstancioSupplier();

	@Override
	public RandomizerSupplier supplier() {
		return SUPPLIER;
	}

	static final class InstancioSupplier implements RandomizerSupplier {
		@Override
		public <T> Randomizer<T> get(Class<T> type) {
			return seed -> Instancio.of(type)
				.withSeed(seed)
				.create();
		}
	}

}

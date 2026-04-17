package io.jenetics.incubator.randomizer;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.ServiceLoader;

public abstract class RandomizerSupplierProvider {

	public abstract RandomizerSupplier supplier();

	public static RandomizerSupplier get(Class<?> type) {
		final var providers = getProviders();
		if (providers.isEmpty()) {
			throw new NoSuchElementException("No SamplerSupplier for type " + type);
		}
		if (providers.size() != 1) {
			throw new IllegalStateException("Multiple SamplerSupplier for type " + type);
		}
		return providers.getFirst().supplier();
	}

	private static List<RandomizerSupplierProvider> getProviders() {
		return ServiceLoader.load(RandomizerSupplierProvider.class).stream()
			.map(ServiceLoader.Provider::get)
			.toList();
	}

}

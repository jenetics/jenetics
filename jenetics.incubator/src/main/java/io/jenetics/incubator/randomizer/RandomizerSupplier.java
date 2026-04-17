package io.jenetics.incubator.randomizer;

public interface RandomizerSupplier {

	<T> Randomizer<T> get(Class<T> type);

}

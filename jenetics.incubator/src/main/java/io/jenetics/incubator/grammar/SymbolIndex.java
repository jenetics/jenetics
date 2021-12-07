package io.jenetics.incubator.grammar;

@FunctionalInterface
public interface SymbolIndex {
	int next(final int bound);
}

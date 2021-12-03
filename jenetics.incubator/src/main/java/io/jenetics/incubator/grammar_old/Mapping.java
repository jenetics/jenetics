package io.jenetics.incubator.grammar_old;

import java.util.List;

import io.jenetics.Gene;
import io.jenetics.Genotype;
import io.jenetics.incubator.grammar_old.Grammar.Terminal;

@FunctionalInterface
public interface Mapping<G extends Gene<?, G>> {
	List<Terminal> map(final Genotype<G> genotype);
}

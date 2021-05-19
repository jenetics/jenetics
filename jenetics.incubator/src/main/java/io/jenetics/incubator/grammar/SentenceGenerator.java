package io.jenetics.incubator.grammar;

import java.util.List;
import java.util.Random;

import io.jenetics.incubator.grammar.Grammar.Terminal;

@FunctionalInterface
public interface SentenceGenerator {

	List<Terminal> generate(final Grammar grammar, final SymbolIndex index);

	default List<Terminal> generate(final Grammar grammar, final Random random) {
		return generate(grammar, random::nextInt);
	}

}

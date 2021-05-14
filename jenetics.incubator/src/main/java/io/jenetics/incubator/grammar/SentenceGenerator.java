package io.jenetics.incubator.grammar;

import java.util.List;
import java.util.Random;

@FunctionalInterface
public interface SentenceGenerator {

	List<Grammar.Terminal> generate(
		final Grammar grammar,
		final Index index
	);

	default List<Grammar.Terminal> generate(
		final Grammar grammar,
		final Random random
	) {
		return generate(grammar, random::nextInt);
	}

}

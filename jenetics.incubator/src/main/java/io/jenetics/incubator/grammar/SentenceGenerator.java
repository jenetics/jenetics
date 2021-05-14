package io.jenetics.incubator.grammar;

import java.util.List;
import java.util.Random;
import java.util.function.IntUnaryOperator;

public interface SentenceGenerator {

	List<Grammar.Terminal> generate(
		final Grammar grammar,
		final IntUnaryOperator index
	);

	default List<Grammar.Terminal> generate(
		final Grammar grammar,
		final Random random
	) {
		return generate(grammar, random::nextInt);
	}

}

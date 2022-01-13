/*
 * Java Genetic Algorithm Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Author:
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmail.com)
 */
package io.jenetics.incubator.grammar;

import static java.util.Objects.requireNonNull;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import io.jenetics.incubator.grammar.Cfg.NonTerminal;
import io.jenetics.incubator.grammar.Cfg.Symbol;
import io.jenetics.incubator.grammar.Cfg.Terminal;

/**
 * Standard implementation of a sentence generator. The generator can generate
 * sentences in a {@link Expansion#LEFT_FIRST} order or from
 * {@link Expansion#LEFT_TO_RIGHT}.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 7.0
 * @version 7.0
 */
public final class StandardSentenceGenerator implements SentenceGenerator {

	/**
	 * Defines the expansion strategy used when generating the sentences.
	 */
	public enum Expansion {

		/**
		 * The symbol replacement always starting from the leftmost nonterminal
		 * as described in
		 * <a href="https://www.brinckerhoff.org/tmp/grammatica_evolution_ieee_tec_2001.pdf">
		 * Grammatical Evolution</a>.
		 */
		LEFT_FIRST,

		/**
		 * The symbol replacement is performed from left to right and is repeated
		 * until all non-terminal symbol has been expanded.
		 */
		LEFT_TO_RIGHT;
	}

	private final SymbolIndex _index;
	private final Expansion _expansion;
	private final int _limit;

	/**
	 * Create a new sentence generator from the given parameters.
	 *
	 * @param index the symbol index function used for generating the sentences
	 * @param expansion the sentence generation strategy to use for generating
	 *        the sentences
	 * @param limit the maximal allowed sentence length. If the generated
	 *        sentence exceeds this length, the generation is interrupted and
	 *        an empty sentence (empty list) is returned.
	 */
	public StandardSentenceGenerator(
		final SymbolIndex index,
		final Expansion expansion,
		final int limit
	) {
		_index = requireNonNull(index);
		_expansion = requireNonNull(expansion);
		_limit = limit;
	}

	/**
	 * Generates a new sentence from the given grammar, <em>cfg</em>.
	 * <p>
	 * The following code snippet shows how to create a random sentence from a
	 * given grammar:
	 * <pre>{@code
	 * final Cfg cfg = Bnf.parse("""
	 *     <expr> ::= ( <expr> <op> <expr> ) | <num> | <var> |  <fun> ( <arg>, <arg> )
	 *     <fun>  ::= FUN1 | FUN2
	 *     <arg>  ::= <expr> | <var> | <num>
	 *     <op>   ::= + | - | * | /
	 *     <var>  ::= x | y
	 *     <num>  ::= 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9
	 *     """
	 * );
	 *
	 * final RandomGenerator random = RandomGenerator.of("L64X256MixRandom");
	 * final List<Terminal> sentence = Sentence.generate(
	 *     cfg, random::nextInt, 1_000
	 * );
	 * final String string = sentence.stream()
	 *     .map(Symbol::value)
	 *     .collect(Collectors.joining());
	 *
	 * System.out.println(string);
	 * }</pre>
	 * <em>Some sample output:</em>
	 * <pre>{@code
	 * > ((x-FUN1(5,5))+8)
	 * > (FUN2(y,5)-FUN2(0,x))
	 * > x
	 * > FUN2(x,x)
	 * > 5
	 * > FUN2(y,FUN2((FUN1(5,FUN1(y,2))*9),y))
	 * > ((FUN1(x,5)*9)*(x/(y*FUN2(x,y))))
	 * > (9-(y*(x+x)))
	 * > }</pre>
	 *
	 * @param cfg the generating grammar
	 * @return a newly created terminal list (sentence), or an empty list if
	 *         the length of the sentence exceed the defined sentence limit
	 */
	@Override
	public List<Terminal> generate(final Cfg cfg) {
		final var sentence = new LinkedList<Symbol>();
		expand(cfg, _index, sentence, _expansion, _limit);

		return sentence.stream()
			.map(Terminal.class::cast)
			.toList();
	}

	private static void expand(
		final Cfg cfg,
		final SymbolIndex index,
		final List<Symbol> symbols,
		final Expansion expansion,
		final int limit
	) {
		symbols.add(cfg.start());

		boolean proceed;
		do {
			proceed = false;

			final ListIterator<Symbol> sit = symbols.listIterator();
			while (sit.hasNext() &&
				(expansion == Expansion.LEFT_TO_RIGHT || !proceed))
			{
				if (sit.next() instanceof NonTerminal nt) {
					sit.remove();
					expand(cfg, nt, index).forEach(sit::add);
					proceed = true;
				}
			}

			if (symbols.size() > limit) {
				symbols.clear();
				proceed = false;
			}
		} while (proceed);
	}

	private static List<Symbol> expand(
		final Cfg cfg,
		final NonTerminal symbol,
		final SymbolIndex index
	) {
		return cfg.rule(symbol)
			.map(rule -> rule.alternatives()
				.get(index.next(rule, rule.alternatives().size()))
				.symbols())
			.orElse(List.of());
	}

}

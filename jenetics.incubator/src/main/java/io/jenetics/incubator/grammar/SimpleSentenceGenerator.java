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

public final class SimpleSentenceGenerator implements SentenceGenerator {

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

	public SimpleSentenceGenerator(
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
	 * final List<Terminal> sentence = Sentences.generate(
	 *     cfg, random::nextInt, LEFT_TO_RIGHT, 1_000
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
	 * @return a newly created terminal list (sentence)
	 */
	@Override
	public List<Terminal> generate(final Cfg cfg) {
		final var sentence = new LinkedList<Symbol>();
		expand(cfg, _index, sentence, _limit);

		return sentence.stream()
			.map(Terminal.class::cast)
			.toList();
	}

	private void expand(
		final Cfg cfg,
		final SymbolIndex index,
		final List<Symbol> symbols,
		final int limit
	) {
		symbols.add(cfg.start());

		boolean proceed;
		do {
			proceed = false;

			final ListIterator<Symbol> sit = symbols.listIterator();
			while (sit.hasNext() && (_expansion == Expansion.LEFT_TO_RIGHT || !proceed)) {
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
				.get(index.next(rule))
				.symbols())
			.orElse(List.of(symbol));
	}

}

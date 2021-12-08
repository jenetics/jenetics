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

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import io.jenetics.incubator.grammar.Cfg.NonTerminal;
import io.jenetics.incubator.grammar.Cfg.Rule;
import io.jenetics.incubator.grammar.Cfg.Symbol;
import io.jenetics.incubator.grammar.Cfg.Terminal;

/**
 * Standard implementation for creating sentences from a given grammar.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since !__version__!
 * @version !__version__!
 */
public final class Sentences {

	private Sentences() {
	}

	/**
	 * Generates a new sentence from the given grammar, <em>cfg</em>. The
	 * strategy, of which symbol index should be taken, is defined by the given
	 * symbol {@code index} object. The replacement always starting from the
	 * leftmost nonterminal as described in
	 * <a href="https://www.brinckerhoff.org/tmp/grammatica_evolution_ieee_tec_2001.pdf">
	 * Grammatical Evolution</a>.
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
	 * final List<Terminal> sentence = Sentences.generate(cfg, random::nextInt);
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
	 * @param index the symbol index strategy
	 * @return a newly created terminal list (sentence)
	 */
	public static List<Terminal> generate(final Cfg cfg, final SymbolIndex index) {
		return leftFirstExpansion(cfg, index, new LinkedList<>());
	}

	static List<Terminal> leftFirstExpansion(
		final Cfg cfg,
		final SymbolIndex index,
		final List<Symbol> symbols
	) {
		final NonTerminal start = cfg.start();
		symbols.addAll(expand(cfg, start, index));

		boolean expanded = true;
		while (expanded) {
			expanded = false;

			// Always starting the replacement from the leftmost nonterminal.
			final ListIterator<Symbol> sit = symbols.listIterator();
			while (sit.hasNext() && !expanded) {
				if (sit.next() instanceof NonTerminal nt) {
					sit.remove();
					final List<Symbol> exp = expand(cfg, nt, index);
					exp.forEach(sit::add);

					expanded = true;
				}
			}
		}

		return symbols.stream()
			.map(Terminal.class::cast)
			.toList();
	}

	private static List<Symbol> expand(
		final Cfg cfg,
		final NonTerminal symbol,
		final SymbolIndex index
	) {
		return cfg.rule(symbol)
			.map(r -> expand(r, index))
			.orElse(List.of(symbol));
	}

	private static List<Symbol> expand(final Rule rule, final SymbolIndex index) {
		final int size = rule.alternatives().size();
		return rule.alternatives().get(index.next(size)).symbols();
	}

	static List<Terminal> infixGenerate(
		final Cfg cfg,
		final SymbolIndex index,
		final List<Symbol> symbols
	) {
		final NonTerminal start = cfg.start();
		symbols.addAll(expand(cfg, start, index));

		boolean expanded = true;
		while (expanded) {
			expanded = false;

			final ListIterator<Symbol> sit = symbols.listIterator();
			while (sit.hasNext()) {
				if (sit.next() instanceof NonTerminal nt) {
					sit.remove();
					final List<Symbol> exp = expand(cfg, nt, index);
					exp.forEach(sit::add);

					expanded = true;
				}
			}
		}

		return symbols.stream()
			.map(Terminal.class::cast)
			.toList();
	}

}

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
package io.jenetics.ext.grammar;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;
import static io.jenetics.ext.grammar.SentenceGenerator.Expansion.LEFT_MOST;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import io.jenetics.ext.grammar.Cfg.NonTerminal;
import io.jenetics.ext.grammar.Cfg.Symbol;
import io.jenetics.ext.grammar.Cfg.Terminal;

/**
 * Standard implementation of a sentence generator. The generator can generate
 * sentences by expanding the grammar in a {@link Expansion#LEFT_MOST} or
 * {@link Expansion#LEFT_TO_RIGHT} order.
 * <p>
 * The following code snippet shows how to create a random sentence from a
 * given grammar:
 * <pre>{@code
 * final Cfg<String> cfg = Bnf.parse("""
 *     <expr> ::= ( <expr> <op> <expr> ) | <num> | <var> |  <fun> ( <arg>, <arg> )
 *     <fun>  ::= FUN1 | FUN2
 *     <arg>  ::= <expr> | <var> | <num>
 *     <op>   ::= + | - | * | /
 *     <var>  ::= x | y
 *     <num>  ::= 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9
 *     """
 * );
 *
 * final var random = RandomGenerator.of("L64X256MixRandom");
 * final var generator = new SentenceGenerator<String>(
 *     SymbolIndex.of(random),
 *     1_000
 * );
 * final List<Terminal<String>> sentence = generator.generate(cfg);
 * final String string = sentence.stream()
 *     .map(Symbol::name)
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
 * @see DerivationTreeGenerator
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 7.1
 * @version 7.1
 */
public final class SentenceGenerator<T>
	implements Generator<T, List<Terminal<T>>>
{

	/**
	 * Defines the expansion strategy used when generating the sentences.
	 */
	public enum Expansion {

		/**
		 * The symbol replacement always starting from the leftmost nonterminal
		 * as described in
		 * <a href="https://www.brinckerhoff.org/tmp/grammatica_evolution_ieee_tec_2001.pdf">Grammatical Evolution</a>.
		 */
		LEFT_MOST,

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
	public SentenceGenerator(
		final SymbolIndex index,
		final Expansion expansion,
		final int limit
	) {
		_index = requireNonNull(index);
		_expansion = requireNonNull(expansion);
		_limit = limit;
	}

	/**
	 * Create a new sentence generator from the given parameters.
	 *
	 * @param index the symbol index function used for generating the sentences
	 * @param limit the maximal allowed sentence length. If the generated
	 *        sentence exceeds this length, the generation is interrupted and
	 *        an empty sentence (empty list) is returned.
	 */
	public SentenceGenerator(
		final SymbolIndex index,
		final int limit
	) {
		this(index, LEFT_MOST, limit);
	}

	/**
	 * Generates a new sentence from the given grammar, <em>cfg</em>.
	 *
	 * @param cfg the generating grammar
	 * @return a newly created terminal list (sentence), or an empty list if
	 *         the length of the sentence exceed the defined sentence limit
	 */
	@Override
	public List<Terminal<T>> generate(final Cfg<? extends T> cfg) {
		final var sentence = new ArrayList<Symbol<T>>();
		generate(Cfg.upcast(cfg), sentence);

		// The 'generate' step guarantees that the list only
		// contains terminal symbols. So this cast is safe.
		@SuppressWarnings("unchecked")
		final var result = (List<Terminal<T>>)(Object)sentence;
		return List.copyOf(result);
	}

	void generate(final Cfg<T> cfg, final List<Symbol<T>> symbols) {
		symbols.add(cfg.start());

		boolean proceed;
		do {
			proceed = false;


			final ListIterator<Symbol<T>> sit = symbols.listIterator();
			while (sit.hasNext() &&
				(_expansion == Expansion.LEFT_TO_RIGHT || !proceed))
			{
				if (sit.next() instanceof NonTerminal<T> nt) {
					sit.remove();
					Generator.select(nt, cfg, _index).forEach(sit::add);
					proceed = true;
				}
			}

			if (symbols.size() > _limit) {
				symbols.clear();
				proceed = false;
			}
		} while (proceed);
	}

	/**
	 * Converts a list of symbols to a string, by concatenating the names of
	 * the given symbols.
	 *
	 * @param sentence the symbols list to covert
	 * @return the converted sentences
	 */
	public static String toString(final List<? extends Symbol<?>> sentence) {
		return sentence.stream().map(Symbol::name).collect(joining());
	}

}

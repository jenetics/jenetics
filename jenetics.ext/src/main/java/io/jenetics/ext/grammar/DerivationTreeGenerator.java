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

import java.util.Optional;

import io.jenetics.ext.grammar.Cfg.NonTerminal;
import io.jenetics.ext.grammar.Cfg.Symbol;
import io.jenetics.ext.util.Tree;
import io.jenetics.ext.util.TreeNode;

/**
 * Standard implementation of a derivation-tree generator. The following code
 * snippet lets you generate a derivation tree from a given grammar.
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
 * final var generator = new DerivationTreeGenerator<String>(
 *     SymbolIndex.of(random),
 *     1_000
 * );
 * final Tree<Symbol<String>, ?> tree = generator.generate(cfg);
 * }</pre>
 *
 * @see SentenceGenerator
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 7.1
 * @version 7.1
 */
public final class DerivationTreeGenerator<T>
	implements Generator<T, Tree<Symbol<T>, ?>>
{

	private final SymbolIndex _index;
	private final int _limit;

	/**
	 * Create a new derivation tree generator from the given parameters.
	 *
	 * @param index the symbol index function used for generating the derivation
	 *        tree
	 * @param limit the maximal allowed nodes of the tree. If the generated
	 *        tree exceeds this length, the generation is interrupted and
	 *        an empty tree is returned. If a tree is empty can be checked with
	 *        {@link Tree#isEmpty()}.
	 */
	public DerivationTreeGenerator(
		final SymbolIndex index,
		final int limit
	) {
		_index = requireNonNull(index);
		_limit = limit;
	}

	/**
	 * Generates a new derivation tree from the given grammar, <em>cfg</em>.
	 *
	 * @see Tree#isEmpty()
	 *
	 * @param cfg the generating grammar
	 * @return a newly created derivation tree, or an empty tree if
	 *         the number of nodes exceed the defined node limit
	 */
	@Override
	public Tree<Symbol<T>, ?> generate(final Cfg<? extends T> cfg) {
		final Cfg<T> grammar = Cfg.upcast(cfg);
		final NonTerminal<T> start = grammar.start();
		final TreeNode<Symbol<T>> symbols = TreeNode.of(start);

		int count = 1;
		boolean expanded = true;
		while (expanded) {
			final Optional<TreeNode<Symbol<T>>> tree = symbols.leaves()
				.filter(leave ->
					leave.value() instanceof NonTerminal<T> nt &&
					cfg.rule(nt).isPresent()
				)
				.findFirst();

			if (tree.isPresent()) {
				final var t = tree.orElseThrow();
				final var selection = Generator.select(
					(NonTerminal<T>)t.value(),
					grammar,
					_index
				);
				count += selection.size();

				if (count > _limit) {
					return TreeNode.of();
				}

				selection.forEach(t::attach);
			}

			expanded = tree.isPresent();
		}

		return symbols;
	}

}

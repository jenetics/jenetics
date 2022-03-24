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

import java.util.List;
import java.util.Optional;

import io.jenetics.ext.grammar.Cfg.NonTerminal;
import io.jenetics.ext.grammar.Cfg.Symbol;
import io.jenetics.ext.util.Tree;
import io.jenetics.ext.util.TreeNode;

/**
 * Standard implementation of a derivation-tree generator.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since !__version__!
 * @version !__version__!
 */
public final class DerivationTreeGenerator<T>
	implements Generator<T, Tree<Symbol<T>, ?>>
{

	private final SymbolIndex _index;
	private final int _limit;

	public DerivationTreeGenerator(
		final SymbolIndex index,
		final int limit
	) {
		_index = requireNonNull(index);
		_limit = limit;
	}

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
				final var expansion = expand(
					grammar,
					(NonTerminal<T>)t.value(),
					_index
				);
				count += expansion.size();

				if (count > _limit) {
					return TreeNode.of();
				}

				expansion.forEach(t::attach);
			}

			expanded = tree.isPresent();
		}

		return symbols;
	}

	static <T> List<Symbol<T>> expand(
		final Cfg<T> cfg,
		final NonTerminal<T> symbol,
		final SymbolIndex index
	) {
		return cfg.rule(symbol)
			.map(rule -> rule.alternatives()
				.get(index.next(rule, rule.alternatives().size()))
				.symbols())
			.orElse(List.of());
	}

}

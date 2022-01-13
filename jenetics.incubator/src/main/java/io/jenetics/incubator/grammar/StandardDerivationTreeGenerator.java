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

import java.util.List;
import java.util.Optional;

import io.jenetics.incubator.grammar.Cfg.NonTerminal;
import io.jenetics.incubator.grammar.Cfg.Symbol;

import io.jenetics.ext.util.TreeNode;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 7.0
 * @version 7.0
 */
public final class StandardDerivationTreeGenerator implements DerivationTreeGenerator {

	private final SymbolIndex _index;
	private final int _limit;

	public StandardDerivationTreeGenerator(
		final SymbolIndex index,
		final int limit
	) {
		_index = requireNonNull(index);
		_limit = limit;
	}

	@Override
	public TreeNode<Symbol> generate(final Cfg cfg) {
		final NonTerminal start = cfg.start();
		final TreeNode<Symbol> symbols = TreeNode.of(start);

		int count = 1;
		boolean expanded = true;
		while (expanded) {
			final Optional<TreeNode<Symbol>> tree = symbols.leaves()
				.filter(leave ->
					leave.value() instanceof NonTerminal nt &&
					cfg.rule(nt).isPresent()
				)
				.findFirst();

			if (tree.isPresent()) {
				final var t = tree.orElseThrow();
				final var expansion = expand(cfg, (NonTerminal)t.value(), _index);
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

	static List<Symbol> expand(
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

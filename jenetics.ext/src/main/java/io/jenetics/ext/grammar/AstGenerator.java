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

import io.jenetics.ext.util.Tree;
import io.jenetics.ext.util.TreeNode;

public class AstGenerator<T> implements Generator<T, Tree<Cfg.Terminal<T>, ?>> {

	private final DerivationTreeGenerator<T> _generator;

	/**
	 * Create a new AST tree generator from the given parameters.
	 *
	 * @param index the symbol index function used for generating the derivation
	 *        tree
	 * @param limit the maximal allowed nodes of the tree. If the generated
	 *        tree exceeds this length, the generation is interrupted and
	 *        an empty tree is returned. If a tree is empty can be checked with
	 *        {@link Tree#isEmpty()}.
	 */
	public AstGenerator(
		final SymbolIndex index,
		final int limit
	) {
		_generator = new DerivationTreeGenerator<>(index, limit);
	}

	@Override
	public Tree<Cfg.Terminal<T>, ?> generate(Cfg<? extends T> cfg) {
		final TreeNode<Cfg.Terminal<T>> ast = TreeNode.of();
		prune(_generator.generate(cfg), ast);
		return ast;
	}

	private static <T> void prune(
		final Tree<Cfg.Symbol<T>, ?> derivationTree,
		final TreeNode<Cfg.Terminal<T>> abstractSyntaxTree
	) {
		if (derivationTree.value() instanceof Cfg.Terminal<T> terminal) {
			abstractSyntaxTree.value(terminal);
		}

		for (int i = 0; i < derivationTree.childCount(); ++i) {
			TreeNode<Cfg.Terminal<T>> targetChild = abstractSyntaxTree;
			if (abstractSyntaxTree.value() != null) {
				targetChild = TreeNode.of();
				abstractSyntaxTree.attach(targetChild);
			}

			prune(derivationTree.childAt(i), targetChild);
		}
	}

}

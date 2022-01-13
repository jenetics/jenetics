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

import io.jenetics.incubator.grammar.Cfg.Symbol;

import io.jenetics.ext.util.Tree;
import io.jenetics.ext.util.TreeNode;

/**
 * This interface is used for creating <em>derivation-trees</em> from a
 * context-free grammar ({@link Cfg}).
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 7.0
 * @version 7.0
 */
@FunctionalInterface
public interface DerivationTreeGenerator {

	/**
	 * Create a parse-tree from the given context-free grammar. If the
	 * generation of the derivation tree fails, an empty tree
	 * ({@link Tree#isEmpty()}}) is returned.
	 *
	 * @param cfg the generating grammar
	 * @return a newly created parse-tree
	 */
	TreeNode<Symbol> generate(final Cfg cfg);

}

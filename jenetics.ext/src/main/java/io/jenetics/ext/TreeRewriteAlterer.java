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
package io.jenetics.ext;

import static java.util.Objects.requireNonNull;

import io.jenetics.ext.rewriting.TreeRewriter;
import io.jenetics.ext.util.TreeNode;

/**
 * This alterer uses a {@link TreeRewriter} for altering the {@link TreeChromosome}.
 *
 * @see TreeRewriter
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 5.0
 * @since 5.0
 */
public class TreeRewriteAlterer<
	A,
	G extends TreeGene<A, G>,
	C extends Comparable<? super C>
>
	extends TreeMutator<A, G, C>
{

	private final TreeRewriter<A> _rewriter;
	private final int _limit;

	/**
	 * Create a new alterer with the given {@code rewriter} and given rewrite
	 * {@code limit}.
	 *
	 * @param rewriter the tree rewriter
	 * @param limit the rewriting limit
	 * @param probability the altering probability
	 * @throws NullPointerException if the {@code rewriter} is {@code null}
	 */
	public TreeRewriteAlterer(
		final TreeRewriter<A> rewriter,
		final int limit,
		final double probability
	) {
		super(probability);
		_rewriter = requireNonNull(rewriter);
		_limit = limit;
	}

	/**
	 * Create a new alterer with the given {@code rewriter} and given rewrite
	 * {@code limit}.
	 *
	 * @param rewriter the tree rewriter
	 * @param limit the rewriting limit
	 * @throws NullPointerException if the {@code rewriter} is {@code null}
	 */
	public TreeRewriteAlterer(final TreeRewriter<A> rewriter, final int limit) {
		this(rewriter, limit, DEFAULT_ALTER_PROBABILITY);
	}

	/**
	 * Create a new alterer with the given {@code rewriter}.
	 *
	 * @param rewriter the tree rewriter
	 * @param probability the altering probability
	 * @throws NullPointerException if the {@code rewriter} is {@code null}
	 */
	public TreeRewriteAlterer(
		final TreeRewriter<A> rewriter,
		final double probability
	) {
		this(rewriter, Integer.MAX_VALUE, probability);
	}

	/**
	 * Create a new alterer with the given {@code rewriter}.
	 *
	 * @param rewriter the tree rewriter
	 * @throws NullPointerException if the {@code rewriter} is {@code null}
	 */
	public TreeRewriteAlterer(final TreeRewriter<A> rewriter) {
		this(rewriter, Integer.MAX_VALUE, DEFAULT_ALTER_PROBABILITY);
	}

	/**
	 * Performs the actual tree rewriting.
	 *
	 * @param tree the tree to rewrite
	 */
	@Override
	protected void mutate(final TreeNode<A> tree) {
		_rewriter.rewrite(tree, _limit);
	}

}

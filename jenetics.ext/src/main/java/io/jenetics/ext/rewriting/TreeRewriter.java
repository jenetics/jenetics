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
package io.jenetics.ext.rewriting;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

import java.util.stream.StreamSupport;

import io.jenetics.ext.util.TreeNode;

/**
 * Interface for rewriting a given tree.
 * <p>
 * <b>Description from <a href="https://en.wikipedia.org/wiki/Rewriting">
 *     Wikipedia: </a></b>
 * <em>
 *     In mathematics, computer science, and logic, rewriting covers a wide
 *     range of (potentially non-deterministic) methods of replacing sub-terms
 *     of a formula with other terms. In their most basic form, they consist of
 *     a set of objects, plus relations on how to transform those objects.
 *     Rewriting can be non-deterministic. One rule to rewrite a term could be
 *     applied in many different ways to that term, or more than one rule could
 *     be applicable. Rewriting systems then do not provide an algorithm for
 *     changing one term to another, but a set of possible rule applications.
 *     When combined with an appropriate algorithm, however, rewrite systems can
 *     be viewed as computer programs, and several theorem provers and
 *     declarative programming languages are based on term rewriting.
 * </em>
 * </p>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
@FunctionalInterface
public interface TreeRewriter<V> {

	/**
	 * Applies the rewriting to the given (mutable) {@code tree}. The tree
	 * rewrite is done in place.
	 *
	 * @param tree the tree to be rewritten
	 * @param limit the maximal number this rewrite rule is applied to the given
	 *        tree. This guarantees the termination of the rewrite method.
	 * @return {@code true} if the tree has been changed (rewritten) by this
	 *         method, {@code false} if the tree hasn't been changed
	 * @throws NullPointerException if the given {@code tree} is {@code null}
	 * @throws IllegalArgumentException if the {@code limit} is smaller than
	 *         zero
	 */
	public int rewrite(final TreeNode<V> tree, final int limit);

	/**
	 * Applies the rewriting to the given (mutable) {@code tree}. The tree
	 * rewrite is done in place. The limit of the applied rewrites is set
	 * unlimited ({@link Integer#MAX_VALUE}).
	 *
	 * @see #rewrite(TreeNode, int)
	 *
	 * @param tree the tree to be rewritten
	 * @return {@code true} if the tree has been changed (rewritten) by this
	 *         method, {@code false} if the tree hasn't been changed
	 * @throws NullPointerException if the given {@code tree} is {@code null}
	 */
	public default int rewrite(final TreeNode<V> tree) {
		return rewrite(tree, Integer.MAX_VALUE);
	}

	/**
	 * Rewrites the given {@code tree} by applying the given {@code rewriters}.
	 * This method to apply the all rewriters, in the order they are given in
	 * the sequence, until the tree stays unchanged.
	 *
	 * @param tree the tree to rewrite
	 * @param rewriters the rewriters applied to the tree
	 * @param limit the maximal number this rewrite rule is applied to the given
	 *        tree. This guarantees the termination of the rewrite method.
	 * @param <V> the tree value type
	 * @return {@code true} if the tree has been changed (rewritten) by this
	 *         method, {@code false} if the tree hasn't been changed
	 * @throws NullPointerException if one of the arguments is {@code null}
	 * @throws IllegalArgumentException if the {@code limit} is smaller than
	 *         zero
	 */
	public static <V> int rewrite(
		final TreeNode<V> tree,
		final Iterable<? extends TreeRewriter<V>> rewriters,
		final int limit
	) {
		requireNonNull(tree);
		requireNonNull(rewriters);
		if (limit < 0) {
			throw new IllegalArgumentException(format(
				"Limit is smaller then zero: %d", limit
			));
		}

		int rewritten = 0;
		int count = 0;
		do {
			count = StreamSupport.stream(rewriters.spliterator(), false)
				.mapToInt(r -> r.rewrite(tree, limit))
				.sum();

			rewritten += count;
		} while(count > 0 && rewritten < limit);

		return rewritten;
	}

	/**
	 * Rewrites the given {@code tree} by applying the given {@code rewriters}.
	 * This method to apply the all rewriters, in the order they are given in
	 * the sequence, until the tree stays unchanged.
	 *
	 * @see #rewrite(TreeNode, Iterable, int)
	 *
	 * @param tree the tree to rewrite
	 * @param rewriters the rewriters applied to the tree
	 * @param <V> the tree value type
	 * @return {@code true} if the tree has been changed (rewritten) by this
	 *         method, {@code false} if the tree hasn't been changed
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public static <V> int rewrite(
		final TreeNode<V> tree,
		final Iterable<? extends TreeRewriter<V>> rewriters
	) {
		return rewrite(tree, rewriters, Integer.MAX_VALUE);
	}

}

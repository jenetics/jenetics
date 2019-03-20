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

import java.util.Arrays;
import java.util.stream.StreamSupport;

import io.jenetics.ext.util.TreeNode;

/**
 * Some helper methods concerning tree-rewriters.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 5.0
 * @since 5.0
 */
public final class TreeRewriters {
	private TreeRewriters() {
	}

	/**
	 * Rewrites the given {@code tree} by applying the given {@code rewriters}.
	 * This method to apply the all rewriters, in the order they are given in
	 * the sequence, until the tree stays unchanged.
	 *
	 * @param <V> the tree value type
	 * @param tree the tree to rewrite
	 * @param limit the maximal number this rewrite rule is applied to the given
	 *        tree. This guarantees the termination of the rewrite method.
	 * @param rewriters the rewriters applied to the tree
	 * @return {@code true} if the tree has been changed (rewritten) by this
	 *         method, {@code false} if the tree hasn't been changed
	 * @throws NullPointerException if one of the arguments is {@code null}
	 * @throws IllegalArgumentException if the {@code limit} is smaller than
	 *         zero
	 */
	public static <V> int rewrite(
		final TreeNode<V> tree,
		final int limit,
		final Iterable<? extends TreeRewriter<V>> rewriters
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
			final int rwrt = rewritten;
			count = StreamSupport.stream(rewriters.spliterator(), false)
				.mapToInt(r -> r.rewrite(tree, limit - rwrt))
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
	 * @see #rewrite(TreeNode, int, Iterable)
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
		return rewrite(tree, Integer.MAX_VALUE, rewriters);
	}

	/**
	 * Concat the given {@code rewriters} to one tree-rewriter.
	 *
	 * @param rewriters the tree-rewriter to concatenate
	 * @param <V> the tree value type
	 * @return a new tree-rewriter which concatenates the given one
	 * @throws NullPointerException if the given {@code rewriters} are
	 *         {@code null}
	 * @throws IllegalArgumentException if the {@code limit} is smaller than
	 *         zero
	 */
	@SafeVarargs
	public static <V> TreeRewriter<V>
	concat(final TreeRewriter<V>... rewriters) {
		if (rewriters.length == 0) {
			throw new IllegalArgumentException(
				"The given rewriter array must not be empty."
			);
		}

		return (tree, limit) ->
			TreeRewriters.rewrite(tree, limit, Arrays.asList(rewriters));
	}

}

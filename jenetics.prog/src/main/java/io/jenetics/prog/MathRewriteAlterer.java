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
package io.jenetics.prog;

import io.jenetics.ext.TreeGene;
import io.jenetics.ext.TreeRewriteAlterer;
import io.jenetics.ext.rewriting.TreeRewriter;

import io.jenetics.prog.op.MathExpr;
import io.jenetics.prog.op.Op;

/**
 * Prunes a given mathematical tree with the given alterer probability.
 *
 * @see TreeRewriteAlterer
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 5.0
 * @since 4.1
 */
public class MathRewriteAlterer<
	G extends TreeGene<Op<Double>, G>,
	C extends Comparable<? super C>
>
	extends TreeRewriteAlterer<Op<Double>, G, C>
{

	/**
	 * Create a new alterer with the given {@code rewriter} and given rewrite
	 * {@code limit}.
	 *
	 * @param rewriter the tree rewriter
	 * @param limit the rewriting limit
	 * @param probability the altering probability
	 * @throws NullPointerException if the {@code rewriter} is {@code null}
	 */
	public MathRewriteAlterer(
		final TreeRewriter<Op<Double>> rewriter,
		final int limit,
		final double probability
	) {
		super(rewriter, limit, probability);
	}

	/**
	 * Create a new alterer with the given {@code rewriter} and given rewrite
	 * {@code limit}.
	 *
	 * @param rewriter the tree rewriter
	 * @param probability the altering probability
	 * @throws NullPointerException if the {@code rewriter} is {@code null}
	 */
	public MathRewriteAlterer(
		final TreeRewriter<Op<Double>> rewriter,
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
	public MathRewriteAlterer(final TreeRewriter<Op<Double>> rewriter) {
		this(rewriter, Integer.MAX_VALUE, DEFAULT_ALTER_PROBABILITY);
	}

	/**
	 * Create a new alterer with the default math rewriter
	 * {@link MathExpr#REWRITER} and given rewrite.
	 *
	 * @param probability the altering probability
	 */
	public MathRewriteAlterer(final double probability) {
		this(MathExpr.REWRITER, Integer.MAX_VALUE, probability);
	}

	/**
	 * Create a new alterer with the default math rewriter
	 * {@link MathExpr#REWRITER} and given rewrite.
	 */
	public MathRewriteAlterer() {
		this(MathExpr.REWRITER, Integer.MAX_VALUE, DEFAULT_ALTER_PROBABILITY);
	}

}

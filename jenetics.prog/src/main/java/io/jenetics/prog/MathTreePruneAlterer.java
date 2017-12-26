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
import io.jenetics.ext.TreeMutator;
import io.jenetics.ext.util.TreeNode;

import io.jenetics.prog.op.MathExpr;
import io.jenetics.prog.op.Op;

/**
 * Prunes a given mathematical tree with the given alterer probability.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class MathTreePruneAlterer<
	G extends TreeGene<Op<Double>, G>,
	C extends Comparable<? super C>
>
	extends TreeMutator<Op<Double>, G, C>
{

	public MathTreePruneAlterer() {
		this(DEFAULT_ALTER_PROBABILITY);
	}

	public MathTreePruneAlterer(double probability) {
		super(probability);
	}

	@Override
	protected void mutate(final TreeNode<Op<Double>> tree) {
		MathExpr.prune(tree);
	}

}

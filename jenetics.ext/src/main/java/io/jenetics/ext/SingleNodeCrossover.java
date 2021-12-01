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

import static java.lang.String.format;

import io.jenetics.util.ISeq;
import io.jenetics.util.RandomRegistry;

import io.jenetics.ext.util.TreeNode;

/**
 * Swaps two, randomly chosen, nodes (sub-trees) from two given trees.
 * <pre> {@code
 *     Tree A                 Tree B
 *   0                      a
 *   ├── 1                  ├── b
 *   │   ├── 4              │   ├── e
 *   │   └── 5              │   └── f
 *   ├── 2                  ├── c
 *   │   └── 6              │   └── g
 *   └── 3                  └── d
 *       ├── 7                  ├── h
 *       │   ├── 10             │   ├── k
 *       │   └── 11             │   └── l
 *       ├── 8                  ├── i
 *       └── 9                  └── j
 *
 *     Swap node "3" of A with node "c" of B
 *
 *   0                      a
 *   ├── 1                  ├── b
 *   │   ├── 4              │   ├── e
 *   │   └── 5              │   └── f
 *   ├── 2                  ├── 3
 *   │   └── 6              │   ├── 7
 *   └── c                  │   │   ├── 10
 *       └── g              │   │   └── 11
 *                          │   ├── 8
 *                          │   └── 9
 *                          └── d
 *                              ├── h
 *                              │   ├── k
 *                              │   └── l
 *                              ├── i
 *                              └── j
 * }</pre>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 5.0
 * @since 3.9
 */
public class SingleNodeCrossover<
	G extends TreeGene<?, G>,
	C extends Comparable<? super C>
>
	extends TreeCrossover<G, C>
{

	public SingleNodeCrossover(double probability) {
		super(probability);
	}

	public SingleNodeCrossover() {
		this(DEFAULT_ALTER_PROBABILITY);
	}

	@Override
	protected <A> int crossover(final TreeNode<A> that, final TreeNode<A> other) {
		return swap(that, other);
	}

	// The static method makes it easier to test.
	static <A> int swap(final TreeNode<A> that, final TreeNode<A> other) {
		assert that != null;
		assert other != null;

		final var random = RandomRegistry.random();

		final ISeq<TreeNode<A>> seq1 = that.breadthFirstStream()
			.collect(ISeq.toISeq());

		final ISeq<TreeNode<A>> seq2 = other.breadthFirstStream()
			.collect(ISeq.toISeq());

		final int changed;
		if (seq1.length() > 1 && seq2.length() > 1) {
			final TreeNode<A> n1 = seq1.get(random.nextInt(seq1.length() - 1) + 1);
			final TreeNode<A> p1 = n1.parent().orElseThrow(AssertionError::new);

			final TreeNode<A> n2 = seq2.get(random.nextInt(seq2.length() - 1) + 1);
			final TreeNode<A> p2 = n2.parent().orElseThrow(AssertionError::new);

			final int i1 = p1.indexOf(n1);
			final int i2 = p2.indexOf(n2);

			p1.insert(i1, n2.detach());
			p2.insert(i2, n1.detach());

			changed = 2;
		} else {
			changed = 0;
		}

		return changed;
	}

	@Override
	public String toString() {
		return format("SingleNodeCrossover[%f]", _probability);
	}

}

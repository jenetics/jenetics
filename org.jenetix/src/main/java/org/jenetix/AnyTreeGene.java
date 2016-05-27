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
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 */
package org.jenetix;

import java.util.Objects;
import java.util.function.ToIntFunction;

import org.jenetics.util.Factory;

import org.jenetix.util.TreeNode;

/**
 * @param <A> the allele type
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class AnyTreeGene<A>
	extends AbstractTreeGene<A, AnyTreeGene<A>>
{

	/**
	 * Create a new {@code TreeGene} instance for the given parameters.
	 *
	 * @param value the tree-gene value (allele)
	 * @param children the gene indexes of the child genes
	 * @param factory the allele factor used for creating new {@code TreeGene}
	 *        instances
	 */
	public AnyTreeGene(
		final A value,
		final int[] children,
		final Factory<A> factory
	) {
		super(value, children, factory);
	}

	@Override
	public AnyTreeGene<A> newInstance(final A value) {
		return AnyTreeGene.of(value, _children, _factory);
	}

	@Override
	public String toString() {
		return Objects.toString(_value);
	}


	/* *************************************************************************
	 * Static factory methods.
	 **************************************************************************/

	/**
	 * Create a new {@code TreeGene} instance for the given parameters.
	 *
	 * @param value the tree-gene value (allele)
	 * @param children the gene indexes of the child genes
	 * @param factory the allele factor used for creating new {@code TreeGene}
	 *        instances
	 * @param <A> the allele type
	 * @return a new {@code TreeGene} instance
	 */
	public static <A> AnyTreeGene<A> of(
		final A value,
		final int[] children,
		final Factory<A> factory
	) {
		return new AnyTreeGene<>(value, children, factory);
	}

	/**
	 * Converts the given tree {@code node} into a {@code TreeGene}.
	 *
	 * @param node the tree {@code node} to convert
	 * @param index the index function which returns the gene index within the
	 *        {@link TreeChromosome} for a given tree {@code node}.
	 * @param factory the allele factor used for creating new {@code TreeGene}
	 *        instances
	 * @param <A> the allele type
	 * @return a new {@code TreeGene} instance
	 */
	public static <A> AnyTreeGene<A> toTreeGene(
		final TreeNode<A> node,
		final ToIntFunction<TreeNode<A>> index,
		final Factory<A> factory
	) {
		final int[] indexes = node.childStream()
			.mapToInt(index)
			.toArray();

		return AnyTreeGene.of(
			node.getValue(),
			indexes,
			factory
		);
	}

}

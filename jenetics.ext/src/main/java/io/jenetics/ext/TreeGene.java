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

import io.jenetics.Gene;

import io.jenetics.ext.util.FlatTree;

/**
 * Representation of tree shaped gene. Since the genes are part of a chromosome,
 * they are implementing the {@link FlatTree} interface, which makes the required
 * storage layout explicit.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 3.9
 * @since 3.9
 */
public interface TreeGene<A, G extends TreeGene<A, G>>
	extends
		Gene<A, G>,
		FlatTree<A, G>
{

	@Override
	public default A getValue() {
		return getAllele();
	}

	/**
	 * Return a new tree gene with the given allele and the <em>local</em> tree
	 * structure.
	 *
	 * @param allele the actual gene allele
	 * @param childOffset the offset of the first node child within the
	 *        chromosome
	 * @param childCount the number of children of the new tree gene
	 * @return a new tree gene with the given parameters
	 * @throws IllegalArgumentException  if the {@code childCount} is smaller
	 *         than zero
	 */
	public G newInstance(
		final A allele,
		final int childOffset,
		final int childCount
	);

}

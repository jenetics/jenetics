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

import static java.util.Objects.requireNonNull;

import java.util.Optional;

import org.jenetics.util.Seq;

import org.jenetix.util.Tree;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
final class TreeGeneTree<A, G extends TreeGene<A, G>>
	implements Tree<G, TreeGeneTree<A, G>>
{
	private final G _gene;
	private final Seq<? extends G> _genes;

	TreeGeneTree(final G gene, final Seq<? extends G> genes)  {
		_gene = requireNonNull(gene);
		_genes = requireNonNull(genes);
	}

	@Override
	public G getValue() {
		return _gene;
	}

	@Override
	public Optional<TreeGeneTree<A, G>> getParent() {
		return _gene.getParent(_genes)
			.map(p -> new TreeGeneTree<A, G>(p, _genes));
	}

	@Override
	public TreeGeneTree<A, G> getChild(final int index) {
		return new TreeGeneTree<A, G>(_gene.getChild(index, _genes), _genes);
	}

	@Override
	public int childCount() {
		return 0;
	}
}

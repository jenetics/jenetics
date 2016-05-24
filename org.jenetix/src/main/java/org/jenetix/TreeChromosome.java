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

import java.util.Iterator;
import java.util.Optional;

import org.jenetics.AbstractChromosome;
import org.jenetics.util.ISeq;
import org.jenetics.util.IntRange;
import org.jenetics.util.Seq;

import org.jenetix.util.TreeNode;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class TreeChromosome<A> extends AbstractChromosome<TreeGene<A>> {

	private final IntRange _childCount;
	private final IntRange _depth;

	protected TreeChromosome(
		final IntRange childCount,
		final IntRange depth,
		final ISeq<TreeGene<A>> genes
	) {
		super(genes);

		_childCount = requireNonNull(childCount);
		_depth = requireNonNull(depth);
	}

	public TreeGene<A> getRoot() {
		return _genes.get(0);
	}

	@Override
	public TreeGene<A> getGene(final int index) {
		return _genes.get(index);
	}

	public boolean isLeaf(final int index) {
		return false;
	}

	@Override
	public Iterator<TreeGene<A>> iterator() {
		return new Iterator<TreeGene<A>>() {
			@Override
			public boolean hasNext() {
				return false;
			}

			@Override
			public TreeGene<A> next() {
				return null;
			}
		};
	}

	public Optional<TreeGene<A>> getParent(final TreeGene<A> gene) {
		return gene.getParent(this);
	}

	public ISeq<TreeGene<A>> getChildren(final TreeGene<A> gene) {
		return gene.getChildren(this);
	}

	@Override
	public boolean isValid() {
		return true;
	}

	@Override
	public TreeChromosome<A> newInstance(final ISeq<TreeGene<A>> genes) {
		return new TreeChromosome<>(_childCount, _depth, genes);
	}

	@Override
	public TreeChromosome<A> newInstance() {
		return null;
	}

	public TreeNode<A> toTree() {
		final TreeNode<A> root = new TreeNode<>();
		toTree(getGene(0), root);
		return root;
	}

	private void toTree(final TreeGene<A> gene, final TreeNode<A> parent) {
		requireNonNull(gene);
		parent.setValue(gene.getAllele());

		gene.getChildren(this).forEach(g -> {
			final TreeNode<A> node = new TreeNode<A>();
			parent.add(node);
			toTree(g, node);
		});
	}

	public static <A> TreeChromosome<A> of(final TreeNode<A> tree) {
		final ISeq<TreeNode<A>> nodes = tree.breathFirstStream()
			.collect(ISeq.toISeq());

		final ISeq<TreeGene<A>> genes = nodes.map(n -> toTreeGene(n, nodes));
		return new TreeChromosome<>(IntRange.of(1, 23), IntRange.of(2, 23), genes);
	}

	private static <A> TreeGene<A> toTreeGene(
		final TreeNode<A> node,
		final Seq<TreeNode<A>> nodes
	) {
		final int[] childIndexes = node.childStream()
			.mapToInt(nodes::indexOf)
			.toArray();

		return new TreeGene<>(
			node.getValue(),
			() -> null,
			nodes.indexOf(node.getParent()),
			childIndexes
		);
	}

}

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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jenetics.Chromosome;
import org.jenetics.util.ISeq;

import org.jenetix.util.TreeNode;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class ProgramChromosome<A> implements Chromosome<ProgramGene<A>> {

	private final ISeq<ProgramGene<A>> _genes;
	private final ISeq<? extends Op<A>> _ops;
	private final Map<Integer, List<Op<A>>> _map = new HashMap<>();

	public ProgramChromosome(final ISeq<ProgramGene<A>> genes) {
		_genes = requireNonNull(genes);
		_ops = _genes.get(0).getOps();
	}

	@Override
	public boolean isValid() {
		return true;
	}

	@Override
	public ProgramChromosome<A> newInstance(final ISeq<ProgramGene<A>> genes) {
		final TreeNode<ProgramGene<A>> tree = TreeNode.of(genes.get(0));

		final ProgramGene<A> root = genes.get(0);
		final int childIndex = root.arity() > 0 ? 1 : -1;

		return null;
	}

	private static <A> TreeNode<Op<A>> unflatten(final ISeq<ProgramGene<A>> genes) {
		return fill(TreeNode.of(), 0, genes);
	}

	private static <A> TreeNode<Op<A>> fill(
		final TreeNode<Op<A>> tree,
		final int index,
		final ISeq<ProgramGene<A>> genes
	) {
		if (index < genes.size()) {
			final ProgramGene<A> gene = genes.get(index);
			tree.setValue(gene.getAllele());

			int childOffset = 1;
			for (int i = 0; i < index; ++i) {
				childOffset += genes.get(i).arity();
			}

			for (int i = 0; i < gene.arity(); ++i) {
				tree.attach(fill(TreeNode.of(),childOffset + i, genes));
			}
		}

		return tree;
	}

	@Override
	public ProgramGene<A> getGene(int index) {
		return null;
	}

	@Override
	public int length() {
		return _genes.length();
	}

	@Override
	public ISeq<ProgramGene<A>> toSeq() {
		return _genes;
	}

	@Override
	public Iterator<ProgramGene<A>> iterator() {
		return _genes.iterator();
	}

	@Override
	public ProgramChromosome<A> newInstance() {
		return null;
	}

}

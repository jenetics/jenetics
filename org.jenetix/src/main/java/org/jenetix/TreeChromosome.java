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

import java.util.Iterator;
import java.util.Optional;
import java.util.stream.Stream;

import org.jenetics.Chromosome;
import org.jenetics.Gene;
import org.jenetics.util.ISeq;

import org.jenetix.util.Tree;
import org.jenetix.util.TreeNode;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class TreeChromosome<A>
	implements Chromosome<TreeGene<A>>, Tree<A, TreeChromosome<A, G>>
{
	@Override
	public boolean isValid() {
		return false;
	}

	@Override
	public A getValue() {
		return null;
	}

	@Override
	public Optional<TreeChromosome<A, G>> getParent() {
		return null;
	}

	@Override
	public TreeChromosome<A, G> getChild(int index) {
		return null;
	}

	@Override
	public int childCount() {
		return 0;
	}

	@Override
	public Chromosome<G> newInstance(ISeq<G> genes) {
		return null;
	}

	@Override
	public G getGene(int index) {
		return null;
	}

	@Override
	public int length() {
		return 0;
	}

	@Override
	public ISeq<G> toSeq() {
		return null;
	}

	@Override
	public Iterator<G> iterator() {
		return null;
	}

	@Override
	public Chromosome<G> newInstance() {
		return null;
	}

	/*
	@Override
	public boolean isValid() {
		return false;
	}

	@Override
	public Chromosome<G> newInstance(ISeq<G> genes) {
		return null;
	}

	@Override
	public G getGene(int index) {
		return null;
	}

	@Override
	public int length() {
		return 0;
	}

	@Override
	public ISeq<G> toSeq() {
		return null;
	}

	@Override
	public Iterator<G> iterator() {
		return null;
	}

	@Override
	public Chromosome<G> newInstance() {
		return null;
	}
	*/


//	/**
//	 * Return the root gene of the {@code TreeChromosome}.
//	 *
//	 * @return the root gene of the {@code TreeChromosome}
//	 */
//	//public default G getRoot() {
//	//	return getGene();
//	//}
//
//
//	public A getValue();
//
//	public Optional<TreeChromosome<A, G>> getParent();
//
//	public TreeChromosome<A, G> getChild(final int index);
//
//	public int childCount();
//
//	public default Optional<G> getParent(final G child) {
//		return child.getParent(toSeq());
//	}
//
//	public default G getChild(final G parent, final int index) {
//		return parent.getChild(index, toSeq());
//	}
//
//	public default int childCount(final G parent) {
//		return parent.childCount();
//	}
//
//	public default Stream<G> children(final G parent) {
//		return parent.children(toSeq());
//	}
//
//	public default boolean isLeaf(final G gene) {
//		return gene.isLeaf();
//	}
//
//	public default TreeNode<A> toTree() {
//		return null;
//		//return getRoot().toTreeNode(toSeq());
//	}


}

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
package org.jenetics;

import static java.util.Objects.requireNonNull;

import org.jenetics.util.ISeq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class TreeGene<A> implements Gene<A, TreeGene<A>> {

	private TreeGene<A> _parent;
	private ISeq<TreeGene<A>> _children;

	private final A _allele;

	private TreeGene(final A allele) {
		_allele = requireNonNull(allele);
	}


	void swap() {

	}

	@Override
	public A getAllele() {
		return _allele;
	}

	@Override
	public TreeGene<A> newInstance() {
		return null;
	}

	@Override
	public TreeGene<A> newInstance(final A value) {
		return of(value);
	}

	@Override
	public boolean isValid() {
		return false;
	}

	public static <A> TreeGene<A> of(final A allele) {
		return new TreeGene<>(allele);
	}

}

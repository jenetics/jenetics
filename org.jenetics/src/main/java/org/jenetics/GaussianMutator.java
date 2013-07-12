/*
 * Java Genetic Algorithm Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * Author:
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 *
 */
package org.jenetics;

import static org.jenetics.util.object.hashCodeOf;

import java.util.Random;

import javolution.lang.Immutable;

import org.jenetics.util.IndexStream;
import org.jenetics.util.MSeq;
import org.jenetics.util.RandomRegistry;
import org.jenetics.util.math;

/**
 * The GaussianMutator class performs the mutation of a {@link NumberGene}.
 * This mutator picks a new value based on a Gaussian distribution around the
 * current value of the gene. The variance of the new value (before clipping to
 * the allowed gene range) will be
 * <p>
 * <img
 *     src="doc-files/gaussian-mutator-var.gif"
 *     alt="\hat{\sigma }^2 = \left ( \frac{ g_{max} - g_{min} }{4}\right )^2"
 * />
 * </p>
 * The new value will be cropped to the gene's boundaries.
 *
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 1.0 &mdash; <em>$Date: 2013-06-05 $</em>
 */
public final class GaussianMutator<G extends NumberGene<?, G>>
	extends Mutator<G>
	implements Immutable
{

	public GaussianMutator() {
	}

	public GaussianMutator(final double probability) {
		super(probability);
	}

	@Override
	protected int mutate(final MSeq<G> genes, final double p) {
		final Random random = RandomRegistry.getRandom();
		final IndexStream stream = IndexStream.Random(genes.length(), p);

		int alterations = 0;
		for (int i = stream.next(); i != -1; i = stream.next()) {
			genes.set(i, mutate(genes.get(i), random));

			++alterations;
		}

		return alterations;
	}

	G mutate(final G gene, final Random random) {
		final double std = (
			gene.getMax().doubleValue() - gene.getMin().doubleValue()
		)*0.25;

		return gene.newInstance(math.clamp(
			random.nextGaussian()*std + gene.doubleValue(),
			gene.getMin().doubleValue(),
			gene.getMax().doubleValue()
		));
	}

	@Override
	public int hashCode() {
		return hashCodeOf(getClass()).and(super.hashCode()).value();
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj == null || obj.getClass() != getClass()) {
			return false;
		}

		return super.equals(obj);
	}

	@Override
	public String toString() {
		return String.format(
			"%s[p=%f]",
			getClass().getSimpleName(),
			_probability
		);
	}

}






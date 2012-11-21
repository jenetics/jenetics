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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * Author:
 *     Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 *
 */
package org.jenetics.performance;

import org.jenetics.Float64Chromosome;
import org.jenetics.Float64Gene;
import org.jenetics.Genotype;
import org.jenetics.util.Array;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
class GenotypeBuilder {

	private int _ngenes = 1;
	private int _nchromosomes = 1;
	private double _min = 0;
	private double _max = 1;

	public GenotypeBuilder() {
	}

	public GenotypeBuilder ngenes(final int ngenes) {
		_ngenes = ngenes;
		return this;
	}

	public GenotypeBuilder nchromosomes(final int nchromosomes) {
		_nchromosomes = nchromosomes;
		return this;
	}

	public GenotypeBuilder min(final double min) {
		_min = min;
		return this;
	}

	public GenotypeBuilder max(final double max) {
		_max = max;
		return this;
	}

	public Genotype<Float64Gene> build() {
		final Array<Float64Chromosome> chromosomes =
			new Array<>(_nchromosomes);

		for (int i = 0; i < _nchromosomes; ++i) {
			chromosomes.set(i, new Float64Chromosome(_min, _max, _ngenes));
		}

		return Genotype.valueOf(chromosomes.toISeq());
	}

}

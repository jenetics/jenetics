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
package org.jenetics.performance;

import org.jenetics.Float64Chromosome;
import org.jenetics.Float64Gene;
import org.jenetics.Genotype;
import org.jenetics.util.Array;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date$</em>
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

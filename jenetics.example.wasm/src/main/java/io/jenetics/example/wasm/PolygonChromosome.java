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
package io.jenetics.example.wasm;

import java.io.Serial;
import java.io.Serializable;

import io.jenetics.AbstractChromosome;
import io.jenetics.Chromosome;
import io.jenetics.util.ISeq;

/**
 * A simple chromosome representing a list of polygons.
 */
public final class PolygonChromosome
	extends AbstractChromosome<PolygonGene>
	implements Serializable
{
	@Serial
	private static final long serialVersionUID = 1L;

	public PolygonChromosome(final ISeq<PolygonGene> genes) {
		super(genes);
	}

	public PolygonChromosome(final int polygonCount, final int polygonLength) {
		super(PolygonGene.seq(polygonCount, polygonLength));
	}

	@Override
	public Chromosome<PolygonGene> newInstance(final ISeq<PolygonGene> genes) {
		return new PolygonChromosome(genes);
	}

	@Override
	public Chromosome<PolygonGene> newInstance() {
		return new PolygonChromosome(length(), gene().allele().length());
	}

	/**
	 * Renders all polygons into the given rasterizer, starting with an opaque
	 * white background.
	 *
	 * @param rasterizer destination rasterizer
	 */
	public void render(final PolygonRasterizer rasterizer) {
		rasterizer.clear(0xFFFFFFFF); // opaque white background
		for (final PolygonGene gene : this) {
			gene.allele().fill(rasterizer);
		}
	}

	/**
	 * Flattens all genes into consecutive packed polygon records.
	 *
	 * @return packed RGBA and normalized coordinate data
	 */
	public double[] toArray() {
		final int count = length();
		if (count == 0) {
			return new double[0];
		}
		final int polyLength = gene().allele().length();
		final int polySize = 4 + 2 * polyLength;
		final double[] array = new double[count * polySize];

		int offset = 0;
		for (final PolygonGene gene : this) {
			gene.allele().writeTo(array, offset);
			offset += polySize;
		}

		return array;
	}
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jenetics.example.image;

import java.util.ListIterator;

import org.jenetics.Mutator;
import org.jenetics.util.MSeq;

/**
 * Polygon mutator class.
 *
 * @param <C> the fitness type
 */
final class PolygonMutator<C extends Comparable<? super C>>
	extends Mutator<PolygonGene, C>
{

	private final float _rate;
	private final float _magnitude;

	public PolygonMutator(final float rate, final float magnitude) {
		super(1.0);
		_rate = rate;
		_magnitude = magnitude;
	}

	@Override
	protected int mutate(final MSeq<PolygonGene> genes, final double p) {
		final ListIterator<PolygonGene> it = genes.listIterator();

		while (it.hasNext()) {
			final PolygonGene gene = it.next();
			final PolygonGene mutated = gene.newInstance(
				gene.getAllele().mutate(_rate, _magnitude)
			);
			it.set(mutated);
		}

		return genes.size();
	}

}

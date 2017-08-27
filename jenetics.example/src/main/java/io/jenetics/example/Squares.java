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
package org.jenetics.example;

import java.awt.Dimension;
import java.util.Random;

import org.jenetics.AnyGene;
import org.jenetics.Phenotype;
import org.jenetics.engine.Engine;
import org.jenetics.engine.EvolutionResult;
import org.jenetics.engine.codecs;
import org.jenetics.util.RandomRegistry;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public class Squares {

	private static Dimension nextDimension() {
		final Random random = RandomRegistry.getRandom();
		return new Dimension(random.nextInt(100), random.nextInt(100));
	}

	private static double area(final Dimension dim) {
		return dim.getHeight()*dim.getWidth();
	}

	public static void main(final String[] args) {
		final Engine<AnyGene<Dimension>, Double> engine = Engine
			.builder(Squares::area, codecs.ofScalar(Squares::nextDimension))
			.build();

		final Phenotype<AnyGene<Dimension>, Double> pt = engine.stream()
			.limit(50)
			.collect(EvolutionResult.toBestPhenotype());

		System.out.println(pt);
	}

}

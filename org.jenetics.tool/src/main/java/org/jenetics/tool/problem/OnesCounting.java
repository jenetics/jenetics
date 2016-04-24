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
package org.jenetics.tool.problem;

import org.jenetics.BitChromosome;
import org.jenetics.BitGene;
import org.jenetics.Genotype;
import org.jenetics.engine.Codec;
import org.jenetics.engine.Problem;
import org.jenetics.util.ISeq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version 3.5
 * @since 3.5
 */
public class OnesCounting {

	Problem<ISeq<BitGene>, BitGene, Integer> ONES_COUNTING =
		Problem.of(
			// Function<ISeq<BitGene>, Integer>
			genes -> (int)genes.stream().filter(BitGene::getBit).count(),
			Codec.of(
				// Factory<Genotype<BitGene>>
				Genotype.of(BitChromosome.of(20, 0.15)),
				// Function<Genotype<BitGene>, <BitGene>>
				gt -> gt.getChromosome().toSeq()
			)
		);

}

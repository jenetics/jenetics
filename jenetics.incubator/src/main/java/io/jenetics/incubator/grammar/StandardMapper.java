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
package io.jenetics.incubator.grammar;

import java.util.List;

import io.jenetics.Genotype;
import io.jenetics.IntegerChromosome;
import io.jenetics.IntegerGene;
import io.jenetics.engine.Codec;
import io.jenetics.incubator.grammar.Cfg.Terminal;
import io.jenetics.util.IntRange;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since !__version__!
 * @version !__version__!
 */
public class StandardMapper implements Mapper<IntegerGene> {

	@Override
	public List<Terminal> map(final Genotype<IntegerGene> gt, final Cfg cfg) {
		final var codons = Codons.ofIntegerGenes(gt.chromosome());
		return Sentence.generate(cfg, codons);
	}


	public static Codec<List<Terminal>, IntegerGene> codec(
		final Cfg cfg,
		final IntRange domain,
		final IntRange length
	) {
		return Codec.of(
			Genotype.of(IntegerChromosome.of(domain, length)),
			gt -> Sentence.generate(cfg, Codons.ofIntegerGenes(gt.chromosome()))
		);
	}

}

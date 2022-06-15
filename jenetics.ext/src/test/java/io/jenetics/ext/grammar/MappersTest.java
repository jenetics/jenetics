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
package io.jenetics.ext.grammar;

import static org.assertj.core.api.Assertions.assertThat;
import static io.jenetics.ext.grammar.SentenceGeneratorTest.CFG;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.testng.annotations.Test;

import io.jenetics.BitChromosome;
import io.jenetics.BitGene;
import io.jenetics.Genotype;
import io.jenetics.IntegerChromosome;
import io.jenetics.IntegerGene;
import io.jenetics.engine.Codec;
import io.jenetics.util.IntRange;
import io.jenetics.util.RandomRegistry;

import io.jenetics.ext.grammar.Cfg.Terminal;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class MappersTest {

	@Test
	public void singleBitChromosomeMapper() {
		final Cfg<String> cfg = Bnf.parse("""
			<expr> ::= <num> | <var> | '(' <expr> <op> <expr> ')'
			<op>   ::= + | - | * | /
			<var>  ::= x | y
			<num>  ::= 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9
			"""
		);

		final Codec<List<Terminal<String>>, BitGene> codec =
			Mappers.singleBitChromosomeMapper(
				cfg,
				1000,
				index -> new SentenceGenerator<>(index, 1000)
			);

		final Genotype<BitGene> gt = codec.encoding().newInstance();
		assertThat(gt.chromosome().length()).isEqualTo(1000);

		final Genotype<BitGene> gt2 = RandomRegistry
			.with(new Random(1648154405976L), r -> Genotype.of(BitChromosome.of(1000)));

		final var sentence = codec.decode(gt2).stream()
			.map(Terminal::name)
			.collect(Collectors.joining());

		assertThat(sentence).isEqualTo("((y+(2/x))+x)");
	}

	@Test
	public void singleIntegerChromosomeMapper() {
		final Codec<List<Terminal<String>>, IntegerGene> codec =
			Mappers.singleIntegerChromosomeMapper(
				CFG,
				IntRange.of(0, 256),
				IntRange.of(100),
				index -> new SentenceGenerator<>(index, 1000)
			);

		final Genotype<IntegerGene> gt = codec.encoding().newInstance();
		assertThat(gt.chromosome().length()).isEqualTo(100);

		final Genotype<IntegerGene> gt2 = RandomRegistry.with(
			new Random(1648154989585L),
			r -> Genotype.of(IntegerChromosome.of(IntRange.of(0, 256), 100))
		);

		final var sentence = codec.decode(gt2).stream()
			.map(Terminal::name)
			.collect(Collectors.joining());

		assertThat(sentence).isEqualTo("((1-7)*x)");
	}

	@Test
	public void multiIntegerChromosomeMapper() {
		final Codec<List<Terminal<String>>, IntegerGene> codec = Mappers
			.multiIntegerChromosomeMapper(
				CFG,
				rule -> IntRange.of(rule.alternatives().size()*25),
				index -> new SentenceGenerator<>(index, 1_000)
			);
	}
}

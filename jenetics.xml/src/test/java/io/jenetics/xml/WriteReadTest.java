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
package io.jenetics.xml;

import static java.util.Collections.emptyList;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.stream.Collectors;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.jenetics.BitChromosome;
import io.jenetics.CharacterChromosome;
import io.jenetics.DoubleChromosome;
import io.jenetics.DoubleGene;
import io.jenetics.Genotype;
import io.jenetics.IntegerChromosome;
import io.jenetics.LongChromosome;
import io.jenetics.PermutationChromosome;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public class WriteReadTest {

	@FunctionalInterface
	interface Write<T> {
		void write(final OutputStream out, final T data) throws Exception;
	}

	@FunctionalInterface
	interface Read<T> {
		T read(final InputStream in) throws Exception;
	}

	@Test(dataProvider = "marshallings")
	public <T> void writeRead(
		final T data,
		final Write<T> writer,
		final Read<T> reader
	)
		throws Exception
	{
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		writer.write(out, data);

		final ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
		Assert.assertEquals(reader.read(in), data);
	}

	@DataProvider
	public Object[][] marshallings() {
		return new Object[][] {
			{
				BitChromosome.of(10),
				(Write<BitChromosome>)Writers.BitChromosome::write,
				(Read<BitChromosome>)Readers.BitChromosome::read
			},
			{
				CharacterChromosome.of(5),
				(Write<CharacterChromosome>)Writers.CharacterChromosome::write,
				(Read<CharacterChromosome>)Readers.CharacterChromosome::read
			},
			{
				IntegerChromosome.of(0, 1_000_000, 20),
				(Write<IntegerChromosome>)Writers.IntegerChromosome::write,
				(Read<IntegerChromosome>)Readers.IntegerChromosome::read
			},
			{
				LongChromosome.of(0, 1_000_000, 20),
				(Write<LongChromosome>)Writers.LongChromosome::write,
				(Read<LongChromosome>)Readers.LongChromosome::read
			},
			{
				DoubleChromosome.of(0, 1_000_000, 20),
				(Write<DoubleChromosome>)Writers.DoubleChromosome::write,
				(Read<DoubleChromosome>)Readers.DoubleChromosome::read
			},
			{
				PermutationChromosome.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
				(Write<PermutationChromosome<Integer>>)
					Writers.PermutationChromosome::write,
				(Read<PermutationChromosome<Integer>>)
					in -> Readers.PermutationChromosome.read(in, Readers.IntegerChromosome.alleleReader())
			},
			{
				Genotype.of(DoubleChromosome.of(0, 1, 2), 20),
				(Write<Genotype<DoubleGene>>)
					(out, data) -> Writers.Genotype.write(out, data, Writers.DoubleChromosome.writer()),
				(Read<Genotype<DoubleGene>>)
					in -> Readers.Genotype.read(in, Readers.DoubleChromosome.reader())
			},
			{
				Genotype.of(DoubleChromosome.of(0, 1, 10), 10)
					.instances()
					.limit(20)
					.collect(Collectors.toList()),
				(Write<Collection<Genotype<DoubleGene>>>)
					(out, data) -> Writers.Genotypes.write(out, data, Writers.DoubleChromosome.writer()),
				(Read<Collection<Genotype<DoubleGene>>>)
					in -> Readers.Genotypes.read(in, Readers.DoubleChromosome.reader())
			},
			{
				emptyList(),
				(Write<Collection<Genotype<DoubleGene>>>)
					(out, data) -> Writers.Genotypes.write(out, data, Writers.DoubleChromosome.writer()),
				(Read<Collection<Genotype<DoubleGene>>>)
					in -> Readers.Genotypes.read(in, Readers.DoubleChromosome.reader())
			}
		};
	}

}

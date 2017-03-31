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
package org.jenetics.xml;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static org.jenetics.internal.math.random.nextByte;
import static org.jenetics.internal.math.random.nextCharacter;
import static org.jenetics.internal.math.random.nextShort;
import static org.jenetics.internal.math.random.nextString;
import static org.jenetics.util.RandomRegistry.using;
import static org.jenetics.xml.Writers.BIT_CHROMOSOME_WRITER;
import static org.jenetics.xml.Writers.CHARACTER_CHROMOSOME_WRITER;
import static org.jenetics.xml.Writers.DOUBLE_CHROMOSOME_WRITER;
import static org.jenetics.xml.Writers.INTEGER_CHROMOSOME_WRITER;
import static org.jenetics.xml.Writers.LONG_CHROMOSOME_WRITER;
import static org.jenetics.xml.Writers.genotypeWriter;
import static org.jenetics.xml.Writers.genotypesWriter;
import static org.jenetics.xml.Writers.permutationChromosomeWriter;
import static org.jenetics.xml.stream.Writer.text;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.stream.XMLStreamException;

import org.jenetics.BitChromosome;
import org.jenetics.BitGene;
import org.jenetics.CharacterChromosome;
import org.jenetics.CharacterGene;
import org.jenetics.DoubleChromosome;
import org.jenetics.DoubleGene;
import org.jenetics.EnumGene;
import org.jenetics.Genotype;
import org.jenetics.IntegerChromosome;
import org.jenetics.IntegerGene;
import org.jenetics.LongChromosome;
import org.jenetics.LongGene;
import org.jenetics.PermutationChromosome;
import org.jenetics.Phenotype;
import org.jenetics.Population;
import org.jenetics.util.ISeq;
import org.jenetics.util.LCG64ShiftRandom;
import org.jenetics.util.RandomRegistry;
import org.jenetics.xml.stream.AutoCloseableXMLStreamWriter;
import org.jenetics.xml.stream.Writer;
import org.jenetics.xml.stream.XML;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class PersistentObject<T> {

	private final String _name;
	private final T _value;
	private final Writer<T> _writer;

	public PersistentObject(
		final String name,
		final T value,
		final Writer<T> writer
	) {
		_name = requireNonNull(name);
		_value = requireNonNull(value);
		_writer = requireNonNull(writer);
	}

	public String getName() {
		return _name;
	}

	public T getValue() {
		return _value;
	}

	public Writer<T> getWriter() {
		return _writer;
	}

	public void write(final File baseDir)
		throws IOException, XMLStreamException
	{
		final File file = new File(baseDir, format("%s.xml", _name));
		try (FileOutputStream fout = new FileOutputStream(file);
			 BufferedOutputStream bout = new BufferedOutputStream(fout);
			 AutoCloseableXMLStreamWriter writer = XML.writer(bout, "    "))
		{
			Writer.doc(_writer).write(_value, writer);
		}
	}

	@Override
	public String toString() {
		return format("%s[%s]", getClass().getSimpleName(), getName());
	}

	private static final long SEED = 101010101010101L;

	public static final List<PersistentObject<?>> VALUES = new ArrayList<>();

	private static <T> void put(final String name, final T value, final Writer<T> writer) {
		VALUES.add(new PersistentObject<>(name, value, writer));
		RandomRegistry.getRandom().setSeed(SEED);
	}

	private static void init() {

		/* *********************************************************************
		 * Chromosomes
		 **********************************************************************/

		put("BitChromosome", nextBitChromosome(), BIT_CHROMOSOME_WRITER);
		put("CharacterChromosome", nextCharacterChromosome(), CHARACTER_CHROMOSOME_WRITER);
		put("IntegerChromosome", nextIntegerChromosome(), INTEGER_CHROMOSOME_WRITER);
		put("LongChromosome", nextLongChromosome(), LONG_CHROMOSOME_WRITER);
		put("DoubleChromosome", nextDoubleChromosome(), DOUBLE_CHROMOSOME_WRITER);

		put("PermutationChromosome[Byte]", nextBytePermutationChromosome(), permutationChromosomeWriter());
		put("PermutationChromosome[Short]", nextShortPermutationChromosome(), permutationChromosomeWriter());
		put("PermutationChromosome[Integer]", nextIntegerPermutationChromosome(), permutationChromosomeWriter());
		put("PermutationChromosome[Long]", nextLongPermutationChromosome(), permutationChromosomeWriter());
		put("PermutationChromosome[Float]", nextFloatPermutationChromosome(), permutationChromosomeWriter());
		put("PermutationChromosome[Double]", nextDoublePermutationChromosome(), permutationChromosomeWriter());
		put("PermutationChromosome[Character]", nextCharacterPermutationChromosome(), permutationChromosomeWriter());
		put("PermutationChromosome[String]", nextStringPermutationChromosome(), permutationChromosomeWriter());

		/* *********************************************************************
		 * Genotypes
		 **********************************************************************/

		put("Genotype[BitGene]", nextGenotypeBitGene(), genotypeWriter(BIT_CHROMOSOME_WRITER));
		put("Genotype[CharacterGene]", nextGenotypeCharacterGene(), genotypeWriter(CHARACTER_CHROMOSOME_WRITER));
		put("Genotype[IntegerGene]", nextGenotypeIntegerGene(), genotypeWriter(INTEGER_CHROMOSOME_WRITER));
		put("Genotype[LongGene]", nextGenotypeLongGene(), genotypeWriter(LONG_CHROMOSOME_WRITER));
		put("Genotype[DoubleGene]", nextGenotypeDoubleGene(), genotypeWriter(DOUBLE_CHROMOSOME_WRITER));

		put("Genotype[EnumGene[Byte]]", nextGenotypeEnumGeneByte(), genotypeWriter(permutationChromosomeWriter()));
		put("Genotype[EnumGene[Character]]", nextGenotypeEnumGeneCharacter(), genotypeWriter(permutationChromosomeWriter()));
		put("Genotype[EnumGene[Short]]", nextGenotypeEnumGeneShort(), genotypeWriter(permutationChromosomeWriter()));
		put("Genotype[EnumGene[Integer]]", nextGenotypeEnumGeneInteger(), genotypeWriter(permutationChromosomeWriter()));
		put("Genotype[EnumGene[Long]]", nextGenotypeEnumGeneLong(), genotypeWriter(permutationChromosomeWriter()));
		put("Genotype[EnumGene[Float]]", nextGenotypeEnumGeneFloat(), genotypeWriter(permutationChromosomeWriter()));
		put("Genotype[EnumGene[Double]]", nextGenotypeEnumGeneDouble(), genotypeWriter(permutationChromosomeWriter()));
		put("Genotype[EnumGene[String]]", nextGenotypeEnumGeneString(), genotypeWriter(permutationChromosomeWriter()));

		/* *********************************************************************
		 * Populations
		 **********************************************************************/

		put("Population[BitGene]", nextPopulationBitGene(), genotypesWriter(BIT_CHROMOSOME_WRITER));
		put("Population[CharacterGene]", nextPopulationCharacterGene(), genotypesWriter(CHARACTER_CHROMOSOME_WRITER));
		put("Population[IntegerGene]", nextPopulationIntegerGene(), genotypesWriter(INTEGER_CHROMOSOME_WRITER));
		put("Population[LongGene]", nextPopulationLongGene(), genotypesWriter(LONG_CHROMOSOME_WRITER));
		put("Population[DoubleGene]", nextPopulationDoubleGene(), genotypesWriter(DOUBLE_CHROMOSOME_WRITER));
		put("Population[EnumGene[Integer]]", nextPopulationEnumGene(), genotypesWriter(permutationChromosomeWriter()));
	}


	/* *************************************************************************
	 * Genes
	 **************************************************************************/

	public static CharacterGene nextCharacterGene() {
		return CharacterGene.of();
	}

	public static IntegerGene nextIntegerGene() {
		return IntegerGene.of(Integer.MIN_VALUE, Integer.MAX_VALUE);
	}

	public static LongGene nextLongGene() {
		return LongGene.of(Long.MIN_VALUE, Long.MAX_VALUE);
	}

	public static DoubleGene nextDoubleGene() {
		return DoubleGene.of(0, 1);
	}

	public static EnumGene<Boolean> nextEnumGeneBoolean() {
		return EnumGene.of(ISeq.of(random()::nextBoolean, 5));
	}

	public static EnumGene<Byte> nextEnumGeneByte() {
		return EnumGene.of(ISeq.of(() -> nextByte(random()), 5));
	}

	public static EnumGene<Character> nextEnumGeneCharacter() {
		return EnumGene.of(ISeq.of(() -> nextCharacter(random()), 5));
	}

	public static EnumGene<Short> nextEnumGeneShort() {
		return EnumGene.of(ISeq.of(() -> nextShort(random()), 5));
	}

	public static EnumGene<Integer> nextEnumGeneInteger() {
		return EnumGene.of(ISeq.<Integer>of(random()::nextInt, 5));
	}

	public static EnumGene<Long> nextEnumGeneLong() {
		return EnumGene.of(ISeq.of(random()::nextLong, 5));
	}

	public static EnumGene<Float> nextEnumGeneFloat() {
		return EnumGene.of(ISeq.of(random()::nextFloat, 5));
	}

	public static EnumGene<Double> nextEnumGeneDouble() {
		return EnumGene.of(ISeq.of(random()::nextDouble, 5));
	}

	public static EnumGene<String> nextEnumGeneString() {
		return EnumGene.of(ISeq.of(() -> nextString(random()), 5));
	}

	/* *************************************************************************
	 * Chromosomes
	 **************************************************************************/

	public static BitChromosome nextBitChromosome() {
		return BitChromosome.of(20, 0.5);
	}

	public static CharacterChromosome nextCharacterChromosome() {
		return CharacterChromosome.of(20);
	}

	public static IntegerChromosome nextIntegerChromosome() {
		return IntegerChromosome.of(Integer.MIN_VALUE, Integer.MAX_VALUE, 20);
	}

	public static LongChromosome nextLongChromosome() {
		return LongChromosome.of(Long.MIN_VALUE, Long.MAX_VALUE, 20);
	}

	public static DoubleChromosome nextDoubleChromosome() {
		return DoubleChromosome.of(0.0, 1.0, 20);
	}

	public static PermutationChromosome<Byte> nextBytePermutationChromosome() {
		return PermutationChromosome.of(ISeq.of(() -> nextByte(random()), 15));
	}

	public static PermutationChromosome<Short> nextShortPermutationChromosome() {
		return PermutationChromosome.of(ISeq.of(() -> nextShort(random()), 15));
	}

	public static PermutationChromosome<Integer> nextIntegerPermutationChromosome() {
		return PermutationChromosome.ofInteger(15);
	}

	public static PermutationChromosome<Long> nextLongPermutationChromosome() {
		return PermutationChromosome.of(ISeq.of(random()::nextLong, 15));
	}

	public static PermutationChromosome<Float> nextFloatPermutationChromosome() {
		return PermutationChromosome.of(ISeq.of(random()::nextFloat, 15));
	}

	public static PermutationChromosome<Double> nextDoublePermutationChromosome() {
		return PermutationChromosome.of(ISeq.of(random()::nextDouble, 15));
	}

	public static PermutationChromosome<Character> nextCharacterPermutationChromosome() {
		return PermutationChromosome.of(ISeq.of(() -> nextCharacter(random()), 15));
	}

	public static PermutationChromosome<String> nextStringPermutationChromosome() {
		return PermutationChromosome.of(ISeq.of(() -> nextString(random()), 15));
	}


	/* *************************************************************************
	 * Genotypes
	 **************************************************************************/

	public static Genotype<BitGene> nextGenotypeBitGene() {
		return Genotype.of(ISeq.of(PersistentObject::nextBitChromosome, 5));
	}

	public static Genotype<CharacterGene> nextGenotypeCharacterGene() {
		return Genotype.of(ISeq.of(PersistentObject::nextCharacterChromosome, 5));
	}

	public static Genotype<IntegerGene> nextGenotypeIntegerGene() {
		return Genotype.of(ISeq.of(PersistentObject::nextIntegerChromosome, 5));
	}

	public static Genotype<LongGene> nextGenotypeLongGene() {
		return Genotype.of(ISeq.of(PersistentObject::nextLongChromosome, 5));
	}

	public static Genotype<DoubleGene> nextGenotypeDoubleGene() {
		return Genotype.of(ISeq.of(PersistentObject::nextDoubleChromosome, 5));
	}

	public static Genotype<EnumGene<Byte>> nextGenotypeEnumGeneByte() {
		return Genotype.of(ISeq.of(PersistentObject::nextBytePermutationChromosome, 5));
	}

	public static Genotype<EnumGene<Character>> nextGenotypeEnumGeneCharacter() {
		return Genotype.of(ISeq.of(PersistentObject::nextCharacterPermutationChromosome, 5));
	}

	public static Genotype<EnumGene<Short>> nextGenotypeEnumGeneShort() {
		return Genotype.of(ISeq.of(PersistentObject::nextShortPermutationChromosome, 5));
	}

	public static Genotype<EnumGene<Integer>> nextGenotypeEnumGeneInteger() {
		return Genotype.of(ISeq.of(PersistentObject::nextIntegerPermutationChromosome, 5));
	}

	public static Genotype<EnumGene<Long>> nextGenotypeEnumGeneLong() {
		return Genotype.of(ISeq.of(PersistentObject::nextLongPermutationChromosome, 5));
	}

	public static Genotype<EnumGene<Float>> nextGenotypeEnumGeneFloat() {
		return Genotype.of(ISeq.of(PersistentObject::nextFloatPermutationChromosome, 5));
	}

	public static Genotype<EnumGene<Double>> nextGenotypeEnumGeneDouble() {
		return Genotype.of(ISeq.of(PersistentObject::nextDoublePermutationChromosome, 5));
	}

	public static Genotype<EnumGene<String>> nextGenotypeEnumGeneString() {
		return Genotype.of(ISeq.of(PersistentObject::nextStringPermutationChromosome, 5));
	}

	/* *************************************************************************
	 * Populations
	 **************************************************************************/

	public static Collection<Genotype<BitGene>> nextPopulationBitGene() {
		return Stream.generate(PersistentObject::nextGenotypeBitGene)
			.limit(7)
			.collect(Collectors.toList());
	}

	public static Collection<Genotype<CharacterGene>> nextPopulationCharacterGene() {
		return Stream.generate(PersistentObject::nextGenotypeCharacterGene)
			.limit(7)
			.collect(Collectors.toList());
	}

	public static Collection<Genotype<IntegerGene>> nextPopulationIntegerGene() {
		return Stream.generate(PersistentObject::nextGenotypeIntegerGene)
			.limit(7)
			.collect(Collectors.toList());
	}

	public static Collection<Genotype<LongGene>> nextPopulationLongGene() {
		return Stream.generate(PersistentObject::nextGenotypeLongGene)
			.limit(7)
			.collect(Collectors.toList());
	}

	public static Collection<Genotype<DoubleGene>> nextPopulationDoubleGene() {
		return Stream.generate(PersistentObject::nextGenotypeDoubleGene)
			.limit(7)
			.collect(Collectors.toList());
	}

	public static Collection<Genotype<EnumGene<Integer>>> nextPopulationEnumGene() {
		return Stream.generate(PersistentObject::nextGenotypeEnumGeneInteger)
			.limit(7)
			.collect(Collectors.toList());
	}


	private static Random random() {
		return RandomRegistry.getRandom();
	}

	static {
		final Random random = new LCG64ShiftRandom.ThreadSafe(SEED);
		using(random, r -> init());
	}

	public static void main(final String[] args) throws Exception {
		write();
		//IO.jaxb.write(nextPhenotypeEnumGeneIntegerDouble(), System.out);
	}

	private static void write() throws IOException, XMLStreamException {
		final File baseDir = new File("org.jenetics.xml/src/test/resources/serialization");
		if (!baseDir.isDirectory() && !baseDir.mkdirs()) {
			throw new IOException("Error while creating directory " + baseDir);
		}

		for (PersistentObject<?> object : VALUES) {
			object.write(baseDir);
		}
	}

}

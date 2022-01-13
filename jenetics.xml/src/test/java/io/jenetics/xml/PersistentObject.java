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
package io.jenetics.xml;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static io.jenetics.internal.math.Randoms.nextASCIIString;
import static io.jenetics.internal.math.Randoms.nextByte;
import static io.jenetics.internal.math.Randoms.nextChar;
import static io.jenetics.internal.math.Randoms.nextShort;
import static io.jenetics.util.RandomRegistry.using;
import static io.jenetics.xml.stream.Reader.text;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.random.RandomGenerator;
import java.util.stream.Stream;

import javax.xml.stream.XMLStreamException;

import io.jenetics.BitChromosome;
import io.jenetics.BitGene;
import io.jenetics.CharacterChromosome;
import io.jenetics.CharacterGene;
import io.jenetics.DoubleChromosome;
import io.jenetics.DoubleGene;
import io.jenetics.EnumGene;
import io.jenetics.Genotype;
import io.jenetics.IntegerChromosome;
import io.jenetics.IntegerGene;
import io.jenetics.LongChromosome;
import io.jenetics.LongGene;
import io.jenetics.PermutationChromosome;
import io.jenetics.prngine.LCG64ShiftRandom;
import io.jenetics.util.ISeq;
import io.jenetics.util.RandomRegistry;
import io.jenetics.xml.stream.AutoCloseableXMLStreamWriter;
import io.jenetics.xml.stream.Reader;
import io.jenetics.xml.stream.Writer;
import io.jenetics.xml.stream.XML;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class PersistentObject<T> {

	private final String _name;
	private final T _value;
	private final Writer<? super T> _writer;
	private final Reader<? extends T> _reader;

	public PersistentObject(
		final String name,
		final T value,
		final Writer<? super T> writer,
		final Reader<? extends T> reader
	) {
		_name = requireNonNull(name);
		_value = requireNonNull(value);
		_writer = requireNonNull(writer);
		_reader = requireNonNull(reader);
	}

	public String getName() {
		return _name;
	}

	public T getValue() {
		return _value;
	}

	public Writer<? super T> getWriter() {
		return _writer;
	}

	public Reader<? extends T> getReader() {
		return _reader;
	}

	public void write(final File baseDir)
		throws IOException, XMLStreamException
	{
		final File file = new File(baseDir, format("%s.xml", _name));
		try (FileOutputStream fout = new FileOutputStream(file);
			 BufferedOutputStream bout = new BufferedOutputStream(fout);
			 AutoCloseableXMLStreamWriter writer = XML.writer(bout, "    "))
		{
			Writer.doc(_writer).write(writer, _value);
		}
	}

	@Override
	public String toString() {
		return format("%s[%s]", getClass().getSimpleName(), getName());
	}

	private static final long SEED = 101010101010101L;

	public static final List<PersistentObject<?>> VALUES = new ArrayList<>();

	private static <T> void put(
		final String name,
		final T value,
		final Writer<? super T> writer,
		final Reader<? extends T> reader
	) {
		VALUES.add(new PersistentObject<>(name, value, writer, reader));
		RandomRegistry.random(new LCG64ShiftRandom(SEED));
	}

	private static void init() {

		/* *********************************************************************
		 * Chromosomes
		 **********************************************************************/

		put("BitChromosome", nextBitChromosome(),
			Writers.BitChromosome.writer(),
			Readers.BitChromosome.reader());
		put("CharacterChromosome", nextCharacterChromosome(),
			Writers.CharacterChromosome.writer(),
			Readers.CharacterChromosome.reader());
		put("IntegerChromosome", nextIntegerChromosome(),
			Writers.IntegerChromosome.writer(),
			Readers.IntegerChromosome.reader());
		put("LongChromosome", nextLongChromosome(),
			Writers.LongChromosome.writer(),
			Readers.LongChromosome.reader());
		put("DoubleChromosome", nextDoubleChromosome(),
			Writers.DoubleChromosome.writer(),
			Readers.DoubleChromosome.reader());

		put("PermutationChromosome[Byte]", nextBytePermutationChromosome(),
			Writers.PermutationChromosome.writer(),
			Readers.PermutationChromosome.reader(text().map(Byte::parseByte)));
		put("PermutationChromosome[Short]", nextShortPermutationChromosome(),
			Writers.PermutationChromosome.writer(),
			Readers.PermutationChromosome.reader(text().map(Short::parseShort)));
		put("PermutationChromosome[Integer]", nextIntegerPermutationChromosome(),
			Writers.PermutationChromosome.writer(),
			Readers.PermutationChromosome.reader(text().map(Integer::parseInt)));
		put("PermutationChromosome[Long]", nextLongPermutationChromosome(),
			Writers.PermutationChromosome.writer(),
			Readers.PermutationChromosome.reader(text().map(Long::parseLong)));
		put("PermutationChromosome[Float]", nextFloatPermutationChromosome(),
			Writers.PermutationChromosome.writer(),
			Readers.PermutationChromosome.reader(text().map(Float::parseFloat)));
		put("PermutationChromosome[Double]", nextDoublePermutationChromosome(),
			Writers.PermutationChromosome.writer(),
			Readers.PermutationChromosome.reader(text().map(Double::parseDouble)));
		put("PermutationChromosome[Character]", nextCharacterPermutationChromosome(),
			Writers.PermutationChromosome.writer(),
			Readers.PermutationChromosome.reader(text().map(s -> s.charAt(0))));
		put("PermutationChromosome[String]", nextStringPermutationChromosome(),
			Writers.PermutationChromosome.writer(),
			Readers.PermutationChromosome.reader(text()));

		/* *********************************************************************
		 * Genotypes
		 **********************************************************************/

		put("Genotype[BitGene]", nextGenotypeBitGene(),
			Writers.Genotype.writer(Writers.BitChromosome.writer()),
			Readers.Genotype.reader(Readers.BitChromosome.reader()));
		put("Genotype[CharacterGene]", nextGenotypeCharacterGene(),
			Writers.Genotype.writer(Writers.CharacterChromosome.writer()),
			Readers.Genotype.reader(Readers.CharacterChromosome.reader()));
		put("Genotype[IntegerGene]", nextGenotypeIntegerGene(),
			Writers.Genotype.writer(Writers.IntegerChromosome.writer()),
			Readers.Genotype.reader(Readers.IntegerChromosome.reader()));
		put("Genotype[LongGene]", nextGenotypeLongGene(),
			Writers.Genotype.writer(Writers.LongChromosome.writer()),
			Readers.Genotype.reader(Readers.LongChromosome.reader()));
		put("Genotype[DoubleGene]", nextGenotypeDoubleGene(),
			Writers.Genotype.writer(Writers.DoubleChromosome.writer()),
			Readers.Genotype.reader(Readers.DoubleChromosome.reader()));

		put("Genotype[EnumGene[Byte]]", nextGenotypeEnumGeneByte(),
			Writers.Genotype.writer(Writers.PermutationChromosome.writer()),
			Readers.Genotype.reader(Readers.PermutationChromosome.reader(text().map(Byte::parseByte))));
		put("Genotype[EnumGene[Character]]", nextGenotypeEnumGeneCharacter(),
			Writers.Genotype.writer(Writers.PermutationChromosome.writer()),
			Readers.Genotype.reader(Readers.PermutationChromosome.reader(text().map(s -> s.charAt(0)))));
		put("Genotype[EnumGene[Short]]", nextGenotypeEnumGeneShort(),
			Writers.Genotype.writer(Writers.PermutationChromosome.writer()),
			Readers.Genotype.reader(Readers.PermutationChromosome.reader(text().map(Short::parseShort))));
		put("Genotype[EnumGene[Integer]]", nextGenotypeEnumGeneInteger(),
			Writers.Genotype.writer(Writers.PermutationChromosome.writer()),
			Readers.Genotype.reader(Readers.PermutationChromosome.reader(text().map(Integer::parseInt))));
		put("Genotype[EnumGene[Long]]", nextGenotypeEnumGeneLong(),
			Writers.Genotype.writer(Writers.PermutationChromosome.writer()),
			Readers.Genotype.reader(Readers.PermutationChromosome.reader(text().map(Long::parseLong))));
		put("Genotype[EnumGene[Float]]", nextGenotypeEnumGeneFloat(),
			Writers.Genotype.writer(Writers.PermutationChromosome.writer()),
			Readers.Genotype.reader(Readers.PermutationChromosome.reader(text().map(Float::parseFloat))));
		put("Genotype[EnumGene[Double]]", nextGenotypeEnumGeneDouble(),
			Writers.Genotype.writer(Writers.PermutationChromosome.writer()),
			Readers.Genotype.reader(Readers.PermutationChromosome.reader(text().map(Double::parseDouble))));
		put("Genotype[EnumGene[String]]", nextGenotypeEnumGeneString(),
			Writers.Genotype.writer(Writers.PermutationChromosome.writer()),
			Readers.Genotype.reader(Readers.PermutationChromosome.reader(text())));

		/* *********************************************************************
		 * Populations
		 **********************************************************************/

		put("Genotypes[BitGene]", nextPopulationBitGene(),
			Writers.Genotypes.writer(Writers.BitChromosome.writer()),
			Readers.Genotypes.reader(Readers.BitChromosome.reader()));
		put("Genotypes[CharacterGene]", nextPopulationCharacterGene(),
			Writers.Genotypes.writer(Writers.CharacterChromosome.writer()),
			Readers.Genotypes.reader(Readers.CharacterChromosome.reader()));
		put("Genotypes[IntegerGene]", nextPopulationIntegerGene(),
			Writers.Genotypes.writer(Writers.IntegerChromosome.writer()),
			Readers.Genotypes.reader(Readers.IntegerChromosome.reader()));
		put("Genotypes[LongGene]", nextPopulationLongGene(),
			Writers.Genotypes.writer(Writers.LongChromosome.writer()),
			Readers.Genotypes.reader(Readers.LongChromosome.reader()));
		put("Genotypes[DoubleGene]", nextPopulationDoubleGene(),
			Writers.Genotypes.writer(Writers.DoubleChromosome.writer()),
			Readers.Genotypes.reader(Readers.DoubleChromosome.reader()));
		put("Genotypes[EnumGene[Integer]]", nextPopulationEnumGene(),
			Writers.Genotypes.writer(Writers.PermutationChromosome.writer()),
			Readers.Genotypes.reader(Readers.PermutationChromosome.reader(text().map(Integer::parseInt))));
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
		return EnumGene.of(ISeq.of(() -> nextChar(random()), 5));
	}

	public static EnumGene<Short> nextEnumGeneShort() {
		return EnumGene.of(ISeq.of(() -> nextShort(random()), 5));
	}

	public static EnumGene<Integer> nextEnumGeneInteger() {
		return EnumGene.of(ISeq.of(random()::nextInt, 5));
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
		return EnumGene.of(ISeq.of(() -> nextASCIIString(random()), 5));
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
		return PermutationChromosome.of(ISeq.of(() -> nextChar(random()), 15));
	}

	public static PermutationChromosome<String> nextStringPermutationChromosome() {
		return PermutationChromosome.of(ISeq.of(() -> nextASCIIString(random()), 15));
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

	public static List<Genotype<BitGene>> nextPopulationBitGene() {
		return Stream.generate(PersistentObject::nextGenotypeBitGene)
			.limit(7)
			.toList();
	}

	public static List<Genotype<CharacterGene>> nextPopulationCharacterGene() {
		return Stream.generate(PersistentObject::nextGenotypeCharacterGene)
			.limit(7)
			.toList();
	}

	public static List<Genotype<IntegerGene>> nextPopulationIntegerGene() {
		return Stream.generate(PersistentObject::nextGenotypeIntegerGene)
			.limit(7)
			.toList();
	}

	public static List<Genotype<LongGene>> nextPopulationLongGene() {
		return Stream.generate(PersistentObject::nextGenotypeLongGene)
			.limit(7)
			.toList();
	}

	public static List<Genotype<DoubleGene>> nextPopulationDoubleGene() {
		return Stream.generate(PersistentObject::nextGenotypeDoubleGene)
			.limit(7)
			.toList();
	}

	public static List<Genotype<EnumGene<Integer>>> nextPopulationEnumGene() {
		return Stream.generate(PersistentObject::nextGenotypeEnumGeneInteger)
			.limit(7)
			.toList();
	}


	private static RandomGenerator random() {
		return RandomRegistry.random();
	}

	static {
		final var random = new LCG64ShiftRandom(SEED);
		using(random, r -> init());
	}

	public static void main(final String[] args) throws Exception {
		write();
	}

	private static void write() throws IOException, XMLStreamException {
		final File baseDir = new File("jenetics.xml/src/test/resources/serialization");
		if (!baseDir.isDirectory() && !baseDir.mkdirs()) {
			throw new IOException("Error while creating directory " + baseDir);
		}

		for (PersistentObject<?> object : VALUES) {
			object.write(baseDir);
		}
	}

}

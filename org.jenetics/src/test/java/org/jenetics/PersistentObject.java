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
package org.jenetics;

import static org.jenetics.util.RandomUtils.BigDecimalFactory;
import static org.jenetics.util.RandomUtils.BigIntegerFactory;
import static org.jenetics.util.RandomUtils.BooleanFactory;
import static org.jenetics.util.RandomUtils.ByteFactory;
import static org.jenetics.util.RandomUtils.CharacterFactory;
import static org.jenetics.util.RandomUtils.DoubleFactory;
import static org.jenetics.util.RandomUtils.FloatFactory;
import static org.jenetics.util.RandomUtils.ISeq;
import static org.jenetics.util.RandomUtils.IntegerFactory;
import static org.jenetics.util.RandomUtils.LongFactory;
import static org.jenetics.util.RandomUtils.ShortFactory;
import static org.jenetics.util.RandomUtils.StringFactory;
import static org.jenetics.util.lambda.factory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import org.jenetics.util.Array;
import org.jenetics.util.Duration;
import org.jenetics.util.Factory;
import org.jenetics.util.Function;
import org.jenetics.util.IO;
import org.jenetics.util.ISeq;
import org.jenetics.util.LCG64ShiftRandom;
import org.jenetics.util.RandomRegistry;
import org.jenetics.util.Scoped;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date: 2014-04-09 $</em>
 */
public class PersistentObject<T> {

	public static final class Marshalling {
		public final String name;
		public final IO io;

		public Marshalling(final String name, final IO io) {
			this.name = name;
			this.io = io;
		}

		public void write(final File dir, final PersistentObject<?> object)
			throws IOException
		{
			final File file = new File(dir, object.getName() + "." + name);
			System.out.println(String.format("Write '%s'", file));
			try (FileOutputStream out = new FileOutputStream(file)) {
				io.write(object.getValue(), out);
			}
		}

		public static Marshalling of(final String name) {
			switch (name) {
				case "jaxb": return new Marshalling(name, IO.jaxb);
				case "object": return new Marshalling(name, IO.object);
				default: throw new IllegalArgumentException(name);
			}
		}

		@Override
		public String toString() {
			return String.format("IO[%s]", name);
		}
	}

	private static final Function<String, Marshalling> ToMarshalling =
		new Function<String, Marshalling>() {
			@Override
			public Marshalling apply(final String value) {
				return Marshalling.of(value);
			}
		};

	private final String _name;
	private final T _value;
	private final ISeq<Marshalling> _marshallings;

	public PersistentObject(final String name, final T value, final String... ios) {
		_name = Objects.requireNonNull(name);
		_value = Objects.requireNonNull(value);
		_marshallings = Array.of(ios).map(ToMarshalling).toISeq();
	}

	public String getName() {
		return _name;
	}

	public T getValue() {
		return _value;
	}

	public ISeq<Marshalling> getMarshallings() {
		return _marshallings;
	}

	@Override
	public String toString() {
		return String.format("%s[%s]", getClass().getSimpleName(), getName());
	}

	private static final long SEED = 101010101010101L;

	public static final List<PersistentObject<?>> VALUES = new ArrayList<>();

	private static <T> void put(final String name, final T value, final String... ios) {
		VALUES.add(new PersistentObject<T>(name, value, ios));
		RandomRegistry.getRandom().setSeed(SEED);
	}

	private static void init() {
		/* *********************************************************************
		 * Genes
		 **********************************************************************/

		final String[] ios = {"object", "jaxb"};

		put("BitGene_true", BitGene.TRUE, ios);
		put("BitGene_false", BitGene.FALSE, ios);
		put("CharacterGene", nextCharacterGene(), ios);
		put("IntegerGene", nextIntegerGene(), ios);
		put("LongGene", nextLongGene(), ios);
		put("DoubleGene", nextDoubleGene(), ios);

		put("EnumGene[Boolean]", nextEnumGeneBoolean(), ios);
		put("EnumGene[Byte]", nextEnumGeneByte(), ios);
		put("EnumGene[Character]", nextEnumGeneCharacter(), ios);
		put("EnumGene[Short]", nextEnumGeneShort(), ios);
		put("EnumGene[Integer]", nextEnumGeneInteger(), ios);
		put("EnumGene[Long]", nextEnumGeneLong(), ios);
		put("EnumGene[Float]", nextEnumGeneFloat(), ios);
		put("EnumGene[Double]", nextEnumGeneDouble(), ios);
		put("EnumGene[BigInteger]", nextEnumGeneBigInteger(), ios);
		put("EnumGene[BigDecimal]", nextEnumGeneBigDecimal(), ios);
		put("EnumGene[String]", nextEnumGeneString(), ios);

		/* *********************************************************************
		 * Chromosomes
		 **********************************************************************/

		put("BitChromosome", nextBitChromosome(), ios);
		put("CharacterChromosome", nextCharacterChromosome(), ios);
		put("IntegerChromosome", nextIntegerChromosome(), ios);
		put("LongChromosome", nextLongChromosome(), ios);
		put("DoubleChromosome", nextDoubleChromosome(), ios);

		put("PermutationChromosome[Byte]", nextBytePermutationChromosome(), ios);
		put("PermutationChromosome[Short]", nextShortPermutationChromosome(), ios);
		put("PermutationChromosome[Integer]", nextIntegerPermutationChromosome(), ios);
		put("PermutationChromosome[Long]", nextLongPermutationChromosome(), ios);
		put("PermutationChromosome[Float]", nextFloatPermutationChromosome(), ios);
		put("PermutationChromosome[Double]", nextDoublePermutationChromosome(), ios);
		put("PermutationChromosome[Character]", nextCharacterPermutationChromosome(), ios);
		put("PermutationChromosome[String]", nextStringPermutationChromosome(), ios);

		/* *********************************************************************
		 * Genotypes
		 **********************************************************************/

		put("Genotype[BitGene]", nextGenotypeBitGene(), ios);
		put("Genotype[CharacterGene]", nextGenotypeCharacterGene(), ios);
		put("Genotype[IntegerGene]", nextGenotypeIntegerGene(), ios);
		put("Genotype[LongGene]", nextGenotypeLongGene(), ios);
		put("Genotype[DoubleGene]", nextGenotypeDoubleGene(), ios);
		put("Genotype[EnumGene[Byte]]", nextGenotypeEnumGeneByte(), ios);
		put("Genotype[EnumGene[Character]]", nextGenotypeEnumGeneCharacter(), ios);
		put("Genotype[EnumGene[Short]]", nextGenotypeEnumGeneShort(), ios);
		put("Genotype[EnumGene[Integer]]", nextGenotypeEnumGeneInteger(), ios);
		put("Genotype[EnumGene[Long]]", nextGenotypeEnumGeneLong(), ios);
		put("Genotype[EnumGene[Float]]", nextGenotypeEnumGeneFloat(), ios);
		put("Genotype[EnumGene[Double]]", nextGenotypeEnumGeneDouble(), ios);
		put("Genotype[EnumGene[String]]", nextGenotypeEnumGeneString(), ios);

		/* *********************************************************************
		 * Phenotypes
		 **********************************************************************/

		put("Phenotype[BitGene, Integer]", nextPhenotypeBitGeneInteger(), ios);
		put("Phenotype[CharacterGene, Integer]", nextPhenotypeCharacterGeneInteger(), ios);

		put("Phenotype[IntegerGene, Integer]", nextPhenotypeIntegerGeneInteger(), ios);
		put("Phenotype[IntegerGene, Long]", nextPhenotypeIntegerGeneLong(), ios);
		put("Phenotype[IntegerGene, Double]", nextPhenotypeIntegerGeneDouble(), ios);

		put("Phenotype[LongGene, Integer]", nextPhenotypeLongGeneInteger(), ios);
		put("Phenotype[LongGene, Long]", nextPhenotypeLongGeneLong(), ios);
		put("Phenotype[LongGene, Double]", nextPhenotypeLongGeneDouble(), ios);

		put("Phenotype[DoubleGene, Integer]", nextPhenotypeDoubleGeneInteger(), ios);
		put("Phenotype[DoubleGene, Long]", nextPhenotypeDoubleGeneLong(), ios);
		put("Phenotype[DoubleGene, Double]", nextPhenotypeDoubleGeneDouble(), ios);

		put("Phenotype[EnumGene[Character], Double]", nextPhenotypeEnumGeneCharacterDouble(), ios);
		put("Phenotype[EnumGene[Integer], Double]", nextPhenotypeEnumGeneIntegerDouble(), ios);
		put("Phenotype[EnumGene[Long], Double]", nextPhenotypeEnumGeneLongDouble(), ios);
		put("Phenotype[EnumGene[Float], Double]", nextPhenotypeEnumGeneFloatDouble(), ios);
		put("Phenotype[EnumGene[Double], Double]", nextPhenotypeEnumGeneDoubleDouble(), ios);
		put("Phenotype[EnumGene[String], BigDecimal]", nextPhenotypeEnumGeneStringBigDecimal(), ios);

		/* *********************************************************************
		 * Populations
		 **********************************************************************/

		put("Population[BitGene, Integer]", nextPopulationBitGeneInteger(), ios);
		put("Population[CharacterGene, Integer]", nextPopulationCharacterGeneInteger(), ios);
		put("Population[IntegerGene, Integer]", nextPopulationIntegerGeneInteger(), ios);
		put("Population[LongGene, Integer]", nextPopulationLongGeneInteger(), ios);
		put("Population[DoubleGene, Integer]", nextPopulationDoubleGeneInteger(), ios);
		put("Population[EnumGene[Integer], Double]", nextPopulationEnumGeneIntegerDouble(), ios);
		put("Population[EnumGene[String], BigDecimal]", nextPopulationEnumGeneStringBigDecimal(), ios);

		//put("Statistics.Time", nextStatisticsTime());

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
		return EnumGene.of(ISeq(5, BooleanFactory));
	}

	public static EnumGene<Byte> nextEnumGeneByte() {
		return EnumGene.of(ISeq(5, ByteFactory));
	}

	public static EnumGene<Character> nextEnumGeneCharacter() {
		return EnumGene.of(ISeq(5, CharacterFactory));
	}

	public static EnumGene<Short> nextEnumGeneShort() {
		return EnumGene.of(ISeq(5, ShortFactory));
	}

	public static EnumGene<Integer> nextEnumGeneInteger() {
		return EnumGene.of(ISeq(5, IntegerFactory));
	}

	public static EnumGene<Long> nextEnumGeneLong() {
		return EnumGene.of(ISeq(5, LongFactory));
	}

	public static EnumGene<Float> nextEnumGeneFloat() {
		return EnumGene.of(ISeq(5, FloatFactory));
	}

	public static EnumGene<Double> nextEnumGeneDouble() {
		return EnumGene.of(ISeq(5, DoubleFactory));
	}

	public static EnumGene<BigInteger> nextEnumGeneBigInteger() {
		return EnumGene.of(ISeq(5, BigIntegerFactory));
	}

	public static EnumGene<BigDecimal> nextEnumGeneBigDecimal() {
		return EnumGene.of(ISeq(5, BigDecimalFactory));
	}

	public static EnumGene<String> nextEnumGeneString() {
		return EnumGene.of(ISeq(5, StringFactory));
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
		return PermutationChromosome.of(ISeq(15, ByteFactory));
	}

	public static PermutationChromosome<Short> nextShortPermutationChromosome() {
		return PermutationChromosome.of(ISeq(15, ShortFactory));
	}

	public static PermutationChromosome<Integer> nextIntegerPermutationChromosome() {
		return PermutationChromosome.ofInteger(15);
	}

	public static PermutationChromosome<Long> nextLongPermutationChromosome() {
		return PermutationChromosome.of(ISeq(15, LongFactory));
	}

	public static PermutationChromosome<Float> nextFloatPermutationChromosome() {
		return PermutationChromosome.of(ISeq(15, FloatFactory));
	}

	public static PermutationChromosome<Double> nextDoublePermutationChromosome() {
		return PermutationChromosome.of(ISeq(15, DoubleFactory));
	}

	public static PermutationChromosome<Character> nextCharacterPermutationChromosome() {
		return PermutationChromosome.of(ISeq(15, CharacterFactory));
	}

	public static PermutationChromosome<String> nextStringPermutationChromosome() {
		return PermutationChromosome.of(ISeq(15, StringFactory));
	}


	/* *************************************************************************
	 * Genotypes
	 **************************************************************************/

	public static Genotype<BitGene> nextGenotypeBitGene() {
		return new Genotype<>(ISeq(5, BitChromosomeFactory));
	}

	public static Genotype<CharacterGene> nextGenotypeCharacterGene() {
		return new Genotype<>(ISeq(5, CharacterChromosomeFactory));
	}

	public static Genotype<IntegerGene> nextGenotypeIntegerGene() {
		return new Genotype<>(ISeq(5, IntegerChromosomeFactory));
	}

	public static Genotype<LongGene> nextGenotypeLongGene() {
		return new Genotype<>(ISeq(5, LongChromosomeFactory));
	}

	public static Genotype<DoubleGene> nextGenotypeDoubleGene() {
		return new Genotype<>(ISeq(5, DoubleChromosomeFactory));
	}

	public static Genotype<EnumGene<Byte>> nextGenotypeEnumGeneByte() {
		return new Genotype<>(ISeq(5, PermutationChromosomeByteFactory));
	}

	public static Genotype<EnumGene<Character>> nextGenotypeEnumGeneCharacter() {
		return new Genotype<>(ISeq(5, PermutationChromosomeCharacterFactory));
	}

	public static Genotype<EnumGene<Short>> nextGenotypeEnumGeneShort() {
		return new Genotype<>(ISeq(5, PermutationChromosomeShortFactory));
	}

	public static Genotype<EnumGene<Integer>> nextGenotypeEnumGeneInteger() {
		return new Genotype<>(ISeq(5, PermutationChromosomeIntegerFactory));
	}

	public static Genotype<EnumGene<Long>> nextGenotypeEnumGeneLong() {
		return new Genotype<>(ISeq(5, PermutationChromosomeLongFactory));
	}

	public static Genotype<EnumGene<Float>> nextGenotypeEnumGeneFloat() {
		return new Genotype<>(ISeq(5, PermutationChromosomeFloatFactory));
	}

	public static Genotype<EnumGene<Double>> nextGenotypeEnumGeneDouble() {
		return new Genotype<>(ISeq(5, PermutationChromosomeDoubleFactory));
	}

	public static Genotype<EnumGene<String>> nextGenotypeEnumGeneString() {
		return new Genotype<>(ISeq(5, PermutationChromosomeStringFactory));
	}

	/* *************************************************************************
	 * Phenotypes
	 **************************************************************************/

	public static Phenotype<BitGene, Integer> nextPhenotypeBitGeneInteger() {
		return Phenotype.of(
			nextGenotypeBitGene(),
			FitnessFunction(IntegerFactory.newInstance()),
			Math.abs(IntegerFactory.newInstance())
		).evaluate();
	}

	public static Phenotype<CharacterGene, Integer> nextPhenotypeCharacterGeneInteger() {
		return Phenotype.of(
			nextGenotypeCharacterGene(),
			FitnessFunction(IntegerFactory.newInstance()),
			Math.abs(IntegerFactory.newInstance())
		).evaluate();
	}

	public static Phenotype<IntegerGene, Integer> nextPhenotypeIntegerGeneInteger() {
		return Phenotype.of(
			nextGenotypeIntegerGene(),
			FitnessFunction(IntegerFactory.newInstance()),
			Math.abs(IntegerFactory.newInstance())
		).evaluate();
	}

	public static Phenotype<IntegerGene, Long> nextPhenotypeIntegerGeneLong() {
		return Phenotype.of(
			nextGenotypeIntegerGene(),
			FitnessFunction(LongFactory.newInstance()),
			Math.abs(IntegerFactory.newInstance())
		).evaluate();
	}

	public static Phenotype<IntegerGene, Double> nextPhenotypeIntegerGeneDouble() {
		return Phenotype.of(
			nextGenotypeIntegerGene(),
			FitnessFunction(DoubleFactory.newInstance()),
			Math.abs(IntegerFactory.newInstance())
		).evaluate();
	}

	public static Phenotype<LongGene, Integer> nextPhenotypeLongGeneInteger() {
		return Phenotype.of(
			nextGenotypeLongGene(),
			FitnessFunction(IntegerFactory.newInstance()),
			Math.abs(IntegerFactory.newInstance())
		).evaluate();
	}

	public static Phenotype<LongGene, Long> nextPhenotypeLongGeneLong() {
		return Phenotype.of(
			nextGenotypeLongGene(),
			FitnessFunction(LongFactory.newInstance()),
			Math.abs(IntegerFactory.newInstance())
		).evaluate();
	}

	public static Phenotype<LongGene, Double> nextPhenotypeLongGeneDouble() {
		return Phenotype.of(
			nextGenotypeLongGene(),
			FitnessFunction(DoubleFactory.newInstance()),
			Math.abs(IntegerFactory.newInstance())
		).evaluate();
	}

	public static Phenotype<DoubleGene, Integer> nextPhenotypeDoubleGeneInteger() {
		return Phenotype.of(
			nextGenotypeDoubleGene(),
			FitnessFunction(IntegerFactory.newInstance()),
			Math.abs(IntegerFactory.newInstance())
		).evaluate();
	}

	public static Phenotype<DoubleGene, Long> nextPhenotypeDoubleGeneLong() {
		return Phenotype.of(
			nextGenotypeDoubleGene(),
			FitnessFunction(LongFactory.newInstance()),
			Math.abs(IntegerFactory.newInstance())
		).evaluate();
	}

	public static Phenotype<DoubleGene, Double> nextPhenotypeDoubleGeneDouble() {
		return Phenotype.of(
			nextGenotypeDoubleGene(),
			FitnessFunction(DoubleFactory.newInstance()),
			Math.abs(IntegerFactory.newInstance())
		).evaluate();
	}

	public static Phenotype<EnumGene<Character>, Double> nextPhenotypeEnumGeneCharacterDouble() {
		return Phenotype.of(
			nextGenotypeEnumGeneCharacter(),
			FitnessFunction(DoubleFactory.newInstance()),
			Math.abs(IntegerFactory.newInstance())
		).evaluate();
	}

	public static Phenotype<EnumGene<Integer>, Double> nextPhenotypeEnumGeneIntegerDouble() {
		return Phenotype.of(
			nextGenotypeEnumGeneInteger(),
			FitnessFunction(DoubleFactory.newInstance()),
			Math.abs(IntegerFactory.newInstance())
		).evaluate();
	}

	public static Phenotype<EnumGene<Long>, Double> nextPhenotypeEnumGeneLongDouble() {
		return Phenotype.of(
			nextGenotypeEnumGeneLong(),
			FitnessFunction(DoubleFactory.newInstance()),
			Math.abs(IntegerFactory.newInstance())
		).evaluate();
	}

	public static Phenotype<EnumGene<Float>, Double> nextPhenotypeEnumGeneFloatDouble() {
		return Phenotype.of(
			nextGenotypeEnumGeneFloat(),
			FitnessFunction(DoubleFactory.newInstance()),
			Math.abs(IntegerFactory.newInstance())
		).evaluate();
	}

	public static Phenotype<EnumGene<Double>, Double> nextPhenotypeEnumGeneDoubleDouble() {
		return Phenotype.of(
			nextGenotypeEnumGeneDouble(),
			FitnessFunction(DoubleFactory.newInstance()),
			Math.abs(IntegerFactory.newInstance())
		).evaluate();
	}

	public static Phenotype<EnumGene<String>, BigDecimal> nextPhenotypeEnumGeneStringBigDecimal() {
		return Phenotype.of(
			nextGenotypeEnumGeneString(),
			FitnessFunction(BigDecimalFactory.newInstance()),
			Math.abs(IntegerFactory.newInstance())
		).evaluate();
	}

	/* *************************************************************************
	 * Populations
	 **************************************************************************/

	public static Population<BitGene, Integer> nextPopulationBitGeneInteger() {
		final ISeq<Phenotype<BitGene, Integer>> seq = ISeq(7,
			PersistentObject.<Phenotype<BitGene, Integer>>Factory(
				"nextPhenotypeBitGeneInteger"
			)
		);

		return new Population<>(seq.asList());
	}

	public static Population<CharacterGene, Integer> nextPopulationCharacterGeneInteger() {
		final ISeq<Phenotype<CharacterGene, Integer>> seq = ISeq(7,
			PersistentObject.<Phenotype<CharacterGene, Integer>>Factory(
				"nextPhenotypeCharacterGeneInteger"
			)
		);

		return new Population<>(seq.asList());
	}

	public static Population<IntegerGene, Integer> nextPopulationIntegerGeneInteger() {
		final ISeq<Phenotype<IntegerGene, Integer>> seq = ISeq(7,
			PersistentObject.<Phenotype<IntegerGene, Integer>>Factory(
				"nextPhenotypeIntegerGeneInteger"
			)
		);

		return new Population<>(seq.asList());
	}

	public static Population<LongGene, Integer> nextPopulationLongGeneInteger() {
		final ISeq<Phenotype<LongGene, Integer>> seq = ISeq(7,
			PersistentObject.<Phenotype<LongGene, Integer>>Factory(
				"nextPhenotypeLongGeneInteger"
			)
		);

		return new Population<>(seq.asList());
	}

	public static Population<DoubleGene, Integer> nextPopulationDoubleGeneInteger() {
		final ISeq<Phenotype<DoubleGene, Integer>> seq = ISeq(7,
			PersistentObject.<Phenotype<DoubleGene, Integer>>Factory(
				"nextPhenotypeDoubleGeneInteger"
			)
		);

		return new Population<>(seq.asList());
	}

	public static Population<EnumGene<Integer>, Double> nextPopulationEnumGeneIntegerDouble() {
		final ISeq<Phenotype<EnumGene<Integer>, Double>> seq = ISeq(7,
			PersistentObject.<Phenotype<EnumGene<Integer>, Double>>Factory(
				"nextPhenotypeEnumGeneIntegerDouble"
			)
		);

		return new Population<>(seq.asList());
	}

	public static Population<EnumGene<String>, BigDecimal> nextPopulationEnumGeneStringBigDecimal() {
		final ISeq<Phenotype<EnumGene<String>, BigDecimal>> seq = ISeq(7,
			PersistentObject.<Phenotype<EnumGene<String>, BigDecimal>>Factory(
				"nextPhenotypeEnumGeneStringBigDecimal"
			)
		);

		return new Population<>(seq.asList());
	}

	/* *************************************************************************
	 * Statistics
	 **************************************************************************/

	public static Statistics.Time nextStatisticsTime() {
		final Random random = RandomRegistry.getRandom();

		final Statistics.Time time = new Statistics.Time();
		time.alter.set(Duration.ofSeconds(random.nextInt(1233)));
		time.combine.set(Duration.ofSeconds(random.nextInt(1233)));
		time.evaluation.set(Duration.ofSeconds(random.nextInt(1233)));
		time.execution.set(Duration.ofSeconds(random.nextInt(1233)));
		time.selection.set(Duration.ofSeconds(random.nextInt(1233)));
		time.statistics.set(Duration.ofSeconds(random.nextInt(1233)));
		return time;
	}

	/* *************************************************************************
	 * Factories
	 **************************************************************************/

	private static <T> Factory<T> Factory(final String name) {
		return factory(PersistentObject.class, name);
	}

	public static final Factory<BitChromosome> BitChromosomeFactory = factory(
		PersistentObject.class, "nextBitChromosome"
	);

	public static final Factory<CharacterChromosome> CharacterChromosomeFactory = factory(
		PersistentObject.class, "nextCharacterChromosome"
	);

	public static final Factory<IntegerChromosome> IntegerChromosomeFactory = factory(
		PersistentObject.class, "nextIntegerChromosome"
	);

	public static final Factory<LongChromosome> LongChromosomeFactory = factory(
		PersistentObject.class, "nextLongChromosome"
	);

	public static final Factory<DoubleChromosome> DoubleChromosomeFactory = factory(
		PersistentObject.class, "nextDoubleChromosome"
	);

	public static final Factory<PermutationChromosome<Byte>>
	PermutationChromosomeByteFactory = factory(
		PersistentObject.class, "nextBytePermutationChromosome"
	);

	public static final Factory<PermutationChromosome<Character>>
	PermutationChromosomeCharacterFactory = factory(
		PersistentObject.class, "nextCharacterPermutationChromosome"
	);

	public static final Factory<PermutationChromosome<Short>>
	PermutationChromosomeShortFactory = factory(
		PersistentObject.class, "nextShortPermutationChromosome"
	);

	public static final Factory<PermutationChromosome<Integer>>
	PermutationChromosomeIntegerFactory = factory(
		PersistentObject.class, "nextIntegerPermutationChromosome"
	);

	public static final Factory<PermutationChromosome<Long>>
	PermutationChromosomeLongFactory = factory(
		PersistentObject.class, "nextLongPermutationChromosome"
	);

	public static final Factory<PermutationChromosome<Float>>
	PermutationChromosomeFloatFactory = factory(
		PersistentObject.class, "nextFloatPermutationChromosome"
	);

	public static final Factory<PermutationChromosome<Double>>
	PermutationChromosomeDoubleFactory = factory(
		PersistentObject.class, "nextDoublePermutationChromosome"
	);

	public static final Factory<PermutationChromosome<String>>
	PermutationChromosomeStringFactory = factory(
		PersistentObject.class, "nextStringPermutationChromosome"
	);

	public static <T, R extends Comparable<R>> Function<T, R>
	FitnessFunction(final R result) {
		return new Function<T, R>() {
			@Override
			public R apply(final T value) {
				return result;
			}
		};
	}

	static {
		final Random random = new LCG64ShiftRandom.ThreadSafe(SEED);
		try (Scoped<?> s = RandomRegistry.scope(random)) {
			init();
		}
	}


	public static void main(final String[] args) throws Exception {
		write();
		//IO.jaxb.write(nextPhenotypeEnumGeneIntegerDouble(), System.out);
	}

	private static void write() throws IOException {
		final File baseDir = new File("org.jenetics/src/test/resources/org/jenetics/serialization");
		if (!baseDir.isDirectory() && !baseDir.mkdirs()) {
			throw new IOException("Error while creating directory " + baseDir);
		}

		for (PersistentObject<?> object : VALUES) {
			for (Marshalling marshalling : object.getMarshallings()) {
				marshalling.write(baseDir, object);
			}
		}
	}

}

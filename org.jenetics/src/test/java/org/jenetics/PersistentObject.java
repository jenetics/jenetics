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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import javax.measure.Measure;
import javax.measure.unit.SI;

import javolution.context.LocalContext;

import org.jenetics.util.Array;
import org.jenetics.util.Factory;
import org.jenetics.util.Function;
import org.jenetics.util.IO;
import org.jenetics.util.ISeq;
import org.jenetics.util.LCG64ShiftRandom;
import org.jenetics.util.RandomRegistry;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date: 2014-03-11 $</em>
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

	public static List<PersistentObject<?>> VALUES = new ArrayList<>();

	private static <T> void put(final String name, final T value, final String... ios) {
		VALUES.add(new PersistentObject<T>(name, value, ios));
		RandomRegistry.getRandom().setSeed(SEED);
	}

	private static void init() {
		/* *********************************************************************
		 * Genes
		 **********************************************************************/

		final String[] ios = {"object", "jaxb"};

		put("BitGene[true]", BitGene.TRUE, ios);
		put("BitGene[false]", BitGene.FALSE, ios);
		put("CharacterGene", nextCharacterGene(), ios);
		put("LongGene", nextLongGene(), ios);
		put("DoubleGene", nextDoubleGene(), ios);

		put("EnumGene<Boolean>", nextEnumGeneBoolean(), ios);
		put("EnumGene<Byte>", nextEnumGeneByte(), ios);
		put("EnumGene<Character>", nextEnumGeneCharacter(), ios);
		put("EnumGene<Short>", nextEnumGeneShort(), ios);
		put("EnumGene<Integer>", nextEnumGeneInteger(), ios);
		put("EnumGene<Long>", nextEnumGeneLong(), ios);
		put("EnumGene<Float>", nextEnumGeneFloat(), ios);
		put("EnumGene<Double>", nextEnumGeneDouble(), ios);
		put("EnumGene<String>", nextEnumGeneString(), ios);

		/* *********************************************************************
		 * Chromosomes
		 **********************************************************************/

		put("BitChromosome", nextBitChromosome(), ios);
		put("CharacterChromosome", nextCharacterChromosome(), ios);
		put("LongChromosome", nextLongChromosome(), ios);
		put("DoubleChromosome", nextDoubleChromosome(), ios);

		put("PermutationChromosome<Integer>", nextIntegerPermutationChromosome(), ios);
		put("PermutationChromosome<Double>", nextDoublePermutationChromosome(), ios);
		put("PermutationChromosome<Character>", nextCharacterPermutationChromosome(), ios);
		put("PermutationChromosome<String>", nextStringPermutationChromosome(), ios);

		/* *********************************************************************
		 * Genotypes
		 **********************************************************************/

		put("Genotype<BitGene>", nextGenotypeBitGene(), ios);
		put("Genotype<CharacterGene>", nextGenotypeCharacterGene(), ios);
		put("Genotype<LongGene>", nextGenotypeLongGene(), ios);
		put("Genotype<DoubleGene>", nextGenotypeDoubleGene(), ios);


		/* *********************************************************************
		 * Phenotypes
		 **********************************************************************/

		put("Phenotype<LongGene, Integer>", nextPhenotypeLongGeneInteger(), ios);
		put("Phenotype<LongGene, Long>", nextPhenotypeLongGeneLong(), ios);
		put("Phenotype<LongGene, Double>", nextPhenotypeLongGeneDouble(), ios);

		put("Phenotype<DoubleGene, Integer>", nextPhenotypeDoubleGeneInteger(), ios);
		put("Phenotype<DoubleGene, Long>", nextPhenotypeDoubleGeneLong(), ios);
		put("Phenotype<DoubleGene, Double>", nextPhenotypeDoubleGeneDouble(), ios);

		/* *********************************************************************
		 * Populations
		 **********************************************************************/

		put("Population<LongGene, Integer>", nextPopulationLongGeneInteger(), ios);
		put("Population<DoubleGene, Integer>", nextPopulationDoubleGeneInteger(), ios);

		//put("Statistics.Time", nextStatisticsTime());

	}

	/* *************************************************************************
	 * Genes
	 **************************************************************************/

	public static CharacterGene nextCharacterGene() {
		return CharacterGene.of();
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

	public static LongChromosome nextLongChromosome() {
		return LongChromosome.of(Long.MIN_VALUE, Long.MAX_VALUE, 20);
	}

	public static DoubleChromosome nextDoubleChromosome() {
		return DoubleChromosome.of(0.0, 1.0, 20);
	}

	public static PermutationChromosome<Integer> nextIntegerPermutationChromosome() {
		return PermutationChromosome.ofInteger(15);
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

	public static Genotype<LongGene> nextGenotypeLongGene() {
		return new Genotype<>(ISeq(5, LongChromosomeFactory));
	}

	public static Genotype<DoubleGene> nextGenotypeDoubleGene() {
		return new Genotype<>(ISeq(5, DoubleChromosomeFactory));
	}

	/* *************************************************************************
	 * Phenotypes
	 **************************************************************************/

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

	/* *************************************************************************
	 * Populations
	 **************************************************************************/

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

	/* *************************************************************************
	 * Statistics
	 **************************************************************************/

	public static Statistics.Time nextStatisticsTime() {
		final Random random = RandomRegistry.getRandom();

		final Statistics.Time time = new Statistics.Time();
		time.alter.set(Measure.valueOf(random.nextInt(1233), SI.SECOND));
		time.combine.set(Measure.valueOf(random.nextInt(1233), SI.MILLI(SI.SECOND)));
		time.evaluation.set(Measure.valueOf(random.nextInt(1233), SI.MICRO(SI.SECOND)));
		time.execution.set(Measure.valueOf(random.nextInt(1233), SI.NANO(SI.SECOND)));
		time.selection.set(Measure.valueOf(random.nextInt(1233), SI.HECTO(SI.SECOND)));
		time.statistics.set(Measure.valueOf(random.nextInt(1233), SI.KILO(SI.SECOND)));
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

	public static final Factory<LongChromosome> LongChromosomeFactory = factory(
		PersistentObject.class, "nextLongChromosome"
	);

	public static final Factory<DoubleChromosome> DoubleChromosomeFactory = factory(
		PersistentObject.class, "nextDoubleChromosome"
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
		LocalContext.enter();
		try {
			RandomRegistry.setRandom(new LCG64ShiftRandom.ThreadSafe(SEED));
			init();
		} finally {
			LocalContext.exit();
		}
	}


	public static void main(final String[] args) throws Exception {
		write();
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

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
import static org.jenetics.util.RandomUtils.Float64Factory;
import static org.jenetics.util.RandomUtils.FloatFactory;
import static org.jenetics.util.RandomUtils.ISeq;
import static org.jenetics.util.RandomUtils.Integer64Factory;
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

import org.jscience.mathematics.number.Float64;
import org.jscience.mathematics.number.Integer64;

import org.jenetics.util.Array;
import org.jenetics.util.Factory;
import org.jenetics.util.Function;
import org.jenetics.util.IO;
import org.jenetics.util.ISeq;
import org.jenetics.util.LCG64ShiftRandom;
import org.jenetics.util.RandomRegistry;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date: 2014-03-06 $</em>
 */
@SuppressWarnings("deprecation")
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
				case "xml": return new Marshalling(name, IO.xml);
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

		final String[] oldIO = {"object", "xml", "jaxb"};
		final String[] newIO = {"object", "jaxb"};
		final String[] minIO = {"object", "jaxb"};

		put("BitGene[true]", BitGene.TRUE, oldIO);
		put("BitGene[false]", BitGene.FALSE, oldIO);
		put("CharacterGene", nextCharacterGene(), oldIO);
		put("LongGene", nextLongGene(), newIO);
		put("DoubleGene", nextDoubleGene(), newIO);
		put("Integer64Gene", nextInteger64Gene(), oldIO);
		put("Float64Gene", nextFloat64Gene(), oldIO);

		put("EnumGene<Boolean>", nextEnumGeneBoolean(), oldIO);
		put("EnumGene<Byte>", nextEnumGeneByte(), oldIO);
		put("EnumGene<Character>", nextEnumGeneCharacter(), oldIO);
		put("EnumGene<Short>", nextEnumGeneShort(), oldIO);
		put("EnumGene<Integer>", nextEnumGeneInteger(), oldIO);
		put("EnumGene<Long>", nextEnumGeneLong(), oldIO);
		put("EnumGene<Float>", nextEnumGeneFloat(), oldIO);
		put("EnumGene<Double>", nextEnumGeneDouble(), oldIO);
		put("EnumGene<String>", nextEnumGeneString(), oldIO);
		put("EnumGene<Float64>", nextEnumGeneFloat64(), oldIO);
		put("EnumGene<Integer64>", nextEnumGeneInteger64(), oldIO);

		/* *********************************************************************
		 * Chromosomes
		 **********************************************************************/

		put("BitChromosome", nextBitChromosome(), oldIO);
		put("CharacterChromosome", nextCharacterChromosome(), oldIO);
		put("LongChromosome", nextLongChromosome(), newIO);
		put("DoubleChromosome", nextDoubleChromosome(), newIO);
		put("Integer64Chromosome", nextInteger64Chromosome(), oldIO);
		put("Float64Chromosome", nextFloat64Chromosome(), oldIO);

		put("PermutationChromosome<Integer>", nextIntegerPermutationChromosome(), oldIO);
		put("PermutationChromosome<Double>", nextDoublePermutationChromosome(), oldIO);
		put("PermutationChromosome<Float64>", nextFloat64PermutationChromosome(), oldIO);
		put("PermutationChromosome<Character>", nextCharacterPermutationChromosome(), oldIO);
		put("PermutationChromosome<String>", nextStringPermutationChromosome(), oldIO);

		/* *********************************************************************
		 * Genotypes
		 **********************************************************************/

		put("Genotype<BitGene>", nextGenotypeBitGene(), oldIO);
		put("Genotype<CharacterGene>", nextGenotypeCharacterGene(), oldIO);
		put("Genotype<LongGene>", nextGenotypeLongGene(), minIO);
		put("Genotype<DoubleGene>", nextGenotypeDoubleGene(), minIO);
		put("Genotype<Integer64Gene>", nextGenotypeInteger64Gene(), oldIO);
		put("Genotype<Float64Gene>", nextGenotypeFloat64Gene(), oldIO);


		/* *********************************************************************
		 * Phenotypes
		 **********************************************************************/

		put("Phenotype<Integer64Gene, Integer>", nextPhenotypeInteger64GeneInteger(), oldIO);
		put("Phenotype<Integer64Gene, Long>", nextPhenotypeInteger64GeneLong(), oldIO);
		put("Phenotype<Integer64Gene, Double>", nextPhenotypeInteger64GeneDouble(), oldIO);

		put("Phenotype<LongGene, Integer>", nextPhenotypeLongGeneInteger(), minIO);
		put("Phenotype<LongGene, Long>", nextPhenotypeLongGeneLong(), minIO);
		put("Phenotype<LongGene, Double>", nextPhenotypeLongGeneDouble(), minIO);

		put("Phenotype<Integer64Gene, Integer64>", nextPhenotypeInteger64GeneInteger64(), oldIO);
		put("Phenotype<Integer64Gene, Float64>", nextPhenotypeInteger64GeneFloat64(), oldIO);

		put("Phenotype<DoubleGene, Integer>", nextPhenotypeDoubleGeneInteger(), minIO);
		put("Phenotype<DoubleGene, Long>", nextPhenotypeDoubleGeneLong(), minIO);
		put("Phenotype<DoubleGene, Double>", nextPhenotypeDoubleGeneDouble(), minIO);

		put("Phenotype<Float64Gene, Integer>", nextPhenotypeFloat64GeneInteger(), oldIO);
		put("Phenotype<Float64Gene, Long>", nextPhenotypeFloat64GeneLong(), oldIO);
		put("Phenotype<Float64Gene, Double>", nextPhenotypeFloat64GeneDouble(), oldIO);

		put("Phenotype<Float64Gene, Integer64>", nextPhenotypeFloat64GeneInteger64(), oldIO);
		put("Phenotype<Float64Gene, Float64>", nextPhenotypeFloat64GeneFloat64(), oldIO);

		/* *********************************************************************
		 * Populations
		 **********************************************************************/

		put("Population<Integer64Gene, Integer>", nextPopulationInteger64GeneInteger(), oldIO);
		put("Population<Float64Gene, Integer>", nextPopulationFloat64GeneInteger(), oldIO);

		put("Population<LongGene, Integer>", nextPopulationLongGeneInteger(), minIO);
		put("Population<DoubleGene, Integer>", nextPopulationDoubleGeneInteger(), minIO);

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

	public static Integer64Gene nextInteger64Gene() {
		return Integer64Gene.valueOf(Long.MIN_VALUE, Long.MAX_VALUE);
	}

	public static Float64Gene nextFloat64Gene() {
		return Float64Gene.valueOf(0, 1.0);
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

	public static EnumGene<Integer64> nextEnumGeneInteger64() {
		return EnumGene.of(ISeq(5, Integer64Factory));
	}

	public static EnumGene<Float64> nextEnumGeneFloat64() {
		return EnumGene.of(ISeq(5, Float64Factory));
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

	public static Integer64Chromosome nextInteger64Chromosome() {
		return new Integer64Chromosome(Long.MIN_VALUE, Long.MAX_VALUE, 20);
	}

	public static Float64Chromosome nextFloat64Chromosome() {
		return new Float64Chromosome(0.0, 1.0, 20);
	}

	public static PermutationChromosome<Integer> nextIntegerPermutationChromosome() {
		return PermutationChromosome.ofInteger(15);
	}

	public static PermutationChromosome<Double> nextDoublePermutationChromosome() {
		return new PermutationChromosome<>(ISeq(15, DoubleFactory));
	}

	public static PermutationChromosome<Float64> nextFloat64PermutationChromosome() {
		return new PermutationChromosome<>(ISeq(15, Float64Factory));
	}

	public static PermutationChromosome<Character> nextCharacterPermutationChromosome() {
		return new PermutationChromosome<>(ISeq(15, CharacterFactory));
	}

	public static PermutationChromosome<String> nextStringPermutationChromosome() {
		return new PermutationChromosome<>(ISeq(15, StringFactory));
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

	public static Genotype<Integer64Gene> nextGenotypeInteger64Gene() {
		return new Genotype<>(ISeq(5, Integer64ChromosomeFactory));
	}

	public static Genotype<Float64Gene> nextGenotypeFloat64Gene() {
		return new Genotype<>(ISeq(5, Float64ChromosomeFactory));
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

	public static Phenotype<Integer64Gene, Integer> nextPhenotypeInteger64GeneInteger() {
		return Phenotype.of(
			nextGenotypeInteger64Gene(),
			FitnessFunction(IntegerFactory.newInstance()),
			Math.abs(IntegerFactory.newInstance())
		).evaluate();
	}

	public static Phenotype<Integer64Gene, Long> nextPhenotypeInteger64GeneLong() {
		return Phenotype.of(
			nextGenotypeInteger64Gene(),
			FitnessFunction(LongFactory.newInstance()),
			Math.abs(IntegerFactory.newInstance())
		).evaluate();
	}

	public static Phenotype<Integer64Gene, Double> nextPhenotypeInteger64GeneDouble() {
		return Phenotype.of(
			nextGenotypeInteger64Gene(),
			FitnessFunction(DoubleFactory.newInstance()),
			Math.abs(IntegerFactory.newInstance())
		).evaluate();
	}

	public static Phenotype<Integer64Gene, Integer64> nextPhenotypeInteger64GeneInteger64() {
		return Phenotype.of(
			nextGenotypeInteger64Gene(),
			FitnessFunction(Integer64Factory.newInstance()),
			Math.abs(IntegerFactory.newInstance())
		).evaluate();
	}

	public static Phenotype<Integer64Gene, Float64> nextPhenotypeInteger64GeneFloat64() {
		return Phenotype.of(
			nextGenotypeInteger64Gene(),
			FitnessFunction(Float64Factory.newInstance()),
			Math.abs(IntegerFactory.newInstance())
		).evaluate();
	}

	public static Phenotype<Float64Gene, Integer> nextPhenotypeFloat64GeneInteger() {
		return Phenotype.of(
			nextGenotypeFloat64Gene(),
			FitnessFunction(IntegerFactory.newInstance()),
			Math.abs(IntegerFactory.newInstance())
		).evaluate();
	}

	public static Phenotype<Float64Gene, Long> nextPhenotypeFloat64GeneLong() {
		return Phenotype.of(
			nextGenotypeFloat64Gene(),
			FitnessFunction(LongFactory.newInstance()),
			Math.abs(IntegerFactory.newInstance())
		).evaluate();
	}

	public static Phenotype<Float64Gene, Double> nextPhenotypeFloat64GeneDouble() {
		return Phenotype.of(
			nextGenotypeFloat64Gene(),
			FitnessFunction(DoubleFactory.newInstance()),
			Math.abs(IntegerFactory.newInstance())
		).evaluate();
	}

	public static Phenotype<Float64Gene, Integer64> nextPhenotypeFloat64GeneInteger64() {
		return Phenotype.of(
			nextGenotypeFloat64Gene(),
			FitnessFunction(Integer64Factory.newInstance()),
			Math.abs(IntegerFactory.newInstance())
		).evaluate();
	}

	public static Phenotype<Float64Gene, Float64> nextPhenotypeFloat64GeneFloat64() {
		return Phenotype.of(
			nextGenotypeFloat64Gene(),
			FitnessFunction(Float64Factory.newInstance()),
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

	public static Population<Integer64Gene, Integer> nextPopulationInteger64GeneInteger() {
		final ISeq<Phenotype<Integer64Gene, Integer>> seq = ISeq(7,
			PersistentObject.<Phenotype<Integer64Gene, Integer>>Factory(
				"nextPhenotypeInteger64GeneInteger"
			)
		);

		return new Population<>(seq.asList());
	}

	public static Population<Float64Gene, Integer> nextPopulationFloat64GeneInteger() {
		final ISeq<Phenotype<Float64Gene, Integer>> seq = ISeq(7,
			PersistentObject.<Phenotype<Float64Gene, Integer>>Factory(
				"nextPhenotypeFloat64GeneInteger"
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

	public static final Factory<Integer64Chromosome> Integer64ChromosomeFactory = factory(
		PersistentObject.class, "nextInteger64Chromosome"
	);

	public static final Factory<Float64Chromosome> Float64ChromosomeFactory = factory(
		PersistentObject.class, "nextFloat64Chromosome"
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

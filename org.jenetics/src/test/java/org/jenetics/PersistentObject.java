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

import org.jenetics.util.Factory;
import org.jenetics.util.Function;
import org.jenetics.util.IO;
import org.jenetics.util.ISeq;
import org.jenetics.util.JSONIO;
import org.jenetics.util.LCG64ShiftRandom;
import org.jenetics.util.RandomRegistry;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version 1.6 &mdash; <em>$Date$</em>
 * @since 1.6
 */
public class PersistentObject<T> {

	private final String _name;
	private final T _value;

	public PersistentObject(final String name, final T value) {
		_name = Objects.requireNonNull(name);
		_value = Objects.requireNonNull(value);
	}

	public String getName() {
		return _name;
	}

	public T getValue() {
		return _value;
	}

	@Override
	public String toString() {
		return String.format("%s[%s]", getClass().getSimpleName(), getName());
	}

	private static final long SEED = 101010101010101L;

	public static List<PersistentObject<?>> VALUES = new ArrayList<>();

	private static <T> void put(final String name, final T value) {
		VALUES.add(new PersistentObject<T>(name, value));
		RandomRegistry.getRandom().setSeed(SEED);
	}

	private static void init() {
		/* *********************************************************************
		 * Genes
		 **********************************************************************/

		put("LongGene", nextLongGene());
		put("DoubleGene", nextDoubleGene());

//		put("BitGene[true]", BitGene.TRUE);
//		put("BitGene[false]", BitGene.FALSE);
//		put("CharacterGene", nextCharacterGene());
//		put("Integer64Gene", nextInteger64Gene());
//		put("Float64Gene", nextFloat64Gene());
//
//		put("EnumGene<Boolean>", nextEnumGeneBoolean());
//		put("EnumGene<Byte>", nextEnumGeneByte());
//		put("EnumGene<Character>", nextEnumGeneCharacter());
//		put("EnumGene<Short>", nextEnumGeneShort());
//		put("EnumGene<Integer>", nextEnumGeneInteger());
//		put("EnumGene<Long>", nextEnumGeneLong());
//		put("EnumGene<Float>", nextEnumGeneFloat());
//		put("EnumGene<Double>", nextEnumGeneDouble());
//		put("EnumGene<String>", nextEnumGeneString());
//		put("EnumGene<Float64>", nextEnumGeneFloat64());
//		put("EnumGene<Integer64>", nextEnumGeneInteger64());
//
//		/* *********************************************************************
//		 * Chromosomes
//		 **********************************************************************/
//
//		put("BitChromosome", nextBitChromosome());
//		put("CharacterChromosome", nextCharacterChromosome());
//		put("Integer64Chromosome", nextInteger64Chromosome());
//		put("Float64Chromosome", nextFloat64Chromosome());
//
//		put("PermutationChromosome<Integer>", nextIntegerPermutationChromosome());
//		put("PermutationChromosome<Double>", nextDoublePermutationChromosome());
//		put("PermutationChromosome<Float64>", nextFloat64PermutationChromosome());
//		put("PermutationChromosome<Character>", nextCharacterPermutationChromosome());
//		put("PermutationChromosome<String>", nextStringPermutationChromosome());
//
//		/* *********************************************************************
//		 * Genotypes
//		 **********************************************************************/
//
//		put("Genotype<BitGene>", nextGenotypeBitGene());
//		put("Genotype<CharacterGene>", nextGenotypeCharacterGene());
//		put("Genotype<Integer64Gene>", nextGenotypeInteger64Gene());
//		put("Genotype<Float64Gene>", nextGenotypeFloat64Gene());
//
//
//		/* *********************************************************************
//		 * Phenotypes
//		 **********************************************************************/
//
//		put("Phenotype<Integer64Gene, Integer>", nextPhenotypeInteger64GeneInteger());
//		put("Phenotype<Integer64Gene, Long>", nextPhenotypeInteger64GeneLong());
//		put("Phenotype<Integer64Gene, Double>", nextPhenotypeInteger64GeneDouble());
//		put("Phenotype<Integer64Gene, Integer64>", nextPhenotypeInteger64GeneInteger64());
//		put("Phenotype<Integer64Gene, Float64>", nextPhenotypeInteger64GeneFloat64());
//		put("Phenotype<Float64Gene, Integer>", nextPhenotypeFloat64GeneInteger());
//		put("Phenotype<Float64Gene, Long>", nextPhenotypeFloat64GeneLong());
//		put("Phenotype<Float64Gene, Double>", nextPhenotypeFloat64GeneDouble());
//		put("Phenotype<Float64Gene, Integer64>", nextPhenotypeFloat64GeneInteger64());
//		put("Phenotype<Float64Gene, Float64>", nextPhenotypeFloat64GeneFloat64());
//
//		/* *********************************************************************
//		 * Populations
//		 **********************************************************************/
//
//		put("Population<Integer64Gene, Integer>", nextPopulationInteger64GeneInteger());
//		put("Population<Float64Gene, Integer>", nextPopulationFloat64GeneInteger());

		//put("Statistics.Time", nextStatisticsTime());

	}

	/* *************************************************************************
	 * Genes
	 **************************************************************************/

	public static CharacterGene nextCharacterGene() {
		return CharacterGene.valueOf();
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
		return EnumGene.valueOf(ISeq(5, BooleanFactory));
	}

	public static EnumGene<Byte> nextEnumGeneByte() {
		return EnumGene.valueOf(ISeq(5, ByteFactory));
	}

	public static EnumGene<Character> nextEnumGeneCharacter() {
		return EnumGene.valueOf(ISeq(5, CharacterFactory));
	}

	public static EnumGene<Short> nextEnumGeneShort() {
		return EnumGene.valueOf(ISeq(5, ShortFactory));
	}

	public static EnumGene<Integer> nextEnumGeneInteger() {
		return EnumGene.valueOf(ISeq(5, IntegerFactory));
	}

	public static EnumGene<Long> nextEnumGeneLong() {
		return EnumGene.valueOf(ISeq(5, LongFactory));
	}

	public static EnumGene<Float> nextEnumGeneFloat() {
		return EnumGene.valueOf(ISeq(5, FloatFactory));
	}

	public static EnumGene<Double> nextEnumGeneDouble() {
		return EnumGene.valueOf(ISeq(5, DoubleFactory));
	}

	public static EnumGene<String> nextEnumGeneString() {
		return EnumGene.valueOf(ISeq(5, StringFactory));
	}

	public static EnumGene<Integer64> nextEnumGeneInteger64() {
		return EnumGene.valueOf(ISeq(5, Integer64Factory));
	}

	public static EnumGene<Float64> nextEnumGeneFloat64() {
		return EnumGene.valueOf(ISeq(5, Float64Factory));
	}

	/* *************************************************************************
	 * Chromosomes
	 **************************************************************************/

	public static BitChromosome nextBitChromosome() {
		return new BitChromosome(20, 0.5);
	}

	public static CharacterChromosome nextCharacterChromosome() {
		return new CharacterChromosome(20);
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
		return Genotype.valueOf(ISeq(5, BitChromosomeFactory));
	}

	public static Genotype<CharacterGene> nextGenotypeCharacterGene() {
		return Genotype.valueOf(ISeq(5, CharacterChromosomeFactory));
	}

	public static Genotype<LongGene> nextGenotypeLongGene() {
		return Genotype.valueOf(ISeq(5, LongChromosomeFactory));
	}

	public static Genotype<DoubleGene> nextGenotypeDoubleGene() {
		return Genotype.valueOf(ISeq(5, DoubleChromosomeFactory));
	}

	public static Genotype<Integer64Gene> nextGenotypeInteger64Gene() {
		return Genotype.valueOf(ISeq(5, Integer64ChromosomeFactory));
	}

	public static Genotype<Float64Gene> nextGenotypeFloat64Gene() {
		return Genotype.valueOf(ISeq(5, Float64ChromosomeFactory));
	}

	/* *************************************************************************
	 * Phenotypes
	 **************************************************************************/

	public static Phenotype<Integer64Gene, Integer> nextPhenotypeInteger64GeneInteger() {
		return Phenotype.valueOf(
			nextGenotypeInteger64Gene(),
			FitnessFunction(IntegerFactory.newInstance()),
			Math.abs(IntegerFactory.newInstance())
		);
	}

	public static Phenotype<Integer64Gene, Long> nextPhenotypeInteger64GeneLong() {
		return Phenotype.valueOf(
			nextGenotypeInteger64Gene(),
			FitnessFunction(LongFactory.newInstance()),
			Math.abs(IntegerFactory.newInstance())
		);
	}

	public static Phenotype<Integer64Gene, Double> nextPhenotypeInteger64GeneDouble() {
		return Phenotype.valueOf(
			nextGenotypeInteger64Gene(),
			FitnessFunction(DoubleFactory.newInstance()),
			Math.abs(IntegerFactory.newInstance())
		);
	}

	public static Phenotype<Integer64Gene, Integer64> nextPhenotypeInteger64GeneInteger64() {
		return Phenotype.valueOf(
			nextGenotypeInteger64Gene(),
			FitnessFunction(Integer64Factory.newInstance()),
			Math.abs(IntegerFactory.newInstance())
		);
	}

	public static Phenotype<Integer64Gene, Float64> nextPhenotypeInteger64GeneFloat64() {
		return Phenotype.valueOf(
			nextGenotypeInteger64Gene(),
			FitnessFunction(Float64Factory.newInstance()),
			Math.abs(IntegerFactory.newInstance())
		);
	}

	public static Phenotype<Float64Gene, Integer> nextPhenotypeFloat64GeneInteger() {
		return Phenotype.valueOf(
			nextGenotypeFloat64Gene(),
			FitnessFunction(IntegerFactory.newInstance()),
			Math.abs(IntegerFactory.newInstance())
		);
	}

	public static Phenotype<Float64Gene, Long> nextPhenotypeFloat64GeneLong() {
		return Phenotype.valueOf(
			nextGenotypeFloat64Gene(),
			FitnessFunction(LongFactory.newInstance()),
			Math.abs(IntegerFactory.newInstance())
		);
	}

	public static Phenotype<Float64Gene, Double> nextPhenotypeFloat64GeneDouble() {
		return Phenotype.valueOf(
			nextGenotypeFloat64Gene(),
			FitnessFunction(DoubleFactory.newInstance()),
			Math.abs(IntegerFactory.newInstance())
		);
	}

	public static Phenotype<Float64Gene, Integer64> nextPhenotypeFloat64GeneInteger64() {
		return Phenotype.valueOf(
			nextGenotypeFloat64Gene(),
			FitnessFunction(Integer64Factory.newInstance()),
			Math.abs(IntegerFactory.newInstance())
		);
	}

	public static Phenotype<Float64Gene, Float64> nextPhenotypeFloat64GeneFloat64() {
		return Phenotype.valueOf(
			nextGenotypeFloat64Gene(),
			FitnessFunction(Float64Factory.newInstance()),
			Math.abs(IntegerFactory.newInstance())
		);
	}

	/* *************************************************************************
	 * Populations
	 **************************************************************************/

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


	public static <R extends Comparable<R>> Function<Object, R>
	FitnessFunction(final R result) {
		return new Function<Object, R>() {
			@Override
			public R apply(final Object value) {
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


	@SuppressWarnings("deprecation")
	public static void main(final String[] args) throws Exception {
		IO.jaxb.write(nextGenotypeLongGene(), System.out);
		System.out.println();
		JSONIO.write(nextGenotypeLongGene(), System.out);
		//IO.jaxb.write(nextDoubleGene(), System.out);
		//IO.object.write(nextLongGene(), System.out);
		//write(IO.jaxb, "jaxb");
		//write(IO.xml, "xml");
		//write(IO.object, "object");
	}

	private static void write(final IO io, final String suffix) throws IOException {
		final File baseDir = new File("org.jenetics/src/test/resources/org/jenetics/serialization");
		if (!baseDir.isDirectory() && !baseDir.mkdirs()) {
			throw new IOException("Error while creating directory " + baseDir);
		}

		for (PersistentObject<?> object : VALUES) {
			final File file = new File(baseDir, object.getName() + "." + suffix);
			System.out.println(object.getName());
			try (FileOutputStream out = new FileOutputStream(file)) {
				io.write(object.getValue(), out);
			}
		}
	}
}

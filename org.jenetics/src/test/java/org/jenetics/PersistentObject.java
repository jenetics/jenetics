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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import javolution.context.LocalContext;

import org.jscience.mathematics.number.Float64;
import org.jscience.mathematics.number.Integer64;

import org.jenetics.util.Factory;
import org.jenetics.util.Function;
import org.jenetics.util.LCG64ShiftRandom;
import org.jenetics.util.RandomRegistry;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version @__version__@ &mdash; <em>$Date: 2014-01-28 $</em>
 * @since @__version__@
 */
public class PersistentObject<T> {

	public static final Factory<BitChromosome> BitChromosomeFactory = factory(
		PersistentObject.class, "nextBitChromosome"
	);

	public static final Factory<CharacterChromosome> CharacterChromosomeFactory = factory(
		PersistentObject.class, "nextCharacterChromosome"
	);

	public static final Factory<Integer64Chromosome> Integer64ChromosomeFactory = factory(
		PersistentObject.class, "nextInteger64Chromosome"
	);

	public static final Factory<Float64Chromosome> Float64ChromosomeFactory = factory(
		PersistentObject.class, "nextFloat64Chromosome"
	);

	private final String _name;
	private final T _value;

	public PersistentObject(
		final String name,
		final T value
	) {
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




	public static List<PersistentObject<?>> VALUES = new ArrayList<>();
	public static Map<String, PersistentObject<?>> OBJECTS = new HashMap<>();

	private static <T> void put(final String name, final T value ) {
		VALUES.add(new PersistentObject<T>(name, value));
	}

	static {
		final LCG64ShiftRandom random = new LCG64ShiftRandom.ThreadSafe(1010);
		final Random originalRandom = RandomRegistry.getRandom();
		RandomRegistry.setRandom(random);

		/*
		 * Genes
		 */

		put("BitGene[true]", BitGene.TRUE);
		put("BitGene[false]", BitGene.FALSE);
		put("CharacterGene", nextCharacterGene());
		put("Integer64Gene", nextInteger64Gene());
		put("Float64Gene", nextFloat64Gene());

		put("EnumGene<Boolean>", nextEnumGeneBoolean());
		put("EnumGene<Byte>", nextEnumGeneByte());
		put("EnumGene<Character>", nextEnumGeneCharacter());
		put("EnumGene<Short>", nextEnumGeneShort());
		put("EnumGene<Integer>", nextEnumGeneInteger());
		put("EnumGene<Long>", nextEnumGeneLong());
		put("EnumGene<Float>", nextEnumGeneFloat());
		put("EnumGene<Double>", nextEnumGeneDouble());
		put("EnumGene<String>", nextEnumGeneString());
		put("EnumGene<Float64>", nextEnumGeneFloat64());
		put("EnumGene<Integer64>", nextEnumGeneInteger64());

		/*
		 * Chromosomes
		 */

		put("BitChromosome", nextBitChromosome());
		put("CharacterChromosome", nextCharacterChromosome());
		put("Integer64Chromosome", nextInteger64Chromosome());
		put("Float64Chromosome", nextFloat64Chromosome());

		/*
		 * Genotypes
		 */

		put("Genotype<BitGene>", nextGenotypeBitGene());
		put("Genotype<CharacterGene>", nextGenotypeCharacterGene());
		put("Genotype<Integer64Gene>", nextGenotypeInteger64Gene());
		put("Genotype<Float64Gene>", nextGenotypeFloat64Gene());


		/*
		 * Phenotypes
		 */

		@SuppressWarnings("rawtypes")
		final Function<Object, Comparable> ff = new Function<Object, Comparable>() {
			@Override
			public Comparable apply(Object value) {
				//return Float64Gene.valueOf(0, 10);
				//return "fooasdfadsf";
				return 0.0;
				//return Real.ONE;
				//return Calendar.getInstance();
			}
		};

		VALUES.add(new PersistentObject<>(
			"Phenotype<Float64Chromosome>",
			Phenotype.valueOf(
				Genotype.valueOf(
					new Float64Chromosome(0.0, 1.0, 5),
					new Float64Chromosome(0.0, 2.0, 10),
					new Float64Chromosome(0.0, 3.0, 15),
					new Float64Chromosome(0.0, 4.0, 3)
				),
				ff,
				34
			)
		));

		for (PersistentObject<?> obj :  VALUES) {
			OBJECTS.put(obj.getName(), obj);
		}

		RandomRegistry.setRandom(originalRandom);
	}

	public static Factory<PersistentObject<BitChromosome>> bitChromosomeModel() {
		return new Factory<PersistentObject<BitChromosome>>() {
			@Override
			public PersistentObject<BitChromosome> newInstance() {
				final Random random = new LCG64ShiftRandom(0);
				LocalContext.enter();
				try {
					RandomRegistry.setRandom(random);
					return new PersistentObject<>(
						"BitChromosome",
						new BitChromosome(500, 0.5)
					);
				} finally {
					LocalContext.exit();
				}
			}
		};
	}

	public static CharacterGene nextCharacterGene() {
		return CharacterGene.valueOf();
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

	public static BitChromosome nextBitChromosome() {
		return new BitChromosome(20, 0.5);
	}

	public static CharacterChromosome nextCharacterChromosome() {
		return new CharacterChromosome(20);
	}

	public static Integer64Chromosome nextInteger64Chromosome() {
		return new Integer64Chromosome(Long.MIN_VALUE, Long.MAX_VALUE, 20);
	}

	public static Float64Chromosome nextFloat64Chromosome() {
		return new Float64Chromosome(0.0, 1.0, 20);
	}

	public static Genotype<BitGene> nextGenotypeBitGene() {
		return Genotype.valueOf(ISeq(5, BitChromosomeFactory));
	}

	public static Genotype<CharacterGene> nextGenotypeCharacterGene() {
		return Genotype.valueOf(ISeq(5, CharacterChromosomeFactory));
	}

	public static Genotype<Integer64Gene> nextGenotypeInteger64Gene() {
		return Genotype.valueOf(ISeq(5, Integer64ChromosomeFactory));
	}

	public static Genotype<Float64Gene> nextGenotypeFloat64Gene() {
		return Genotype.valueOf(ISeq(5, Float64ChromosomeFactory));
	}



	public static final Factory<EnumGene<Integer>> EnumGeneIntegerFactory = factory(
		PersistentObject.class, "nextEnumGeneInteger"
	);

}

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import javolution.context.LocalContext;

import org.jenetics.util.Factory;
import org.jenetics.util.Function;
import org.jenetics.util.LCG64ShiftRandom;
import org.jenetics.util.RandomRegistry;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version @__version__@ &mdash; <em>$Date$</em>
 * @since @__version__@
 */
public class PersistentObject<T> {

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
		RandomRegistry.setRandom(random);

		put("BitGene[true]", BitGene.TRUE);
		put("BitGene[false]", BitGene.FALSE);
		put("CharacterGene", CharacterGene.valueOf());
		put("Integer64Gene", Integer64Gene.valueOf(Long.MIN_VALUE, Long.MAX_VALUE));
		put("Float64Gene", Float64Gene.valueOf(0, 1.0));

		put("EnumGene<Boolean>", EnumGene.valueOf(ISeq(5, BooleanFactory)));
		put("EnumGene<Byte>", EnumGene.valueOf(ISeq(5, ByteFactory)));
		put("EnumGene<Character>", EnumGene.valueOf(ISeq(5, CharacterFactory)));
		put("EnumGene<Short>", EnumGene.valueOf(ISeq(5, ShortFactory)));
		put("EnumGene<Integer>", EnumGene.valueOf(ISeq(5, IntegerFactory)));
		put("EnumGene<Long>", EnumGene.valueOf(ISeq(5, LongFactory)));
		put("EnumGene<Float>", EnumGene.valueOf(ISeq(5, FloatFactory)));
		put("EnumGene<Double>", EnumGene.valueOf(ISeq(5, DoubleFactory)));
		put("EnumGene<String>", EnumGene.valueOf(ISeq(5, StringFactory)));
		put("EnumGene<Float64>", EnumGene.valueOf(ISeq(5, Float64Factory)));
		put("EnumGene<Integer64>", EnumGene.valueOf(ISeq(5, Integer64Factory)));

		/*
		 * Chromosomes
		 */

		put("BitChromosome", new BitChromosome(10, 0.5));
		put("CharacterChromosome", new CharacterChromosome(20));
		put("Integer64Chromosome",
			new Integer64Chromosome(Integer.MIN_VALUE, Integer.MAX_VALUE, 20)
		);
		put("Float64Chromosome",
			new Float64Chromosome(0.0, 1.0, 20)
		);

		/*
		 * Genotypes
		 */

//		VALUES.add(new PersistentObject<>(
//			"Genotype<Float64Chromosome>", Genotype.class, Genotype.valueOf(
//			new Float64Chromosome(0.0, 1.0, 5),
//			new Float64Chromosome(0.0, 2.0, 10),
//			new Float64Chromosome(0.0, 3.0, 15),
//			new Float64Chromosome(0.0, 4.0, 3)
//		)));

		/*
		 * Phenotypes
		 */

		final Function<Genotype<Float64Gene>, Comparable> ff = new Function<Genotype<Float64Gene>, Comparable>() {
			@Override
			public Comparable apply(Genotype<Float64Gene> value) {
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

	public static void main(final String[] args) throws Exception {
		//final Object value = OBJECTS.get("BitGene[true]").getValue();
		//final Object value = OBJECTS.get("BitGene[true]").getValue();
		//final Object value = characterGene().newInstance().getModel();
		//final Object value = enumFloat64Gene().newInstance().getValue();
		//final Object value = float64Gene().newInstance().getModel();
		//final Object value = integer64Gene().newInstance().getModel();

		//IO.xml.write(value, System.out);
		System.out.println();
		//IO.jaxb.write(value, System.out);
	}

}

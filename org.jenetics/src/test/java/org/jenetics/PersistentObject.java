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

import static org.jenetics.internal.math.random.ByteFactory;
import static org.jenetics.internal.math.random.CharacterFactory;
import static org.jenetics.internal.math.random.Float64Factory;
import static org.jenetics.internal.math.random.Integer64Factory;
import static org.jenetics.internal.math.random.StringFactory;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import javolution.context.LocalContext;

import org.jscience.mathematics.number.Float64;
import org.jscience.mathematics.number.Integer64;
import org.jscience.mathematics.number.Real;

import org.jenetics.util.Array;
import org.jenetics.util.Factory;
import org.jenetics.util.Function;
import org.jenetics.util.LCG64ShiftRandom;
import org.jenetics.util.RandomRegistry;
import org.jenetics.util.RandomUtils;
import org.jenetics.util.lambda;
import static org.jenetics.util.RandomUtils.*;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version @__version__@ &mdash; <em>$Date: 2014-01-27 $</em>
 * @since @__version__@
 */
public class PersistentObject<T> {

	private final String _name;
	private final Class<T> _type;
	private final T _value;

	public PersistentObject(
		final String name,
		final Class<T> type,
		final T value
	) {
		_name = Objects.requireNonNull(name);
		_type = Objects.requireNonNull(type);
		_value = Objects.requireNonNull(value);
	}

	public String getName() {
		return _name;
	}

	public Class<T> getType() {
		return _type;
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
		VALUES.add(new PersistentObject<T>(name, null, value));
	}

	static {
		final LCG64ShiftRandom random = new LCG64ShiftRandom.ThreadSafe(1010);
		RandomRegistry.setRandom(random);

		put("BitGene[true]", BitGene.TRUE);
		put("BitGene[false]", BitGene.FALSE);
		put("CharacterGene", CharacterGene.valueOf());
		put("Integer64Gene", Integer64Gene.valueOf(Integer.MIN_VALUE, Integer.MAX_VALUE));
		put("Float64Gene", Float64Gene.valueOf(0, 1.0));

		put("EnumGene<Byte>", getEnumGeneByte());
		put("EnumGene<Character>", getEnumGeneCharacter());
		put("EnumGene<Short>", EnumGene.valueOf(new Array<Short>(5).fill(SFact).toISeq()));
		put("EnumGene<Integer>", EnumGene.valueOf(new Array<Integer>(5).fill(IFact).toISeq()));
		put("EnumGene<Long>", EnumGene.valueOf(new Array<Long>(5).fill(LFact).toISeq()));
		put("EnumGene<Double>", EnumGene.valueOf(new Array<Double>(5).fill(DFact).toISeq()));
		put("EnumGene<String>", getEnumGeneString());
		put("EnumGene<Float64>", getEnumGeneFloat64());
		put("EnumGene<Integer64>", getEnumGeneInteger64());

		/*
		 * Chromosomes
		 */

		put("BitChromosome", getBitChromosome());
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
			"Phenotype<Float64Chromosome>", Phenotype.class,
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

	public static CharacterGene getCharacterGene() {
		return CharacterGene.valueOf();
	}

	public static Integer64Gene getInteger64Gene() {
		return Integer64Gene.valueOf(Integer.MIN_VALUE, Integer.MAX_VALUE);
	}

	public static Float64Gene getFloat64Gene() {
		return Float64Gene.valueOf(0, 1.0);
	}

	public static EnumGene<Byte> getEnumGeneByte() {
		return EnumGene.valueOf(
			new Array<Byte>(5).fill(ByteFactory((byte)0, (byte)10)).toISeq()
		);
	}

	public static EnumGene<Character> getEnumGeneCharacter() {
		return EnumGene.valueOf(
			new Array<Character>(5).fill(CharacterFactory()).toISeq()
		);
	}

	public static EnumGene<String> getEnumGeneString() {
		return EnumGene.valueOf(
			new Array<String>(5).fill(StringFactory()).toISeq()
		);
	}

	public static EnumGene<Integer64> getEnumGeneInteger64() {
		return EnumGene.valueOf(
			new Array<Integer64>(5).fill(Integer64Factory(0, 10)).toISeq()
		);
	}

	public static EnumGene<Float64> getEnumGeneFloat64() {
		return EnumGene.valueOf(
			new Array<Float64>(5).fill(Float64Factory(0, 10)).toISeq()
		);
	}

	public static BitChromosome getBitChromosome() {
		return new BitChromosome(10, 0.5);
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
						BitChromosome.class,
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

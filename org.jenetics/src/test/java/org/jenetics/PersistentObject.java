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

import static org.jenetics.internal.math.random.DoubleFactory;
import static org.jenetics.internal.math.random.Float64Factory;
import static org.jenetics.internal.math.random.Integer64Factory;
import static org.jenetics.internal.math.random.LongFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import javolution.context.LocalContext;

import org.jscience.mathematics.number.Float64;
import org.jscience.mathematics.number.Integer64;

import org.jenetics.util.Array;
import org.jenetics.util.Factory;
import org.jenetics.util.LCG64ShiftRandom;
import org.jenetics.util.RandomRegistry;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version @__version__@ &mdash; <em>$Date$</em>
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
	static {
		VALUES.add(new PersistentObject<>(
			"BitGene[true]", BitGene.class, BitGene.TRUE
		));
		VALUES.add(new PersistentObject<>(
			"BitGene[false]", BitGene.class, BitGene.FALSE
		));
		VALUES.add(new PersistentObject<>(
			"CharacterGene", CharacterGene.class, getCharacterGene()
		));
		VALUES.add(new PersistentObject<>(
			"Integer64Gene", Integer64Gene.class, getInteger64Gene()
		));
		VALUES.add(new PersistentObject<>(
			"Float64Gene", Float64Gene.class, getFloat64Gene()
		));
		VALUES.add(new PersistentObject<>(
			"EnumGene<Float64>", EnumGene.class, getEnumGeneFloat64()
		));
		VALUES.add(new PersistentObject<>(
			"EnumGene<Double>", EnumGene.class, getEnumGeneDouble()
		));
		VALUES.add(new PersistentObject<>(
			"EnumGene<Integer64>", EnumGene.class, getEnumGeneInteger64()
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

	public static EnumGene<Float64> getEnumGeneFloat64() {
		return EnumGene.valueOf(
			new Array<Float64>(5).fill(Float64Factory(0, 10)).toISeq()
		);
	}

	public static EnumGene<Double> getEnumGeneDouble() {
		return EnumGene.valueOf(
			new Array<Double>(5).fill(DoubleFactory(0, 10)).toISeq()
		);
	}

	public static EnumGene<Integer64> getEnumGeneInteger64() {
		return EnumGene.valueOf(
			new Array<Integer64>(5).fill(Integer64Factory(0, 10)).toISeq()
		);
	}

	public static EnumGene<Long> getEnumGeneLong() {
		return EnumGene.valueOf(
			new Array<Long>(5).fill(LongFactory(0, 10)).toISeq()
		);
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

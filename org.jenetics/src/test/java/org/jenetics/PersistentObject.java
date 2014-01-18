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

import static org.jenetics.internal.math.random.Float64Factory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import javolution.context.LocalContext;

import org.jscience.mathematics.number.Float64;

import org.jenetics.util.Array;
import org.jenetics.util.Factory;
import org.jenetics.util.IO;
import org.jenetics.util.ISeq;
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

	public static List<Factory<? extends PersistentObject<?>>> MODELS = new ArrayList<>();
	/*static {
		MODELS.add(float64Gene());
		MODELS.add(integer64Gene());
		MODELS.add(bitChromosomeModel());
	}*/

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

		for (PersistentObject<?> obj :  VALUES) {
			OBJECTS.put(obj.getName(), obj);
		}
	}

	public static CharacterGene getCharacterGene() {
		final Random random = new LCG64ShiftRandom(0);
		LocalContext.enter();
		try {
			RandomRegistry.setRandom(random);
			return CharacterGene.valueOf();
		} finally {
			LocalContext.exit();
		}
	}

	public static Integer64Gene getInteger64Gene() {
		final Random random = new LCG64ShiftRandom(0);
		LocalContext.enter();
		try {
			RandomRegistry.setRandom(random);
			return Integer64Gene.valueOf(Integer.MAX_VALUE, Integer.MAX_VALUE);
		} finally {
			LocalContext.exit();
		}
	}

	public static Float64Gene getFloat64Gene() {
		final Random random = new LCG64ShiftRandom(0);
		LocalContext.enter();
		try {
			RandomRegistry.setRandom(random);
			return Float64Gene.valueOf(0, 1.0);
		} finally {
			LocalContext.exit();
		}
	}

	public static EnumGene<Float64> getEnumGeneFloat64() {
		final Random random = new LCG64ShiftRandom(0);
		LocalContext.enter();
		try {
			RandomRegistry.setRandom(random);
			return EnumGene.valueOf(
				new Array<Float64>(5).fill(Float64Factory(random, 0, 10)).toISeq()
			);
		} finally {
			LocalContext.exit();
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

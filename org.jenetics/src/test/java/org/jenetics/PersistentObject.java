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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import javolution.context.LocalContext;

import org.jscience.mathematics.number.Float64;

import static org.jenetics.internal.math.random.float64Factory;
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
	private final T _object;

	public PersistentObject(
		final String name,
		final Class<T> type,
		final T object
	) {
		_name = Objects.requireNonNull(name);
		_type = type;
		_object = object;
	}

	public String getName() {
		return _name;
	}

	public Class<T> getType() {
		return _type;
	}

	public T getObject() {
		return _object;
	}

	public static List<Factory<? extends PersistentObject<?>>> MODELS = new ArrayList<>();
	static {
		MODELS.add(bitGeneTrue());
		MODELS.add(bitGeneFalse());
		MODELS.add(float64Gene());
		MODELS.add(integer64Gene());
		MODELS.add(bitChromosomeModel());
	}

	public static Factory<PersistentObject<BitGene>> bitGeneTrue() {
		return new Factory<PersistentObject<BitGene>>() {
			@Override
			public PersistentObject<BitGene> newInstance() {
				return new PersistentObject<>(
					"BitGene(True)",
					BitGene.class,
					BitGene.TRUE
				);
			}
		};
	}

	public static Factory<PersistentObject<BitGene>> bitGeneFalse() {
		return new Factory<PersistentObject<BitGene>>() {
			@Override
			public PersistentObject<BitGene> newInstance() {
				return new PersistentObject<>(
					"BitGene(False)",
					BitGene.class,
					BitGene.FALSE
				);
			}
		};
	}

	public static Factory<PersistentObject<CharacterGene>> characterGene() {
		return new Factory<PersistentObject<CharacterGene>>() {
			@Override
			public PersistentObject<CharacterGene> newInstance() {
				final Random random = new LCG64ShiftRandom(0);
				LocalContext.enter();
				try {
					RandomRegistry.setRandom(random);
					return new PersistentObject<>(
						"Integer64Gene",
						CharacterGene.class,
						CharacterGene.valueOf()
					);
				} finally {
					LocalContext.exit();
				}
			}
		};
	}

	public static Factory<PersistentObject<EnumGene>> enumFloat64Gene() {
		return new Factory<PersistentObject<EnumGene>>() {
			@SuppressWarnings("unchecked")
			@Override
			public PersistentObject<EnumGene> newInstance() {
				final Random random = new LCG64ShiftRandom(0);
				LocalContext.enter();
				try {
					RandomRegistry.setRandom(random);
					final ISeq<Float64> alleles =
						new Array<Float64>(5).fill(float64Factory(random, 0, 10)).toISeq();
					return new PersistentObject<EnumGene>(
						"EnumGene<Float64>",
						EnumGene.class,
						EnumGene.valueOf(alleles)
					);
				} finally {
					LocalContext.exit();
				}
			}
		};
	}

	public static Factory<PersistentObject<Float64Gene>> float64Gene() {
		return new Factory<PersistentObject<Float64Gene>>() {
			@Override
			public PersistentObject<Float64Gene> newInstance() {
				final Random random = new LCG64ShiftRandom(0);
				LocalContext.enter();
				try {
					RandomRegistry.setRandom(random);
					return new PersistentObject<>(
						"Float64Gene",
						Float64Gene.class,
						Float64Gene.valueOf(0, 1.0)
					);
				} finally {
					LocalContext.exit();
				}
			}
		};
	}

	public static Factory<PersistentObject<Integer64Gene>> integer64Gene() {
		return new Factory<PersistentObject<Integer64Gene>>() {
			@Override
			public PersistentObject<Integer64Gene> newInstance() {
				final Random random = new LCG64ShiftRandom(0);
				LocalContext.enter();
				try {
					RandomRegistry.setRandom(random);
					return new PersistentObject<>(
						"Integer64Gene",
						Integer64Gene.class,
						Integer64Gene.valueOf(Integer.MIN_VALUE, Integer.MAX_VALUE)
					);
				} finally {
					LocalContext.exit();
				}
			}
		};
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
		//final Object value = bitGeneTrue().newInstance().getModel();
		//final Object value = bitGeneFalse().newInstance().getModel();
		//final Object value = characterGene().newInstance().getModel();
		final Object value = enumFloat64Gene().newInstance().getObject();
		//final Object value = float64Gene().newInstance().getModel();
		//final Object value = integer64Gene().newInstance().getModel();

		IO.xml.write(value, System.out);
		System.out.println();
		//IO.jaxb.write(value, System.out);
	}

}

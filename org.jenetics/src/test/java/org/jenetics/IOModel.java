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
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javolution.context.LocalContext;

import org.jenetics.util.Factory;
import org.jenetics.util.IO;
import org.jenetics.util.LCG64ShiftRandom;
import org.jenetics.util.RandomRegistry;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz
 *         Wilhelmstötter</a>
 * @version @__version__@ &mdash; <em>$Date: 2014-01-17 $</em>
 * @since @__version__@
 */
public class IOModel<T> {

	private final String _name;
	private final Class<T> _type;
	private final T _model;

	public IOModel(final String name, final Class<T> type, final T model) {
		_name = name;
		_type = type;
		_model = model;
	}

	public String getName() {
		return _name;
	}

	public Class<T> getType() {
		return _type;
	}

	public T getModel() {
		return _model;
	}

	public static List<Factory<? extends IOModel<?>>> MODELS = new ArrayList<>();
	static {
		MODELS.add(bitGeneTrue());
		MODELS.add(bitGeneFalse());
		MODELS.add(float64Gene());
		MODELS.add(bitChromosomeModel());
	}

	public static Factory<IOModel<BitGene>> bitGeneTrue() {
		return new Factory<IOModel<BitGene>>() {
			@Override
			public IOModel<BitGene> newInstance() {
				return new IOModel<>(
					"BitGene(True)",
					BitGene.class,
					BitGene.TRUE
				);
			}
		};
	}

	public static Factory<IOModel<BitGene>> bitGeneFalse() {
		return new Factory<IOModel<BitGene>>() {
			@Override
			public IOModel<BitGene> newInstance() {
				return new IOModel<>(
					"BitGene(False)",
					BitGene.class,
					BitGene.FALSE
				);
			}
		};
	}

	public static Factory<IOModel<Float64Gene>> float64Gene() {
		return new Factory<IOModel<Float64Gene>>() {
			@Override
			public IOModel<Float64Gene> newInstance() {
				final Random random = new LCG64ShiftRandom(0);
				LocalContext.enter();
				try {
					RandomRegistry.setRandom(random);
					return new IOModel<>(
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

	public static Factory<IOModel<BitChromosome>> bitChromosomeModel() {
		return new Factory<IOModel<BitChromosome>>() {
			@Override
			public IOModel<BitChromosome> newInstance() {
				final Random random = new LCG64ShiftRandom(0);
				LocalContext.enter();
				try {
					RandomRegistry.setRandom(random);
					return new IOModel<>(
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
		final Object value = float64Gene().newInstance().getModel();

		IO.xml.write(value, System.out);
		System.out.println();
		IO.jaxb.write(value, System.out);
	}

}

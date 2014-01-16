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
package org.jenetics.util;

import java.io.File;
import java.util.Random;

import javolution.context.LocalContext;

import org.jenetics.Float64Chromosome;
import org.jenetics.Integer64Chromosome;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz
 *         Wilhelmstötter</a>
 * @version @__version__@ &mdash; <em>$Date: 2014-01-16 $</em>
 * @since @__version__@
 */
public class SerializationCompatibilityMain {

	private final IO _io;
	private final Object _value;

	public SerializationCompatibilityMain(
		final IO io,
		final Object value
	) {
		_io = io;
		_value = value;
	}

	public void write() {

	}

	public static void main(final String[] args) throws Exception {
		final File base = new File("/home/fwilhelm/Temp");
		final Object value = newFloat64Chromosome();

		IO.jaxb.write(value, new File(base, value.getClass().getCanonicalName() + ".jaxb"));
	}

	private static Object newFloat64Chromosome() {
		final Random random = new LCG64ShiftRandom(0);
		LocalContext.enter();
		try {
			RandomRegistry.setRandom(random);
			return new Float64Chromosome(-1000.0, 1000, 500);
		} finally {
			LocalContext.exit();
		}
	}

	private static Object newInteger64Chromosome() {
		final Random random = new LCG64ShiftRandom(0);
		LocalContext.enter();
		try {
			RandomRegistry.setRandom(random);
			return new Integer64Chromosome(Integer.MIN_VALUE, Integer.MAX_VALUE, 500);
		} finally {
			LocalContext.exit();
		}
	}

}

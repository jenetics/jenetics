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
package org.jenetics.internal.util;

import static java.lang.String.format;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Random;


/**
 * Class for testing a given random engine using the
 * <a href="http://www.phy.duke.edu/~rgb/General/dieharder.php">dieharder</a>
 * test application.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.5
 * @version 1.5 &mdash; <em>$Date: 2014-02-15 $</em>
 */
public final class DieHarder {

	/**
	 * Writes random numbers to an given data output stream.
	 *
	 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
	 * @since 1.5
	 * @version 1.5 &mdash; <em>$Date: 2014-02-15 $</em>
	 */
	private static final class Randomizer implements Runnable {
		private final Random _random;
		private final OutputStream _out;

		public Randomizer(final Random random, final OutputStream out) {
			_random = Objects.requireNonNull(random);
			_out = Objects.requireNonNull(out);
		}

		@Override
		public void run() {
			try {
				final byte[] data = new byte[4096];
				while (!Thread.currentThread().isInterrupted()) {
					_random.nextBytes(data);
					_out.write(data);
				}
			} catch (IOException ignore) {
			}
		}

	}

	public static void main(final String[] args) throws Exception {
		if ( args.length < 1) {
			println("Usage: java org.jenetics.internal.util.DieHarder <random-class-name>");
			return;
		}

		final String randomName = args[0];
		Random random = null;
		try {
			random = (Random)Class.forName(randomName).newInstance();
			printt(
				"Testing: %s (%s)",
				randomName,
				new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date())
			);
		} catch (Exception e) {
			System.out.println("Can't create random class " + randomName);
			return;
		}

		final List<String> dieharderArgs = new ArrayList<>();
		dieharderArgs.add("dieharder");
		for (int i  = 1; i < args.length; ++i) {
			dieharderArgs.add(args[i]);
		}
		dieharderArgs.add("-g");
		dieharderArgs.add("200");

		printv();

		final long start = System.currentTimeMillis();
		final ProcessBuilder builder = new ProcessBuilder(dieharderArgs);
		final Process dieharder = builder.start();

		final Thread randomizer = new Thread(new Randomizer(
			random,
			dieharder.getOutputStream()
		));
		randomizer.start();

		final BufferedReader stdout = new BufferedReader (
			new InputStreamReader(dieharder.getInputStream())
		);
		String line = null;
		while ((line = stdout.readLine()) != null) {
			System.out.println(line);
		}

		dieharder.waitFor();
		randomizer.interrupt();
		final long sec = (System.currentTimeMillis() - start)/1000;

		printt("Runtime: %d:%02d:%02d", sec/3600, (sec%3600)/60, (sec%60));

	}

	private static void printt(final String title, final Object... args) {
		println("#=============================================================================#");
		println("# %-76s#", format(title, args));
		println("#=============================================================================#");
	}

	private static void printv() {
		println("#=============================================================================#");
		println(
			"# %-76s#",
			format("%s %s (%s) ", p("os.name"), p("os.version"), p("os.arch"))
		);
		println(
			"# %-76s#",
			format("java version \"%s\"", p("java.version"))
		);
		println(
			"# %-76s#",
			format("%s (build %s)", p("java.runtime.name"), p("java.runtime.version"))
		);
		println(
			"# %-76s#",
			format("%s (build %s)", p("java.vm.name"), p("java.vm.version"))
		);
		println("#=============================================================================#");
	}

	private static String p(final String name) {
		return System.getProperty(name);
	}

	private static void println(final String pattern, final Object... args) {
		System.out.println(format(pattern, args));
	}

}

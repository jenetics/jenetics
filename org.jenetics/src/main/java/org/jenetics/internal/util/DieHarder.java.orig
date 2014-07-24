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
import static java.util.regex.Pattern.quote;
import static org.jenetics.internal.util.Equality.eq;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.Random;

import org.jenetics.internal.util.DieHarder.Result.Assessment;

/**
 * Class for testing a given random engine using the
 * <a href="http://www.phy.duke.edu/~rgb/General/dieharder.php">dieharder</a>
 * test application.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.5
 * @version !__version__! &mdash; <em>$Date$</em>
 */
public final class DieHarder {

	/**
	 * Writes random numbers to an given data output stream.
	 *
	 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
	 * @since 1.5
	 * @version !__version__! &mdash; <em>$Date$</em>
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

		// The dieharder console output.
		final BufferedReader stdout = new BufferedReader (
			new InputStreamReader(dieharder.getInputStream())
		);

		final List<Result> results = new ArrayList<>();

		String line = null;
		while ((line = stdout.readLine()) != null) {
			Result.parse(line).ifPresent(results::add);
			System.out.println(line);
		}

		dieharder.waitFor();
		randomizer.interrupt();
		final long sec = (System.currentTimeMillis() - start)/1000;

		// Calculate statistics.
		final long passed = results.stream()
			.filter(r -> r.assessment == Assessment.PASSED)
			.count();
		final long weak = results.stream()
			.filter(r -> r.assessment == Assessment.WEAK)
			.count();
		final long failed = results.stream()
			.filter(r -> r.assessment == Assessment.FAILED)
			.count();


		printt("Summary: PASSED=%d, WEAK=%d, FAILED=%d", passed, weak, failed);
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



	/**
	 * Represents one DieHarder test result.
	 *
	 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
	 * @since !__version__!
	 * @version !__version__! &mdash; <em>$Date$</em>
	 */
	static final class Result {

		static enum Assessment {
			PASSED,
			FAILED,
			WEAK;

			static Optional<Assessment> of(final String assessment) {
				switch (assessment) {
					case "PASSED": return Optional.of(PASSED);
					case "FAILED": return Optional.of(FAILED);
					case "WEAK": return Optional.of(WEAK);
					default: return Optional.empty();
				}
			}
		}

		final String testName;
		final int ntup;
		final int tsamples;
		final int psamples;
		final double pvalue;
		final Assessment assessment;

		private Result(
			final String testName,
			final int ntup,
			final int tsamples,
			final int psamples,
			final double pvalue,
			final Assessment assessment
		) {
			this.testName = testName;
			this.ntup = ntup;
			this.tsamples = tsamples;
			this.psamples = psamples;
			this.pvalue = pvalue;
			this.assessment = assessment;
		}

		static Optional<Result> parse(final String line) {
			final String[] parts = line.split(quote("|"));

			if (parts.length == 6) {
				final String name = parts[0].trim();
				final OptionalInt ntup = parseInt(parts[1].trim());
				final OptionalInt tsamples = parseInt(parts[2].trim());
				final OptionalInt psamples = parseInt(parts[3].trim());
				final OptionalDouble pvalue = parseDouble(parts[4].trim());
				final Optional<Assessment> assessment = Assessment.of(parts[5].trim());

				if (ntup.isPresent() &&
					tsamples.isPresent() &&
					psamples.isPresent() &&
					pvalue.isPresent() &&
					assessment.isPresent())
				{
					return Optional.of(new Result(
						name,
						ntup.getAsInt(),
						tsamples.getAsInt(),
						psamples.getAsInt(),
						pvalue.getAsDouble(),
						assessment.get()
					));
				}
			}

			return Optional.empty();
		}

		private static OptionalInt parseInt(final String value) {
			try {
				return OptionalInt.of(Integer.parseInt(value));
			} catch (NumberFormatException e) {
				return OptionalInt.empty();
			}
		}

		private static OptionalDouble parseDouble(final String value) {
			try {
				return OptionalDouble.of(Double.parseDouble(value));
			} catch (NumberFormatException e) {
				return OptionalDouble.empty();
			}
		}

		@Override
		public int hashCode() {
			return Hash.of(getClass())
				.and(testName)
				.and(ntup)
				.and(tsamples)
				.and(psamples)
				.and(pvalue)
				.and(assessment).value();
		}

		@Override
		public boolean equals(final Object obj) {
			return Equality.of(this, obj).test(result ->
				eq(testName, result.testName) &&
				eq(ntup, result.ntup) &&
				eq(tsamples, result.tsamples) &&
				eq(psamples, result.psamples) &&
				eq(pvalue, result.psamples) &&
				eq(assessment, result.assessment)
			);
		}

		@Override
		public String toString() {
			return format(
				"%s[ntup=%d, tsamples=%d, psamples=%d, pvalue=%f, assessment=%s]",
				testName, ntup, tsamples, psamples, pvalue, assessment
			);
		}

	}

}

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
import static java.util.Objects.requireNonNull;
import static java.util.regex.Pattern.quote;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static org.jenetics.internal.util.Equality.eq;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.Random;

import org.jenetics.util.ISeq;
import org.jenetics.util.Seq;

/**
 * Class for testing a given random engine using the
 * <a href="http://www.phy.duke.edu/~rgb/General/dieharder.php">dieharder</a>
 * test application.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.5
 * @version 3.0
 */
public final class DieHarder {

	/**
	 * Writes random numbers to an given data output stream.
	 *
	 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
	 * @since 1.5
	 * @version 3.0
	 */
	private static final class Randomizer implements Runnable {
		private final Random _random;
		private final CountingOutputStream _out;

		Randomizer(final Random random, final OutputStream out) {
			_random = requireNonNull(random);
			_out = new CountingOutputStream(out);
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

		long getCount() {
			return _out.getCount();
		}
	}

	public static void main(final String[] args) throws Exception {
		if ( args.length < 1) {
			println("Usage: \n" +
				"   java org.jenetics.internal.util.DieHarder <random-class-name>");
			return;
		}

		test(args[0], ISeq.of(args).subSeq(1));
	}

	private static void test(final String randomName, final Seq<String> args)
		throws IOException, InterruptedException
	{
		final Random random;
		try {
			random = (Random)Class
				.forName(randomName)
				.getConstructor()
				.newInstance();

			printt(
				"Testing: %s (%s)",
				randomName,
				new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date())
			);
		} catch (Exception e) {
			println("Can't create random class '%s'.", randomName);
			return;
		}

		final List<String> dieharderArgs = new ArrayList<>();
		dieharderArgs.add("dieharder");
		dieharderArgs.addAll(args.asList());
		dieharderArgs.add("-g");
		dieharderArgs.add("200");

		printv();

		final long start = System.currentTimeMillis();
		final ProcessBuilder builder = new ProcessBuilder(dieharderArgs);
		final Process dieharder = builder.start();

		final Randomizer randomizer = new Randomizer(
			random,
			dieharder.getOutputStream()
		);
		final Thread randomizerThread = new Thread(randomizer);
		randomizerThread.start();

		// The dieharder console output.
		final BufferedReader stdout = new BufferedReader (
			new InputStreamReader(dieharder.getInputStream())
		);

		final List<Result> results = new ArrayList<>();
		for (String l = stdout.readLine(); l != null; l = stdout.readLine()) {
			Result.parse(l).ifPresent(results::add);
			System.out.println(l);
		}

		dieharder.waitFor();
		randomizerThread.interrupt();

		final long millis = System.currentTimeMillis() - start;
		final long sec = millis/1000;
		final double megaBytes = randomizer.getCount()/(1024.0*1024.0);

		// Calculate statistics.
		final Map<Assessment, Long> grouped = results.stream()
			.collect(groupingBy(r -> r.assessment, counting()));

		final long passed = grouped.getOrDefault(Assessment.PASSED, 0L);
		final long weak = grouped.getOrDefault(Assessment.WEAK, 0L);
		final long failed = grouped.getOrDefault(Assessment.FAILED, 0L);

		final NumberFormat formatter = NumberFormat.getIntegerInstance();
		formatter.setMinimumFractionDigits(3);
		formatter.setMaximumFractionDigits(3);

		println("#=============================================================================#");
		println(
			"# %-76s#",
			format("Summary: PASSED=%d, WEAK=%d, FAILED=%d", passed, weak, failed)
		);
		println(
			"# %-76s#",
			format("         %s MB of random data created with %s MB/sec",
				formatter.format(megaBytes),
				formatter.format(megaBytes/(millis/1000.0))
			)
		);
		println("#=============================================================================#");
		printt("Runtime: %d:%02d:%02d", sec/3600, (sec%3600)/60, sec%60);

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
	 * @since 3.0
	 * @version 3.0
	 */
	static final class Result {
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
				final OptionalInt ntup = toOptionalInt(parts[1].trim());
				final OptionalInt tsamples = toOptionalInt(parts[2].trim());
				final OptionalInt psamples = toOptionalInt(parts[3].trim());
				final OptionalDouble pvalue = toOptionalDouble(parts[4].trim());
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

		private static OptionalInt toOptionalInt(final String value) {
			try {
				return OptionalInt.of(Integer.parseInt(value));
			} catch (NumberFormatException e) {
				return OptionalInt.empty();
			}
		}

		private static OptionalDouble toOptionalDouble(final String value) {
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
			return obj instanceof Result &&
				eq(testName, ((Result)obj).testName) &&
				eq(ntup, ((Result)obj).ntup) &&
				eq(tsamples, ((Result)obj).tsamples) &&
				eq(psamples, ((Result)obj).psamples) &&
				eq(pvalue, ((Result)obj).psamples) &&
				eq(assessment, ((Result)obj).assessment);
		}

		@Override
		public String toString() {
			return format(
				"%s[ntup=%d, tsamples=%d, psamples=%d, pvalue=%f, assessment=%s]",
				testName, ntup, tsamples, psamples, pvalue, assessment
			);
		}
	}

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

	/**
	 * Counts the written bytes.
	 *
	 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
	 * @since 3.0
	 * @version 3.0
	 */
	private static final class CountingOutputStream extends OutputStream {
		private final OutputStream _delegate;
		private long _count;

		CountingOutputStream(final OutputStream delegate) {
			_delegate = requireNonNull(delegate);
		}

		@Override
		public void write(final byte[] b) throws IOException {
			_delegate.write(b);
			_count += b.length;
		}

		@Override
		public void write(final byte[] b, final int offset, final int length)
			throws IOException
		{
			_delegate.write(b, offset, length);
			_count += length;
		}

		@Override
		public void write(final int b) throws IOException {
			_delegate.write(b);
			_count += 1;
		}

		long getCount() {
			return _count;
		}

	}

}

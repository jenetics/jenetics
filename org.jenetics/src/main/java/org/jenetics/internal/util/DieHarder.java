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
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.regex.Pattern;

/**
 * Class for testing a given random engine using the
 * <a href="http://www.phy.duke.edu/~rgb/General/dieharder.php">dieharder</a>
 * test application.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.5
 * @version 1.5 &mdash; <em>$Date: 2014-01-10 $</em>
 */
public final class DieHarder {

	public static final Appendable DevNull = new Appendable() {
		@Override
		public Appendable append(CharSequence csq) {
			return this;
		}
		@Override
		public Appendable append(CharSequence csq, int start, int end) {
			return this;
		}
		@Override
		public Appendable append(char c) {
			return this;
		}
	};

	private final Random _random;
	private final Appendable _output;
	private final boolean _verbose;

	private List<TestResult> _results = new ArrayList<>();

	public DieHarder(final Random random, final Appendable output, final boolean verbose) {
		_random = Objects.requireNonNull(random);
		_output = Objects.requireNonNull(output);
		_verbose = verbose;
	}

	public DieHarder(final Random random) {
		this(random, System.out, true);
	}

	public void run() throws IOException, InterruptedException {
		final List<String> dieharderArgs = new ArrayList<>();
		dieharderArgs.add("dieharder");

		// The random number to test are read from stdin_input_raw.
		dieharderArgs.add("-g");
		dieharderArgs.add("200");

		// Perform all random tests.
		dieharderArgs.add("-a");
		//dieharderArgs.add("-d 6");

		printv();

		final long start = System.currentTimeMillis();
		final ProcessBuilder builder = new ProcessBuilder(dieharderArgs);
		final Process dieharder = builder.start();

		final Thread randomizer = new Thread(new Randomizer(
			_random,
			dieharder.getOutputStream()
		));
		randomizer.start();

		final BufferedReader stdout = new BufferedReader (
			new InputStreamReader(dieharder.getInputStream())
		);
		String line = null;
		while ((line = stdout.readLine()) != null) {
			final TestResult result = TestResult.valueOf(line);
			if (result != null) {
				_results.add(result);
			}

			if (_verbose) {
				println(line);
			}
		}

		dieharder.waitFor();
		randomizer.interrupt();
		final long sec = (System.currentTimeMillis() - start)/1000;

		/*
		printt(
			"Summary: PASSED: %d, FAILED: %d, WEAK: %d",
			_passedTestCounter, _failedTestCounter, _weakTestCounter
		);
		*/
		printt("Runtime: %d:%02d:%02d", sec/3600, (sec%3600)/60, (sec%60));
	}

	public Random getRandom() {
		return _random;
	}

	public List<TestResult> getResults() {
		return Collections.unmodifiableList(_results);
	}

	public int getPassed() {
		int sum = 0;
		for (TestResult result : _results) {
			if (result.assessment == TestResult.Assessment.PASSED) {
				++sum;
			}
		}
		return sum;
	}

	public int getWeak() {
		int sum = 0;
		for (TestResult result : _results) {
			if (result.assessment == TestResult.Assessment.WEAK) {
				++sum;
			}
		}
		return sum;
	}

	public int getFailed() {
		int sum = 0;
		for (TestResult result : _results) {
			if (result.assessment == TestResult.Assessment.FAILED) {
				++sum;
			}
		}
		return sum;
	}

	private void printt(final String title, final Object... args) throws IOException {
		println("#=============================================================================#");
		println("# %-76s#", format(title, args));
		println("#=============================================================================#");
	}

	private void printv() throws IOException {
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

	private void println(final String pattern, final Object... args)
		throws IOException
	{
		_output.append(format(pattern, args));
		_output.append(System.lineSeparator());
	}

	@Override
	public String toString() {
		return String.format(
			"p=%d, w=%d, f=%d",
			getPassed(), getWeak(), getFailed()
		);
	}

	/**
	 * Writes random numbers to an given data output stream.
	 *
	 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
	 * @since 1.5
	 * @version 1.5 &mdash; <em>$Date: 2014-01-10 $</em>
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

	private static final class TestResult {
		public static enum Assessment {
			PASSED, FAILED, WEAK;
		}

		public final String testName;
		public final int ntup;
		public final int tsamples;
		public final int psamples;
		public final double pvalue;
		public final Assessment assessment;

		public TestResult(
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

		public static TestResult valueOf(final String line) {
			TestResult result = null;
			if (isResultLine(line)) {
				final String[] columns = line.split(Pattern.quote("|"));
				result = new TestResult(
					columns[0].trim(),
					Integer.parseInt(columns[1].trim()),
					Integer.parseInt(columns[2].trim()),
					Integer.parseInt(columns[3].trim()),
					Double.parseDouble(columns[4].trim()),
					Assessment.valueOf(columns[5].trim())
				);
			}

			return result;
		}

		private static boolean isResultLine(final String line) {
			return line.contains("PASSED") ||
				line.contains("FAILED") ||
				line.contains("WEAK");
		}

		@Override
		public String toString() {
			return String.format(
				"Result[name=%s, ntup=%d, tsamples=%d, psamples=%d, pvalue=%s, assessment=%s]",
				testName, ntup, tsamples, psamples, pvalue, assessment
			);
		}
	}

	/**
	 * Command line interface for the dieharder Random class tester.
	 */
	public static void main(final String[] args) throws Exception {
		if ( args.length < 1) {
			System.out.println(
				"Usage: java org.jenetics.internal.util.DieHarder <random-class-name>"
			);
			return;
		}

		final DieHarder dieHarder = new DieHarder(newInstance(args[0]));
		dieHarder.printt(
			"Testing: %s (%s)",
			dieHarder.getRandom().getClass().getName(),
			new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date())
		);

		dieHarder.run();
	}

	private static Random newInstance(final String randomName) {
		try {
			return (Random)Class.forName(randomName).newInstance();
		} catch (Exception e) {
			System.err.println("Can't create random class " + randomName);
			System.exit(1);
		}
		return null;
	}

}






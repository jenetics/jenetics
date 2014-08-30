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

import java.io.PrintStream;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 3.0
 * @version 3.0 &mdash; <em>$Date: 2014-08-29 $</em>
 */
public class CU {
	private final PrintStream _stream;

	public CU(final PrintStream stream) {
		_stream = requireNonNull(stream);
	}

	public void printTitle(final String title, final Object... args) {
		printHLine();
		println("# %-76s#", format(title, args));
		printHLine();
	}

	private void printEnvironment() {
		printHLine();
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
		printHLine();
	}

	private void printHLine() {
		_stream.print('#');
		print('=', 78);
		_stream.println('#');
	}

	private void print(final char c, final int times) {
		for (int i = 0; i < times; ++i) {
			_stream.print(c);
		}
	}

	private void print(final String pattern, final Object... args) {
		_stream.print(format(pattern, args));
	}

	private void println(final String pattern, final Object... args) {
		_stream.println(format(pattern, args));
	}

	private static String p(final String name) {
		return System.getProperty(name);
	}

}

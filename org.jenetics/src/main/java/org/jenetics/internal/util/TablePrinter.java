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

import java.io.IOException;
import java.io.UncheckedIOException;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 3.0
 * @version 3.0 &mdash; <em>$Date: 2014-12-10 $</em>
 */
public final class TablePrinter {

	private Appendable _out;
	private final int _width;

	private final String _hline;

	public TablePrinter(final int width, final Appendable out) {
		_out = requireNonNull(out);
		_width = width;

		_hline = "+" + mult("-", _width - 2) + "+";
	}

	public TablePrinter(final Appendable out) {
		this(77, out);
	}

	public TablePrinter header(final String value) {
		hline();
		final String pattern = format("| %%-%ds|", _width - 3);
		println(format(pattern, value));
		hline();

		return this;
	}

	public TablePrinter row(final String a, final String b) {
		println(format("| %22s %-51s|", a, b));
		return this;
	}

	public TablePrinter hline() {
		println(_hline);
		return this;
	}

	public Appendable out() {
		return _out;
	}



	private void println(final String value) {
		print(value + "\n");
	}

	private void print(final String value) {
		try {
			_out.append(value);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	private static String mult(final String string, final int times) {
		final StringBuilder builder = new StringBuilder();
		for (int i = 0; i < times; ++i) {
			builder.append(string);
		}

		return builder.toString();
	}

	public static void main(final String[] args) {
		final TablePrinter printer = new TablePrinter(System.out);
		printer.header("Some header line");
		printer.row("Foo:", "foo");
		printer.header("Another header line");
	}

}

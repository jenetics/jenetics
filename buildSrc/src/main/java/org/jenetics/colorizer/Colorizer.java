/*
 * Java Genetic Algorithm Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * Author:
 *     Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 *
 */
package org.jenetics.colorizer;

import static java.lang.String.format;
import static java.util.Arrays.asList;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 1.4 &mdash; <em>$Date: 2013-08-20 $</em>
 */
public final class Colorizer extends SimpleFileVisitor<Path> {

	public static void main(final String[] args) {
		final File dir = new File(args[0]);
		if (!dir.isDirectory()) {
			System.err.println(args[0] + " is not a directory.");
			System.exit(1);
		}

		try {
			final Colorizer colorizer = new Colorizer();
			Files.walkFileTree(dir.toPath(), colorizer);

			System.out.println(format(
				"Colorizer processed %d files and modified %d.",
				colorizer.getProcessed(),
				colorizer.getModified()
			));
		} catch (IOException e) {
			System.err.println("Error while processing files: " + e);
			System.exit(1);
		}
	}



	private static final Charset CHARSET = Charset.forName("UTF-8");

	private int _processed = 0;
	private int _modified = 0;

	int getProcessed() {
		return _processed;
	}

	int getModified() {
		return _modified;
	}

	@Override
	public FileVisitResult visitFile(
		final Path file,
		final BasicFileAttributes attrs
	) {
		if (file.toString().endsWith(".html")) {
			try {
				colorize(file);
			} catch (IOException e) {
				System.out.println("Error while processing file: " + file);
				return FileVisitResult.TERMINATE;
			}
		}

		return FileVisitResult.CONTINUE;
	}

	private void colorize(final Path file) throws IOException {
		++_processed;

		try (FileInputStream fis = new FileInputStream(file.toFile());
			InputStreamReader isr = new InputStreamReader(fis, CHARSET);
			BufferedReader in = new BufferedReader(isr))
		{
			final StringBuilder out = new StringBuilder(10000);
			State state = State.DATA;
			boolean modified = false;

			for (int ch = in.read(); ch != -1; ch = in.read()) {
				if (state != State.DATA) {
					switch (ch) {
						case '<': out.append("&lt;"); break;
						case '>': out.append("&gt;"); break;
						case '&': out.append("&amp;"); break;
						default: out.append((char)ch); break;
					}
				} else {
					out.append((char)ch);
				}

				if (state == State.CODE_TAG) {
					modified = true;
				}

				state = state.apply(ch, out);
			}

			if (modified) {
				++_modified;
				try (FileOutputStream fout = new FileOutputStream(file.toFile());
					OutputStreamWriter writer = new OutputStreamWriter(fout, CHARSET))
				{
					writer.write(out.toString());
				}
			}
		}
	}

	/**
	 * Represents the current 'Colorize' state.
	 *
	 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
	 * @since 1.0
	 * @version 1.4 &mdash; <em>$Date: 2013-08-20 $</em>
	 */
	private static enum State {

		DATA {
			@Override
			public State apply(final int read, final StringBuilder out) {
				State state = this;
				if ((read == ']') &&
					(out.length() > 5) &&
					out.substring(out.length() - 6).equalsIgnoreCase("[code]"))
				{
					out.setLength(out.length() - 6);
					out.append("<div class=\"code\"><code lang=\"java\">");
					state = SKIP_NEWLINE;
				}

				return state;
			}
		},

		SKIP_NEWLINE {
			@Override
			public State apply(final int read, final StringBuilder out) {
				State state = this;
				if (read == '\n') {
					out.setLength(out.length() - 1);
					state = CODE_TAG;
				}
				return state;
			}
		},

		CODE_TAG {
			@Override
			public State apply(final int read, final StringBuilder out) {
				State state = this;
				if (Character.isJavaIdentifierPart((char)read)) {
					state = IDENTIFIER;
					state._start = out.length() - 1;
				} else if (read == '"') {
					state = STRING_LITERAL;
					out.insert(
						out.length() - 1,
						"<font color=\"" + STRING_COLOR + "\">"
					);
				} else if ((read == '/') &&
							(out.charAt(out.length() - 2) == '/'))
				{
					state = COMMENT;
					out.insert(
						out.length() - 2,
						"<font color=\"" + COMMENT_COLOR + "\">"
					);
				}

				return state;
			}
		},

		IDENTIFIER {
			@Override
			public State apply(final int read, final StringBuilder out) {
				State state = this;

				// Code identifier.
				if ((read == ']') &&
					out.substring(out.length() - 7).equalsIgnoreCase("[/code]"))
				{
					int index = out.lastIndexOf("\n");
					out.setLength(index);
					out.append("</code></div>");
					state = DATA;

				// End of identifier.
				} else if (!Character.isJavaIdentifierPart((char)read)) {
					final String name = out.substring(_start, out.length() - 1);

					// Identifier found.
					if (IDENTIFIERS.contains(name)) {
						out.insert(_start + name.length(), "</b></font>");
						out.insert(
							_start,
							"<font color=\"" + KEYWORD_COLOR + "\"><b>"
						);
					}
					state = CODE_TAG;
				}

				return state;
			}
		},

		STRING_LITERAL {
			@Override
			public State apply(final int read, final StringBuilder out) {
				State state = this;
				if ((read == '"') && (out.charAt(out.length() - 2) != '\\')) {
					out.append("</font>");
					state = CODE_TAG;
				}
				return state;
			}
		},

		COMMENT {
			@Override
			public State apply(final int read, final StringBuilder out) {
				State state = this;
				if ((read == '\n') || (read == '\r')) {
					out.insert(out.length() - 1, "</font>");
					state = CODE_TAG;
				}
				return state;
			}
		};

		int _start = -1;

		public abstract State apply(final int read, final StringBuilder doc);

		private static final String KEYWORD_COLOR = "#7F0055";
		private static final String COMMENT_COLOR = "#3F7F5F";
		private static final String STRING_COLOR = "#0000FF";

		private static final Set<String> IDENTIFIERS = new HashSet<>(asList(
			"abstract",
			"assert",
			"boolean",
			"break",
			"byte",
			"case",
			"catch",
			"char",
			"class",
			"const",
			"continue",
			"default",
			"do",
			"double",
			"else",
			"enum",
			"extends",
			"final",
			"finally",
			"float",
			"for",
			"goto",
			"if",
			"implements",
			"import",
			"instanceof",
			"int",
			"interface",
			"long",
			"native",
			"new",
			"package",
			"private",
			"protected",
			"public",
			"return",
			"short",
			"static",
			"strictfp",
			"super",
			"switch",
			"synchronized",
			"this",
			"throw",
			"throws",
			"transient",
			"try",
			"void",
			"volatile",
			"while"
		));
	}

}

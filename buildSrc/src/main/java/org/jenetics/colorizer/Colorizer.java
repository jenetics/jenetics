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
package org.jenetics.colorizer;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;

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
 * @version 3.1
 */
public final class Colorizer extends SimpleFileVisitor<Path> {

	private static final Charset CHARSET = Charset.forName("UTF-8");

	// Original start tag: <pre>{@code
	private static final String START_TAG = "<pre><code>";

	// Original end tag: }</pre>
	private static final String END_TAG = "</code></pre>";

	private File _baseDir;

	private int _processed = 0;
	private int _modified = 0;

	public Colorizer(final File baseDir) {
		_baseDir = requireNonNull(baseDir, "Base dir must not be null.");
	}

	public Colorizer() {
		this(new File("."));
	}

	public void setBaseDir(final File baseDir) {
		_baseDir = requireNonNull(baseDir, "Base dir must not be null.");
	}

	public File getBaseDir() {
		return _baseDir;
	}

	public int getProcessed() {
		return _processed;
	}

	public int getModified() {
		return _modified;
	}

	public void colorize() throws IOException {
		Files.walkFileTree(_baseDir.toPath(), this);
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
						//case '<': out.append("&lt;"); break;
						//case '>': out.append("&gt;"); break;
						//case '&': out.append("&amp;"); break;
						default: out.append((char)ch); break;
					}
				} else {
					out.append((char)ch);
				}

				if (state == State.CODE_TAG) {
					modified = true;
				}

				state = state.apply((char)ch, out);
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
	 * Represents the current 'Colorizer' state.
	 *
	 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
	 * @since 1.0
	 * @version 1.4
	 */
	private static enum State {

		DATA {
			@Override
			public State apply(final char ch, final StringBuilder out) {
				State state = this;
				if ((ch == '>') &&
					(out.length() >= START_TAG.length()) &&
					out.substring(out.length() - START_TAG.length())
						.equalsIgnoreCase(START_TAG))
				{
					out.setLength(out.length() - START_TAG.length());
					out.append("<div class=\"code\"><code lang=\"java\">");
					state = CODE_TAG;
				}

				return state;
			}
		},

		SKIP_NEWLINE {
			@Override
			public State apply(final char ch, final StringBuilder out) {
				State state = this;
				if (ch == '\n') {
					out.setLength(out.length() - 1);
					state = CODE_TAG;
				}
				return state;
			}
		},

		CODE_TAG {
			@Override
			public State apply(final char ch, final StringBuilder out) {
				State state = this;
				if (Character.isJavaIdentifierPart(ch)) {
					state = IDENTIFIER;
					state._start = out.length() - 1;
				} else if (ch == '"') {
					state = STRING_LITERAL;
					out.insert(
						out.length() - 1,
						"<font color=\"" + STRING_COLOR + "\">"
					);
				} else if ((ch == '/') && (out.charAt(out.length() - 2) == '/')) {
					state = COMMENT;
					out.insert(
						out.length() - 2,
						"<font color=\"" + COMMENT_COLOR + "\">"
					);
				} else if ((ch == '@') &&
							(out.charAt(out.length() - 2) == '\\') &&
							(out.charAt(out.length() - 3) != '\\'))
				{
					state = ANNOTATION;
					out.deleteCharAt(out.length() - 2);
					out.insert(
						out.length() - 1,
						"<font color=\"" + ANNOTATION_COLOR + "\"><b>"
					);
				}

				return state;
			}
		},

		IDENTIFIER {
			@Override
			public State apply(final char ch, final StringBuilder out) {
				State state = this;
				if ((ch == '>') &&
					out.substring(out.length() - END_TAG.length())
						.equalsIgnoreCase(END_TAG))
				{
					int index = out.lastIndexOf("\n");
					out.setLength(index);
					out.append("</code></div>");
					state = DATA;
				} else if (!Character.isJavaIdentifierPart(ch)) {
					final String name = out.substring(_start, out.length() - 1);
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

		ANNOTATION {
			@Override
			public State apply(final char ch, final StringBuilder out) {
				State state = this;
				if (!Character.isJavaIdentifierPart(ch)) {
					out.insert(out.length() - 1, "</b></font>");
					state = CODE_TAG;
				}
				return state;
			}
		},

		STRING_LITERAL {
			@Override
			public State apply(final char ch, final StringBuilder out) {
				State state = this;
				if ((ch == '"') && (out.charAt(out.length() - 2) != '\\')) {
					out.append("</font>");
					state = CODE_TAG;
				}
				return state;
			}
		},

		COMMENT {
			@Override
			public State apply(final char ch, final StringBuilder out) {
				State state = this;
				if ((ch == '\n') || (ch == '\r')) {
					out.insert(out.length() - 1, "</font>");
					state = CODE_TAG;
				}
				return state;
			}
		};

		int _start = -1;

		public abstract State apply(final char read, final StringBuilder doc);

		private static final String ANNOTATION_COLOR = "#808080";
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
			"null",
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


	public static void main(final String[] args) {
		final File dir = new File(args[0]);
		if (!dir.isDirectory()) {
			System.err.println(args[0] + " is not a directory.");
			System.exit(1);
		}

		try {
			final Colorizer colorizer = new Colorizer(dir);
			colorizer.colorize();

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

}

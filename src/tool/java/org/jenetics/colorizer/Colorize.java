/*
 * Java Genetic Algorithm Library (@!identifier!@).
 * Copyright (c) @!year!@ Franz Wilhelmstötter
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public final class Colorize {

	public static void main(final String[] args) {
		final File dir = new File(args[0]);
		if (!dir.isDirectory()) {
			System.err.println(args[0] + " is not a directory.");
			System.exit(1);
		}

		try {
			final Colorizer colorizer = new Colorizer();
			Files.walkFileTree(dir.toPath(), colorizer);
			
			System.out.println(String.format(
					"Colorizer processed %d files and modified %d.",
					colorizer.getProcessed(),
					colorizer.getModified()
				));
		} catch (IOException e) {
			System.err.println("Error while processing files: " + e);
			System.exit(1);
		}
	}

	private static final class Colorizer extends SimpleFileVisitor<Path> {	
		
		private static final String ENCODING = "UTF-8";
		
		private int _processed = 0;
		private int _modified = 0;

		public int getProcessed() {
			return _processed;
		}
		
		public int getModified() {
			return _modified;
		}
		
		@Override
		public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) {
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
			_processed++;
	
			try (BufferedReader in = new BufferedReader(new InputStreamReader(
					new FileInputStream(file.toFile()), ENCODING)))
			{
				final StringBuilder doc = new StringBuilder(10000);
				State state = State.DATA;
				boolean modified = false;
				
				for (int read = in.read(); read != -1; read = in.read()) {
					if (state != State.DATA) {
						if (read == '<') {
							doc.append("&lt;");
						} else if (read == '>') {
							doc.append("&gt;");
						} else if (read == '&') {
							doc.append("&amp;");
						} else {
							doc.append((char)read);
						}
					} else {
						doc.append((char)read);
					}
					
					if (state == State.CODE) {
						modified = true;
					}
					
					state = state.apply(read, doc);
				}

				if (modified) {
					_modified++;
					try (final OutputStreamWriter out = new OutputStreamWriter(
								new FileOutputStream(file.toFile()), ENCODING)
							)
					{
						out.write(doc.toString());
					}
				}				
			}
		}
		
	}
	
	private static enum State {
		
		DATA {
			@Override
			public State apply(final int read, final StringBuilder doc) {
				State state = this;
				if ((read == ']') &&
					(doc.length() > 5) &&
					doc.substring(doc.length() - 6).equalsIgnoreCase("[code]"))
				{
					doc.setLength(doc.length() - 6);
					doc.append("<div class=\"code\"><code lang=\"java\">");
					state = SKIP_NL;
				}			
				
				return state;
			}
		},
		
		SKIP_NL {
			@Override
			public State apply(final int read, final StringBuilder doc) {
				State state = this;
				if (read == '\n') {
					doc.setLength(doc.length() - 1);
					state = CODE;
				}
				return state;
			}
		},
		
		CODE {
			@Override
			public State apply(final int read, final StringBuilder doc) {
				State state = this;
				if (Character.isJavaIdentifierPart((char)read)) {
					state = IDENTIFIER;
					state._start = doc.length() - 1;
				} else if (read == '"') {
					state = STRING_LITERAL;
					doc.insert(doc.length() - 1, "<font color=\"" + STRING_COLOR + "\">");
				} else if ((read == '/') &&
							(doc.charAt(doc.length() - 2) == '/'))
				{
					state = COMMENT;
					doc.insert(doc.length() - 2, "<font color=\"" + COMMENT_COLOR + "\">");
				}	
				
				return state;
			}
		},
		
		IDENTIFIER {
			@Override
			public State apply(final int read, final StringBuilder doc) {
				State state = this;
				if ((read == ']') && // code identifier.
					doc.substring(doc.length() - 7).equalsIgnoreCase("[/code]"))
				{
					int index = doc.lastIndexOf("\n");
					doc.setLength(index);
					doc.append("</code></div>");
					state = DATA;
				} else if (!Character.isJavaIdentifierPart((char)read)) { // End of identifier.
					String name = doc.substring(_start, doc.length() - 1);
					if (IDENTIFIERS.contains(name)) { // Identifier found.
						doc.insert(_start + name.length(), "</b></font>");
						doc.insert(_start, "<font color=\"" + KEYWORD_COLOR + "\"><b>");
					}
					state = CODE;
				}
				
				return state;
			}
		},
		
		STRING_LITERAL {
			@Override
			public State apply(final int read, final StringBuilder doc) {
				State state = this;
				if ((read == '"') && (doc.charAt(doc.length() - 2) != '\\')) {
					doc.append("</font>");
					state = CODE;
				}
				return state;
			}
		},
		
		COMMENT {
			@Override
			public State apply(final int read, final StringBuilder doc) {
				State state = this;
				if ((read == '\n') || (read == '\r')) {
					doc.insert(doc.length() - 1, "</font>");
					state = CODE;
				}
				return state;
			}
		}
		
		;
		
		int _start = -1;
		
		public abstract State apply(final int read, final StringBuilder doc);
		
		private static final String KEYWORD_COLOR = "#7F0055";
		private static final String COMMENT_COLOR = "#3F7F5F";
		private static final String STRING_COLOR = "#0000FF";
		
		private static final String[] KEYWORDS = {
			"abstract",
			"continue",
			"for",
			"new",
			"switch",
			"assert",
			"default",
			"if",
			"package",
			"synchronized",
			"boolean",
			"do",
			"goto",
			"private",
			"this",
			"break",
			"double",
			"implements",
			"protected",
			"throw",
			"byte",
			"else",
			"import",
			"public",
			"throws",
			"case",
			"enum",
			"instanceof",
			"return",
			"transient",
			"catch",
			"extends",
			"int",
			"short",
			"try",
			"char",
			"final",
			"interface",
			"static",
			"void",
			"class",
			"finally",
			"long",
			"strictfp",
			"volatile",
			"const",
			"float",
			"native",
			"super",
			"while"
		};

		private static final Set<String> IDENTIFIERS = new HashSet<>();
		static {
			for (int i = 0; i < KEYWORDS.length; i++) {
				IDENTIFIERS.add(KEYWORDS[i]);
			}
		}		
		
	}
	
	

}











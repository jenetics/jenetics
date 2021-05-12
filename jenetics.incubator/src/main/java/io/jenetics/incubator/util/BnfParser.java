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
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmail.com)
 */
package io.jenetics.incubator.util;

import static java.lang.String.format;

import java.util.ArrayList;
import java.util.List;
import java.util.Spliterator;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since !__version__!
 * @version !__version__!
 */
final class BnfParser {
	private BnfParser() {}


	static record Token(String value, int kind) {
		static final int ASSIGNMENT = 0;
		static final int OR = 1;
		static final int TERMINAL = 2;
		static final int NON_TERMINAL = 3;
	}

	private static final char SEPARATOR = ',';
	private static final char QUOTE = '"';

	private static final String SEPARATOR_STR = ",";
	private static final String QUOTE_STR = "\"";
	private static final String DOUBLE_QUOTE_STR = "\"\"";

	private static final  int QUOTED = 1;
	private static final int ESCAPED = 2;

	static List<Token> tokenize(final CharSequence value) {
		final var tokens = new ArrayList<Token>();
		final var token = new StringBuilder(32);

		boolean quoted = false;
		boolean escaped = false;

		int state = 0;


		final char[] buffer = new char[3];

		for (int i = 0, n = value.length(); i < n; ++i) {
			buffer[2] = buffer[1];
			buffer[1] = buffer[0];
			buffer[0] = value.charAt(i);

			switch (buffer[0]) {
				case '"':

			}
		}

//			final int previous = i > 0 ? value.charAt(i - 1) : -1;
//			final char current = value.charAt(i);
//			final int next = i + 1 < value.length() ? value.charAt(i + 1) : -1;
//
//			switch (current) {
//				case QUOTE:
//					if (quoted) {
//						if (!escaped && QUOTE == next) {
//							escaped = true;
//						} else {
//							if (escaped) {
//								token.append(QUOTE);
//								escaped = false;
//							} else {
//								tokens.add(token.toString());
//								token.setLength(0);
//								quoted = false;
//							}
//						}
//					} else {
//						quoted = true;
//					}
//					break;
///*				case ' ', '|', ':', '=':
//					if (quoted) {
//						token.append(current);
//					} else if (' ' == previous || '|' == previous || previous == -1) {
//						tokens.add("" + current);
//						//token.setLength(0);
//					}
//					break;*/
//				default:
//					if (quoted) {
//						token.append(current);
//					} else {
//						token.append(current);
//					}
////					int j = i;
////
////					// Read till the next token separator.
////					char c;
////					while (j < n && !isTokenSeparator(c = value.charAt(j))) {
////						token.append(c);
////						++j;
////					}
////
////					if (j != i) {
////						i = j - 1;
////					}
////
////					if (!quoted) {
////						tokens.add(token.toString());
////						token.setLength(0);
////					}
//					break;
//			}
//		}
//
//		if (quoted) {
//			throw new IllegalArgumentException("Unbalanced quote character.");
//		}
//		if (value.length() == 0 ||
//			SEPARATOR == value.charAt(value.length() - 1))
//		{
//			tokens.add("");
//		}

		return tokens;
	}

	private static boolean isQuoted(final int state) {
		return (state & QUOTE) != 0;
	}

	private static boolean isEscaped(final int state) {
		return (state & ESCAPED) != 0;
	}

	private static boolean isTokenSeparator(final char c) {
		return c == ' ' || c == '|' || c == ':' || c == '=' || c == QUOTE;
	}

	static Grammar parse(final CharSequence value) {

		return null;
	}

}

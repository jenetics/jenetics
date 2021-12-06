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
package io.jenetics.incubator.grammar;

import static java.lang.Character.isDigit;
import static java.lang.Character.isWhitespace;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 7.0
 * @version 7.0
 */
final class Bnfs {
	private Bnfs() {}

	static final char[] SYMBOL_CHARS = {'<', '>', '|', ':', '='};

	static boolean isSymbolChar(final int ch) {
		for (char symbolChar : SYMBOL_CHARS) {
			if (ch == symbolChar) {
				return true;
			}
		}
		return false;
	}

	static boolean isStringChar(final char c) {
		return !isWhitespace(c) && !isSymbolChar(c);
	}

	static boolean isAlphabetic(final char c) {
		return ('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z');
	}

	static boolean isIdChar(final char c) {
		return isAlphabetic(c) || isDigit(c) || (c == '-');
	}

	static boolean isValidId(final String id) {
		if (id.isBlank()) {
			return false;
		}
		if (!isAlphabetic(id.charAt(0))) {
			return false;
		}
		for (int i = 1; i < id.length(); ++i) {
			if (!isIdChar(id.charAt(i))) {
				return false;
			}
		}

		return true;
	}

}

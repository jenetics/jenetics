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
package io.jenetics.ext.util;

/**
 * Escaping for the parenthesis tree string representation.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
final class Escaping {
	private Escaping() {}

	private static final char ESCAPE_CHAR = '\\';

	private static final char[] PROTECTED_CHARS = new char[] {
		'(', ')', ',', ESCAPE_CHAR
	};

	static String escape(final String value) {
		final StringBuilder result = new StringBuilder();
		for (int i = 0; i < value.length(); ++i) {
			final char c = value.charAt(i);
			if (isEscapeChar(c)) {
				result.append(ESCAPE_CHAR);
			}
			result.append(c);
		}

		return result.toString();
	}

	private static boolean isEscapeChar(final char c) {
		for (int i = 0; i < PROTECTED_CHARS.length; ++i) {
			if (c == PROTECTED_CHARS[i]) {
				return true;
			}
		}
		return false;
	}


	static String unescape(final String value) {
		final StringBuilder result = new StringBuilder();

		boolean escaping = false;
		for (int i = 0; i < value.length(); ++i) {
			final char c = value.charAt(i);

			if (c == ESCAPE_CHAR && !escaping) {
				escaping = true;
				continue;
			}

			if (escaping) {
				escaping = false;
			}
			result.append(c);
		}

		return result.toString();
	}


}

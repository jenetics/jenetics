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
package io.jenetics.ext.internal.util;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 5.0
 * @since 5.0
 */
public final class Escaper {

	private final char[] _protect;
	private final char _escape;


	public Escaper(final char escape, final char... protect) {
		_protect = protect.clone();
		_escape = escape;
	}

	public String escape(final CharSequence value) {
		final StringBuilder result = new StringBuilder();
		for (int i = 0; i < value.length(); ++i) {
			final char c = value.charAt(i);
			if (isProtectedChar(c)) {
				result.append(_escape);
			}
			result.append(c);
		}

		return result.toString();
	}

	private boolean isProtectedChar(final char c) {
		for (var c1 : _protect) {
			if (c == c1) return true;
		}
		return false;
	}


	public String unescape(final CharSequence value) {
		final StringBuilder result = new StringBuilder();

		boolean escaping = false;
		for (int i = 0; i < value.length(); ++i) {
			final char c = value.charAt(i);

			if (c == _escape &&
				!escaping &&
				i + 1 < value.length() &&
				isProtectedChar(value.charAt(i + 1)))
			{
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

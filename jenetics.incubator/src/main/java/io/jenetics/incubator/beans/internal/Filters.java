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
package io.jenetics.incubator.beans.internal;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import io.jenetics.incubator.beans.PathEntry;
import io.jenetics.incubator.beans.description.Description;
import io.jenetics.incubator.beans.property.Property;
import io.jenetics.incubator.beans.property.SimpleProperty;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class Filters {
	private Filters() {
	}

	public static final Predicate<? super PathEntry<? extends Type>>
		STANDARD_SOURCE_DESCRIPTION_FILTER =
		type -> {
			final var cls = type.value() instanceof ParameterizedType pt
				? (Class<?>)pt.getRawType()
				: (Class<?>)type.value();

			final var name = cls.getName();

			return
				// Allow native Java arrays, except byte[] arrays.
				(name.startsWith("[") && !name.endsWith("[B")) ||
					// Allow Java collection classes.
					Collection.class.isAssignableFrom(cls) ||
					(
						!name.startsWith("java") &&
							!name.startsWith("com.sun") &&
							!name.startsWith("sun") &&
							!name.startsWith("jdk")
					);
		};

	public static final Predicate<? super Description>
		STANDARD_TARGET_DESCRIPTION_FILTER =
		prop -> !(prop.value() instanceof Description.Value.Single &&
			prop.value().enclosure().getName().startsWith("java"));


	public static final Predicate<? super PathEntry<?>>
		STANDARD_SOURCE_FILTER =
		object -> {
			final var type = object.value() != null
				? object.value().getClass()
				: Object.class;

			return Filters.STANDARD_SOURCE_DESCRIPTION_FILTER
				.test(PathEntry.of(object.path(), type));
		};

	public static final Predicate<? super Property> STANDARD_TARGET_FILTER = prop ->
		!(prop instanceof SimpleProperty &&
			prop.value().enclosure().getClass().getName().startsWith("java"));


	public static Pattern toRegexPattern(final String glob) {
		return Pattern.compile(
			"^" +
				Pattern.quote(glob)
					.replace("*", "\\E.*\\Q")
					.replace("?", "\\E.\\Q") +
				"$"
		);
	}

	private static final class Glob {
		private static final String META_CHARS = "\\*?[{";
		private static final char EOL = 0;

		static String toRegexPattern(final String pattern) {
			boolean inGroup = false;
			StringBuilder regex = new StringBuilder("^");

			int i = 0;
			while (i < pattern.length()) {
				char c = pattern.charAt(i++);
				switch (c) {
					case '\\' -> {
						if (i == pattern.length()) {
							throw new PatternSyntaxException(
								"No character to escape", pattern, i - 1
							);
						}
						char next = pattern.charAt(i++);
						if (isGlobMeta(next)) {
							regex.append('\\');
						}
						regex.append(next);
					}
					case '[' -> {
						regex.append("[[^/]&&[");
						if (next(pattern, i) == '^') {
							regex.append("\\^");
							i++;
						} else {
							if (next(pattern, i) == '!') {
								regex.append('^');
								i++;
							}
							if (next(pattern, i) == '-') {
								regex.append('-');
								i++;
							}
						}
						boolean hasRangeStart = false;
						char last = 0;
						while (i < pattern.length()) {
							c = pattern.charAt(i++);
							if (c == ']') {
								break;
							}
							if (c == '/') {
								throw new PatternSyntaxException(
									"Explicit 'name separator' in class",
									pattern, i - 1
								);
							}
							if (c == '\\' || c == '[' || c == '&' &&
								next(pattern, i) == '&')
							{
								regex.append('\\');
							}
							regex.append(c);

							if (c == '-') {
								if (!hasRangeStart) {
									throw new PatternSyntaxException(
										"Invalid range", pattern, i - 1
									);
								}
								if ((c = next(pattern, i++)) == EOL || c == ']') {
									break;
								}
								if (c < last) {
									throw new PatternSyntaxException(
										"Invalid range", pattern, i - 3
									);
								}
								regex.append(c);
								hasRangeStart = false;
							} else {
								hasRangeStart = true;
								last = c;
							}
						}
						if (c != ']') {
							throw new PatternSyntaxException(
								"Missing ']", pattern, i - 1
							);
						}
						regex.append("]]");
					}
					case '{' -> {
						if (inGroup) {
							throw new PatternSyntaxException(
								"Cannot nest groups", pattern, i - 1
							);
						}
						regex.append("(?:(?:");
						inGroup = true;
					}
					case '}' -> {
						if (inGroup) {
							regex.append("))");
							inGroup = false;
						} else {
							regex.append('}');
						}
					}
					case ',' -> {
						if (inGroup) {
							regex.append(")|(?:");
						} else {
							regex.append(',');
						}
					}
					case '*' -> {
						if (next(pattern, i) == '*') {
							regex.append(".*");
							i++;
						} else {
							regex.append("[^/]*");
						}
					}
					case '?' -> regex.append("[^/]");
					default -> regex.append(c);
				}
			}

			if (inGroup) {
				throw new PatternSyntaxException("Missing '}", pattern, i - 1);
			}

			return regex.append('$').toString();
		}

		private static boolean isGlobMeta(final char c) {
			return META_CHARS.indexOf(c) != -1;
		}

		private static char next(final String glob, final int i) {
			if (i < glob.length()) {
				return glob.charAt(i);
			}
			return EOL;
		}
	}

}

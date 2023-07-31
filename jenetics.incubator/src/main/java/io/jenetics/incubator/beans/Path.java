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
package io.jenetics.incubator.beans;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

/**
 * Represents the path, which uniquely identifies a property/description. A
 * path can be created with the {@link Path#of(String)} method.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class Path implements Iterable<Path>, Comparable<Path> {

	private static final Pattern PATH_ELEMENT_PATTERN =
		Pattern.compile("\\b[_a-zA-Z][_a-zA-Z0-9]*\\b|\\[[0-9^]+]");

	/**
	 * Represents the path element.
	 */
	public sealed interface Element extends Comparable<Element> {
		private static Element parse(final String value) {
			if (value.startsWith("[") && value.endsWith("]")) {
				try {
					final var index = Integer
						.parseInt(value.substring(1, value.length() - 1));

					return new Index(index);
				} catch (NumberFormatException e) {
					throw new IllegalArgumentException(
						"Invalid list-index path element '%s'."
							.formatted(value)
					);
				}
			} else {
				return new Name(value);
			}
		}
	}

	/**
	 * Path element which represents a property name.
	 *
	 * @param name the property name
	 */
	public record Name(String name) implements Element {

		public Name {
			if (!isValid(name)) {
				throw new IllegalArgumentException(
					"'%s' is not a valid field name.".formatted(name)
				);
			}
		}

		private static boolean isValid(final String name) {
			if (name.isEmpty()) {
				return false;
			}
			int cp = name.codePointAt(0);
			if (!Character.isJavaIdentifierStart(cp)) {
				return false;
			}
			for (int i = Character.charCount(cp);
				 i < name.length();
				 i += Character.charCount(cp)) {
				cp = name.codePointAt(i);
				if (!Character.isJavaIdentifierPart(cp)) {
					return false;
				}
			}
			return true;
		}

		@Override
		public int compareTo(final Element other) {
			if (other instanceof Name nme) {
				return name.compareTo(nme.name);
			} else {
				return 1;
			}
		}

		@Override
		public String toString() {
			return name;
		}
	}

	/**
	 * Path element which represents a list/array index.
	 *
	 * @param index the list/array index
	 */
	public record Index(int index) implements Element {

		@Override
		public int compareTo(final Element other) {
			if (other instanceof Index idx) {
				return Integer.compare(index, idx.index);
			} else {
				return -1;
			}
		}

		@Override
		public String toString() {
			return "[%d]".formatted(index);
		}
	}

	private static final Path EMPTY = new Path(List.of());

	private final List<Element> elements;

	/**
	 * Create a new <em>path</em> object with the given element.
	 *
	 * @param elements the path elements
	 */
	private Path(final List<Element> elements) {
		this.elements = List.copyOf(elements);
	}

	/**
	 * Return the head path. This is the path element <em>farthest</em> away
	 * from the root.
	 *
	 * @return the property name, or {@code null} if {@code this} path is empty
	 */
	public Path head() {
		return isEmpty() ? null : get(count() - 1);
	}

	/**
	 * Return the path element of {@code this path}.
	 *
	 * @return the path element, or {@code null} if {@code this} path is empty
	 */
	public Element element() {
		return isEmpty() ? null : elements.get(count() - 1);
	}

	/**
	 * Returns the number of elements in the path.
	 *
	 * @return the number of elements in the path, or 0 if this path only
	 * represents a root component
	 */
	public int count() {
		return elements.size();
	}

	/**
	 * Returns the <em>parent path</em>, or {@code null} if this
	 * path has no parent.
	 *
	 * @return a path representing the path's parent, or {@code null} if
	 *         {@code this} path has no parent
	 */
	public Path parent() {
		return count() > 1 ? subPath(0, count() - 1) : null;
	}

	/**
	 * Returns a relative {@code Path} that is a subsequence of the name
	 * elements of this path.
	 *
	 * @param fromIndex low endpoint (inclusive) of the subPath
	 * @param toIndex   high endpoint (exclusive) of the subPath
	 * @return a new {@code Path} object that is a subsequence of the
	 * elements in this {@code Path}
	 * @throws IndexOutOfBoundsException for an illegal endpoint index value
	 *        ({@code fromIndex < 0 || toIndex > size || fromIndex > toIndex})
	 */
	public Path subPath(final int fromIndex, final int toIndex) {
		return new Path(elements.subList(fromIndex, toIndex));
	}

	/**
	 * Appends the given {@code paths} to {@code this} and returns a new
	 * path object.
	 *
	 * @param paths the paths to append to {@code this} path
	 * @return a new path object
	 */
	public Path append(final Path... paths) {
		final var list = new ArrayList<>(this.elements);
		for (var path : paths) {
			list.addAll(path.elements);
		}
		return new Path(list);
	}

	/**
	 * Appends the given {@code elements} to {@code this} and returns a new
	 * path object.
	 *
	 * @param elements the paths to append to {@code this} path
	 * @return a new path object
	 */
	public Path append(final Element... elements) {
		final var list = new ArrayList<>(this.elements);
		list.addAll(Arrays.asList(elements));
		return new Path(list);
	}

	/**
	 * Appends the given {@code names} to {@code this} and returns a new
	 * path object.
	 *
	 * @param names the paths to append to {@code this} path
	 * @return a new path object
	 */
	public Path append(final String... names) {
		final var list = new ArrayList<>(this.elements);
		for (var name : names) {
			list.add(new Name(name));
		}
		return new Path(list);
	}

	/**
	 * Return {@code true} if this path contains no elements.
	 *
	 * @return {@code true} if this path contains no elements, {@code false}
	 * otherwise
	 */
	public boolean isEmpty() {
		return count() == 0;
	}

	/**
	 * Returns an element of this path. The index parameter is the index of
	 * the path element to return. The element that is closest to the root
	 * in the property hierarchy has index 0. The element that is farthest
	 * from the root has index count - 1.
	 *
	 * @param index the path index
	 * @return the path element
	 */
	public Path get(final int index) {
		return new Path(List.of(elements.get(index)));
	}

	@Override
	public int compareTo(final Path other) {
		for (int i = 0, n = Math.min(count(), other.count()); i < n; ++i) {
			int cmp = elements.get(i).compareTo(other.elements.get(i));
			if (cmp != 0) {
				return cmp;
			}
		}

		return Integer.compare(count(), other.count());
	}

	@Override
	public Iterator<Path> iterator() {
		return IntStream.range(0, count())
			.mapToObj(this::get)
			.iterator();
	}

	@Override
	public int hashCode() {
		return elements.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		return obj == this ||
			obj instanceof Path path &&
			elements.equals(path.elements);
	}

	@Override
	public String toString() {
		return toString(elements);
	}

	private static String toString(final List<Element> elements) {
		final var out = new StringBuilder();

		for (int i = 0; i < elements.size(); ++i) {
			Element element = elements.get(i);
			out.append(element);

			if (i < elements.size() - 1
				&& !(elements.get(i + 1) instanceof Index)) {
				out.append('.');
			}
		}

		return out.toString();
	}

	/**
	 * Create a new path object which consists of the given path <em>elements</em>.
	 *
	 * @param elements the path elements of the created path
	 * @return a new path object.
	 */
	public static Path of(final Element... elements) {
		return elements.length == 0
			? EMPTY
			: new Path(List.of(elements));
	}

	/**
	 * Create a new property path form the given string {@code value}. A
	 * valid path consists of a names, which must be a valid Java identifier,
	 * and indexes, separated by a dot, '.'. A valid path with three elements
	 * will look like this:
	 * <pre>{@code
	 * final var path = Path.of("name1.name2[9].value");
	 * }</pre>
	 *
	 * @param value the path value
	 * @return a new property path
	 * @throws IllegalArgumentException if the given path is invalid
	 */
	public static Path of(final String value) {
		if (value.isEmpty()) {
			return EMPTY;
		}

		final var matcher = PATH_ELEMENT_PATTERN.matcher(value);
		final var elements = new ArrayList<Element>();
		while (matcher.find()) {
			elements.add(Element.parse(matcher.group()));
		}

		if (!toString(elements).equals(value)) {
			throw new IllegalArgumentException(
				"Invalid path: '%s'.".formatted(value)
			);
		}

		return new Path(elements);
	}

}

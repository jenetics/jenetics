package io.jenetics.incubator.property;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import io.jenetics.incubator.property.Property.Path;

class PathMatcher {

	private interface Matcher {
		boolean match(final Iterator<Path> path);
	}

	private static abstract class SinglePathMatcher implements Matcher {

	}

	private static final class MultiPathMatcher implements Matcher {
		private Matcher next;

		MultiPathMatcher() {
		}

		void next(final Matcher matcher) {
			next = matcher;
		}

		@Override
		public boolean match(final Iterator<Path> path) {
			if (next == null) {
				while (path.hasNext()) {
					path.next();
				}
				return true;
			}

			boolean matching;
			do {
				matching = next.match(path);
			} while (!matching && path.hasNext());

			return matching;
		}
	}

	private static final class SingleMatch implements Matcher {
		private final String name;
		private final Integer index;

		SingleMatch(final String name, final Integer index) {
			this.name = name;
			this.index = index;
		}

		@Override
		public boolean match(final Iterator<Path> path) {
			if (path.hasNext()) {
				final var element = path.next();
				return
					(name == null || name.equals(element.name())) &&
						(index == null || index.equals(element.index()));
			} else {
				return false;
			}
		}

		@Override
		public String toString() {
			return (name != null ? name : "*") +
				(index != null ? "[" + index + "]" : "*");
		}
	}

	private final List<Matcher> matchers;

	PathMatcher(final List<Matcher> matchers) {
		this.matchers = List.copyOf(matchers);
	}

	public boolean matches(final Path path) {
		final var paths = path.iterator();
		final var matchers = this.matchers.iterator();

		while (matchers.hasNext()) {
			final var matcher = matchers.next();

			final var matches = matcher.match(paths);
			if (!matches) {
				return false;
			}
		}

		return !paths.hasNext() && !matchers.hasNext();
	}

	private record PreMatcher(String name, Integer index) {
	}

	/**
	 * Compiles a expression into a path pattern.
	 * <p>
	 * * foos[2].bar.foos[1].value.index
	 * * foos*.*.foos[*].value.*
	 * <p>
	 * foos*, foos[*], value, *
	 *
	 * <b>Single path matcher</b>
	 * foos*[1]
	 * foos[*]
	 * foos[1]
	 * foos*
	 * foos
	 * foos*[*]
	 * *[0]
	 * *
	 *
	 * <b>Multi path matcher</b>
	 * ** // Not valid in single path matcher
	 *
	 * <li>
	 * <ul>'<b>*</b>': matches an arbitrary path element</ul>
	 * <ul>'<b>**</b>': matches an arbitrary path elements</ul>
	 * </li>
	 *
	 * @param pattern
	 * @return
	 */
	static PathPattern compile(final String pattern) {
		final String[] parts = pattern.split(Pattern.quote("."), -1);


		final var pre = Stream.of(parts)
			.map(part -> new PreMatcher(name(part), index(part)))
			.toList();

		final var matchers = new ArrayList<Matcher>();
		for (int i = 0; i < pre.size(); ++i) {
			final var p = pre.get(i);
			matchers.add(matcher(pre.get(i), i < pre.size() - 1 ? pre.get(i + 1) : null));
		}

		return null;
	}

	private static Matcher matcher(final PreMatcher matcher, final PreMatcher next) {
		if (matcher == null) {
			return null;
		}

		/*
		if ("**".equals(matcher.name)) {
			return new MultiPathMatcher(matcher(next, null));
		} else {
			return new SingleMatch(matcher.name, matcher.index);
		}
		 */

		return null;
	}

	private static String name(final String pattern) {
		if ("*".equals(pattern)) {
			return null;
		}

		final int begin = pattern.indexOf('[');
		if (begin != -1) {
			return pattern.substring(0, begin);
		} else {
			return pattern;
		}
	}

	private static Integer index(final String pattern) {
		final int begin = pattern.indexOf('[');
		final int end = pattern.indexOf(']');

		if (begin != -1 && end != -1) {
			final String index = pattern.substring(begin + 1, end);
			try {
				return Integer.parseInt(index);
			} catch (NumberFormatException ignore) {
				return null;
			}
		} else {
			return null;
		}
	}

}

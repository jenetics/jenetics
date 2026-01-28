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
package io.jenetics.incubator.restful;

import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.requireNonNull;
import static java.util.function.Predicate.not;

import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * This class represents the path of an endpoint. The path parameters are put
 * between curly braces.
 * {@snippet lang=java:
 * var path = Path.of("/users/{user-id}/addresses/{address-id}/street");
 * assert path.parameters().equals(Set.of("user-id", "address-id"));
 *
 * path = path.resolve("user-id", "4", "address-id", "8");
 * assert path.toString().equals("/users/4/addresses/8/street");
 * }
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since !__version__!
 * @version !__version__!
 */
public final class Path {

	private final String value;
	private final List<ParamIndex> params;

	private Path(final String value, final List<ParamIndex> params) {
		this.value = requireNonNull(value);
		this.params = List.copyOf(params);
	}

	/**
	 * Tests whether {@code this} path has unresolved path parameters.
	 *
	 * @return {@code true} if {@code this} path hasn't any <em>free</em> path
	 *         parameter, {@code false} otherwise
	 */
	public boolean isResolved() {
		return params.isEmpty();
	}

	/**
	 * Return the unresolved parameter names.
	 *
	 * @return the unresolved parameter names
	 */
	public Set<String> parameters() {
		return params.stream()
			.map(ParamIndex::name)
			.collect(Collectors.toUnmodifiableSet());
	}

	/**
	 * Resolves the path with the given list of path parameters.
	 *
	 * @param parameters the path parameters
	 * @return a new resolved path object
	 */
	public Path resolve(final Collection<? extends Parameter.Path> parameters) {
		if (parameters.isEmpty()) {
			return this;
		}

		final Map<String, List<Parameter>> params = parameters.stream()
			.collect(Collectors.groupingBy(Parameter::key));

		final var duplicates = params.entrySet().stream()
			.filter(entry -> entry.getValue().size() > 1)
			.map(Map.Entry::getKey)
			.toList();

		if (!duplicates.isEmpty()) {
			throw new IllegalArgumentException(
				"Duplicate parameters: " + duplicates
			);
		}

		final var updatedParams = new ArrayList<ParamIndex>();
		final var resolved = new StringBuilder(value);
		int offset = 0;

		for (var param : this.params) {
			final var key = param.name;

			if (params.containsKey(key)) {
				var value = params.get(key).getFirst().value();
				value = URLEncoder.encode(value, UTF_8);

				resolved.insert(param.index + offset, value);
				resolved.delete(param.index + offset - 1, param.index + offset);
				offset += value.length() - 1;
			} else {
				updatedParams.add(new ParamIndex(param.name, param.index + offset));
			}
		}

		return new Path(resolved.toString(), updatedParams);
	}

	/**
	 * Resolves the path with the given array of name-value pairs. The length of
	 * the parameter array must be even.
	 *
	 * @param values array of name-value pairs
	 * @return the resolved path
	 * @throws IllegalArgumentException if the length of name-value array is
	 *         odd
	 */
	public Path resolve(final String...values) {
		if (values.length%2 != 0) {
			throw new IllegalArgumentException(format(
				"Path value length must be even %d",
				values.length
			));
		}
		if (values.length == 0) {
			return this;
		}

		final var params = IntStream.range(0, values.length/2)
			.mapToObj(i -> Parameter.path(values[i*2], values[i*2 + 1]))
			.toList();

		return resolve(params);
	}

	/**
	 * Resolves the given path parameter
	 *
	 * @param parameter the parameter to be resolved
	 * @return the resolved path
	 */
	public Path resolve(final Parameter.Path parameter) {
		return resolve(List.of(parameter));
	}

	/**
	 * Converts {@code this} path to an {@code URI}. The returned {@code URI} is
	 * always <em>relative</em> ({@link URI#isAbsolute()} == {@code false}).
	 *
	 * @return {@code this} path as {@code URI}
	 */
	public URI toURI() {
		return URI.create("./" + this).normalize();
	}

	@Override
	public int hashCode() {
		return Objects.hash(value, params);
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof Path path &&
			value.equals(path.value) &&
			params.equals(path.params);
	}

	@Override
	public String toString() {
		if (isResolved()) {
			return value;
		}

		final var result = new StringBuilder();

		int index = 0;
		for (var param : params) {
			result.append(value, index, param.index - 1);
			result.append("{").append(param.name).append("}");
			index = param.index;
		}
		result.append(value.substring(index));

		return result.toString();
	}

	/* *************************************************************************
	 * Static factory methods.
	 * ************************************************************************/

	private record ParamIndex(String name, int index) {
	}

	private static final Pattern PARAM_PATTERN = Pattern.compile("\\{(.*?)}");

	/**
	 * Create a new Path object from the given path string.
	 * <ul>
	 *     <li>{@code asdf}</li>
	 * </ul>
	 *
	 * @param path the path string to parse.
	 * @return the newly created {@code Path} object
	 * @throws NullPointerException if the given SQL string is {@code null}
	 * @throws IllegalArgumentException if one of the parameter names is not a
	 *         valid Java identifier
	 */
	public static Path of(final String path) {
		final List<ParamIndex> params = new ArrayList<>();
		final var parsed = new StringBuilder();

		var normalizedPath = java.nio.file.Path.of(path)
			.normalize()
			.toString();
		if (!normalizedPath.startsWith("/")) {
			normalizedPath = "/" + normalizedPath;
		}

		final Matcher matcher = PARAM_PATTERN.matcher(normalizedPath);

		while (matcher.find()) {
			final var group = matcher.group();
			final String name = group.substring(1, group.length() - 1);
			matcher.appendReplacement(parsed, "?");
			final int index = parsed.length();
			params.add(new ParamIndex(name, index));
		}
		matcher.appendTail(parsed);

		final var invalid = params.stream()
			.map(p -> p.name)
			.filter(not(Path::isValid))
			.toList();

		if (!invalid.isEmpty()) {
			throw new IllegalArgumentException(format(
				"Found invalid parameter names: %s", invalid
			));
		}

		return new Path(parsed.toString(), params);
	}

	private static boolean isValid(final String name) {
		if (name.isBlank()) {
			return false;
		}

		for (int i = 0, n = name.length(); i < n; ++i) {
			final char c = name.charAt(i);
			if (Character.isWhitespace(c)) {
				return true;
			}
		}

		return true;
	}

}

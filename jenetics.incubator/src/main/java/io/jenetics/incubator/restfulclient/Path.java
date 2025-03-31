package io.jenetics.incubator.restfulclient;

import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.requireNonNull;
import static java.util.function.Predicate.not;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.Externalizable;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class Path implements Serializable {

	private record Param(int index, String name) {}

	private static final Pattern PARAM_PATTERN = Pattern.compile("\\{(.*?)}");

	private final String string;
	private final List<Param> params;

	private List<String> paramNames = null;

	private Path(final String string, final List<Param> params) {
		this.string = requireNonNull(string);
		this.params = List.copyOf(params);
	}

	/**
	 * Return the original path string, this object is created with.
	 *
	 * @return the original path string
	 */
	String path() {
		final var sql = new StringBuilder();

		int index = 0;
		for (var param : params) {
			sql.append(string, index, param.index - 1);
			sql.append("{").append(param.name).append("}");
			index = param.index;
		}
		sql.append(string.substring(index));

		return sql.toString();
	}

	/**
	 * Return the list of parsed parameter names. The list may be empty or
	 * contain duplicate entries, depending on the input string. The list is
	 * in exactly the order they appeared in the SQL string.
	 *
	 * @return the parsed parameter names
	 */
	List<String> paramNames() {
		List<String> names = paramNames;
		if (names == null) {
			paramNames = names = params.stream()
				.map(Param::name)
				.toList();
		}

		return names;
	}

	Path resolve(final String name, final String value) {
		if (params.isEmpty()) {
			return this;
		}

		final var expansion = URLEncoder.encode(value, UTF_8);

		final List<Param> parameters = new ArrayList<>();
		final var resolved = new StringBuilder(string);
		int offset = 0;

		for (var param : params) {
			if (param.name.equals(name)) {
				resolved.insert(param.index + offset, expansion);
				resolved.delete(param.index + offset - 1, param.index + offset);
			} else {
				parameters.add(new Param(param.index + offset, param.name));
			}
		}

		return new Path(resolved.toString(), parameters);
	}

	Path resolve(final Parameter.Path parameter) {
		return resolve(parameter.key(), parameter.value());
	}

	@Override
	public int hashCode() {
		return Objects.hash(string, params);
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof Path path &&
			string.equals(path.string) &&
			params.equals(path.params);
	}

	@Override
	public String toString() {
		return path();
	}

	/* *************************************************************************
	 * Static factory methods.
	 * ************************************************************************/

	/**
	 * Create a new Path object from the given path string.
	 *
	 * @param path the path string to parse.
	 * @return the newly created {@code Path} object
	 * @throws NullPointerException if the given SQL string is {@code null}
	 * @throws IllegalArgumentException if one of the parameter names is not a
	 *         valid Java identifier
	 */
	static Path of(final String path) {
		final List<Param> params = new ArrayList<>();
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
			params.add(new Param(index, name));
		}
		matcher.appendTail(parsed);

		final var invalid = params.stream()
			.map(p -> p.name)
			.filter(not(Path::isIdentifier))
			.toList();

		if (!invalid.isEmpty()) {
			throw new IllegalArgumentException(format(
				"Found invalid parameter names: %s", invalid
			));
		}

		return new Path(parsed.toString(), params);
	}

	private static boolean isIdentifier(final String name) {
		if (name.isEmpty()) {
			return false;
		}
		int cp = name.codePointAt(0);
		if (!Character.isJavaIdentifierStart(cp)) {
			return false;
		}
		for (int i = Character.charCount(cp);
		     i < name.length();
		     i += Character.charCount(cp))
		{
			cp = name.codePointAt(i);
			if (!Character.isJavaIdentifierPart(cp)) {
				return false;
			}
		}
		return true;
	}

	/* *************************************************************************
	 *  Serialization methods
	 * ************************************************************************/

	@java.io.Serial
	private Object writeReplace() {
		return new Serial(this);
	}

	@java.io.Serial
	private void readObject(final ObjectInputStream stream)
		throws InvalidObjectException
	{
		throw new InvalidObjectException("Serialization proxy required.");
	}

	private static final class Serial implements Externalizable {
		@java.io.Serial
		private static final long serialVersionUID = 1;

		/**
		 * The object being serialized.
		 */
		private Path object;

		/**
		 * Constructor for deserialization.
		 */
		public Serial() {
		}

		/**
		 * Creates an instance for serialization.
		 *
		 * @param object  the object
		 */
		Serial(final Path object) {
			this.object = object;
		}

		@Override
		public void writeExternal(final ObjectOutput out) throws IOException {
			object.write(out);
		}

		@Override
		public void readExternal(final ObjectInput in) throws IOException {
			object = Path.read(in);
		}

		@java.io.Serial
		private Object readResolve() {
			return object;
		}

	}

	private void write(final DataOutput out) throws IOException {
		out.writeUTF(path());
	}

	private static Path read(final DataInput in) throws IOException {
		final String path = in.readUTF();
		return Path.of(path);
	}

}

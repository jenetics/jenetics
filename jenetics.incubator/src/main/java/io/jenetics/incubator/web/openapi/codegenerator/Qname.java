package io.jenetics.incubator.web.openapi.codegenerator;

import static java.util.Objects.requireNonNull;

/**
 * Represents the qualified name of a Java type.
 *
 * @param namespace the package of the type
 * @param name the simple name of the type
 */
public record Qname(String namespace, String name) {

	public Qname {
		requireNonNull(namespace);
		requireNonNull(name);
	}

	/**
	 * Create a new type name with the default namespace/package, {@code ""}.
	 *
	 * @param name the simple type name
	 */
	public Qname(String name) {
		this("", name);
	}

	public boolean isJavaName() {
		return namespace.startsWith("java.") ||
			namespace.startsWith("javax.") ||
			name.equals("boolean") ||
			name.equals("boolean[]") ||
			name.equals("byte") ||
			name.equals("byte[]") ||
			name.equals("short") ||
			name.equals("short[]") ||
			name.equals("char") ||
			name.equals("char[]") ||
			name.equals("int") ||
			name.equals("int[]") ||
			name.equals("long") ||
			name.equals("long[]") ||
			name.equals("float") ||
			name.equals("float[]") ||
			name.equals("double") ||
			name.equals("double[]");
	}

	public boolean isSchemaName() {
		return !isJavaName();
	}

	@Override
	public String toString() {
		if (namespace.isEmpty()) {
			return name;
		} else {
			return namespace + "." + name;
		}
	}


	/**
	 * Create a new qualified type name from the given, possible qualified, type
	 * name.
	 *
	 * @param typeName the possible qualified type name
	 * @return a new qualified name
	 */
	public static Qname of(String typeName) {
		final var index = typeName.lastIndexOf('.');
		if (index != -1) {
			return new Qname(
				typeName.substring(0, index),
				typeName.substring(index + 1)
			);
		} else {
			return new Qname(typeName);
		}
	}

}

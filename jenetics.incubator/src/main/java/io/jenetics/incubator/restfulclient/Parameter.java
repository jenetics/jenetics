package io.jenetics.incubator.restfulclient;

public sealed interface Parameter {

	@FunctionalInterface
	interface Value {
		Parameter value(String value);
	}

	non-sealed interface Header extends Parameter {
		static Value key(String key) {
			return value -> header(key, value);
		}
	}
	non-sealed interface Path extends Parameter {
		static Value key(String key) {
			return value -> path(key, value);
		}
	}
	non-sealed interface Query extends Parameter {
		static Value key(String key) {
			return value -> query(key, value);
		}
	}

	String key();
	String value();

	static Header header(final String key, final String value) {
		record SimpleHeader(String key, String value) implements Header {};
		return new SimpleHeader(key, value);
	}

	static Path path(final String key, final String value) {
		record SimplePath(String key, String value) implements Path {};
		return new SimplePath(key, value);
	}

	static Query query(final String key, final String value) {
		record SimpleQuery(String key, String value) implements Query {};
		return new SimpleQuery(key, value);
	}

}

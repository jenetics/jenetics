package io.jenetics.incubator.util;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

public final class ObjectHash {

	@FunctionalInterface
	interface Digester {
		void update(final byte value);
		default void update(final byte[] values) {
			for (var value : values) {
				update(value);
			}
		}
	}

	private ObjectHash() {
	}


	private static void hash(
		final Object value,
		final Predicate<? super Field> accept,
		final Digester digest
	) {
		if (value != null) {
			final var fields = fields(value.getClass(), accept);
			for (var field : fields) {
				final var fieldValue = value(field, value);

				if (fieldValue instanceof Boolean v) {
					hash(v, digest);
				} else if (fieldValue instanceof Byte v) {
					hash(v, digest);
				} else if (fieldValue instanceof Character v) {
					hash(v, digest);
				} else if (fieldValue instanceof Short v) {
					hash(v, digest);
				} else if (fieldValue instanceof Integer v) {
					hash(v, digest);
				} else if (fieldValue instanceof Long v) {
					hash(v, digest);
				} else if (fieldValue instanceof Float v) {
					hash(v, digest);
				} else if (fieldValue instanceof Double v) {
					hash(v, digest);
				} else if (fieldValue instanceof CharSequence v) {
					hash(v, digest);
				} else if (fieldValue instanceof Enum<?> v) {
					hash(v, digest);
				} else if (fieldValue instanceof BigInteger v) {
					hash(v, digest);
				} else if (fieldValue instanceof BigDecimal v) {
					hash(v, digest);
				} else if (fieldValue instanceof Instant v) {
					hash(v, digest);
				} else if (fieldValue instanceof LocalDate v) {
					hash(v, digest);
				} else if (fieldValue instanceof Iterable<?> v) {
					hash(v, accept, digest);
				} else if (fieldValue != null) {
					hash(fieldValue, accept, digest);
				}
			}
		}
	}

	/* *************************************************************************
	 * Hashing primitive types.
	 * ************************************************************************/

	private static void hash(final boolean value, final Digester digest) {
		digest.update((byte)(value ? 1 : 0));
	}

	private static void hash(final byte value, final Digester digest) {
		digest.update(value);
	}

	private static void hash(final char value, final Digester digest) {
		digest.update((byte)(value >>> 8));
		digest.update((byte)value);
	}

	private static void hash(final short value, final Digester digest) {
		digest.update((byte)(value >>> 8));
		digest.update((byte)value);
	}

	private static void hash(final int value, final Digester digest) {
		digest.update((byte)(value >>> 24));
		digest.update((byte)(value >>> 16));
		digest.update((byte)(value >>> 8));
		digest.update((byte)value);
	}

	private static void hash(final long value, final Digester digest) {
		digest.update((byte)(value >>> 56));
		digest.update((byte)(value >>> 48));
		digest.update((byte)(value >>> 40));
		digest.update((byte)(value >>> 32));
		digest.update((byte)(value >>> 24));
		digest.update((byte)(value >>> 16));
		digest.update((byte)(value >>> 8));
		digest.update((byte)value);
	}

	private static void hash(final float value, final Digester digest) {
		hash(Float.floatToIntBits(value), digest);
	}

	private static void hash(final double value, final Digester digest) {
		hash(Double.doubleToLongBits(value), digest);
	}

	private static void hash(final CharSequence value, final Digester digest) {
		if (value != null) {
			for (int i = 0; i < value.length(); ++i) {
				hash(value.charAt(i), digest);
			}
		}
	}

	private static void hash(final Enum<?> value, final Digester digest) {
		if (value != null) {
			hash(value.ordinal(), digest);
		}
	}

	private static void hash(final BigInteger value, final Digester digest) {
		if (value != null) {
			digest.update(value.toByteArray());
		}
	}

	private static void hash(final BigDecimal value, final Digester digest) {
		if (value != null) {
			hash(value.toPlainString(), digest);
		}
	}

	private static void hash(final Instant value, final Digester digest) {
		if (value != null) {
			hash(value.toEpochMilli(), digest);
		}
	}

	private static void hash(final LocalDate value, final Digester digest) {
		if (value != null) {
			hash(value.getYear(), digest);
			hash(value.getDayOfYear(), digest);
		}
	}

	private static void hash(
		final Iterable<?> values,
		final Predicate<? super Field> accept,
		final Digester digest
	) {
		if (values != null) {
			var hash = BigInteger.ZERO;
			for (var value : values) {
				final var md = getMessageDigest("SHA1");
				hash(value, accept, md::update);
				final var result = new BigInteger(md.digest());

				hash = hash.add(result);
			}

			hash(hash, digest);
		}
	}

	private static MessageDigest getMessageDigest(final String algorithm) {
		try {
			return MessageDigest.getInstance(algorithm);
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalArgumentException(e);
		}
	}

	/* *************************************************************************
	 * Reflection methods.
	 * ************************************************************************/

	private static final class FieldWalker {

	}

	public record Property(String path, Object source, Field field) {}

	static Stream<Property> list(final Object root) {

		return null;
	}

	private static Object value(final Field field, final Object object) {
		try {
			return field.get(object);
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException(e);
		}
	}

	private static List<Field> fields(
		final Class<?> type,
		final Predicate<? super Field> accept
	) {
		final var fields = new ArrayList<Field>();
		fields(type, accept, fields);
		fields.sort(Comparator.comparing(Field::getName));

		return List.copyOf(fields);
	}

	private static void fields(
		final Class<?> type,
		final Predicate<? super Field> accept,
		final List<Field> fields
	) {

		for (var field : type.getDeclaredFields()) {
			if (accept.test(field)) {
				fields.add(field);
			}
		}

		final var superType = type.getSuperclass();
		if (superType != null && superType != Object.class) {
			fields(superType, accept, fields);
		}
	}

	/* *************************************************************************
	 * Main test function.
	 * ************************************************************************/

	record Foo(String a, int b, long c, double d) {}

	public static void main(final String[] args) {
		final var foo = new Foo("asdf", 1, 2, 4);

		final var fields = fields(foo.getClass(), f -> true);
		fields.forEach(System.out::println);
	}

}

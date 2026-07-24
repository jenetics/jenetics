package io.jenetics.incubator.csv;

import static java.util.Objects.requireNonNull;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.MonthDay;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Year;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public final class StringFormats implements StringFormat {

	private record Format<T>(
		Function<? super T, String> format,
		Function<String, ? extends T> parser
	) {
		Format {
			requireNonNull(format);
			requireNonNull(parser);
		}

		Format(Function<String, ? extends T> parser) {
			this(Objects::toString, parser);
		}

		String format(T value) {
			return value != null ? format.apply(value) : null;
		}

		T parse(String value) {
			return value != null ? parse(value) : null;
		}
	}

	private static final Map<Class<?>, Format<?>>
		DEFAULT_FORMATS =
		Map.ofEntries(
			Map.entry(String.class, new Format<>(Function.identity())),
			Map.entry(boolean.class, new Format<>(Boolean::parseBoolean)),
			Map.entry(Boolean.class, new Format<>(Boolean::parseBoolean)),
			Map.entry(byte.class, new Format<>(Byte::parseByte)),
			Map.entry(Byte.class, new Format<>(Byte::parseByte)),
			Map.entry(short.class, new Format<>(Short::parseShort)),
			Map.entry(Short.class, new Format<>(Short::parseShort)),
			Map.entry(int.class, new Format<>(Integer::parseInt)),
			Map.entry(Integer.class, new Format<>(Integer::parseInt)),
			Map.entry(long.class, new Format<>(Long::parseLong)),
			Map.entry(Long.class, new Format<>(Long::parseLong)),
			Map.entry(float.class, new Format<>(Float::parseFloat)),
			Map.entry(Float.class, new Format<>(Float::parseFloat)),
			Map.entry(double.class, new Format<>(Double::parseDouble)),
			Map.entry(Double.class, new Format<>(Double::parseDouble)),
			Map.entry(BigInteger.class, new Format<>(BigInteger::new)),
			Map.entry(BigDecimal.class, new Format<>(BigDecimal::new)),
			Map.entry(LocalDate.class, new Format<>(LocalDate::parse)),
			Map.entry(LocalTime.class, new Format<>(LocalTime::parse)),
			Map.entry(LocalDateTime.class, new Format<>(LocalDateTime::parse)),
			Map.entry(OffsetTime.class, new Format<>(OffsetTime::parse)),
			Map.entry(OffsetDateTime.class, new Format<>(OffsetDateTime::parse)),
			Map.entry(Year.class, new Format<>(Year::parse)),
			Map.entry(MonthDay.class, new Format<>(MonthDay::parse)),
			Map.entry(URI.class, new Format<>(URI::create))
		);

	public static final StringFormat
		DEFAULT =
		new StringFormats(DEFAULT_FORMATS);

	private final Map<Class<?>, Format<?>> formats;

	private StringFormats(final Map<Class<?>, Format<?>> formats) {
		this.formats = Map.copyOf(formats);
	}

	@Override
	public String format(Object value) {
		@SuppressWarnings("unchecked")
		final var format = (Format<Object>)formats.get(value.getClass());
		return format != null ? format.format(value) : value.toString();
	}

	@Override
	public <T> T parse(String value, Class<T> type) {
		@SuppressWarnings("unchecked")
		final var format = (Format<T>)formats.get(type);
		if (format != null) {
			return format.parse(value);
		} else {
			throw new UnsupportedOperationException(
				"Parsing '%s' to type '%s' is not supported."
					.formatted(value, type)
			);
		}
	}

	public <T> StringFormats put(
		final Class<T> type,
		final Function<? super T, String> format,
		final Function<String, ? extends T> parser
	) {
		requireNonNull(type);
		requireNonNull(format);
		requireNonNull(parser);

		final var fmts = new HashMap<>(formats);
		fmts.put(type, new Format<>(format, parser));
		return new StringFormats(fmts);
	}

	public <T> StringFormats put(
		final Class<T> type,
		final Function<String, ? extends T> parser
	) {
		requireNonNull(type);
		requireNonNull(parser);

		final var fmts = new HashMap<>(formats);
		fmts.put(type, new Format<>(Objects::toString, parser));
		return new StringFormats(fmts);
	}

}

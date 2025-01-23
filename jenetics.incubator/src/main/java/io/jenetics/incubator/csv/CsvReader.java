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
package io.jenetics.incubator.csv;

import static java.util.Objects.requireNonNull;

import java.nio.CharBuffer;
import java.util.List;
import java.util.stream.Stream;

import io.jenetics.ext.util.CsvSupport.ColumnIndexes;
import io.jenetics.ext.util.CsvSupport.LineReader;
import io.jenetics.ext.util.CsvSupport.LineSplitter;
import io.jenetics.ext.util.CsvSupport.Quote;
import io.jenetics.ext.util.CsvSupport.Separator;

/**
 * Reads a CSV as a list of objects.
 *
 * @param <T> the record type
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
@FunctionalInterface
public interface CsvReader<T> {

	/**
	 * CSV reader with the given <em>default</em> values:
	 * <ul>
	 *     <li><b>Quote:</b> {@code "}</li>
	 *     <li><b>Separator:</b> {@code ,}</li>
	 *     <li><b>Headers:</b> {@code 0}</li>
	 * </ul>
	 */
	CsvReader<String[]> DEFAULT = CsvReader.builder().build();

	/**
	 * Reads the CSV records as {@link Stream} from the given {@code reader}.
	 * <em>The records are fetched lazily.</em>
	 *
	 * @apiNote
	 * The caller is responsible for either closing the given {@code reader} or
	 * the returned record {@code Stream}.
	 *
	 * @param reader the CSV source reader
	 * @return the lazy record stream
	 */
	Stream<T> read(Readable reader);

	/**
	 * Reads the CSV records as {@link List} from the given {@code reader}.
	 *
	 * @param reader the CSV source reader
	 * @return the record list
	 */
	default List<T> readAll(Readable reader) {
		try (var lines = read(reader)) {
			return lines.toList();
		}
	}

	/**
	 * Parses the given CSV string.
	 *
	 * @param csv the CSV string
	 * @return the parsed CSV records
	 */
	default List<T> parse(String csv) {
		return readAll(CharBuffer.wrap(csv));
	}

	/**
	 * Create a new CSV reader builder.
	 *
	 * @return a new CSV reader builder
	 */
	static Builder builder() {
		return new Builder();
	}

	/**
	 * The CSV reader builder class.
	 */
	final class Builder {
		private Separator separator = Separator.DEFAULT;
		private Quote quote = Quote.DEFAULT;
		private ColumnIndexes projection = ColumnIndexes.ALL;
		private String comment = "";
		private int headers = 0;

		private Builder() {
		}

		/**
		 * Set the CSV quote character.
		 *
		 * @param quote the CSV quote character
		 * @return {@code this} builder
		 */
		public Builder quote(final char quote) {
			this.quote = new Quote(quote);
			return this;
		}

		/**
		 * Set the CSV quote character.
		 *
		 * @param quote the CSV quote character
		 * @return {@code this} builder
		 */
		public Builder quote(final Quote quote) {
			this.quote = requireNonNull(quote);
			return this;
		}

		/**
		 * Set the CSV separator character.
		 *
		 * @param separator the CSV separator character
		 * @return {@code this} builder
		 */
		public Builder separator(final char separator) {
			this.separator = new Separator(separator);
			return this;
		}

		/**
		 * Set the CSV separator character.
		 *
		 * @param separator the CSV separator character
		 * @return {@code this} builder
		 */
		public Builder separator(final Separator separator) {
			this.separator = requireNonNull(separator);
			return this;
		}

		/**
		 * Set the column projection indexes.
		 *
		 * @param projection the column projection indexes
		 * @return {@code this} builder
		 */
		public Builder projection(final int... projection) {
			this.projection = new ColumnIndexes(projection);
			return this;
		}

		/**
		 * Set the column projection indexes.
		 *
		 * @param projection the column projection indexes
		 * @return {@code this} builder
		 */
		public Builder projection(final ColumnIndexes projection) {
			this.projection = requireNonNull(projection);
			return this;
		}

		/**
		 * Lines, starting with this string will be skipped.
		 *
		 * @param comment the comment string
		 * @return {@code this} builder
		 */
		public Builder comment(final String comment) {
			if (comment.isBlank()) {
				throw new IllegalArgumentException("Comment cannot be blank.");
			}
			this.comment = comment;
			return this;
		}

		/**
		 * The number of header lines, which will be skipped.
		 *
		 * @param header the number of header lines to be skipped
		 * @return {@code this} builder
		 */
		public Builder headers(final int header) {
			if (header < 0) {
				throw new IllegalArgumentException(
					"Number of header lines must not be negative: %d."
						.formatted(header)
				);
			}
			this.headers = header;
			return this;
		}

		/**
		 * Builds a CSV reader, which reads the rows as {@code String[]} columns.
		 *
		 * @return {@code String[]} columns reader
		 */
		public CsvReader<String[]> build() {
			final var headers = this.headers;
			final var comment = this.comment;
			final var separator = this.separator;
			final var quote = this.quote;
			final var projection = this.projection;

			return reader -> new LineReader(quote)
				.read(reader)
				.skip(headers)
				.filter(line -> comment.isEmpty() || !line.startsWith(comment))
				.map(new LineSplitter(separator, quote, projection)::split);
		}

		/**
		 * Return a CSV reader, which parses the rows with the given row
		 * constructor.
		 *
		 * @param ctor the row constructor used for creating the CSV records
		 * @return a new record CSV reader
		 * @param <T> the record type
		 */
		public <T> CsvReader<T> build(final RecordCtor<? extends T> ctor) {
			requireNonNull(ctor);

			final var base = build();
			return reader -> base.read(reader).map(ctor::apply);
		}

		/**
		 * Return a CSV reader, which parses the rows with the given row
		 * {@code type}. The row type must be a {@link Record} for being parsed
		 * automatically.
		 *
		 * @param type the record type
		 * @param converter the converter to use for converting the string
		 *        columns of the CSV row
		 * @return a new record CSV reader
		 * @param <T> the record type
		 */
		public <T extends Record> CsvReader<T>
		build(final Class<T> type, final Converter converter) {
			if (projection.equals(ColumnIndexes.ALL)) {
				projection(Projection.of(type));
			}
			return build(RecordCtor.of(type, converter));
		}

		/**
		 * Return a CSV reader, which parses the rows with the given row
		 * {@code type}. The row type must be a {@link Record} for being parsed
		 * automatically.
		 *
		 * @param type the record type
		 * @return a new record CSV reader
		 * @param <T> the record type
		 */
		public <T extends Record> CsvReader<T> build(final Class<T> type) {
			return build(type, Converter.DEFAULT);
		}


	}

}

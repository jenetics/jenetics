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

import java.util.stream.Stream;

import io.jenetics.ext.util.CsvSupport.ColumnIndexes;
import io.jenetics.ext.util.CsvSupport.LineReader;
import io.jenetics.ext.util.CsvSupport.LineSplitter;
import io.jenetics.ext.util.CsvSupport.Quote;
import io.jenetics.ext.util.CsvSupport.Separator;

/**
 * Reads a CSV as a list of objects.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 8.2
 * @since 8.2
 */
@FunctionalInterface
public interface RowReader {

	/**
	 * CSV reader with the given <em>default</em> values:
	 * <ul>
	 *     <li><b>Quote:</b> {@code "}</li>
	 *     <li><b>Separator:</b> {@code ,}</li>
	 *     <li><b>Headers:</b> {@code 0}</li>
	 * </ul>
	 */
	RowReader DEFAULT = RowReader.builder().build();

	/**
	 * Reads the CSV records as {@link Stream} from the given {@code reader}.
	 * <em>The records are fetched lazily.</em>
	 *
	 * @apiNote
	 * The caller is responsible for either closing the given {@code reader} or
	 * the returned record {@code Stream}.
	 *
	 * @param readable the CSV source
	 * @return the lazy record stream
	 */
	Rows read(Readable readable);

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
		private StringFormat format = StringFormats.DEFAULT;

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
		 * Set the column projection indexes.
		 *
		 * @param format the string parser
		 * @return {@code this} builder
		 */
		public Builder format(final StringFormat format) {
			this.format = requireNonNull(format);
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
		public RowReader build() {
			final var headers = this.headers;
			final var comment = this.comment;
			final var separator = this.separator;
			final var quote = this.quote;
			final var projection = this.projection;
			final var format = this.format;
			final var reader = new LineReader(quote);

			return source -> new Rows(
				reader
					.read(source)
					.skip(headers)
					.filter(line -> comment.isEmpty() || !line.startsWith(comment))
					.map(new LineSplitter(separator, quote, projection)::split)
					.map(cols -> Row.of(cols, format))
			);
		}

	}

}

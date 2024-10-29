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

import java.io.Flushable;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

import io.jenetics.ext.util.CsvSupport;
import io.jenetics.ext.util.CsvSupport.ColumnIndexes;
import io.jenetics.ext.util.CsvSupport.ColumnJoiner;
import io.jenetics.ext.util.CsvSupport.Quote;
import io.jenetics.ext.util.CsvSupport.Separator;

/**
 *
 * @param <T>
 */
@FunctionalInterface
public interface CsvWriter<T> {

	long write(final Stream<T> stream, final Appendable writer);

	default int write(final List<T> stream, final Appendable writer) {
		return (int)write(stream.stream(), writer);
	}

	static Builder builder() {
		return new Builder();
	}

	/**
	 * The CSV writer builder class.
	 */
	final class Builder {
		private Separator separator = Separator.DEFAULT;
		private Quote quote = Quote.DEFAULT;
		private ColumnIndexes embedding = ColumnIndexes.ALL;
		private Formatter formatter = Formatter.DEFAULT;
		private String[] header = null;


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
		 * Set the column embedding indexes.
		 *
		 * @param embedding the column embedding indexes
		 * @return {@code this} builder
		 */
		public Builder embedding(final int... embedding) {
			this.embedding = new ColumnIndexes(embedding);
			return this;
		}

		/**
		 * Set the column embedding indexes.
		 *
		 * @param embedding the column embedding indexes
		 * @return {@code this} builder
		 */
		public Builder embedding(final ColumnIndexes embedding) {
			this.embedding = requireNonNull(embedding);
			return this;
		}

		/**
		 * The record element formatter.
		 *
		 * @param formatter the record element formatter
		 * @return {@code this} builder
		 */
		public Builder formatter(final Formatter formatter) {
			this.formatter = requireNonNull(formatter);
			return this;
		}

		/**
		 * The number of header lines, which will be skipped.
		 *
		 * @param header the number of header lines to be skipped
		 * @return {@code this} builder
		 */
		public Builder header(final String... header) {
			this.header = requireNonNull(header);
			return this;
		}

		/**
		 * Builds a CSV reader, which reads the rows as {@code String[]} columns.
		 *
		 * @return {@code String[]} columns reader
		 */
		public CsvWriter<String[]> build() {
			final var header = this.header;
			final var separator = this.separator;
			final var quote = this.quote;
			final var embedding = this.embedding;

			final var joiner = new ColumnJoiner(separator, quote, embedding);

			return (values, writer) -> {
				final var count = new AtomicLong();

				final Stream<String> hdr = header != null
					? Stream.ofNullable(new ColumnJoiner(separator, quote).join(header))
					: Stream.empty();

				Stream.concat(hdr, values.map(joiner::join))
					.forEach(line -> {
						writeln(line, writer);
						count.getAndIncrement();
					});

				flush(writer);
				return count.get();
			};
		}

		public <T> CsvWriter<T> build(final RecordDtor<? super T> dtor) {
			requireNonNull(dtor);

			final var header = this.header;
			final var separator = this.separator;
			final var quote = this.quote;
			final var embedding = this.embedding;
			final var joiner = new ColumnJoiner(separator, quote, embedding);

			return (values, writer) -> {
				final var count = new AtomicLong();

				final Stream<String> hdr = header != null
					? Stream.ofNullable(new ColumnJoiner(separator, quote).join(header))
					: Stream.empty();

				Stream.concat(
						hdr,
						values
							.map(dtor::unapply)
							.map(joiner::join)
					)
					.forEach(line -> {
						writeln(line, writer);
						count.getAndIncrement();
					});

				flush(writer);
				return count.get();
			};
		}

		public <T extends Record> CsvWriter<T> build(final Class<T> type) {
			return build(RecordDtor.of(type, formatter));
		}

	}

	private static void writeln(final String line, final Appendable writer) {
		try {
			writer.append(line);
			writer.append(CsvSupport.EOL);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	private static void flush(Appendable writer) {
		try {
			if (writer instanceof Flushable flushable) {
				flushable.flush();
			}
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

}

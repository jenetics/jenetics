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
 * Writes records in CSV format.
 *
 * @param <T> the record type
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 8.2
 * @since 8.2
 */
@FunctionalInterface
public interface CsvWriter<T> {

	/**
	 * CSV writer with the given <em>default</em> values:
	 * <ul>
	 *     <li><b>Quote:</b> {@code "}</li>
	 *     <li><b>Separator:</b> {@code ,}</li>
	 *     <li><b>Headers:</b> {@code null}</li>
	 * </ul>
	 */
	CsvWriter<String[]> DEFAULT = CsvWriter.builder().build();

	/**
	 * Write the given {@code records} to the given {@code writer}.
	 *
	 * @param records the records to write
	 * @param writer the record sink
	 * @return the number of written records
	 */
	long write(final Stream<? extends T> records, final Appendable writer);

	/**
	 * Write the given {@code records} to the given {@code writer}.
	 *
	 * @param records the records to write
	 * @param writer the record sink
	 * @return the number of written records
	 */
	default int write(final List<? extends T> records, final Appendable writer) {
		return (int)write(records.stream(), writer);
	}

	/**
	 * Create a new CSV writer builder.
	 *
	 * @return a new CSV writer builder
	 */
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
		private String[] header = null;

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
		 * Builds a CSV writer, which writes {@code String[]} columns.
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

		/**
		 * CSV writer which deconstructs and writes records of type {@code T}.
		 *
		 * @param dtor the record deconstructor
		 * @return a new CSV writer
		 * @param <T> the record type
		 */
		public <T> CsvWriter<T> build(final RecordDtor<? super T> dtor) {
			requireNonNull(dtor);

			final var base = build();
			return (values, writer) ->
				base.write(values.map(dtor::unapply), writer);
		}

		/**
		 * CSV writer which deconstructs and writes records of type {@code T}.
		 *
		 * @param type the record type
		 * @param formatter the string formatter used for record deconstruction
		 * @return a new CSV writer
		 * @param <T> the record type
		 */
		public <T extends Record> CsvWriter<T>
		build(final Class<T> type, final Formatter formatter) {
			return build(RecordDtor.of(type, formatter));
		}

		/**
		 * CSV writer which deconstructs and writes records of type {@code T}.
		 *
		 * @param type the record type
		 * @return a new CSV writer
		 * @param <T> the record type
		 */
		public <T extends Record> CsvWriter<T> build(final Class<T> type) {
			return build(type, Formatter.DEFAULT);
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

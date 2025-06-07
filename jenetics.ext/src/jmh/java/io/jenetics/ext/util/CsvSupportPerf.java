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
package io.jenetics.ext.util;

import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReaderBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.lang3.StringUtils;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import org.supercsv.io.CsvListReader;
import org.supercsv.prefs.CsvPreference;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
@BenchmarkMode(Mode.AverageTime)
@Fork(value = 1, jvmArgs = {"-server", "-Xms1024M", "-Xmx1024M"})
@Threads(1)
@Warmup(iterations = 5)
@Measurement(iterations = 20)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
public class CsvSupportPerf {

	private String data;

	private Reader newStringReader() {
		return new StringReader(data);
	}

	@Setup
	public void init() throws IOException {
		final var in = CsvSupportPerf.class.getClassLoader()
			.getResourceAsStream("worldcitiespop.txt.gz");

		try (final InputStream gzin = new GZIPInputStream(in, 8192)) {
			data = new String(gzin.readAllBytes(), StandardCharsets.ISO_8859_1);
		}
	}

	@Benchmark
	public int parseCsvSupport(final Blackhole bh) {
		int count = 0;

		final var reader = new CsvSupport.LineReader(CsvSupport.Quote.ZERO);
		final var splitter = new CsvSupport.LineSplitter(CsvSupport.Quote.ZERO);

		final Stream<String[]> result = reader.read(newStringReader())
			.map(splitter::split);

		try (result) {
			count = (int)result.count();
		}

		bh.consume(count);
		return count;
	}

	@Benchmark
	public int parseCommonsCSV(final Blackhole bh) throws Exception {
		int count = 0;

		try (final Reader in = newStringReader()) {
			final CSVFormat format = CSVFormat.Builder.create()
				.setSkipHeaderRecord(true)
				.get();

            for (var record : format.parse(in)) {
                count++;
            }
		}

		bh.consume(count);
		return count;
	}

	@Benchmark
	public int parseJavaCSV(final Blackhole bh) throws Exception {
		int count = 0;

		try (final Reader in = newStringReader()) {
			final var reader = new com.csvreader.CsvReader(in, ',');
			reader.setRecordDelimiter('\n');
			while (reader.readRecord()) {
				count++;
			}
		}

		bh.consume(count);
		return count;
	}

	@Benchmark
	public int parseOpenCSV(final Blackhole bh) throws Exception {
		int count = 0;

		final var parser = new CSVParserBuilder()
			.withSeparator(',')
			.withIgnoreQuotations(true)
			.build();

		try (final Reader in = newStringReader()) {
			final com.opencsv.CSVReader reader = new CSVReaderBuilder(in)
				.withSkipLines(1)
				.withCSVParser(parser)
				.build();

			try (reader) {
				while (reader.readNext() != null) {
					count++;
				}
			}
		}

		bh.consume(count);
		return count;
	}

	@Benchmark
	public int parseSuperCSV(final Blackhole bh) throws Exception {
		int count = 0;

		try (final CsvListReader reader = new CsvListReader(
				newStringReader(), CsvPreference.STANDARD_PREFERENCE))
		{
			while (reader.read() != null) {
				count++;
			}
		}

		bh.consume(count);
		return count;
	}

	@Benchmark
	public int read(final Blackhole bh) throws IOException {
		int count = 0;

		try (BufferedReader reader = new BufferedReader(newStringReader())) {
			while (reader.readLine() != null) {
				count++;
			}
		}

		bh.consume(count);
		return count;
	}

	@Benchmark
	public int scan(final Blackhole bh) {
		int count = 0;

		try (Scanner scanner = new Scanner(newStringReader())) {
			while (scanner.hasNextLine()) {
				scanner.nextLine();
				count++;
			}
		}

		bh.consume(count);
		return count;
	}

	@Benchmark
	public int split(final Blackhole bh) throws Exception {
		int count = 0;

		try (BufferedReader reader = new BufferedReader(newStringReader())) {
			String line;
			while ((line = reader.readLine()) != null) {
				final String[] values = StringUtils.split(line, ',');
				count += values.length;
			}
		}

		bh.consume(count);
		return count;
	}
}
/*
-- 2023.11.04
Benchmark                       Mode  Cnt   Score   Error  Units
CsvSupportPerf.parseCommonsCSV  avgt   20  85.721 ± 0.238  ms/op
CsvSupportPerf.parseCsvSupport  avgt   20  99.520 ± 0.779  ms/op
CsvSupportPerf.parseJavaCSV     avgt   20  25.341 ± 0.064  ms/op
CsvSupportPerf.parseOpenCSV     avgt   20  29.131 ± 0.065  ms/op
CsvSupportPerf.parseSuperCSV    avgt   20  30.959 ± 0.043  ms/op
CsvSupportPerf.read             avgt   20   3.911 ± 0.011  ms/op
CsvSupportPerf.scan             avgt   20  15.739 ± 0.042  ms/op
CsvSupportPerf.split            avgt   20  17.168 ± 0.088  ms/op
 */
/*
-- 2024.10.27
Benchmark                       Mode  Cnt   Score   Error  Units
CsvSupportPerf.parseCommonsCSV  avgt   20  86.489 ± 0.575  ms/op
CsvSupportPerf.parseCsvSupport  avgt   20  25.611 ± 0.499  ms/op
CsvSupportPerf.parseJavaCSV     avgt   20  26.151 ± 0.671  ms/op
CsvSupportPerf.parseOpenCSV     avgt   20  29.932 ± 0.586  ms/op
CsvSupportPerf.parseSuperCSV    avgt   20  30.921 ± 0.126  ms/op
CsvSupportPerf.read             avgt   20   4.125 ± 0.059  ms/op
CsvSupportPerf.scan             avgt   20  16.098 ± 0.205  ms/op
CsvSupportPerf.split            avgt   20  16.725 ± 0.204  ms/op
 */

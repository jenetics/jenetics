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
package io.jenetics.incubator.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.opencsv.ICSVParser;
import com.opencsv.RFC4180Parser;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class CSVPerf {

	private static final String DEFAULT_CHARACTERS =
		"abcdefghijklmnopqrstuvwxyz !\"$%&/()=?`{[]}\\+~*#';.:,-_<>|@^'\t\n\r";

	private static final ICSVParser PARSER = new RFC4180Parser();

	@State(Scope.Benchmark)
	public static class Lines {
		Random random = new Random(1231234);
		String line = CSV.join(nextRow(30, random));
	}

	private static List<String> nextRow(final int columns, final Random random) {
		final List<String> cols = new ArrayList<>(columns);
		for (int i = 0; i < columns; ++i) {
			cols.add(nextString(random.nextInt(30) + 30, random));
		}
		return cols;
	}

	private static String nextString(final int length, final Random random) {
		final Supplier<Character> generator = () -> DEFAULT_CHARACTERS
			.charAt(random.nextInt(DEFAULT_CHARACTERS.length()));

		return Stream.generate(generator)
			.limit(length)
			.map(String::valueOf)
			.collect(Collectors.joining());
	}

	@Benchmark
	public Object split(final Lines lines) {
		return CSV.split(lines.line);
	}

	@Benchmark
	public Object splitOpenCSV(final Lines lines) throws IOException {
		return PARSER.parseLine(lines.line);
	}

	@Benchmark
	public Object splitMultiLineOpenCSV(final Lines lines) throws IOException {
		return PARSER.parseLineMulti(lines.line);
	}

}

// Mac
/*
Benchmark             Mode  Cnt     Score     Error  Units
Benchmark                      Mode  Cnt    Score    Error  Units
CSVPerf.split                  avgt   25  896,393 ± 45,360  ns/op
CSVPerf.splitMultiLineOpenCSV  avgt   25  376,209 ± 24,265  ns/op
CSVPerf.splitOpenCSV           avgt   25  363,144 ±  7,188  ns/op
*/

// Linux: Intel(R) Core(TM) i7-6700HQ CPU @ 2.60GHz
/*
Benchmark                      Mode  Cnt     Score     Error  Units
CSVPerf.split                  avgt   25  7749.107 ± 228.656  ns/op
CSVPerf.splitMultiLineOpenCSV  avgt   25  5967.683 ± 124.829  ns/op
CSVPerf.splitOpenCSV           avgt   25  5879.939 ± 121.605  ns/op
*/

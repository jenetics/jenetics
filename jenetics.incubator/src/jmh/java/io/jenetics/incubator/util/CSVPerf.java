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
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

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

	private static final ICSVParser PARSER = new RFC4180Parser();

	@State(Scope.Benchmark)
	public static class Lines {
		Random random = new Random(1231234);
		String line = CSV.join(nextRow(random));
	}

	private static List<String> nextRow(final Random random) {
		return List.of(
			"" + random.nextDouble(),
			"" + random.nextBoolean(),
			"" + random.nextFloat(),
			"" + random.nextInt(),
			"" + random.nextLong(),
			"" + random.nextLong(),
			"" + random.nextLong(),
			"" + random.nextLong(),
			"" + random.nextLong(),
			"" + random.nextLong(),
			"" + random.nextLong()
		);
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

// Linux
/*
Benchmark                      Mode  Cnt     Score     Error  Units
CSVPerf.split                  avgt   25  1095.412 ± 115.254  ns/op
CSVPerf.splitMultiLineOpenCSV  avgt   25   459.943 ±   6.771  ns/op
CSVPerf.splitOpenCSV           avgt   25   448.073 ±   5.421  ns/op
*/

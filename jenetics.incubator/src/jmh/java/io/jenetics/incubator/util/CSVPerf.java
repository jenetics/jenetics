package io.jenetics.incubator.util;

import com.opencsv.ICSVParser;
import com.opencsv.RFC4180Parser;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class CSVPerf {

	private static final ICSVParser PARSER = new RFC4180Parser();

	@State(Scope.Benchmark)
	public static class Lines {
		Random random = new Random();
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

}


package io.jenetics.tool.measurement;

import java.util.Random;

import org.testng.annotations.Test;

public class SampleTest {

	@Test
	public void toJson() {
		final var sample = Sample.of(new Random().doubles(4).toArray());
		final var json = sample.toJson();
		System.out.println(sample.toJson());
		final var samp = Sample.fromJson(json);
		System.out.println(samp);
	}

}

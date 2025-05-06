package io.jenetics.incubator.stat;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.random.RandomGenerator;

import org.assertj.core.data.Offset;
import org.testng.annotations.Test;

public class GammaDistributionTest {

	private final RandomGenerator random = RandomGenerator.getDefault();

	@Test(invocationCount = 10)
	public void pdf() {
		final var shape = random.nextDouble();
		final var scale = random.nextDouble();

		final var agd =
			org.apache.commons.statistics.distribution.GammaDistribution.of(shape, scale);
		final var jgd = new GammaDistribution(shape, scale);

		final var x = random.nextDouble();
		assertThat(jgd.pdf().apply(x)).isEqualTo(agd.density(x));
	}

	@Test(invocationCount = 10)
	public void cdf() {
		final var shape = random.nextDouble();
		final var scale = random.nextDouble();

		final var agd =
			org.apache.commons.statistics.distribution.GammaDistribution.of(shape, scale);
		final var jgd = new GammaDistribution(shape, scale);

		final var x = random.nextDouble();
		assertThat(jgd.cdf().apply(x)).isEqualTo(agd.cumulativeProbability(x));
	}

	@Test(invocationCount = 10)
	public void icdf() {
		final var shape = random.nextDouble(2, 1000);
		final var scale = 2.0;//random.nextDouble(0.1, 1000);

		final var agd =
			org.apache.commons.statistics.distribution.GammaDistribution.of(shape, scale);
		final var jgd = new GammaDistribution(shape, scale);

		final var x = random.nextDouble(0.1, 0.9);
		System.out.println("shape=%s, scale=%s, x=%s".formatted(shape, scale, x));
		assertThat(jgd.icdf().apply(x))
			.isCloseTo(agd.inverseCumulativeProbability(x), Offset.offset(0.0001));
	}

}

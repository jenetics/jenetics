package org.jenetics;

import java.util.Random;

import org.jscience.mathematics.number.Float64;

import org.jenetics.util.Factory;
import org.jenetics.util.ObjectTester;
import org.jenetics.util.RandomRegistry;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public class NumberStatisticsTest
	extends ObjectTester<NumberStatistics<Float64Gene, Float64>>
{

	final Factory<NumberStatistics<Float64Gene, Float64>>
	_factory = new Factory<NumberStatistics<Float64Gene, Float64>>() {
		@Override
		public NumberStatistics<Float64Gene, Float64> newInstance() {
			final Random random = RandomRegistry.getRandom();
			
			final NumberStatistics.Builder<Float64Gene, Float64>
			builder = new NumberStatistics.Builder<>();
			builder.ageMean(random.nextDouble());
			builder.ageVariance(random.nextDouble());
			builder.invalid(random.nextInt(1000));
			builder.killed(random.nextInt(1000));
			builder.samples(random.nextInt(100000));
			builder.generation(random.nextInt(1000));
			builder.fitnessMean(random.nextDouble());
			builder.fitnessVariance(random.nextDouble());
			builder.standardError(random.nextDouble());
			builder.bestPhenotype(TestUtils.newFloat64Phenotype());
			builder.worstPhenotype(TestUtils.newFloat64Phenotype());
			
			return builder.build();
		}
	};
	@Override
	protected Factory<NumberStatistics<Float64Gene, Float64>> getFactory() {
		return _factory;
	}
	
}



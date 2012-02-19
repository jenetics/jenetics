import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static org.jenetics.ExponentialScaler.SQR_SCALER;

import org.jscience.mathematics.number.Float64;

import org.jenetics.Float64Chromosome;
import org.jenetics.Float64Gene;
import org.jenetics.GeneticAlgorithm;
import org.jenetics.Genotype;
import org.jenetics.MeanAlterer;
import org.jenetics.Mutator;
import org.jenetics.NumberStatistics;
import org.jenetics.Optimize;
import org.jenetics.util.Factory;
import org.jenetics.util.Function;

final class Real 
	implements Function<Genotype<Float64Gene>, Float64> 
{
	@Override
	public Float64 apply(Genotype<Float64Gene> genotype) {
		final double x = genotype.getGene().doubleValue();
		return Float64.valueOf(cos(0.5 + sin(x)) * cos(x));
	}
}

public class RealFunction {
	public static void main(String[] args) {
		Factory<Genotype<Float64Gene>> gtf = Genotype.valueOf(
			new Float64Chromosome(0.0, 2.0 * PI)
		);
		Function<Genotype<Float64Gene>, Float64> ff = new Real();
		GeneticAlgorithm<Float64Gene, Float64> ga = 
			new GeneticAlgorithm<>(gtf, ff, Optimize.MINIMUM);

		ga.setStatisticsCalculator(
			new NumberStatistics.Calculator<Float64Gene, Float64>()
		);
		ga.setPopulationSize(20);
		ga.setAlterers(
			new Mutator<Float64Gene>(0.03),
			new MeanAlterer<Float64Gene>(0.6)
		);

		ga.setup();
		ga.evolve(100);
		System.out.println(ga.getBestStatistics());
	}
}


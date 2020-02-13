package io.jenetics.example;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

import io.jenetics.Optimize;

import io.jenetics.ext.moea.Vec;
import io.jenetics.ext.moea.VecFactory;

public class MixedMOEAOptimization {

	private static final VecFactory<double[]> VEC_FACTORY =
		VecFactory.ofDoubleVec(
			Optimize.MAXIMUM,
			Optimize.MINIMUM,
			Optimize.MAXIMUM,
			Optimize.MINIMUM,
			Optimize.MAXIMUM
		);

	static Vec<double[]> fitness(final double[] point) {
		final double x = point[0];
		final double y = point[1];
		return VEC_FACTORY.newVec(new double[] {
			sin(x)*y,
			cos(y)*x,
			sin(x + y),
			cos(x + y)*x,
			x
		});
	}

}

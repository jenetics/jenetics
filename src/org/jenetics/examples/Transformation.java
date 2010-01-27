/*
 * Java Genetic Algorithm Library (@!identifier!@).
 * Copyright (c) @!year!@ Franz Wilhelmstötter
 *  
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * Author:
 *     Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 *     
 */
package org.jenetics.examples;

import static org.jenetics.ExponentialScaler.SQR_SCALER;

import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;

import org.jenetics.CompositeAlterer;
import org.jenetics.Float64Chromosome;
import org.jenetics.Float64Gene;
import org.jenetics.FitnessFunction;
import org.jenetics.GeneticAlgorithm;
import org.jenetics.Genotype;
import org.jenetics.MeanAlterer;
import org.jenetics.Mutator;
import org.jenetics.RouletteWheelSelector;
import org.jenetics.util.Converter;
import org.jenetics.util.Factory;
import org.jenetics.util.Probability;
import org.jscience.mathematics.number.Float64;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: Transformation.java,v 1.4 2010-01-27 20:35:45 fwilhelm Exp $
 */
public class Transformation {

	private static final class Function 
		implements FitnessFunction<Float64Gene, Float64>,
					Converter<Genotype<Float64Gene>, AffineTransform>
	{
		private static final long serialVersionUID = 1L;
		
		private final Point2D[] _source;
		private final Point2D[] _target;
		
		public Function(final Point2D[] source, final Point2D[] target) {
			_source = source;
			_target = target;
		}

		@Override
		public Float64 evaluate(final Genotype<Float64Gene> genotype) {
			final AffineTransform transform = convert(genotype);
			
			double error = 0;
			final Point2D point = new Point2D.Double();
			for (int i = 0; i < _source.length; ++i) {
				transform.transform(_target[i], point);
				
				error += _source[i].distanceSq(point);
			}
			
			return Float64.valueOf(error);
		}
		
		@Override
		public AffineTransform convert(final Genotype<Float64Gene> genotype) {
			final double theta = genotype.getChromosome(0).getGene().doubleValue();
			final double tx = genotype.getChromosome(1).getGene(0).doubleValue();
			final double ty = genotype.getChromosome(1).getGene(1).doubleValue();
			final double shx = genotype.getChromosome(2).getGene(0).doubleValue();
			final double shy = genotype.getChromosome(2).getGene(1).doubleValue();
			
			final AffineTransform rotate = AffineTransform.getRotateInstance(theta);
			final AffineTransform translate = AffineTransform.getTranslateInstance(tx, ty);
			final AffineTransform shear = AffineTransform.getShearInstance(shx, shy);
			
			rotate.concatenate(translate);
			rotate.concatenate(shear);
			
			return rotate;
		}
		
	}
	
	
	
	public static void main(String[] args) throws NoninvertibleTransformException {
		final Point2D[] source = new Point2D[] {
			new Point2D.Double(10, 10),
			new Point2D.Double(300, 10),
			new Point2D.Double(300, 300),
			new Point2D.Double(10, 300)
		};
		final Point2D[] target = new Point2D[4];
		
		final AffineTransform rotate = AffineTransform.getRotateInstance(2.5);
		final AffineTransform translate = AffineTransform.getTranslateInstance(-50, 30);
		final AffineTransform shear = AffineTransform.getShearInstance(0.9, 1.2);
		rotate.concatenate(translate);
		rotate.concatenate(shear);
		
		for (int i = 0; i < source.length; ++i) {
			target[i]  = rotate.inverseTransform(source[i], null);
		}
		
		final Factory<Genotype<Float64Gene>> gtf = Genotype.valueOf(
			new Float64Chromosome(Float64Gene.valueOf(-Math.PI, Math.PI)), //Rotation
			new Float64Chromosome(Float64Gene.valueOf(-400, 400), Float64Gene.valueOf(-400, 400)), //Translation
			new Float64Chromosome(Float64Gene.valueOf(-400, 400), Float64Gene.valueOf(-400, 400))  //Shear
		);
		
		final Function ff = new Function(source, target);
		final GeneticAlgorithm<Float64Gene, Float64> ga = GeneticAlgorithm.valueOf(gtf, ff);
		
		ga.setFitnessScaler(SQR_SCALER);
		ga.setPopulationSize(1000);
		ga.setAlterer(new CompositeAlterer<Float64Gene>(
			new Mutator<Float64Gene>(Probability.valueOf(0.03)),
			new MeanAlterer<Float64Gene>(Probability.valueOf(0.6))
		));
		ga.setSelectors(new RouletteWheelSelector<Float64Gene, Float64>());
		
		GAUtils.execute(ga, 50);
	}
	
	
	
	
}


























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
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 */
package org.jenetics.example;

import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.io.Serializable;

import org.jenetics.CompositeAlterer;
import org.jenetics.DoubleChromosome;
import org.jenetics.DoubleGene;
import org.jenetics.GeneticAlgorithm;
import org.jenetics.Genotype;
import org.jenetics.MeanAlterer;
import org.jenetics.Mutator;
import org.jenetics.RouletteWheelSelector;
import org.jenetics.util.Factory;
import org.jenetics.util.Function;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version 1.0 &mdash; <em>$Date: 2014-02-25 $</em>
 */
public class Transformation {

	private static final class FF
		implements Function<Genotype<DoubleGene>, Double>,
					Serializable
	{
		private static final long serialVersionUID = 1L;

		private final Point2D[] _source;
		private final Point2D[] _target;

		public FF(final Point2D[] source, final Point2D[] target) {
			_source = source;
			_target = target;
		}

		@Override
		public Double apply(final Genotype<DoubleGene> genotype) {
			final AffineTransform transform = converter.apply(genotype);

			double error = 0;
			final Point2D point = new Point2D.Double();
			for (int i = 0; i < _source.length; ++i) {
				transform.transform(_target[i], point);

				error += _source[i].distanceSq(point);
			}

			return error;
		}

		private Function<Genotype<DoubleGene>, AffineTransform>
		converter = new Function<Genotype<DoubleGene>, AffineTransform>() {
			@Override
			public AffineTransform apply(final Genotype<DoubleGene> genotype) {
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
		};

		@Override
		public String toString() {
			return "Square error";
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

		final Factory<Genotype<DoubleGene>> gtf = Genotype.of(
			DoubleChromosome.of(DoubleGene.of(-Math.PI, Math.PI)), //Rotation
			DoubleChromosome.of(DoubleGene.of(-400, 400), DoubleGene.of(-400, 400)), //Translation
			DoubleChromosome.of(DoubleGene.of(-400, 400), DoubleGene.of(-400, 400))    //Shear
		);

		final FF ff = new FF(source, target);
		final GeneticAlgorithm<DoubleGene, Double> ga = new GeneticAlgorithm<>(gtf, ff);

		ga.setPopulationSize(1000);
		ga.setAlterer(CompositeAlterer.of(
			new Mutator<DoubleGene>(0.03),
			new MeanAlterer<DoubleGene>(0.6)
		));
		ga.setSelectors(new RouletteWheelSelector<DoubleGene, Double>());

		final int generations = 50;

		GAUtils.printConfig(
				"Affine transformation",
				ga,
				generations,
				((CompositeAlterer<?>)ga.getAlterer()).getAlterers().toArray()
			);

		GAUtils.execute(ga, generations, 10);
	}




}

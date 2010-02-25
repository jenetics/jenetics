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
 */
package org.jenetics;

import java.util.Arrays;
//import java.util.concurrent.Executors;

import org.jenetics.util.ArrayUtils;
import org.testng.Reporter;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public class ConcurrentStatisticCalculatorTest {

	@Test
	public void partition() {
		int[] parts = ArrayUtils.partition(10, 3);
		Reporter.log(Arrays.toString(ArrayUtils.partition(10, 3)));
		Reporter.log(Arrays.toString(ArrayUtils.partition(15, 6)));
		Reporter.log(Arrays.toString(ArrayUtils.partition(5, 10)));
		
		parts = ArrayUtils.partition(15, 6);
		for (int i = 0; i < parts.length - 1; ++i) {
			Reporter.log(parts[i] + "\t" + parts[i + 1]); 
		}		
	}
	
	
//	private static class FF implements FitnessFunction<DoubleGene> {
//		private static final long serialVersionUID = 7847202960080699398L;
//		@Override public double evaluate(final Genotype<DoubleGene> genotype) {
//			return genotype.getGene().doubleValue();
//		}
//	}
//	final static FitnessFunction<DoubleGene> FF = new FF();
//	
//	private Population<DoubleGene> newPopulation() {
//		final int size = 1001;
//		Population<DoubleGene> population = new Population<DoubleGene>(size);
//		
//		
//		for (int i = 0; i < size; ++i) {
//			Genotype<DoubleGene> gt = Genotype.valueOf(
//				new DoubleChromosome(DoubleGene.valueOf(i, 0, size - 1))
//			);
//			population.add(Phenotype.valueOf(gt, FF, i));
//		}
//		return population;
//	}
//	
//	@Test
//	public void evaluate() {
//		StatisticCalculator calculator = new StatisticCalculator();
//		Statistic<DoubleGene> s1 = calculator.evaluate(newPopulation());
//		System.out.println(s1);
//		System.out.println("Time: " + calculator.getLastEvaluationTime());
//		System.out.println("---------------------------------");
//		
//		calculator = new ConcurrentStatisticCalculator(2, Executors.newFixedThreadPool(2));
//		Statistic<DoubleGene> s2 = calculator.evaluate(newPopulation());
//		System.out.println(s2);
//		System.out.println("Time: " + calculator.getLastEvaluationTime());
//		
//		assert s1.equals(s2, 10);
//	}
	
}






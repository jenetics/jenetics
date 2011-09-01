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
package org.jenetics.util;

import static org.jenetics.util.array.isSorted;
import static org.jenetics.util.array.sort;

import java.util.Comparator;
import java.util.Random;

import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public class ArrayUtilsTest {
	
	
	@Test
	public void subset() {
		final Random random = new Random();
		
		for (int i = 1; i < 100; ++i) {
			int[] sub = new int[i];
			array.subset(1000, sub, random);
			
			Assert.assertTrue(isSortedd(sub));
		}
		
	}
	
	@Test
	public void iselect() {
		final Array<Integer> array = new Array<Integer>(100);
		for (int i = 0; i < array.length(); ++i) {
			array.set(i, i);
		}
		
		for (int i = 0; i < array.length(); ++i) {
			//System.out.println(ArrayUtils.iselect(array, i));
			//Assert.assertEquals(ArrayUtils.iselect(array, i), i);
		}
		//System.out.println(ArrayUtils.iselect(array, 2));
	}
	
	private static boolean isSortedd(int[] array) {
		boolean sorted = true;
		for (int i = 0; i < array.length - 1 && sorted; ++i) {
			sorted = array[i] < array[i + 1];
		}
		return sorted;
	}
	
	@Test
	public void sorted() {
		final Array<Integer> array = new Array<Integer>(100);
		for (int i = 0; i < array.length(); ++i) {
			array.set(i, i);
		}
		Assert.assertTrue(isSorted(array));
		
		array.set(10, 5);
		Assert.assertFalse(isSorted(array));
		
		array.fill(-234);
		Assert.assertTrue(isSorted(array));
		
		for (int i = 0; i < array.length(); ++i) {
			array.set(i, array.length() - i);
		}
		Assert.assertFalse(isSorted(array));
	}
	
	@Test
	public void sorted2() {
		final Array<Integer> array = new Array<Integer>(100);
		for (int i = 0; i < array.length(); ++i) {
			array.set(i, i);
		}
		Assert.assertFalse(isSorted(array, new Comparator<Integer>() {
			@Override
			public int compare(Integer o1, Integer o2) {
				return -o1.compareTo(o2);
			}
		}));
	}
	
	@Test
	public void sort1() {
		final Random random = new Random();
		final Factory<Integer> factory = new Factory<Integer>() {
			@Override public Integer newInstance() {
				return random.nextInt(10000);
			}
		};
		
		final Array<Integer> array = new Array<Integer>(100);
		array.fill(factory);
		Assert.assertFalse(isSorted(array));
		
		final Array<Integer> clonedArray = array.copy();
		org.jenetics.util.array.sort(array.subSeq(30, 40));
		Assert.assertTrue(isSorted(array.subSeq(30, 40)));
		Assert.assertEquals(array.subSeq(0, 30), clonedArray.subSeq(0, 30));
		Assert.assertEquals(array.subSeq(40), clonedArray.subSeq(40));
	}
	
	@Test
	public void sort2() {
		final Random random = new Random();
		final Factory<Integer> factory = new Factory<Integer>() {
			@Override public Integer newInstance() {
				return random.nextInt(10000);
			}
		};
		
		final Array<Integer> array = new Array<Integer>(100);
		array.fill(factory);
		Assert.assertFalse(isSorted(array));
		
		final Array<Integer> clonedArray = array.copy();
		sort(clonedArray, 30, 40);
		sort(array.subSeq(30, 40));
		Assert.assertEquals(array, clonedArray);
	}
	
//	@Test
//	public void performance() {
//		final int SIZE = 1000;
//		final Population<IntegerGene, Integer64> pop = new Population<IntegerGene, Integer64>(SIZE);
//		for (int i = 0; i < SIZE; ++i) {
//			pop.add(Phenotype.valueOf(
//					Genotype.valueOf(new IntegerChromosome(IntegerGene.valueOf(i, 0, SIZE))), 
//					new FitnessFunction<IntegerGene, Integer64>() {
//						private static final long serialVersionUID = 1L;
//						@Override
//						public Integer64 evaluate(Genotype<IntegerGene> genotype) {
//							return null;
//						}
//					}, i));
//		}
//		
//		final Timer timer = new Timer();
//		timer.start();
//		for (int j = 0; j < 10000; ++j) {
//			for (int i = 0; i < pop.size(); ++i) {
//				final Phenotype<?, ?> pt = pop.get(i);
//			}
//		}
//		timer.stop();
//		System.out.println(timer.toString());
//		
//		timer.reset();
//		timer.start();
//		for (int i = 0; i < 10000; ++i) {
//			ArrayUtils.subset(1000, 400, RandomRegistry.getRandom());
//		}
//		timer.stop();
//		System.out.println(timer);
//		
//	}
	
	public static void main(String[] args) {
		Array<Integer> array = new Array<Integer>(10000000);
		for (int i = 0; i < array.length(); ++i) {
			array.set(i, (int)(Math.random()*1000));
		}
		
		Timer timer = new Timer();
		timer.start();
		sort(array);
		timer.stop();
		Reporter.log(timer.toString());
	}
	
	
}







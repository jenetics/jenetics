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

import static java.util.Arrays.asList;
import static org.jenetics.util.Predicates.nil;
import static org.jenetics.util.Predicates.not;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public class ArrayTest extends ObjectTester<Array<Double>> {

	final Factory<Array<Double>> _factory = new Factory<Array<Double>>() {
		@Override
		public Array<Double> newInstance() {
			final Random random = RandomRegistry.getRandom();
			final Array<Double> array = new Array<Double>(random.nextInt(1000) + 100);
			for (int i = 0; i < array.length(); ++i) {
				array.set(i, random.nextDouble());
			}
			return array;
		}
	};
	@Override
	protected Factory<Array<Double>> getFactory() {
		return _factory;
	}
	
	@Test
	public void newFromCollection() {
		final Array<Integer> a1 = new Array<Integer>(asList(1, 2, 3, 4, 5));
		final Array<Integer> a2 = new Array<Integer>(asList(6, 7, 8, 9, 10, 11, 12, 13));
		final Array<Integer> a3 = new Array<Integer>(a1, a2);
		
		Assert.assertEquals(a3.length(), a1.length() + a2.length());
		for (int i = 0; i < a1.length() + a2.length(); ++i) {
			Assert.assertEquals(a3.get(i), new Integer(i + 1));
		}
	}
	
	@Test
	public void newFromSubArray() {
		final Array<Integer> a1 = new Array<Integer>(asList(0, 1, 2, 3, 4, 5, 6, 7));
		final Array<Integer> a2 = new Array<Integer>(asList(6, 7, 8, 9, 10, 11, 12, 13));
		final Array<Integer> a3 = new Array<Integer>(a1.subSeq(0, 6), a2);

		Assert.assertEquals(a3.length(), a1.length() + a2.length() - 2);
		for (int i = 0; i < a1.length() + a2.length() - 2; ++i) {
			Assert.assertEquals(a3.get(i), new Integer(i));
		}
	}
	
	@Test
	public void newFromOtherSubArray() {
		final Array<Integer> a1 = new Array<Integer>(asList(0, 1, 2, 3, 4, 5, 6, 7));
		final Array<Integer> a2 = new Array<Integer>(asList(6, 7, 8, 9, 10, 11, 12, 13));
		final Array<Integer> a3 = new Array<Integer>(a1.subSeq(1, 6), a2);
		
		Assert.assertEquals(a3.length(), a1.length() + a2.length() - 3);
		for (int i = 1; i < a1.length() + a2.length() - 2; ++i) {
			Assert.assertEquals(a3.get(i - 1), new Integer(i));
		}
	}
	
	@Test
	public void create4() {
		final Array<Integer> a1 = new Array<Integer>(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7));
		final Array<Integer> a2 = new Array<Integer>(Arrays.asList(6, 7, 8, 9, 10, 11, 12, 13));
		final Array<Integer> a3 = new Array<Integer>(a1, a2.subSeq(2, 7));
		
		Assert.assertEquals(a3.length(), a1.length() + a2.length() - 3);
		for (int i = 0; i < a1.length() + a2.length() - 3; ++i) {
			Assert.assertEquals(a3.get(i), new Integer(i));
		}
	}
	
	@Test
	public void filter() {
		final Array<Integer> array = new Array<Integer>(20);
		array.fill(100);
		array.set(18, null);
		array.set(19, null);
		
		final Array<Integer> filtered = array.filter(not(nil()));
		Assert.assertEquals(filtered.length(), array.length() - 2);
	}
	
	@Test
	public void fill1() {
		final ISeq<Integer> array = new Array<Integer>(10).fill(10).seal();
		Assert.assertEquals(array.length(), 10);
		for (Integer i : array) {
			Assert.assertEquals(i, new Integer(10));
		}
	}
	
	@Test
	public void fill2() {
		final Array<Integer> array = new Array<Integer>(10).fill(0);
		Assert.assertEquals(array.length(), 10);
		
		final AtomicInteger integer = new AtomicInteger(0);
		array.fill(new Factory<Integer>() {
			@Override
			public Integer newInstance() {
				return integer.getAndIncrement();
			}
		});
		
		for (int i = 0; i < array.length(); ++i) {
			Assert.assertEquals(array.get(i), new Integer(i));
		}
	}

	@Test
	public void seal1() {
		final Array<Integer> array = new Array<Integer>(10).fill(11);
		final Array<Integer> copy = array.copy();
		
		final ISeq<Integer> sealed = copy.seal();
		for (int i = 0; i < array.length(); ++i) {
			array.set(i, i);
			
			Assert.assertEquals(sealed, copy);
		}
	}
	
	@Test
	public void seal2() {
		final Array<Integer> array = new Array<Integer>(10).fill(11);
		final Array<Integer> copy = array.copy();
		
		final ISeq<Integer> sealed = copy.seal();
		array.fill(34);
		Assert.assertEquals(sealed, copy);
	}
	
	@Test
	public void seal3() {
		final Array<Integer> array = new Array<Integer>(10).fill(10);
		final Array<Integer> copy = array.copy();
		
		final ISeq<Integer> sealed = copy.seal();
		
		for (ListIterator<Integer> it = array.iterator(); it.hasNext();) {
			it.next();
			it.set(4);
		}
		
		Assert.assertEquals(sealed, copy);
		Assert.assertEquals(array, new Array<Integer>(10).fill(4));
	}
	
	@Test
	public void seal4() {
		final Array<Integer> array = new Array<Integer>(10);
		final Array<Integer> copy = array.copy();
		
		final ISeq<Integer> sealed = copy.seal();
		array.subSeq(0, 4).set(0, 1);
		Assert.assertEquals(sealed, copy);
		Assert.assertEquals(array.get(0), new Integer(1));
	}
	
	@Test
	public void foreach() {
		final Array<Integer> array = new Array<Integer>(10).fill(123);
		array.seal();
		final AtomicInteger count = new AtomicInteger(0);
		int value = array.foreach(new Predicate<Integer>() {
			@Override public boolean evaluate(Integer object) {
				Assert.assertEquals(object, new Integer(123));
				count.addAndGet(1);
				return true;
			}
		});
		
		Assert.assertEquals(value, -1);
		Assert.assertEquals(count.get(), 10);
		
		count.set(0);
		value = array.foreach(new Predicate<Integer>() {
			@Override public boolean evaluate(Integer object) {
				Assert.assertEquals(object, new Integer(123));
				return count.addAndGet(1) != 5;
			}
		});
		
		Assert.assertEquals(count.get(), 5);
		Assert.assertEquals(value, 4);
	}
	
	@Test
	public void append1() {
		final Array<Integer> a1 = new Array<Integer>(Arrays.asList(0, 1, 2, 3, 4, 5));
		final Array<Integer> a2 = new Array<Integer>(Arrays.asList(6, 7, 8, 9, 10));
		final Array<Integer> a3 = a1.append(a2);
		
		Assert.assertEquals(a3.length(), 11);
		Assert.assertEquals(a3, 
				new Array<Integer>(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10))
			);
	}
	
	@Test
	public void append2() {
		final Array<Integer> a1 = new Array<Integer>(Arrays.asList(0, 1, 2, 3, 4, 5));
		final Array<Integer> a3 = a1.append(Arrays.asList(6, 7, 8, 9, 10));
		
		Assert.assertEquals(a3.length(), 11);
		Assert.assertEquals(a3, 
				new Array<Integer>(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10))
			);
	}
	
	@Test
	public void append3() {
		final Array<Integer> a1 = new Array<Integer>(Arrays.asList(0, 1, 2, 3, 4, 5));
		final Array<Integer> a2 = a1.append(6);
		final Array<Integer> a3 = a1.append(6);
		
		Assert.assertEquals(a2.length(), a1.length() + 1);
		Assert.assertEquals(a3.length(), a1.length() + 1);
		Assert.assertNotSame(a2, a3);
		Assert.assertEquals(a2, a3);
	}
	
	@Test
	public void indexOf() {
		final Array<Integer> array = new Array<Integer>(20);
		for (int i = 0; i < 10; ++i) {
			array.set(i, i);
		}
		for (int i = 10; i < 20; ++i) {
			array.set(i, i - 10);
		}
		
		int index = array.indexOf(5);
		Assert.assertEquals(index, 5);
		
		index = array.lastIndexOf(5);
		Assert.assertEquals(index, 15);
		
		index = array.lastIndexOf(25);
		Assert.assertEquals(index, -1);
		
		index = array.indexOf(-1);
		Assert.assertEquals(index, -1);
		
		index = array.indexOf(Integer.MIN_VALUE);
		Assert.assertEquals(index, -1);
		
		index = array.indexOf(Integer.MAX_VALUE);
		Assert.assertEquals(index, -1);
	}
	
	@Test
	public void copy() {
		final Array<Integer> array = new Array<Integer>(10);
		for (int i = 0; i < array.length(); ++i) {
			array.set(i, i);
		}
		
		final Array<Integer> copy = array.subSeq(3, 8).copy();
		Assert.assertEquals(copy.length(), 5);
		for (int i = 0; i < 5; ++i) {
			Assert.assertEquals(copy.get(i), new Integer(i + 3));
		}
	}
	
	@Test
	public void subArray() {
		final Array<Integer> array = new Array<Integer>(10);
		for (int i = 0; i < array.length(); ++i) {
			array.set(i, i);
		}
		
		final Array<Integer> sub = array.subSeq(3, 8);
		Assert.assertEquals(sub.length(), 5);
		for (int i = 0; i < 5; ++i) {
			Assert.assertEquals(sub.get(i), new Integer(i + 3));
			sub.set(i, i + 100);
		}
		
		for (int i = 3; i < 8; ++i) {
			Assert.assertEquals(array.get(i), new Integer(i + 97));
		}
		
		final Array<Integer> copy = sub.copy();
		Assert.assertEquals(copy.length(), 5);
		for (int i = 0; i < 5; ++i) {
			Assert.assertEquals(sub.get(i), new Integer(i + 100));
		}
		
		int count = 0;
		for (Integer i : sub) {
			Assert.assertEquals(i, new Integer(count + 100));
			++count;
		}
		Assert.assertEquals(count, 5);
	}
	
	@Test
	public void iterator() {
		final List<Integer> list = asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 0);
		final Array<Integer> array = new Array<Integer>(list);
		
		final Iterator<Integer> ai = array.iterator();
		for (Integer i : list) {
			Assert.assertEquals(i, ai.next());
		}
		Assert.assertFalse(ai.hasNext());
	}
	
	@Test
	public void toObjectArray() {
		final Array<Integer> array = new Array<Integer>(10);
		for (int i = 0; i < array.length(); ++i) {
			array.set(i, i);
		}
		
		Object[] oa = array.toArray();
		Assert.assertEquals(oa.length, array.length());
		Assert.assertEquals(oa.getClass(), Object[].class);
		for (int i = 0; i < oa.length; ++i) {
			Assert.assertEquals(oa[i], array.get(i));
		}
	}
	
	@Test
	public void toTypedArray() {
		final Array<Integer> array = new Array<Integer>(10);
		for (int i = 0; i < array.length(); ++i) {
			array.set(i, i);
		}
		
		Integer[] oa = array.toArray(new Integer[0]);
		Assert.assertEquals(oa.length, array.length());
		Assert.assertEquals(oa.getClass(), Integer[].class);
		for (int i = 0; i < oa.length; ++i) {
			Assert.assertEquals(oa[i], array.get(i));
		}
		Assert.assertEquals(new Array<Integer>(oa), array);
	}
	
	@Test
	public void cloning() {
		final Array<Integer> array = new Array<Integer>(10);
		for (int i = 0; i < array.length(); ++i) {
			array.set(i, i);
		}
		
		Array<Integer> clone = array.clone();
		Assert.assertNotSame(clone, array);
		Assert.assertEquals(clone, array);
	}
	
}







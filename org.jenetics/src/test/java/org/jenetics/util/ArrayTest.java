/*
 * Java Genetic Algorithm Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
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

import static org.jenetics.util.arrays.isSorted;
import static org.jenetics.util.factories.Int;
import static org.jenetics.util.functions.Null;
import static org.jenetics.util.functions.ObjectToString;
import static org.jenetics.util.functions.not;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import org.testng.Assert;
import org.testng.annotations.Test;


/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date$</em>
 */
public class ArrayTest extends ObjectTester<Array<Double>> {

	static Factory<Double> RANDOM = new Factory<Double>() {
		private final Random random = new Random();
		@Override
		public Double newInstance() {
			return random.nextDouble();
		}

	};

	final Factory<Array<Double>> _factory = new Factory<Array<Double>>() {
		@Override
		public Array<Double> newInstance() {
			final Random random = RandomRegistry.getRandom();
			final Array<Double> array = new Array<>(random.nextInt(1000) + 100);
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
		final Array<Integer> a1 = Array.valueOf(1, 2, 3, 4, 5);
		final Array<Integer> a2 = Array.valueOf(6, 7, 8, 9, 10, 11, 12, 13);
		final Array<Integer> a3 = a1.add(a2);

		Assert.assertEquals(a3.length(), a1.length() + a2.length());
		for (int i = 0; i < a1.length() + a2.length(); ++i) {
			Assert.assertEquals(a3.get(i), new Integer(i + 1));
		}
	}

	@Test
	public void newFromSubArray() {
		final Array<Integer> a1 = Array.valueOf(0, 1, 2, 3, 4, 5, 6, 7);
		final Array<Integer> a2 = Array.valueOf(6, 7, 8, 9, 10, 11, 12, 13);
		final Array<Integer> a3 = a1.subSeq(0, 6).add(a2);

		Assert.assertEquals(a3.length(), a1.length() + a2.length() - 2);
		for (int i = 0; i < a1.length() + a2.length() - 2; ++i) {
			Assert.assertEquals(a3.get(i), new Integer(i));
		}
	}

	@Test
	public void newFromOtherSubArray() {
		final Array<Integer> a1 = Array.valueOf(0, 1, 2, 3, 4, 5, 6, 7);
		final Array<Integer> a2 = Array.valueOf(6, 7, 8, 9, 10, 11, 12, 13);
		final Array<Integer> a3 = a1.subSeq(1, 6).add(a2);

		Assert.assertEquals(a3.length(), a1.length() + a2.length() - 3);
		for (int i = 1; i < a1.length() + a2.length() - 2; ++i) {
			Assert.assertEquals(a3.get(i - 1), new Integer(i));
		}
	}

	@Test
	public void create4() {
		final Array<Integer> a1 = Array.valueOf(0, 1, 2, 3, 4, 5, 6, 7);
		final Array<Integer> a2 = Array.valueOf(6, 7, 8, 9, 10, 11, 12, 13);
		final Array<Integer> a3 = a1.add(a2.subSeq(2, 7));

		Assert.assertEquals(a3.length(), a1.length() + a2.length() - 3);
		for (int i = 0; i < a1.length() + a2.length() - 3; ++i) {
			Assert.assertEquals(a3.get(i), new Integer(i));
		}
	}

	@Test
	public void filter() {
		final Array<Integer> array = new Array<>(20);
		array.setAll(100);
		array.set(18, null);
		array.set(19, null);

		final Array<Integer> filtered = array.filter(not(Null));
		Assert.assertEquals(filtered.length(), array.length() - 2);
	}

	@Test
	public void boxBoolean() {
		final Random random = RandomRegistry.getRandom();
		final boolean[] array = new boolean[1000];
		for (int i = 0; i < array.length; ++i) {
			array[i] = random.nextBoolean();
		}

		final Array<Boolean> boxed = Array.box(array);

		for (int i = 0; i < array.length; ++i) {
			Assert.assertEquals(boxed.get(i).booleanValue(), array[i]);
		}
	}

	@Test
	public void boxChar() {
		final Random random = RandomRegistry.getRandom();
		final char[] array = new char[1000];
		for (int i = 0; i < array.length; ++i) {
			array[i] = (char)random.nextInt();
		}

		final Array<Character> boxed = Array.box(array);

		for (int i = 0; i < array.length; ++i) {
			Assert.assertEquals(boxed.get(i).charValue(), array[i]);
		}
	}

	@Test
	public void boxInt() {
		final Random random = RandomRegistry.getRandom();
		final int[] array = new int[1000];
		for (int i = 0; i < array.length; ++i) {
			array[i] = random.nextInt();
		}

		final Array<Integer> boxed = Array.box(array);

		for (int i = 0; i < array.length; ++i) {
			Assert.assertEquals(boxed.get(i).intValue(), array[i]);
		}
	}

	@Test
	public void boxLong() {
		final Random random = RandomRegistry.getRandom();
		final long[] array = new long[1000];
		for (int i = 0; i < array.length; ++i) {
			array[i] = random.nextLong();
		}

		final Array<Long> boxed = Array.box(array);

		for (int i = 0; i < array.length; ++i) {
			Assert.assertEquals(boxed.get(i).longValue(), array[i]);
		}
	}

	@Test
	public void boxFloat() {
		final Random random = RandomRegistry.getRandom();
		final float[] array = new float[1000];
		for (int i = 0; i < array.length; ++i) {
			array[i] = random.nextFloat();
		}

		final Array<Float> boxed = Array.box(array);

		for (int i = 0; i < array.length; ++i) {
			Assert.assertEquals(boxed.get(i).floatValue(), array[i]);
		}
	}

	@Test
	public void boxDouble() {
		final Random random = RandomRegistry.getRandom();
		final double[] array = new double[1000];
		for (int i = 0; i < array.length; ++i) {
			array[i] = random.nextDouble();
		}

		final Array<Double> boxed = Array.box(array);

		for (int i = 0; i < array.length; ++i) {
			Assert.assertEquals(boxed.get(i).doubleValue(), array[i]);
		}
	}

	@Test
	public void unboxBoolean() {
		final Random random = RandomRegistry.getRandom();
		final Array<Boolean> array = new Array<>(1000);
		for (int i = 0; i < array.length(); ++i) {
			array.set(i, random.nextBoolean());
		}

		final boolean[] unboxed = Array.unboxBoolean(array);

		for (int i = 0; i < array.length(); ++i) {
			Assert.assertEquals(unboxed[i], array.get(i).booleanValue());
		}
	}

	@Test
	public void unboxCharacter() {
		final Random random = RandomRegistry.getRandom();
		final Array<Character> array = new Array<>(1000);
		for (int i = 0; i < array.length(); ++i) {
			array.set(i, (char)random.nextInt());
		}

		final char[] unboxed = Array.unboxChar(array);

		for (int i = 0; i < array.length(); ++i) {
			Assert.assertEquals(unboxed[i], array.get(i).charValue());
		}
	}

	@Test
	public void unboxInteger() {
		final Random random = RandomRegistry.getRandom();
		final Array<Integer> array = new Array<>(1000);
		for (int i = 0; i < array.length(); ++i) {
			array.set(i, random.nextInt());
		}

		final int[] bbbbbbb = array.unbox(TypeBound.Integer);

		final Array<Double> da = new Array<>(0);
		final int[] aaggggg = array.unbox(TypeBound.Integer);

		final int[] unboxed = Array.unboxInt(array);

		for (int i = 0; i < array.length(); ++i) {
			Assert.assertEquals(unboxed[i], array.get(i).intValue());
		}
	}

	@Test
	public void unboxLong() {
		final Random random = RandomRegistry.getRandom();
		final Array<Long> array = new Array<>(1000);
		for (int i = 0; i < array.length(); ++i) {
			array.set(i, random.nextLong());
		}

		final long[] unboxed = Array.unboxLong(array);

		for (int i = 0; i < array.length(); ++i) {
			Assert.assertEquals(unboxed[i], array.get(i).longValue());
		}
	}

	@Test
	public void unboxFloat() {
		final Random random = RandomRegistry.getRandom();
		final Array<Float> array = new Array<>(1000);
		for (int i = 0; i < array.length(); ++i) {
			array.set(i, random.nextFloat());
		}

		final float[] unboxed = Array.unboxFloat(array);

		for (int i = 0; i < array.length(); ++i) {
			Assert.assertEquals(unboxed[i], array.get(i).floatValue());
		}
	}

	@Test
	public void unboxDouble() {
		final Random random = RandomRegistry.getRandom();
		final Array<Double> array = new Array<>(1000);
		for (int i = 0; i < array.length(); ++i) {
			array.set(i, random.nextDouble());
		}

		final double[] unboxed = Array.unboxDouble(array);

		for (int i = 0; i < array.length(); ++i) {
			Assert.assertEquals(unboxed[i], array.get(i).doubleValue());
		}
	}

	@Test
	public void sort() {
		final Array<Integer> integers = new Array<Integer>(10000).fill(Int());

		Assert.assertTrue(arrays.isSorted(integers));

		integers.sort();
		Assert.assertTrue(arrays.isSorted(integers));

		arrays.shuffle(integers, new Random());
		integers.sort();
		Assert.assertTrue(arrays.isSorted(integers));
	}

	@Test
	public void sort2() {
		final Random random = new Random();
		final Factory<Integer> factory = new Factory<Integer>() {
			@Override public Integer newInstance() {
				return random.nextInt(10000);
			}
		};

		final Array<Integer> array = new Array<>(100);
		array.fill(factory);
		Assert.assertFalse(isSorted(array));

		final Array<Integer> clonedArray = array.copy();
		Assert.assertEquals(array, clonedArray);

		clonedArray.sort(30, 40);
		array.subSeq(30, 40).sort();
		Assert.assertEquals(array, clonedArray);
	}

	@Test
	public void map() {
		final Array<Integer> integers = new Array<Integer>(20).fill(Int());

		final Array<String> strings = integers.map(ObjectToString);

		Assert.assertEquals(strings.length(), integers.length());
		for (int i = 0; i < strings.length(); ++i) {
			Assert.assertEquals(strings.get(i), Integer.toString(i));
		}
	}

	@Test
	public void reverse() {
		final Array<Integer> integers = new Array<Integer>(1000).fill(Int(999, -1));

		Assert.assertFalse(arrays.isSorted(integers));
		integers.reverse();
		Assert.assertTrue(arrays.isSorted(integers));
	}

	@Test
	public void swap() {
		final Array<Integer> array = new Array<Integer>(10).fill(Int());
		for (int i = 0; i < array.length(); ++i) {
			for (int j = i; j < array.length(); ++j) {
				final Array<Integer> copy = array.copy();
				copy.swap(i, j);

				Assert.assertEquals(copy.get(j), array.get(i));
			}
		}

		array.swap(4, 4);
	}

	@Test(expectedExceptions = ArrayIndexOutOfBoundsException.class)
	public void swap3() {
		final Array<Integer> array = new Array<Integer>(10).fill(Int());
		array.swap(5, 10);
	}

	@Test(expectedExceptions = ArrayIndexOutOfBoundsException.class)
	public void swap4() {
		final Array<Integer> array = new Array<Integer>(10).fill(Int());
		array.swap(-1, 8);
	}

	@Test
	public void swap5() {
		final Array<Integer> a1 = new Array<Integer>(50).fill(Int());
		final Array<Integer> a2 = new Array<Integer>(33).fill(Int());

		for (int i = 0; i < a1.length(); ++i) {
			for (int j = i; j < a1.length(); ++j) {
				for (int k = 0; k < a2.length() - (j - i); ++k) {
					final Array<Integer> ca1 = a1.copy();
					final Array<Integer> ca2 = a2.copy();

					ca1.swap(i, j, ca2, k);

					Assert.assertEquals(ca1.subSeq(i, j), a2.subSeq(k, k + (j - i)));
					Assert.assertEquals(ca2.subSeq(k, k + (j - i)), a1.subSeq(i, j));
				}
			}
		}
	}

	@Test
	public void asList() {
		final Array<Integer> integers = new Array<Integer>(1000).fill(Int());
		Assert.assertTrue(arrays.isSorted(integers));

		arrays.shuffle(integers, new Random());
		Assert.assertFalse(arrays.isSorted(integers));

		Collections.sort(integers.asList());
		Assert.assertTrue(arrays.isSorted(integers));
	}

	@Test
	public void fillConstant() {
		final ISeq<Integer> array = new Array<Integer>(10).setAll(10).toISeq();
		Assert.assertEquals(array.length(), 10);
		for (Integer i : array) {
			Assert.assertEquals(i, new Integer(10));
		}
	}

	@Test
	public void fillFactory() {
		final Array<Integer> array = new Array<Integer>(10).setAll(0);
		Assert.assertEquals(array.length(), 10);

		array.fill(Int());

		for (int i = 0; i < array.length(); ++i) {
			Assert.assertEquals(array.get(i), new Integer(i));
		}
	}

	static interface ArrayAlterer {
		void alter(final MSeq<Double> seq);
	}

	@Test
	void immutableFill() {
		immutable(new ArrayAlterer() {
			@Override public void alter(final MSeq<Double> seq) {
				seq.fill(RANDOM);
			}
		});
	}

	@Test
	void immutableSet() {
		immutable(new ArrayAlterer() {
			@Override public void alter(final MSeq<Double> seq) {
				for (int i = 0; i < seq.length(); ++i) {
					seq.set(i, Math.random());
				}
			}
		});
	}

	private void immutable(final ArrayAlterer alterer) {
		Array<Double> array = getFactory().newInstance();
		Array<Double> copy = array.copy();
		Assert.assertEquals(copy, array);

		int i = 0;
		int j = array.length();
		while (i < j) {
			final Array<Double> sub = array.subSeq(i, j);
			assertEquals(sub, 0, array, i, j - i);

			final ISeq<Double> iseq = sub.toISeq();
			final MSeq<Double> cseq = iseq.copy();
			assertEquals(iseq, 0, cseq, 0, iseq.length());

			alterer.alter(sub);
			Assert.assertEquals(sub, array.subSeq(i, j));
			Assert.assertEquals(iseq, cseq);

			++i; --j;
			array = copy.copy();
		}
	}

	static <T> void assertEquals(
		final Seq<T> actual, final int srcPos,
		final Seq<T> expected, final int desPos, final int length
	) {
		for (int i = 0; i < length; ++i) {
			Assert.assertEquals(actual.get(srcPos + i), expected.get(desPos + i));
		}
	}

	@Test
	public void foreach() {
		final Array<Integer> array = new Array<Integer>(10).setAll(123);
		array.toISeq();
		final AtomicInteger count = new AtomicInteger(0);
		boolean value = array.forall(new Function<Integer, Boolean>() {
			@Override public Boolean apply(Integer object) {
				Assert.assertEquals(object, new Integer(123));
				count.addAndGet(1);
				return Boolean.TRUE;
			}
		});

		Assert.assertEquals(value, true);
		Assert.assertEquals(count.get(), 10);

		count.set(0);
		int result = array.indexWhere(new Function<Integer, Boolean>() {
			@Override public Boolean apply(Integer object) {
				Assert.assertEquals(object, new Integer(123));
				return count.addAndGet(1) == 5;
			}
		});

		Assert.assertEquals(count.get(), 5);
		Assert.assertEquals(result, 4);
	}

	@Test
	public void append1() {
		final Array<Integer> a1 = Array.valueOf(0, 1, 2, 3, 4, 5);
		final Array<Integer> a2 = Array.valueOf(6, 7, 8, 9, 10);
		final Array<Integer> a3 = a1.add(a2);

		Assert.assertEquals(a3.length(), 11);
		Assert.assertEquals(a3,
				Array.valueOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
			);
	}

	@Test
	public void append2() {
		final Array<Integer> a1 = Array.valueOf(0, 1, 2, 3, 4, 5);
		final Array<Integer> a3 = a1.add(Arrays.asList(6, 7, 8, 9, 10));

		Assert.assertEquals(a3.length(), 11);
		Assert.assertEquals(a3,
				Array.valueOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
			);
	}

	@Test
	public void append3() {
		final Array<Integer> a1 = Array.valueOf(0, 1, 2, 3, 4, 5);
		final Array<Integer> a2 = a1.add(6);
		final Array<Integer> a3 = a1.add(6);

		Assert.assertEquals(a2.length(), a1.length() + 1);
		Assert.assertEquals(a3.length(), a1.length() + 1);
		Assert.assertNotSame(a2, a3);
		Assert.assertEquals(a2, a3);
	}

	@Test
	public void indexOf() {
		final Array<Integer> array = new Array<>(20);
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
		final Array<Integer> array = new Array<>(10);
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
		final Array<Integer> array = new Array<>(10);
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
		final List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 0);
		final Array<Integer> array = Array.valueOf(list);

		final Iterator<Integer> ai = array.iterator();
		for (Integer i : list) {
			Assert.assertEquals(i, ai.next());
		}
		Assert.assertFalse(ai.hasNext());
	}

	@Test
	public void toObjectArray() {
		final Array<Integer> array = new Array<>(10);
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
		final Array<Integer> array = new Array<>(10);
		for (int i = 0; i < array.length(); ++i) {
			array.set(i, i);
		}

		Integer[] oa = array.toArray(new Integer[0]);
		Assert.assertEquals(oa.length, array.length());
		Assert.assertEquals(oa.getClass(), Integer[].class);
		for (int i = 0; i < oa.length; ++i) {
			Assert.assertEquals(oa[i], array.get(i));
		}
		Assert.assertEquals(Array.valueOf(oa), array);
	}

}







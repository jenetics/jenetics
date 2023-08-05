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
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmail.com)
 */
package io.jenetics;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.jenetics.internal.util.Bits;
import io.jenetics.internal.util.EquivalentValidator;
import io.jenetics.util.Factory;
import io.jenetics.util.RandomRegistry;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class BitChromosomeTest extends ChromosomeTester<BitGene> {

	private static final EquivalentValidator<byte[], BitChromosome> FROM_BYTES =
		EquivalentValidator.of(BitChromosome::new, (v, a) -> a.toByteArray());

	private static final EquivalentValidator<BigInteger, BitChromosome> FROM_BIGINT =
		EquivalentValidator.of(BitChromosome::of, (v, a) -> a.toBigInteger());

	private static final EquivalentValidator<String, BitChromosome> FROM_STRING =
		EquivalentValidator.of(BitChromosome::of, (v, a) -> a.toCanonicalString());

	private static final EquivalentValidator<BitChromosome, byte[]> TO_BYTES =
		EquivalentValidator.of(
			BitChromosome::toByteArray,
			(a, v) -> new BitChromosome(v, 0, a.length())
		);

	private static final EquivalentValidator<BitChromosome, BigInteger> TO_BIGINT =
		EquivalentValidator.of(
			BitChromosome::toBigInteger,
			(a, v) -> BitChromosome.of(v, a.length())
		);

	private static final EquivalentValidator<BitChromosome, String> TO_STRING =
		EquivalentValidator.of(
			BitChromosome::toCanonicalString,
			(a, v) -> BitChromosome.of(v)
		);

	@Override
	protected Factory<Chromosome<BitGene>> factory() {
		return () -> BitChromosome.of(500, 0.3);
	}

	@Test(dataProvider = "bytes")
	public void fromBytes(final byte[] value) {
		FROM_BYTES.verify(value);
	}

	@DataProvider
	public Object[][] bytes() {
		final Random random = new Random();
		final Supplier<byte[]> supplier = () -> {
			final int length = random.nextInt(100) + 1;
			final byte[] array = new byte[length];
			random.nextBytes(array);
			return array;
		};

		return Stream.generate(supplier)
			.limit(25)
			.map(value -> new Object[]{value})
			.toArray(Object[][]::new);
	}

	@Test(dataProvider = "bigIntegers")
	public void fromBigInteger(final BigInteger value) {
		FROM_BIGINT.verify(value);
	}

	@DataProvider
	public Object[][] bigIntegers() {
		final Random random = new Random();
		final Supplier<BigInteger> supplier = () ->
			BigInteger.probablePrime(random.nextInt(100) + 100, random);

		return Stream.generate(supplier)
			.limit(25)
			.map(value -> new Object[]{value})
			.toArray(Object[][]::new);
	}

	@Test(dataProvider = "strings")
	public void fromString(final String value) {
		FROM_STRING.verify(value);
	}

	@DataProvider
	public Object[][] strings() {
		final var random = new Random(1234);

		final List<Object[]> values = new ArrayList<>();
		for (int i = 0; i < 25; ++i) {
			final int length = random.nextInt(1000) + 1;
			final var string = IntStream.range(0, length)
				.mapToObj(__ -> random.nextBoolean() ? "1" : "0")
				.collect(Collectors.joining());

			values.add(new Object[]{string});
		}

		return values.toArray(new Object[0][]);
	}

	@Test(dataProvider = "chromosomes")
	public void toBytes(final BitChromosome chromosome) {
		TO_BYTES.verify(chromosome);
	}

	// TODO: Support this identity.
	//@Test(dataProvider = "chromosomes", invocationCount = 1000)
	public void toBigInteger(final BitChromosome chromosomes) {
		TO_BIGINT.verify(chromosomes);
	}

	@Test(dataProvider = "chromosomes")
	public void toString(final BitChromosome chromosomes) {
		TO_STRING.verify(chromosomes);
	}

	@DataProvider
	public Object[][] chromosomes() {
		final var random = new Random();
		final Supplier<BitChromosome> supplier = () -> {
			final int length = random.nextInt(1000) + 10;
			return BitChromosome.of(length);
		};

		return Stream.generate(supplier)
			.limit(25)
			.map(value -> new Object[]{value})
			.toArray(Object[][]::new);
	}

	@Test
	public void createWithStartAndBegin() {
		final var random = new Random();
		final var bits = new byte[100];
		random.nextBytes(bits);

		final var ch = new BitChromosome(bits, 13, 71, 0.33);
		Assert.assertEquals(ch.length(), 71 - 13);
		Assert.assertEquals(ch.oneProbability(), 0.33);
	}

	@Test(invocationCount = 20, successPercentage = 90)
	public void newInstance() {
		final int size = 50_000;
		final BitChromosome base = BitChromosome.of(size, 0.5);

		for (int i = 0; i < 100; ++i) {
			final BitChromosome other = base.newInstance();
			Assert.assertNotEquals(other, base);

			Assert.assertEquals(other.bitCount(), size/2.0, size/100.0);
		}
	}

	@Test
	public void chromosomeProbability() {
		final byte[] data = new byte[1234];
		RandomRegistry.random().nextBytes(data);

		final BitChromosome c = new BitChromosome(data);
		Assert.assertEquals(
			c.oneProbability(),
			(double)Bits.count(data)/(double)(data.length*8),
			0.15
		);
	}

	@Test
	public void invert() {
		final BitChromosome c1 = BitChromosome.of(100, 0.3);
		final BitChromosome c3 = c1.invert();

		for (int i = 0; i < c1.length(); ++i) {
			Assert.assertTrue(c1.get(i).bit() != c3.get(i).bit());
		}

		BitChromosome c4 = c3.invert();
		Assert.assertEquals(c4, c1);
	}

	@Test
	public void numValue() {
		BitChromosome c1 = BitChromosome.of(10);

		int value = c1.intValue();
		assertEquals((short)value, c1.shortValue());
		assertEquals(value, c1.longValue());
		assertEquals((float)value, c1.floatValue());
		assertEquals(value, c1.doubleValue());
	}

	@Test
	public void intProbability() {
		BitChromosome c = BitChromosome.of(10, 0);
		for (BitGene g : c) {
			assertFalse(g.bit());
		}

		c = BitChromosome.of(10, 1);
		for (BitGene g : c) {
			assertTrue(g.bit());
		}
	}

	@Test
	public void bitChromosomeBitSet() {
		BitSet bits = new BitSet(10);
		for (int i = 0; i < 10; ++i) {
			bits.set(i, i%2 == 0);
		}

		BitChromosome c = BitChromosome.of(bits, 10);
		for (int i = 0; i < bits.length(); ++i) {
			assertEquals(c.get(i).bit(), i%2 == 0);
		}
	}

	@Test
	public void ones() {
		final BitChromosome c = BitChromosome.of(1000, 0.5);

		final int ones = (int)c.ones().count();
		assertEquals(ones, c.bitCount());
		assertTrue(c.ones().allMatch(c::booleanValue));
	}

	@Test
	public void zeros() {
		final BitChromosome c = BitChromosome.of(1000, 0.5);

		final int zeros = (int)c.zeros().count();
		assertEquals(zeros, c.length() - c.bitCount());
		assertTrue(c.zeros().noneMatch(c::booleanValue));
	}

	@Test(invocationCount = 5)
	public void toBigInteger() {
		final var random = new Random();
		final var bytes = new byte[random.nextInt(50, 1000)];
		random.nextBytes(bytes);

		final BigInteger value = new BigInteger(bytes);
		final BitChromosome chromosome = BitChromosome.of(value);

		assertThat(chromosome.toBigInteger()).isEqualTo(value);
	}

	@Test(invocationCount = 5)
	public void toFromBigInteger() {
		final var ch1 = BitChromosome.of(100, 0.9);
		final var value = ch1.toBigInteger();
		final var ch2 = BitChromosome.of(value);

		assertThat(ch2.toBigInteger()).isEqualTo(value);
		assertThat(ch2.toBigInteger()).isEqualTo(ch1.toBigInteger());
	}

	@Test
	public void toBitSet() {
		BitChromosome c1 = BitChromosome.of(34);
		BitChromosome c2 = BitChromosome.of(c1.toBitSet(), 34);

		for (int i = 0; i < c1.length(); ++i) {
			assertEquals(c1.get(i).bit(), c2.get(i).bit());
		}
	}

	@Test
	public void toByteArray() {
		byte[] data = new byte[16];
		for (int i = 0; i < data.length; ++i) {
			data[i] = (byte)(Math.random()*256);
		}
		BitChromosome bc = new BitChromosome(data);

		Assert.assertEquals(bc.toByteArray(), data);

	}

	@Test
	public void toCanonicalString() {
		BitChromosome c = BitChromosome.of(BigInteger.valueOf(234902));
		String value = c.toCanonicalString();
		BitChromosome sc = BitChromosome.of(value);

		Assert.assertEquals(sc, c);
	}

	@Test
	public void toStringToByteArray() {
		byte[] data = new byte[10];
		for (int i = 0; i < data.length; ++i) {
			data[i] = (byte)(Math.random()*256);
		}

		final String dataString = Bits.toByteString(data);
		Reporter.log(dataString);

		final byte[] sdata = Bits.fromByteString(dataString);
		Assert.assertEquals(sdata, data);
	}

	@Test
	public void fromBitSet() {
		final var random = new Random(234);
		final var size = 2343;
		final var bits = new BitSet(size);
		for (int i = 0; i < size; ++i) {
			bits.set(i, random.nextBoolean());
		}

		final var ch = BitChromosome.of(bits, size);
		for (int i = 0; i < size; ++i) {
			Assert.assertEquals(ch.get(i).bit(), bits.get(i));
		}
	}

	@Test
	public void fromByteArrayBitSet() {
		final Random random = new Random(123);
		final byte[] bytes = new byte[234];
		random.nextBytes(bytes);

		final var bits = BitSet.valueOf(bytes);
		final var ch = BitChromosome.of(bits, bytes.length*8);
		for (int i = 0; i < bytes.length*8; ++i) {
			Assert.assertEquals(ch.get(i).bit(), bits.get(i));
		}
	}

	@Test(dataProvider = "bitCountProbability")
	public void bitCount(final Double p) {
		final int size = 1_000;
		final BitChromosome base = BitChromosome.of(size, p);

		for (int i = 0; i < 1_000; ++i) {
			final BitChromosome other = base.newInstance();

			int bitCount = 0;
			for (BitGene gene : other) {
				if (gene.booleanValue()) {
					++bitCount;
				}
			}

			Assert.assertEquals(other.bitCount(), bitCount);
		}
	}

	@Test(dataProvider = "bitCountProbability")
	public void bitSetBitCount(final Double p) {
		final int size = 1_000;
		final BitChromosome base = BitChromosome.of(size, p);

		for (int i = 0; i < 1_000; ++i) {
			final BitChromosome other = base.newInstance();
			Assert.assertEquals(other.toBitSet().cardinality(), other.bitCount());
		}
	}

	@DataProvider(name = "bitCountProbability")
	public Object[][] getBitCountProbability() {
		return new Object[][] {
			{0.01}, {0.1}, {0.125}, {0.333}, {0.5}, {0.75}, {0.85}, {0.999}
		};
	}

	@Test
	public void map() {
		final var ch1 = BitChromosome.of(1000, 0.3);

		final var ch2 = ch1.map(BitChromosomeTest::flip);

		Assert.assertNotSame(ch2, ch1);
		Assert.assertEquals(ch2.toBitSet(), flip(ch1.toBitSet()));
	}

	static BitSet flip(final BitSet values) {
		values.flip(0, values.length());
		return values;
	}

	@Test(expectedExceptions = NullPointerException.class)
	public void mapNull() {
		final var ch = BitChromosome.of(1000, 0.3);
		ch.map(null);
	}

	@Test(dataProvider = "bitSetLength")
	public void ofBitSetSize(final int length) {
		final var ch1 = BitChromosome.of(length, 0.33333);
		Assert.assertEquals(ch1.length(), length);

		final var bits = ch1.toBitSet();

		final var ch2 = BitChromosome.of(bits, length);
		Assert.assertEquals(ch2, ch1);
	}

	@DataProvider
	public Object[][] bitSetLength() {
		return new Object[][] {
			{1}, {2}, {3}, {5}, {7}, {11}, {16}, {17}, {23}, {55}, {101}, {1111}
		};
	}

	@Test
	public void and() {
		final var ch1 = BitChromosome.of(100);
		final var ch2 = BitChromosome.of(129);

		final var ch3 = ch1.and(ch2);
		assertThat(ch3.length()).isEqualTo(ch1.length());
		for (int i = 0; i < ch3.length(); ++i) {
			assertThat(ch3.get(i).bit())
				.isEqualTo(ch1.get(i).bit() && ch2.get(i).bit());
		}

		final var ch4 = ch2.and(ch1);
		assertThat(ch4.length()).isEqualTo(ch2.length());
		for (int i = 0; i < ch1.length(); ++i) {
			assertThat(ch4.get(i).bit())
				.isEqualTo(ch2.get(i).bit() && ch1.get(i).bit());
		}
		for (int i = ch1.length(); i < ch2.length(); ++i) {
			assertThat(ch4.get(i)).isEqualTo(ch2.get(i));
		}
	}

	@Test
	public void or() {
		final var ch1 = BitChromosome.of(100);
		final var ch2 = BitChromosome.of(129);

		final var ch3 = ch1.or(ch2);
		assertThat(ch3.length()).isEqualTo(ch1.length());
		for (int i = 0; i < ch3.length(); ++i) {
			assertThat(ch3.get(i).bit())
				.isEqualTo(ch1.get(i).bit() || ch2.get(i).bit());
		}

		final var ch4 = ch2.or(ch1);
		assertThat(ch4.length()).isEqualTo(ch2.length());
		for (int i = 0; i < ch1.length(); ++i) {
			assertThat(ch4.get(i).bit())
				.isEqualTo(ch2.get(i).bit() || ch1.get(i).bit());
		}
		for (int i = ch1.length(); i < ch2.length(); ++i) {
			assertThat(ch4.get(i)).isEqualTo(ch2.get(i));
		}
	}

	@Test
	public void xor() {
		final var ch1 = BitChromosome.of(100);
		final var ch2 = BitChromosome.of(129);

		final var ch3 = ch1.xor(ch2);
		assertThat(ch3.length()).isEqualTo(ch1.length());
		for (int i = 0; i < ch3.length(); ++i) {
			assertThat(ch3.get(i).bit())
				.isEqualTo(ch1.get(i).bit() ^ ch2.get(i).bit());
		}

		final var ch4 = ch2.xor(ch1);
		assertThat(ch4.length()).isEqualTo(ch2.length());
		for (int i = 0; i < ch1.length(); ++i) {
			assertThat(ch4.get(i).bit())
				.isEqualTo(ch2.get(i).bit() ^ ch1.get(i).bit());
		}
		for (int i = ch1.length(); i < ch2.length(); ++i) {
			assertThat(ch4.get(i)).isEqualTo(ch2.get(i));
		}
	}

	@Test(dataProvider = "shifts")
	public void shiftLeft(final int shift) {
		final var chromosome = BitChromosome.of(1000);

		final var shifted = chromosome.shiftLeft(shift);
		assertThat(shifted.length()).isEqualTo(chromosome.length());

		if (shift >= 0) {
			// Check the 0 bits after shift.
			for (int i = 0; i < shift; ++i) {
				assertThat(shifted.get(i)).isEqualTo(BitGene.ZERO);
			}

			// Check the shifted bits.
			for (int i = shift; i < chromosome.length(); ++i) {
				assertThat(shifted.get(i)).isEqualTo(chromosome.get(i - shift));
			}
		} else {
			// Check the 0 bits after shift.
			for (int i = 0; i < Math.abs(shift); ++i) {
				assertThat(shifted.get(shifted.length() - i - 1))
					.isEqualTo(BitGene.ZERO);
			}

			// Check the shifted bits.
			for (int i = Math.abs(shift); i < chromosome.length(); ++i) {
				assertThat(shifted.get(i - Math.abs(shift)))
					.isEqualTo(chromosome.get(i));
			}
		}
	}

	@Test(dataProvider = "shifts")
	public void shiftRight(final int shift) {
		final var chromosome = BitChromosome.of(1000);

		final var shifted = chromosome.shiftRight(shift);
		assertThat(shifted.length()).isEqualTo(chromosome.length());

		if (shift >= 0) {
			// Check the 0 bits after shift.
			for (int i = 0; i < shift; ++i) {
				assertThat(shifted.get(shifted.length() - i - 1))
					.isEqualTo(BitGene.ZERO);
			}

			// Check the shifted bits.
			for (int i = shift; i < chromosome.length(); ++i) {
				assertThat(shifted.get(i - shift))
					.isEqualTo(chromosome.get(i));
			}
		} else {
			// Check the 0 bits after shift.
			for (int i = 0; i < Math.abs(shift); ++i) {
				assertThat(shifted.get(i)).isEqualTo(BitGene.ZERO);
			}

			// Check the shifted bits.
			for (int i = Math.abs(shift); i < chromosome.length(); ++i) {
				assertThat(shifted.get(i)).isEqualTo(chromosome.get(i - Math.abs(shift)));
			}
		}
	}

	@DataProvider
	public Object[][] shifts() {
		return new Object[][] {
			{0}, {1}, {3}, {5}, {7}, {11}, {33}, {98}, {232},
			{-1}, {-3}, {-5}, {-7}, {-11}, {-33}, {-98}, {-232},
		};
	}

}

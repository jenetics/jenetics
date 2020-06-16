package io.jenetics.internal.util;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class BitArrayTest {

	@Test(dataProvider = "arrays")
	public void toBigInteger(final BitArray ba) {
		final var bint = ba.toBigInteger();
		final var bar = BitArray.of(bint, ba.length());

		Assert.assertEquals(bar, ba);
		Assert.assertEquals(bar.toBigInteger(), bint);

		final var bits = BitArray.of("1111111010100110010110110010011110110101");
		final var bint2 = bits.toBigInteger();
		Assert.assertEquals(BitArray.of(bint2, bits.length()), bits);

		final var length = 2048;
		final var bint3 = BigInteger.probablePrime(length, new Random());
		final var bits3 = BitArray.of(bint3, length + 1);
		Assert.assertEquals(bits3.toBigInteger(), bint3);
	}

	@DataProvider
	public Object[][] arrays() {
		final var random = new Random(1234);

		final List<Object[]> values = new ArrayList<>();
		for (int i = 0; i < 20; ++i) {
			final int length = random.nextInt(30) + 5;
			final int start = random.nextInt(23);
			final int end = length*Byte.SIZE - random.nextInt(25);

			final byte[] bits = new byte[length];
			random.nextBytes(bits);

			values.add(new Object[]{new BitArray(bits, start, end)});
		}

		return values.toArray(new Object[0][]);
	}

	@Test
	public void fromBigInteger() {
		final var string = "11111110101001100101101100100111101101011101";
		final var ba = BitArray.of(string);
		System.out.println(string.length());

		var bi = ba.toBigInteger();
		System.out.println(bi);
		System.out.println(BitArray.of(bi, 40));
		System.out.println(BitArray.of(bi, string.length()).toBigInteger());
	}

	@Test(dataProvider = "bitStrings")
	public void fromString(final String string) {
		var array = BitArray.of(string);

		for (int i = 0; i < string.length(); ++i) {
			Assert.assertEquals(
				array.get(i),
				string.charAt(string.length() - 1 - i) == '1'
			);
		}
		Assert.assertEquals(array.toString(), string);
	}

	@DataProvider(name = "bitStrings")
	public Object[][] bitStrings() {
		final var random = new Random(1234);

		final List<Object[]> values = new ArrayList<>();
		for (int i = 0; i < 20; ++i) {
			final int length = random.nextInt(33) + 1;
			final var string = IntStream.range(0, length)
				.mapToObj(__ -> random.nextBoolean() ? "1" : "0")
				.collect(Collectors.joining());

			values.add(new Object[]{string});
		}

		return values.toArray(new Object[0][]);
	}

}

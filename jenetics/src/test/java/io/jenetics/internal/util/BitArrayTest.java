package io.jenetics.internal.util;

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
		Assert.assertEquals(bar.toString(), ba.toString());
		Assert.assertEquals(bar.toBigInteger(), bint);
	}

	@Test(dataProvider = "arrays")
	public void toString(final BitArray ba) {
		final var bint = ba.toBigInteger();
		final var bar = BitArray.of(bint, ba.length());
		Assert.assertEquals(bar.toString(), ba.toString());
	}

	@DataProvider
	public Object[][] arrays() {
		final var random = new Random(1234);

		final List<Object[]> values = new ArrayList<>();
		for (int i = 0; i < 25; ++i) {
			final int length = random.nextInt(30) + 5;
			final int start = random.nextInt(23);
			final int end = length*Byte.SIZE - random.nextInt(25);

			final byte[] bits = new byte[length];
			random.nextBytes(bits);

			values.add(new Object[]{new BitArray(bits, start, end)});
		}
		values.add(new Object[]{BitArray.of("11111110101001100101101100100111101101011101")});

		return values.toArray(new Object[0][]);
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
		for (int i = 0; i < 25; ++i) {
			final int length = random.nextInt(1000) + 1;
			final var string = IntStream.range(0, length)
				.mapToObj(__ -> random.nextBoolean() ? "1" : "0")
				.collect(Collectors.joining());

			values.add(new Object[]{string});
		}

		return values.toArray(new Object[0][]);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void fromEmptyString() {
		BitArray.of("");
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void withZeroLength() {
		BitArray.ofLength(0);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void withNegativeLength() {
		BitArray.ofLength(-10);
	}

}

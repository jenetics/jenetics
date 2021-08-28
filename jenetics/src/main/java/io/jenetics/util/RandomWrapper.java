package io.jenetics.util;

import static java.util.Objects.requireNonNull;

import java.io.Serial;
import java.util.Random;
import java.util.random.RandomGenerator;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

public final class RandomWrapper extends Random {

	@Serial
	private static final long serialVersionUID = 1;

	private final RandomGenerator _random;

	public RandomWrapper(final RandomGenerator random) {
		_random = requireNonNull(random);
	}

	@Override
	public boolean isDeprecated() {
		return _random.isDeprecated();
	}

	@Override
	public DoubleStream doubles() {
		return _random.doubles();
	}

	@Override
	public DoubleStream doubles(double randomNumberOrigin, double randomNumberBound) {
		return _random.doubles(randomNumberOrigin, randomNumberBound);
	}

	@Override
	public DoubleStream doubles(long streamSize) {
		return _random.doubles(streamSize);
	}

	@Override
	public DoubleStream doubles(long streamSize, double randomNumberOrigin, double randomNumberBound) {
		return _random.doubles(streamSize, randomNumberOrigin, randomNumberBound);
	}

	@Override
	public IntStream ints() {
		return _random.ints();
	}

	@Override
	public IntStream ints(int randomNumberOrigin, int randomNumberBound) {
		return _random.ints(randomNumberOrigin, randomNumberBound);
	}

	@Override
	public IntStream ints(long streamSize) {
		return _random.ints(streamSize);
	}

	@Override
	public IntStream ints(long streamSize, int randomNumberOrigin, int randomNumberBound) {
		return _random.ints(streamSize, randomNumberOrigin, randomNumberBound);
	}

	@Override
	public LongStream longs() {
		return _random.longs();
	}

	@Override
	public LongStream longs(long randomNumberOrigin, long randomNumberBound) {
		return _random.longs(randomNumberOrigin, randomNumberBound);
	}

	@Override
	public LongStream longs(long streamSize) {
		return _random.longs(streamSize);
	}

	@Override
	public LongStream longs(long streamSize, long randomNumberOrigin, long randomNumberBound) {
		return _random.longs(streamSize, randomNumberOrigin, randomNumberBound);
	}

	@Override
	public boolean nextBoolean() {
		return _random.nextBoolean();
	}

	@Override
	public void nextBytes(byte[] bytes) {
		_random.nextBytes(bytes);
	}

	@Override
	public float nextFloat() {
		return _random.nextFloat();
	}

	@Override
	public float nextFloat(float bound) {
		return _random.nextFloat(bound);
	}

	@Override
	public float nextFloat(float origin, float bound) {
		return _random.nextFloat(origin, bound);
	}

	@Override
	public double nextDouble() {
		return _random.nextDouble();
	}

	@Override
	public double nextDouble(double bound) {
		return _random.nextDouble(bound);
	}

	@Override
	public double nextDouble(double origin, double bound) {
		return _random.nextDouble(origin, bound);
	}

	@Override
	public int nextInt() {
		return _random.nextInt();
	}

	@Override
	public int nextInt(int bound) {
		return _random.nextInt(bound);
	}

	@Override
	public int nextInt(int origin, int bound) {
		return _random.nextInt(origin, bound);
	}

	@Override
	public long nextLong() {
		return _random.nextLong();
	}

	@Override
	public long nextLong(long bound) {
		return _random.nextLong(bound);
	}

	@Override
	public long nextLong(long origin, long bound) {
		return _random.nextLong(origin, bound);
	}

	@Override
	public double nextGaussian() {
		return _random.nextGaussian();
	}

	@Override
	public double nextGaussian(double mean, double stddev) {
		return _random.nextGaussian(mean, stddev);
	}

	@Override
	public double nextExponential() {
		return _random.nextExponential();
	}
}

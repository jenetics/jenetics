package io.jenetics.testfixtures.stat;

public abstract class Hist {

	public record Bin(double min, double max) {
	}

	protected final double[] _bins;
	protected final long[] _table;

	protected long _count = 0;

	protected Hist(final double... bins) {
		_bins = bins.clone();
		_table = new long[bins.length - 1];
	}

	protected abstract int binIndexOf(final double value);

	public int binCount() {
		return _table.length;
	}

	public long count() {
		return _count;
	}

}

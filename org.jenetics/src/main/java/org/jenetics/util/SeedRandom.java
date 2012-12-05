package org.jenetics.util;

public class SeedRandom extends Random64 {

	private static final long serialVersionUID = 1L;

	@Override
	public long nextLong() {
		return random.seed();
	}

}

package io.jenetics.incubator.util;

import com.google.common.hash.Hashing;

import org.testng.annotations.Test;

public class MurmurDigestTest {

	@Test
	public void randomBytesHash() {
		final var hasher = Hashing.murmur3_128();
	}

}

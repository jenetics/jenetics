package io.jenetics.incubator.util;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

public class IOTest {

	private static final List<Object> OBJECTS = List.of(
		"asdf",
		"asfasdf",
		1L,
		3,
		5.4,
		5345.445F
	);

	@Test
	public void readWrite() throws IOException {
		final var tempFile = Files.createTempFile("IO", "TEST");
		System.out.println(tempFile);
		try {
			IO.__write(OBJECTS, tempFile);
			try (var objects = IO.__read(tempFile)) {
				Assert.assertEquals(
					OBJECTS,
					objects.collect(Collectors.toList())
				);
			}
		} finally {
			Files.deleteIfExists(tempFile);
		}
	}

}

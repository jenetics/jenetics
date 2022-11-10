package io.jenetics.incubator.property;

import java.io.IOException;

import org.testng.annotations.Test;

import io.jenetics.jpx.GPX;
import io.jenetics.jpx.GPX.Reader;

public class RecursivePropertyExtractorTest {

	@Test
	public void extract() throws IOException {
		final GPX gpx = Reader.DEFAULT.read(
			RecursivePropertyExtractorTest.class
				.getResourceAsStream("/Austria.gpx")
		);

		Properties.walk(new PathObject(gpx), "io.jenetics.*")
			.forEach(System.out::println);
	}

}

package io.jenetics.incubator.structural;

import org.testng.annotations.Test;

public class StructuresTest {

	@Test
	public void components() {
		Structures.components(Ticket.class)
			.forEach(System.out::println);
	}

}

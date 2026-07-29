package io.jenetics.incubator.structural;

import org.testng.annotations.Test;

import java.util.Map;

public class StructureViewTest {

	@Test
	public void proxy() {
		final var ticket = StructureView.of(
			Map.of(
				"ticketId", "_ticket_id_",
				"event", Map.of(
					"id", 234234L,
					"name", "Some fancy name"
				)
			),
			Ticket.class
		);

		IO.println(ticket.ticketId());
		IO.println(ticket.event().id());
		IO.println(ticket.event().name());
	}

}

package io.jenetics.incubator.structural;

import org.testng.annotations.Test;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class StructureViewTest {

	@Test
	public void accessorMethodsReturnStoredValues() {
		final var ticket = StructureView.of(ticketStore(), Ticket.class);

		assertThat(ticket.ticketId()).isEqualTo("_ticket_id_");
		assertThat(ticket.ticketDate()).isEqualTo(LocalDate.of(2024, 4, 18));
		assertThat(ticket.ticketType()).isEqualTo("vip");
	}

	@Test
	public void nestedMapsAreReturnedAsStructureViews() {
		final var ticket = StructureView.of(ticketStore(), Ticket.class);

		assertThat(ticket.event().id()).isEqualTo(234234L);
		assertThat(ticket.event().name()).isEqualTo("Some fancy name");
	}

	@Test
	public void objectMethodsUseStructureTypeAndStore() {
		final var store = ticketStore();
		final var ticket = StructureView.of(store, Ticket.class);
		final var equal = StructureView.of(ticketStore(), Ticket.class);
		final var different = StructureView.of(ticketStore("regular"), Ticket.class);

		assertThat(ticket).isEqualTo(equal);
		assertThat(ticket).isNotEqualTo(different);
		assertThat(ticket).isNotEqualTo(StructureView.of(store, Event.class));
		assertThat(ticket).isNotEqualTo(null);
		assertThat(ticket.hashCode()).isEqualTo(equal.hashCode());
		assertThat(ticket.toString()).isEqualTo(
			"Ticket[ticketId=_ticket_id_, ticketDate=2024-04-18, " +
			"ticketType=vip, event=Event[id=234234, name=Some fancy name]]"
		);
	}

	@Test
	public void builderMethodsUpdateStore() {
		final var store = new LinkedHashMap<String, Object>();
		final var builder = StructureView.of(store, Ticket.Builder.class);

		assertThat(builder.ticketId("_ticket_id_")).isSameAs(builder);
		assertThat(builder.ticketDate(LocalDate.of(2024, 4, 18))).isSameAs(builder);
		assertThat(builder.ticketType("vip")).isSameAs(builder);

		assertThat(store).containsExactly(
			Map.entry("ticketId", "_ticket_id_"),
			Map.entry("ticketDate", LocalDate.of(2024, 4, 18)),
			Map.entry("ticketType", "vip")
		);
		assertThat(builder.ticketId()).isEqualTo("_ticket_id_");
		assertThat(builder.ticketDate()).isEqualTo(LocalDate.of(2024, 4, 18));
		assertThat(builder.ticketType()).isEqualTo("vip");
	}

	@Test
	public void nestedBuilderMethodCreatesNestedStore() {
		final var store = new LinkedHashMap<String, Object>();
		final var builder = StructureView.of(store, Ticket.Builder.class);

		assertThat(builder.event(event -> event
			.id(123L)
			.name("Concert")
		)).isSameAs(builder);

		assertThat(store.get("event")).isInstanceOf(Map.class);
		assertThat(builder.event().id()).isEqualTo(123L);
		assertThat(builder.event().name()).isEqualTo("Concert");
	}

	@Test
	public void nestedBuilderMethodUpdatesExistingNestedStore() {
		final var store = new LinkedHashMap<String, Object>();
		final var event = new LinkedHashMap<String, Object>();
		event.put("id", 123L);
		store.put("event", event);

		final var builder = StructureView.of(store, Ticket.Builder.class);
		builder.event(value -> value.name("Concert"));

		assertThat(store.get("event")).isSameAs(event);
		assertThat(builder.event().id()).isEqualTo(123L);
		assertThat(builder.event().name()).isEqualTo("Concert");
	}

	@Test
	public void nullStoreIsRejected() {
		assertThatExceptionOfType(NullPointerException.class)
			.isThrownBy(() -> StructureView.of(null, Ticket.class));
	}

	@Test
	public void nullTypeIsRejected() {
		assertThatExceptionOfType(NullPointerException.class)
			.isThrownBy(() -> StructureView.of(Map.of(), null));
	}

	private static Map<String, Object> ticketStore() {
		return ticketStore("vip");
	}

	private static Map<String, Object> ticketStore(final String ticketType) {
		final var event = new LinkedHashMap<String, Object>();
		event.put("id", 234234L);
		event.put("name", "Some fancy name");

		final var ticket = new LinkedHashMap<String, Object>();
		ticket.put("ticketId", "_ticket_id_");
		ticket.put("ticketDate", LocalDate.of(2024, 4, 18));
		ticket.put("ticketType", ticketType);
		ticket.put("event", event);

		return ticket;
	}

}

package io.jenetics.incubator.structural;

import java.time.LocalDate;
import java.util.function.Consumer;

public interface Ticket {
	String ticketId();
	LocalDate ticketDate();
	String ticketType();
	Event event();

	interface Builder extends Ticket {
		Builder ticketId(String value);
		Builder ticketDate(LocalDate value);
		Builder ticketType(String value);
		Builder event(Event value);
		Builder event(Consumer<? super Event.Builder> builder);
	}

}

/*
 * Java Genetic Algorithm Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Author:
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmail.com)
 */
package io.jenetics.incubator.web.openapi;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

interface Ticket {
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

interface Event {
	Long id();
	String name();

	interface Builder extends Event {
		Builder id(Long value);
		Builder name(String value);
	}
}

final class Names {
	static final class TicketId extends Type0 {}
	static final class TicketDate extends Type0 {}
	static final class TicketType extends Type0 {}
	static final class Event extends Type0 {}
	static final class Id extends Type0 {}
	static final class Name extends Type0 {}

	private Names() {
	}
}

final class Types {

	abstract class Ticket extends
		Type4<
			Type2<Names.TicketId, String>,
			Type2<Names.TicketDate, LocalDate>,
			Type2<Names.TicketType, String>,
			Type2<
				Names.Event,
				Type2<
					Type2<Names.Id, Long>,
					Type2<Names.Name, String>
				>
			>
		>
	{}

	private Types() {
	}
}

class Test{

	void foo() {
		Type4<
			Type2<Names.TicketId, String>,
			Type2<Names.TicketDate, LocalDate>,
			Type2<Names.TicketType, String>,
			Type2<Names.Event, Event>
		> ticket;
	}

}


final class JsonProxy {
	private JsonProxy() {
	}

	static <T> T builder(Class<T> type, Map<String, Object> store) {
		return null;
	}

}

final class Reflects {
	private Reflects() {
	}

	static <T> T builder(Class<T> type, Map<String, Object> values) {
		return null;
	}

	@SuppressWarnings("unchecked")
	static Ticket viewOf(Map<String, Object> values) {
		return new Ticket() {
			@Override
			public String ticketId() {
				return (String)values.get("ticketId");
			}
			@Override
			public LocalDate ticketDate() {
				return (LocalDate)values.get("ticketDate");
			}
			@Override
			public String ticketType() {
				return (String)values.get("ticketType");
			}
			@Override
			public Event event() {
				return builder(Event.class, (Map<String, Object>)values.get("event"));
			}
		};
	}

	static Map<String, Object> get(String path) {
		return Map.of();
	}

	static void main() {
		final var values = new HashMap<String, Object>();

		final Ticket ticket = builder(Ticket.Builder.class, values)
			.ticketId("ticket_id")
			.ticketType("TEUER")
			.event(Reflects::buildEvents)
			.ticketDate(LocalDate.now());
	}

	static void buildEvents(Event.Builder event) {
		event
			.id(23L)
			.name("Concert");
	}

}


// allOf -> 'extends'
interface MuseumTicketsConfirmation extends Ticket, Address {
	String message();
	String confirmationCode();
}

interface Address extends Name, Street {
	Address address();
}

interface TicketId {
	String ticketId();
}

interface Name {
	String name();
}

interface Street {
	String street();
}


abstract class Type0 {}
abstract class Type1<T1> {}
abstract class Type2<T1, T2> {}
abstract class Type3<T1, T2, T3> {}
abstract class Type4<T1, T2, T3, T4> {}


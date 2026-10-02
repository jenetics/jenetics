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
package io.jenetics.incubator.structural;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatNoException;

import java.time.LocalDate;
import java.util.function.Consumer;

import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class StructuresTest {

	@Test
	public void components() {
		Structures.components(Ticket.class)
			.forEach(System.out::println);
	}

	@Test
	public void validBuilder() {
		assertThatNoException()
			.isThrownBy(() -> Structures.Builders.check(Ticket.Builder.class));
	}

	@Test
	public void validBuilderWithoutNestedBuilderMethod() {
		assertThatNoException()
			.isThrownBy(() -> Structures.Builders.check(SimpleTicketBuilder.class));
	}

	@Test
	public void builderMustBeAnInterface() {
		assertThatExceptionOfType(IllegalArgumentException.class)
			.isThrownBy(() -> Structures.Builders.check(NotAnInterface.class));
	}

	@Test
	public void builderMustExtendAStructure() {
		assertThatExceptionOfType(IllegalArgumentException.class)
			.isThrownBy(() -> Structures.Builders.check(NoStructureBuilder.class));
	}

	@Test
	public void builderMustContainAllComponentMethods() {
		assertThatExceptionOfType(IllegalArgumentException.class)
			.isThrownBy(() -> Structures.Builders.check(MissingTicketTypeBuilder.class));
	}

	@Test
	public void builderMethodMustUseComponentType() {
		assertThatExceptionOfType(IllegalArgumentException.class)
			.isThrownBy(() -> Structures.Builders.check(WrongTicketIdTypeBuilder.class));
	}

	@Test
	public void builderMethodMustReturnBuilderType() {
		assertThatExceptionOfType(IllegalArgumentException.class)
			.isThrownBy(() -> Structures.Builders.check(WrongTicketIdReturnTypeBuilder.class));
	}

	@Test
	public void nestedBuilderMethodMustUseComponentBuilderConsumer() {
		assertThatExceptionOfType(IllegalArgumentException.class)
			.isThrownBy(() -> Structures.Builders.check(WrongEventConsumerBuilder.class));
	}

	private static final class NotAnInterface {
	}

	private interface NoStructureBuilder {
		NoStructureBuilder value(String value);
	}

	private interface SimpleTicketBuilder extends Ticket {
		SimpleTicketBuilder ticketId(String value);
		SimpleTicketBuilder ticketDate(LocalDate value);
		SimpleTicketBuilder ticketType(String value);
		SimpleTicketBuilder event(Event value);
	}

	private interface MissingTicketTypeBuilder extends Ticket {
		MissingTicketTypeBuilder ticketId(String value);
		MissingTicketTypeBuilder ticketDate(LocalDate value);
		MissingTicketTypeBuilder event(Event value);
	}

	private interface WrongTicketIdTypeBuilder extends Ticket {
		WrongTicketIdTypeBuilder ticketId(Object value);
		WrongTicketIdTypeBuilder ticketDate(LocalDate value);
		WrongTicketIdTypeBuilder ticketType(String value);
		WrongTicketIdTypeBuilder event(Event value);
	}

	private interface WrongTicketIdReturnTypeBuilder extends Ticket {
		Ticket ticketId(String value);
		WrongTicketIdReturnTypeBuilder ticketDate(LocalDate value);
		WrongTicketIdReturnTypeBuilder ticketType(String value);
		WrongTicketIdReturnTypeBuilder event(Event value);
	}

	private interface WrongEventConsumerBuilder extends Ticket {
		WrongEventConsumerBuilder ticketId(String value);
		WrongEventConsumerBuilder ticketDate(LocalDate value);
		WrongEventConsumerBuilder ticketType(String value);
		WrongEventConsumerBuilder event(Event value);
		WrongEventConsumerBuilder event(Consumer<? super Ticket.Builder> builder);
	}

}

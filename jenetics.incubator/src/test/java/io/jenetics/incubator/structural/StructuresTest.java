package io.jenetics.incubator.structural;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatNoException;

import java.time.LocalDate;
import java.util.function.Consumer;

import org.testng.annotations.Test;

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

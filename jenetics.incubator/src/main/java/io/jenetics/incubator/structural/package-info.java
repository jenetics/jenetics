/**
 * <h1>Description</h1>
 * This package contains classes for working with <em>structural</em> types. In
 * contrast to <a href="https://www.scala-lang.org/">Scale</a>, Java has no such
 * feature as structural types. In Java, all types are nominal, which means,
 * that every type (class) must have a name. The advantage of structural types is,
 * that they can bridge the gap between to different APIs, with different data
 * structures, but where these data structures are essential equal, according to
 * its structure. The API gap, this package tries to mitigate is the semantical
 * and API gap between JSON objects, as defined in OpenAPI specifications and
 * Java's nominal type system.
 *
 * <h2>"Structural" types</h2>
 * Interfaces have the greatest flexibility, when it comes to flexibility and
 * extensibility. That's why Java interfaces are used to simulate structural
 * types. There are no (tagging) interfaces or annotation a structural type must
 * extend or must be annotated with. Whether an interfaces can be treated as
 * structural interfaces is sole defined by its methods (structure). The following
 * properties must be fulfilled to be recognized as structural interface:
 * <ol>
 *     <li>A structural interface must only contain methods with zero arguments.</li>
 *     <li>All methods must return a value. No {@code void} methods are allowed.</li>
 * </ol>
 * These two properties essentially says, that every structural interface can be
 * implemented by a {@link Record}.
 * {@snippet lang=java:
 * public interface Ticket {
 *     String ticketId();
 *     LocalDate ticketDate();
 *     String ticketType();
 * }
 *
 * // Every structural interface is implementable by a Java record.
 * public record TicketImpl(
 *     String ticketId,
 *     LocalDate ticketDate,
 *     String ticketType
 * ) implements Ticket {}
 * }
 *
 * <h2>"Structural" type builder</h2>
 * Structural types defines only accessors to their components. Changing the
 * component values is done via a builder interface and the corresponding
 * builder methods. Builder methods have the following properties:
 * <ol>
 *     <li>The builder method has the same name as the corresponding component.</li>
 *     <li>The builder method takes only one argument with the component type.</li>
 *     <li>The builder method returns a {@code this} reference, for method chaining.</li>
 *     <li>Components which are itself structural types may have an additional
 *     builder method, which takes a {@link java.util.function.Consumer} of the
 *     structural type builder, if available.</li>
 * </ol>
 * {@snippet lang=java:
 * public interface Ticket {
 *     String ticketId();
 *     LocalDate ticketDate();
 *     String ticketType();
 *     Event event();
 *
 *     interface Builder extends Ticket {
 *         Builder ticketId(String value);
 *         Builder ticketDate(LocalDate value);
 *         Builder ticketType(String value);
 *         Builder event(Event value);
 *         Builder event(Consumer<? super Event.Builder> builder);
 *     }
 * }
 * }
 */
package io.jenetics.incubator.structural;

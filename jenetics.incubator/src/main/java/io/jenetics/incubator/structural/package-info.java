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
 */
package io.jenetics.incubator.structural;

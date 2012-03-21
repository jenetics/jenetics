/*
 * Java Genetic Algorithm Library (@!identifier!@).
 * Copyright (c) @!year!@ Franz Wilhelmstötter
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * Author:
 *   Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 *
 */
package org.jenetics;

import static org.jenetics.util.object.NonNull;
import static org.jenetics.util.object.eq;
import static org.jenetics.util.object.hashCodeOf;
import static org.jenetics.util.object.nonNull;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.jenetics.util.Array;
import org.jenetics.util.Function;
import org.jenetics.util.ISeq;
import org.jenetics.util.Seq;

/**
 * Combines several alterers to one.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version $Id$
 */
public final class CompositeAlterer<G extends Gene<?, G>>
	extends AbstractAlterer<G>
{

	private final ISeq<Alterer<G>> _alterers;

	/**
	 * Combine the given alterers.
	 *
	 * @param a1 first alterer.
	 * @param a2 second alterer.
	 * @throws NullPointerException if one of the alterer is {@code null}.
	 */
	public CompositeAlterer(
		final Alterer<G> a1,
		final Alterer<G> a2
	) {
		this(new Array<>(
				nonNull(a1),
				nonNull(a2)
			));
	}

	/**
	 * Combine the given alterers.
	 *
	 * @param a1 first alterer.
	 * @param a2 second alterer.
	 * @param a3 third alterer.
	 * @throws NullPointerException if one of the alterer is {@code null}.
	 */
	public CompositeAlterer(
		final Alterer<G> a1,
		final Alterer<G> a2,
		final Alterer<G> a3
	) {
		this(new Array<>(
				nonNull(a1),
				nonNull(a2),
				nonNull(a3)
			));
	}

	/**
	 * Combine the given alterers.
	 *
	 * @param a1 first alterer.
	 * @param a2 second alterer.
	 * @param a3 third alterer.
	 * @param a4 fourth alterer.
	 * @throws NullPointerException if one of the alterer is {@code null}.
	 */
	public CompositeAlterer(
		final Alterer<G> a1,
		final Alterer<G> a2,
		final Alterer<G> a3,
		final Alterer<G> a4
	) {
		this(new Array<>(
				nonNull(a1),
				nonNull(a2),
				nonNull(a3),
				nonNull(a4)
			));
	}

	/**
	 * Combine the given alterers.
	 *
	 * @param a1 first alterer.
	 * @param a2 second alterer.
	 * @param a3 third alterer.
	 * @param a4 fourth alterer.
	 * @param a5 fifth alterer.
	 * @throws NullPointerException if one of the alterer is {@code null}.
	 */
	public CompositeAlterer(
		final Alterer<G> a1,
		final Alterer<G> a2,
		final Alterer<G> a3,
		final Alterer<G> a4,
		final Alterer<G> a5
	) {
		this(new Array<>(
				nonNull(a1),
				nonNull(a2),
				nonNull(a3),
				nonNull(a4),
				nonNull(a5)
			));
	}

	/**
	 * Combine the given alterers.
	 *
	 * @param alterers the alterers to combine.
	 * @throws NullPointerException if one of the alterers is {@code null}.
	 */
	@SafeVarargs
	public CompositeAlterer(final Alterer<G>... alterers) {
		this(new Array<>(alterers));
	}

	/**
	 * Combine the given alterers.
	 *
	 * @param alterers the alterers to combine.
	 * @throws NullPointerException if one of the alterers is {@code null}.
	 */
	public CompositeAlterer(final Seq<Alterer<G>> alterers) {
		super(1.0);

		alterers.foreach(NonNull("Alterer"));
		_alterers = normalize(alterers).toISeq();
	}

	private Array<Alterer<G>> normalize(final Seq<Alterer<G>> alterers) {
		final Deque<Alterer<G>> stack = new LinkedList<>(alterers.asList());

		final List<Alterer<G>> normalized = new LinkedList<>();

		while (!stack.isEmpty()) {
			final Alterer<G> alterer = stack.pollFirst();

			if (alterer instanceof CompositeAlterer<?>) {
				final CompositeAlterer<G> calterer = (CompositeAlterer<G>)alterer;

				for (int i = calterer.getAlterers().length(); --i >= 0;) {
					stack.addFirst(calterer.getAlterers().get(i));
				}
			} else {
				normalized.add(alterer);
			}
		}

		return new Array<>(normalized);
	}

	@Override
	public <C extends Comparable<? super C>> int alter(
		final Population<G, C> population,
		final int generation
	) {
		final AtomicInteger alterations = new AtomicInteger(0);

		_alterers.foreach(new Function<Alterer<G>, Boolean>() {
			@Override public Boolean apply(final Alterer<G> alterer) {
				alterations.addAndGet(alterer.alter(population, generation));
				return Boolean.TRUE;
			}
		});

		return alterations.get();
	}

	/**
	 * Create a new CompositeAlterer with the given alterer appended.
	 *
	 * @param alterer the alterer to append.
	 * @return a new CompositeAlterer.
	 * @throws NullPointerException if the given alterer is {@code null}.
	 */
	public CompositeAlterer<G> append(final Alterer<G> alterer) {
		return new CompositeAlterer<>(this, nonNull(alterer, "Alterer"));
	}

	/**
	 * Return the alterers this alterer consists of. The returned array is sealed
	 * and cannot be changed.
	 *
	 * @return the alterers this alterer consists of.
	 */
	public Seq<Alterer<G>> getAlterers() {
		return _alterers;
	}

	@Override
	public int hashCode() {
		return hashCodeOf(getClass()).and(_alterers).value();
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj == null || obj.getClass() != getClass()) {
			return false;
		}

		final CompositeAlterer<?> alterer = (CompositeAlterer<?>)obj;
		return eq(_alterers, alterer._alterers);
	}

	@Override
	public String toString() {
		return String.format("%s[%s]", getClass().getSimpleName(), _alterers);
	}

	/**
	 * Joins the given alterer and returns a new CompositeAlterer object. If one
	 * of the given alterers is a CompositeAlterer the sub alterers of it are
	 * unpacked and appended to the newly created CompositeAlterer.
	 *
	 * @param <T> the gene type of the alterers.
	 * @param a1 the first alterer.
	 * @param a2 the second alterer.
	 * @return a new CompositeAlterer object.
	 * @throws NullPointerException if one of the given alterer is {@code null}.
	 */
	public static <T extends Gene<?, T>> CompositeAlterer<T> join(
		final Alterer<T> a1,
		final Alterer<T> a2
	) {
		return new CompositeAlterer<>(a1, a2);
	}
}








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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * Author:
 *     Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 *     
 */
package org.jenetics;

import static org.jenetics.util.Validator.nonNull;

import org.jenetics.util.Array;
import org.jenetics.util.Predicate;
import org.jenetics.util.Validator;

/**
 * Combines several alterers to one.
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: CompositeAlterer.java,v 1.5 2010-01-28 19:34:14 fwilhelm Exp $
 */
public final class CompositeAlterer<G extends Gene<?, G>> extends AbstractAlterer<G> {

	private final Array<Alterer<G>> _alterers;
	
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
		super(1.0);
		_alterers = new Array<Alterer<G>>(a1, a2);
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
		super(1.0);
		_alterers = new Array<Alterer<G>>(a1, a2, a3);
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
		super(1.0);
		_alterers = new Array<Alterer<G>>(a1, a2, a3, a4);
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
		super(1.0);
		_alterers = new Array<Alterer<G>>(a1, a2, a3, a4, a5);
	}
	
	public CompositeAlterer(final Alterer<G>... alterers) {
		super(1.0);
		nonNull(alterers, "Alterers array");
		for (Alterer<?> a : alterers) {
			nonNull(a, "Alterer");
		}
		
		_alterers = new Array<Alterer<G>>(alterers);
	}
	
	public CompositeAlterer(final Array<Alterer<G>> alterers) {
		super(1.0);
		nonNull(alterers, "Alterers");
		alterers.foreach(Validator.NonNull("Alterer"));
		
		_alterers = alterers.copy().seal();
	}

	@Override
	public <C extends Comparable<C>> void alter(
		final Population<G, C> population, 
		final int generation
	) {
		_alterers.foreach(new Predicate<Alterer<G>>() {
			@Override public boolean evaluate(final Alterer<G> alterer) {
				alterer.alter(population, generation);
				return true;
			}
		});
	}

	public Array<Alterer<G>> getAlterers() {
		return _alterers.seal();
	}
	
	public static <T extends Gene<?, T>> CompositeAlterer<T> join(
		final Alterer<T> a1,
		final Alterer<T> a2
	) {
		CompositeAlterer<T> alterer = null;
		
		if (a1 instanceof CompositeAlterer<?> && a2 instanceof CompositeAlterer<?>) {
			final CompositeAlterer<T> ca1 = (CompositeAlterer<T>)a1;
			final CompositeAlterer<T> ca2 = (CompositeAlterer<T>)a2;
			
			alterer = new CompositeAlterer<T>(ca1._alterers.append(ca2._alterers));
		} else if (a1 instanceof CompositeAlterer<?>) {
			final CompositeAlterer<T> ca1 = (CompositeAlterer<T>)a1;
			
			final Array<Alterer<T>> alterers = new Array<Alterer<T>>(ca1._alterers.length() + 1);
			for (int i = 0; i < ca1._alterers.length(); ++i) {
				alterers.set(i, ca1._alterers.get(i));
			}
			alterers.set(alterers.length() - 1, a2);
			
			alterer = new CompositeAlterer<T>(alterers);
		} else if (a2 instanceof CompositeAlterer<?>) {
			alterer = join(a2, a1);
		} else {
			alterer = new CompositeAlterer<T>(a1, a2);
		}
		
		return alterer;
	}
}








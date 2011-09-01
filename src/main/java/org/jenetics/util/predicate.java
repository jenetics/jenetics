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
 * 	 Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 * 	 
 */
package org.jenetics.util;

import static org.jenetics.util.object.nonNull;

/**
 * This class contains some short general purpose predicates, like {@code Nil},
 * {@code Not}, {@code And} and {@code Or}.
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public final class predicate {

	private predicate() {
		throw new AssertionError("Don't create an 'predicate' instance.");
	}
	
	/**
	 * A predicate which return {@code true} if an given value is {@code null}.
	 */
	public static final Predicate<Object> Null = new Predicate<Object>() {
		@Override public boolean evaluate(final Object object) {
			return object == null;
		}
		@Override public String toString() {
			return String.format("%s", getClass().getSimpleName());
		}
	};
	
	/**
	 * Return a predicate which negates the return value of the given predicate.
	 * 
	 * @param <T> the value type to check.
	 * @param a the predicate to negate.
	 * @return a predicate which negates the return value of the given predicate.
	 * @throws NullPointerException if the given predicate is {@code null}.
	 */
	public static <T> Predicate<T> Not(final Predicate<? super T> a) {
		return new Predicate<T>() {
			{nonNull(a);}
			@Override public boolean evaluate(final T object) {
				return !a.evaluate(object);
			}
			@Override public String toString() {
				return String.format("%s[%s]", getClass().getSimpleName(), a);
			}
		};
	}
	
	/**
	 * Return a {@code and} combination of the given predicates.
	 * 
	 * @param <T> the value type to check.
	 * @param a the first predicate
	 * @param b the second predicate
	 * @return a {@code and} combination of the given predicates.
	 * @throws NullPointerException if one of the given predicates is 
	 * 		  {@code null}.
	 */
	public static <T> Predicate<T> And(
		final Predicate<? super T> a, 
		final Predicate<? super T> b
	) {
		return new Predicate<T>() {
			{nonNull(a); nonNull(b);}
			@Override public boolean evaluate(final T object) {
				return a.evaluate(object) && b.evaluate(object);
			}
			@Override public String toString() {
				return String.format("%s[%s, %s]", getClass().getSimpleName(), a, b);
			}
		};
	}
	
	/**
	 * Return a {@code or} combination of the given predicates.
	 * 
	 * @param <T> the value type to check.
	 * @param a the first predicate
	 * @param b the second predicate
	 * @return a {@code and} combination of the given predicates.
	 * @throws NullPointerException if one of the given predicates is 
	 * 		  {@code null}.
	 */
	public static <T> Predicate<T> Or(
		final Predicate<? super T> a, 
		final Predicate<? super T> b
	) {
		return new Predicate<T>() {
			{nonNull(a); nonNull(b);}
			@Override public boolean evaluate(final T object) {
				return a.evaluate(object) || b.evaluate(object);
			}
			@Override public String toString() {
				return String.format("%s[%s, %s]", getClass().getSimpleName(), a, b);
			}
		};
	}
	
}







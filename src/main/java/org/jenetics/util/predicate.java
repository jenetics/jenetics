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
	 * This predicate return {@code true} if the given value is {@code null}.
	 * 
	 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
	 * @version $Id$
	 */
	public static class Nil implements Predicate<Object> {

		/**
		 * Return {@code true} if the given value is {@code null}.
		 * 
		 * @return {@code true} if the given value is {@code null}, {@code false}
		 * 		  otherwise.
		 */
		@Override
		public boolean evaluate(final Object object) {
			return object == null;
		}
		
		@Override
		public String toString() {
			return String.format("%s", getClass().getSimpleName());
		}
		
	}
	
	/**
	 * This predicate negates the value of its given, adapted predicate.
	 * 
	 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
	 * @version $Id$
	 */
	public static class Not<T> implements Predicate<T> {
		private final Predicate<? super T> _a;
		
		/**
		 * The predicate which will be negated.
		 * 
		 * @param a the predicate which will be negated.
		 * @throws NullPointerException if the given predicate is {@code null}.
		 */
		public Not(final Predicate<? super T> a) {
			_a = nonNull(a);
		}
		
		/**
		 * Negate the result of the adopted predicate.
		 */
		@Override
		public boolean evaluate(final T object) {
			return !_a.evaluate(object);
		}
		
		@Override
		public String toString() {
			return String.format("%s[%s]", getClass().getSimpleName(), _a);
		}
		
	}
	
	/**
	 * A logical {@code and} combination of two predicates.
	 * 
	 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
	 * @version $Id$
	 */
	public static class And<T> implements Predicate<T> {
		private final Predicate<? super T> _a;
		private final Predicate<? super T> _b;
		
		/**
		 * Create a new {@code and} combination of the two given predicates.
		 * 
		 * @param a the first predicate
		 * @param b the second predicate
		 * @throws NullPointerException if one of the predicates is {@code null}.
		 */
		public And(final Predicate<? super T> a, final Predicate<? super T> b) {
			_a = nonNull(a);
			_b = nonNull(b);
		}
		
		@Override
		public boolean evaluate(final T object) {
			return _a.evaluate(object) && _b.evaluate(object);
		}
		
		@Override
		public String toString() {
			return String.format("%s[%s, %s]", getClass().getSimpleName(), _a, _b);
		}
	}
	
	/**
	 * A logical {@code or} combination of two predicates.
	 * 
	 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
	 * @version $Id$
	 */
	public static class Or<T> implements Predicate<T> {
		private final Predicate<? super T> _a;
		private final Predicate<? super T> _b;
		
		/**
		 * Create a new {@code or} combination of the two given predicates.
		 * 
		 * @param a the first predicate
		 * @param b the second predicate
		 * @throws NullPointerException if one of the predicates is {@code null}.
		 */
		public Or(final Predicate<? super T> a, final Predicate<? super T> b) {
			_a = nonNull(a);
			_b = nonNull(b);
		}
		
		@Override
		public boolean evaluate(final T object) {
			return _a.evaluate(object) || _b.evaluate(object);
		}
		
		@Override
		public String toString() {
			return String.format("%s[%s, %s]", getClass().getSimpleName(), _a, _b);
		}
	}
	
	/**
	 * Return a predicate which return {@code true} if an given value is 
	 * {@code null}.
	 * 
	 * @return a predicate which return {@code true} if an given value is 
	 * 		  {@code null}.
	 */
	public static Predicate<Object> nil() {
		return new Nil();
	}
	
	/**
	 * Return a predicate which negates the return value of the given predicate.
	 * 
	 * @param <T> the value type to check.
	 * @param a the predicate to negate.
	 * @return a predicate which negates the return value of the given predicate.
	 * @throws NullPointerException if the given predicate is {@code null}.
	 */
	public static <T> Predicate<T> not(final Predicate<? super T> a) {
		return new Not<T>(a);
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
	public static <T> Predicate<T> and(
		final Predicate<? super T> a, 
		final Predicate<? super T> b
	) {
		return new And<T>(a, b);
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
	public static <T> Predicate<T> or(
		final Predicate<? super T> a, 
		final Predicate<? super T> b
	) {
		return new Or<T>(a, b);
	}
	
}







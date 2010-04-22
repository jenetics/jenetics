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

/**
 * This {@code enum} determines whether the GA should maximize or minimize the 
 * fitness function.
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public enum Optimize {
	
	/**
	 * GA minimization
	 */
	MINIMUM(new Min()),
	
	/**
	 * GA maximization
	 */
	MAXIMUM(new Max());
	
	private final Comp _comparator;
	
	private Optimize(final Comp comparator) {
		_comparator = comparator;
	}
	
	/**
	 * Compares two comparable objects. Returns a negative integer, zero, or a 
	 * positive integer as the first argument is better than, equal to, or worse 
	 * than the second.
	 * 
	 * @param <T> the comparable type
	 * @param o1 the first object to be compared.
	 * @param o2 the second object to be compared.
	 * @return a negative integer, zero, or a positive integer as the first 
	 *         argument is better than, equal to, or worse than the second.
	 * @throws NullPointerException if one of the arguments is {@code null}.
	 */
	public <T extends Comparable<T>> int compare(final T o1, final T o2) {
		return _comparator.compare(o1, o2);
	}
	
	/**
	 * Return the best phenotype, according to this optimization direction.
	 * 
	 * @param <G> the gene type.
	 * @param <C> the fitness value type.
	 * @param a the first phenotype.
	 * @param b the second phenotype.
	 * @return the best phenotype. If both phenotype are equal the first one is
	 *         returned.
	 */
	public <G extends Gene<?, G>, C extends Comparable<C>>
	Phenotype<G, C> best(final Phenotype<G, C> a, final Phenotype<G, C> b) {
		Phenotype<G, C> best = a;
		if (compare(b.getFitness(), best.getFitness()) > 0) {
			best = b;
		}
		return best;
	}
	
	/**
	 * Return the worst phenotype, according to this optimization direction.
	 * 
	 * @param <G> the gene type.
	 * @param <C> the fitness value type.
	 * @param a the first phenotype.
	 * @param b the second phenotype.
	 * @return the best phenotype. If both phenotype are equal the first one is
	 *         returned.
	 */
	public <G extends Gene<?, G>, C extends Comparable<C>>
	Phenotype<G, C> worst(final Phenotype<G, C> a, final Phenotype<G, C> b) {
		Phenotype<G, C> worst = a;
		if (compare(b.getFitness(), worst.getFitness()) < 0) {
			worst = b;
		}
		return worst;
	}
	
	/**
	 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
	 * @version $Id$
	 */
	private static interface Comp {
		public <T extends Comparable<T>> int compare(final T o1, final T o2);
	}
	
	/**
	 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
	 * @version $Id$
	 */
	private static final class Min implements Comp {
		@Override
		public <T extends Comparable<T>> int compare(final T o1, final T o2) {
			return -o1.compareTo(o2);
		}
	}
	
	/**
	 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
	 * @version $Id$
	 */
	private static final class Max implements Comp {
		@Override
		public <T extends Comparable<T>> int compare(final T o1, final T o2) {
			return o1.compareTo(o2);
		}
	}
	
}

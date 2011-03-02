package org.jenetics.util;

import javolution.lang.Immutable;


/**
 * The immutable part of a sequence.
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public interface ImmutableSeq<T> 
	extends 
		Seq<T>,
		Copyable<MutableSeq<T>>,
		Immutable 
{
			
	/**
	 * <p>
	 * The {@code upcast} method returns an array of type {@code Array<? super T>} 
	 * instead of {@code Array<T>}. This allows you to assign this array to an 
	 * array where the element type is a super type of {@code T}.
	 * </p>
	 * [code]
	 *     Sequence.Immutable<Double> da = new Array<Double>(Arrays.asList(0.0, 1.0, 2.0)).seal();
	 *     Sequence.Immutable<Number> na = da.upcast(da);
	 *     Sequence.Immutable<Object>; oa = na.upcast(na);
	 *     oa = da.upcast(da);
	 * [/code]
	 * 
	 * This array must be {@code sealed} for an save <em>up-cast</em>, otherwise an 
	 * {@link UnsupportedOperationException} will be thrown. 
	 * 
	 * @return the up-casted array.
	 * @throws UnsupportedOperationException if this array is not {@code sealed}.
	 */
	public <A> ImmutableSeq<A> upcast(final ImmutableSeq<? extends A> seq);
	
}
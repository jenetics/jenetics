/*
 * Java Genetic Algorithm Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
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

import javolution.lang.Immutable;

/**
 * Immutable, ordered, fixed sized sequence.
 *
 * @see MSeq
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 1.0 &mdash; <em>$Date: 2013-03-01 $</em>
 */
public interface ISeq<T>
	extends
		Seq<T>,
		Copyable<MSeq<T>>,
		Immutable
{

	@Override
	public ISeq<T> subSeq(final int start, final int end);

	@Override
	public ISeq<T> subSeq(final int start);

	@Override
	public <B> ISeq<B> map(final Function<? super T, ? extends B> mapper);

	/**
	 * <p>
	 * Helper method for up-casting an given immutable sequence. This allows you
	 * to assign this sequence to an sequence where the element type is a super
	 * type of {@code T}.
	 * </p>
	 * [code]
	 * ISeq<Double> da = new Array<Double>(0.0, 1.0, 2.0).toISeq();
	 * ISeq<Number> na = da.upcast(da);
	 * ISeq<Object> oa = na.upcast(na);
	 * [/code]
	 *
	 * @param seq the sequence to cast.
	 * @return the up-casted sequence.
	 *
	 * @deprecated Will be removed in the next version.
	 */
	@Deprecated
	public <A> ISeq<A> upcast(final ISeq<? extends A> seq);

	/**
	 * Return a shallow copy of this sequence. The sequence elements are not
	 * cloned.
	 *
	 * @return a shallow copy of this sequence.
	 */
	@Override
	public MSeq<T> copy();

}






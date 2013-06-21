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
 *     Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 *
 */
package org.jenetics;

import static java.util.Objects.requireNonNull;

import java.util.AbstractList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.RandomAccess;

import org.jenetics.util.Function;
import org.jenetics.util.ISeq;
import org.jenetics.util.MSeq;
import org.jenetics.util.bit;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since @__new_version__@
 * @version @__new_version__@ &mdash; <em>$Date: 2013-06-21 $</em>
 */
final class BitGeneSeq implements ISeq<BitGene> {

	private final byte[] _genes;
	private final int _length;

	BitGeneSeq(final byte[] genes, final int length) {
		_genes = genes;
		_length = length;
	}

	@Override
	public BitGene get(int index) {
		return BitGene.valueOf(bit.get(_genes, index));
	}

	@Override
	public Iterator<BitGene> iterator() {
		return new BitGeneSeqIterator(_genes, _length);
	}

	@Override
	public <B> Iterator<B> iterator(
		final Function<? super BitGene, ? extends B> mapper
	) {
		requireNonNull(mapper, "Maooer");

		return new Iterator<B>() {
			private final Iterator<BitGene> _iterator = iterator();
			@Override public boolean hasNext() {
				return _iterator.hasNext();
			}
			@Override public B next() {
				return mapper.apply(_iterator.next());
			}
			@Override public void remove() {
				_iterator.remove();
			}
		};
	}

	@Override
	@Deprecated
	public <R> void foreach(final Function<? super BitGene, ? extends R> function) {
		forEach(function);
	}

	@Override
	public <R> void forEach(final Function<? super BitGene, ? extends R> function) {
		requireNonNull(function, "Function");

		for (int i = 0; i < _length; ++i) {
			function.apply(BitGene.valueOf(bit.get(_genes, i)));
		}
	}

	@Override
	@Deprecated
	public boolean forall(final Function<? super BitGene, Boolean> predicate) {
		return forAll(predicate);
	}

	@Override
	public boolean forAll(final Function<? super BitGene, Boolean> predicate) {
		requireNonNull(predicate, "Predicate");

		boolean valid = true;
		for (int i = 0; i < _length && valid; ++i) {
			valid = predicate.apply(BitGene.valueOf(bit.get(_genes, i)));
		}
		return valid;
	}

	@Override
	public boolean contains(final Object element) {
		return indexOf(element) != -1;
	}

	@Override
	public int indexOf(final Object element) {
		return indexOf(element, 0, length());
	}

	@Override
	public int indexOf(final Object element, final int start) {
		return indexOf(element, start, _length);
	}

	@Override
	public int indexOf(final Object element, final int start, final int end) {
		int index = -1;

		if (element instanceof BitGene) {
			final boolean gene = ((BitGene)element).booleanValue();
			for (int i = start; i < end && index == -1; ++i) {
				if (bit.get(_genes, i) == gene) {
					index = i;
				}
			}
		}

		return index;
	}

	@Override
	public int indexWhere(final Function<? super BitGene, Boolean> predicate) {
		return indexWhere(predicate, 0, _length);
	}

	@Override
	public int indexWhere(
		final Function<? super BitGene, Boolean> predicate,
		final int start
	) {
		return indexWhere(predicate, start, _length);
	}

	@Override
	public int indexWhere(
		final Function<? super BitGene, Boolean> predicate,
		final int start,
		final int end
	) {
		requireNonNull(predicate, "Predicate");

		int index = -1;

		for (int i = start, n = end; i < n && index == -1; ++i) {
			if (predicate.apply(BitGene.valueOf(bit.get(_genes, i)))) {
				index = i;
			}
		}

		return index;
	}

	@Override
	public int lastIndexOf(final Object element) {
		return lastIndexOf(element, 0, _length);
	}

	@Override
	public int lastIndexOf(final Object element, final int end) {
		return lastIndexOf(element, 0, end);
	}

	@Override
	public int lastIndexOf(final Object element, final int start, final int end) {
		int index = -1;

		if (element instanceof BitGene) {
			final boolean gene = ((BitGene)element).booleanValue();
			for (int i = end; --i >= start && index == -1;) {
				if (bit.get(_genes, i) == gene) {
					index = i;
				}
			}
		}

		return index;
	}

	@Override
	public int lastIndexWhere(final Function<? super BitGene, Boolean> predicate) {
		return lastIndexWhere(predicate, 0, _length);
	}

	@Override
	public int lastIndexWhere(
		final Function<? super BitGene, Boolean> predicate,
		final int end
	) {
		return lastIndexWhere(predicate, 0, end);
	}

	@Override
	public int lastIndexWhere(
		final Function<? super BitGene, Boolean> predicate,
		final int start,
		final int end
	) {
		requireNonNull(predicate, "Predicate");

		int index = -1;

		for (int i = end; --i >= 0 && index == -1;) {
			if (predicate.apply(BitGene.valueOf(bit.get(_genes, i)))) {
				index = i;
			}
		}

		return index;
	}

	@Override
	public ISeq<BitGene> subSeq(int start, int end) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ISeq<BitGene> subSeq(int start) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <B> ISeq<B> map(Function<? super BitGene, ? extends B> mapper) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Deprecated
	public <A> ISeq<A> upcast(ISeq<? extends A> seq) {
		throw new UnsupportedOperationException();
	}

	@Override
	public MSeq<BitGene> copy() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int length() {
		return _length;
	}

	@Override
	public List<BitGene> asList() {
		return new BitGeneSeqList(_genes, _length);
	}

	@Override
	public Object[] toArray() {
		final Object[] array = new Object[_length];
		for (int i = 0; i < _length; ++i) {
			array[i] = BitGene.valueOf(bit.get(_genes, i));
		}
		return array;
	}

	@Override
	public BitGene[] toArray(final BitGene[] array) {
		if (array.length < _length) {
			final BitGene[] genes = new BitGene[_length];
			for (int i = 0; i < _length; ++i) {
				genes[i] = BitGene.valueOf(bit.get(_genes, i));
			}
			return genes;
		} else {
			for (int i = 0; i < _length; ++i) {
				array[i] = BitGene.valueOf(bit.get(_genes, i));
			}
			return array;
		}
	}

	@Override
	public String toString(
		final String prefix,
		final String separator,
		final String suffix
	) {
		  final StringBuilder out = new StringBuilder();

		  out.append(prefix);
		  if (_length > 0) {
			out.append(Boolean.toString(bit.get(_genes, 0)));
		  }
		  for (int i = 1; i < _length; ++i) {
			out.append(separator);
			out.append(Boolean.toString(bit.get(_genes, i)));
		  }
		  out.append(suffix);

		  return out.toString();
	}

	@Override
	public String toString(String separator) {
		return toString("", separator, "");
	}

	@Override
	public String toString() {
		  return toString("[", ",", "]");
	}

}

/**
 * Helper class which iterates over an given bit array.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since @__new_version__@
 * @version @__new_version__@ &mdash; <em>$Date: 2013-06-21 $</em>
 */
final class BitGeneSeqIterator implements ListIterator<BitGene> {

	private final byte[] _genes;
	private final int _length;

	private int _pos = -1;

	BitGeneSeqIterator(final byte[] genes, final int length) {
		_genes = genes;
		_length = length;
	}

	@Override
	public boolean hasNext() {
		return _pos < _length - 1;
	}

	@Override
	public BitGene next() {
		if (!hasNext()) {
			throw new NoSuchElementException();
		}
		return BitGene.valueOf(bit.get(_genes, ++_pos));
	}

	@Override
	public int nextIndex() {
		return _pos + 1;
	}

	@Override
	public boolean hasPrevious() {
		return _pos > 0;
	}

	@Override
	public BitGene previous() {
		if (!hasPrevious()) {
			throw new NoSuchElementException();
		}
		return BitGene.valueOf(bit.get(_genes, --_pos));
	}

	@Override
	public int previousIndex() {
		return _pos - 1;
	}

	@Override
	public void set(final BitGene value) {
		throw new UnsupportedOperationException("BitChromosome is immutable.");
	}

	@Override
	public void add(final BitGene o) {
		throw new UnsupportedOperationException("Can't change Chromosome size.");
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException("Can't change Chromosome size.");
	}

}

class BitGeneSeqList extends AbstractList<BitGene>
	implements RandomAccess
{
	private final byte[] _genes;
	private final int _length;

	BitGeneSeqList(final byte[] genes, final int length) {
		_genes = genes;
		_length = length;
	}

	@Override
	public BitGene get(final int index) {
		return BitGene.valueOf(bit.get(_genes, index));
	}

	@Override
	public int size() {
		return _length;
	}

	@Override
	public int indexOf(final Object element) {
		int index = -1;

		if (element instanceof BitGene) {
			final boolean gene = ((BitGene)element).booleanValue();
			for (int i = 0; i < _length && index == -1; ++i) {
				if (bit.get(_genes, i) == gene) {
					index = i;
				}
			}
		}

		return index;
	}

	@Override
	public boolean contains(final Object element) {
		return indexOf(element) != -1;
	}

	@Override
	public Object[] toArray() {
		final Object[] array = new Object[_length];
		for (int i = 0; i < _length; ++i) {
			array[i] = BitGene.valueOf(bit.get(_genes, i));
		}
		return array;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <E> E[] toArray(final E[] array) {
		if (array.length < _length) {
			final E[] copy = (E[])java.lang.reflect.Array.newInstance(
				array.getClass().getComponentType(), _length
			);
			for (int i = 0; i < _length; ++i) {
				copy[i] = (E)BitGene.valueOf(bit.get(_genes, i));
			}

			return copy;
		}

		for (int i = 0; i < _length; ++i) {
			array[i] = (E)BitGene.valueOf(bit.get(_genes, i));
		}
		return array;
	}

}



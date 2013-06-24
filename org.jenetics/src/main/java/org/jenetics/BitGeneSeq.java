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

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

import java.util.AbstractList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.RandomAccess;

import org.jenetics.util.Array;
import org.jenetics.util.Factory;
import org.jenetics.util.Function;
import org.jenetics.util.Seq;
import org.jenetics.util.ISeq;
import org.jenetics.util.MSeq;
import org.jenetics.util.bit;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since @__new_version__@
 * @version @__new_version__@ &mdash; <em>$Date: 2013-06-24 $</em>
 */
abstract class BitGeneSeq implements Seq<BitGene> {

	final BitGeneArrayRef _genes;
	final int _start;
	final int _end;
	final int _length;

	BitGeneSeq(final BitGeneArrayRef genes, final int start, final int end) {
		_genes = genes;
		_start = start;
		_end = end;
		_length = _end - _start;
	}

	final void checkIndex(final int index) {
		if (index < 0 || index >= _length) {
			throw new ArrayIndexOutOfBoundsException(format(
				"Index %s is out of bounds [0, %s)", index, length()
			));
		}
	}

	final void checkIndex(final int from, final int to) {
		if (from > to) {
			throw new ArrayIndexOutOfBoundsException(
				"fromIndex(" + from + ") > toIndex(" + to+ ")"
			);
		}
		if (from < 0 || to > _length) {
			throw new ArrayIndexOutOfBoundsException(format(
				"Invalid index range: [%d, %s)", from, to
			));
		}
	}

	@Override
	public final BitGene get(final int index) {
		checkIndex(index);
		return BitGene.valueOf(bit.get(_genes.data, index + _start));
	}

	final BitGene uncheckedGet(final int index) {
		return BitGene.valueOf(bit.get(_genes.data, index + _start));
	}

	final BitGene uncheckedOffsetGet(final int absoluteIndex) {
		return BitGene.valueOf(bit.get(_genes.data, absoluteIndex));
	}

	@Override
	public Iterator<BitGene> iterator() {
		return new BitGeneSeqIterator(this);
	}

	public ListIterator<BitGene> listIterator() {
		return new BitGeneSeqIterator(this);
	}

	@Override
	public <B> Iterator<B> iterator(
		final Function<? super BitGene, ? extends B> mapper
	) {
		requireNonNull(mapper, "Mapper must not be null");

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

		for (int i = _start; i < _end; ++i) {
			function.apply(uncheckedGet(i));
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
		for (int i = _start; i < _end && valid; ++i) {
			valid = predicate.apply(uncheckedGet(i));
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
		checkIndex(start, end);

		int index = -1;
		if (element instanceof BitGene) {
			for (int i = start; i < end && index == -1; ++i) {
				if (uncheckedOffsetGet(i) == element) {
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
		checkIndex(start, end);
		requireNonNull(predicate, "Predicate");

		int index = -1;

		for (int i = start; i < end && index == -1; ++i) {
			if (predicate.apply(uncheckedOffsetGet(i))) {
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
		checkIndex(start, end);
		int index = -1;

		if (element instanceof BitGene) {
			for (int i = end; --i >= start && index == -1;) {
				if (uncheckedOffsetGet(i) == element) {
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
		checkIndex(start, end);
		requireNonNull(predicate, "Predicate");

		int index = -1;

		for (int i = end; --i >= start && index == -1;) {
			if (predicate.apply(uncheckedOffsetGet(i))) {
				index = i;
			}
		}

		return index;
	}

	@Override
	public int length() {
		return _length;
	}

	@Override
	public List<BitGene> asList() {
		return new BitGeneSeqList(this);
	}

	@Override
	public Object[] toArray() {
		final Object[] array = new Object[length()];
		for (int i = length(); --i >= 0;) {
			array[i] = uncheckedGet(i);
		}
		return array;
	}

	@Override
	public BitGene[] toArray(final BitGene[] array) {
		if (array.length < length()) {
			final BitGene[] copy = new BitGene[length()];
			for (int i = length(); --i >= 0;) {
				copy[i] = uncheckedGet(i);
			}

			return copy;
		}

		for (int i = length(); --i >= 0;) {
			array[i] = uncheckedGet(i);
		}
		return array;
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
			out.append(Boolean.toString(bit.get(_genes.data, 0)));
		  }
		  for (int i = 1; i < _length; ++i) {
			out.append(separator);
			out.append(Boolean.toString(bit.get(_genes.data, i)));
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
 * @version @__new_version__@ &mdash; <em>$Date: 2013-06-24 $</em>
 */
final class BitGeneSeqIterator implements ListIterator<BitGene> {

	final BitGeneSeq _seq;

	private int _pos = -1;

	BitGeneSeqIterator(final BitGeneSeq seq) {
		_seq = seq;
	}

	@Override
	public boolean hasNext() {
		return _pos < _seq.length() - 1;
	}

	@Override
	public BitGene next() {
		if (!hasNext()) {
			throw new NoSuchElementException();
		}
		return _seq.uncheckedGet(++_pos);
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
		return _seq.uncheckedGet(--_pos);
	}

	@Override
	public int previousIndex() {
		return _pos - 1;
	}

	@Override
	public void set(final BitGene value) {
		throw new UnsupportedOperationException(
			"BitChromosome is immutable."
		);
	}

	@Override
	public void add(final BitGene o) {
		throw new UnsupportedOperationException(
			"Can't change Chromosome size."
		);
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException(
			"Can't change Chromosome size."
		);
	}

}

class BitGeneISeq extends BitGeneSeq implements ISeq<BitGene> {

	BitGeneISeq(final BitGeneArrayRef genes, final int start, final int end) {
		super(genes, start, end);
	}

	@Override
	public ISeq<BitGene> subSeq(int start, int end) {
		return new BitGeneISeq(_genes, start + _start, end + _start);
	}

	@Override
	public ISeq<BitGene> subSeq(int start) {
		return new BitGeneISeq(_genes, start + _start, _end);
	}

	@Override
	public <B> ISeq<B> map(Function<? super BitGene, ? extends B> mapper) {
		final Array<B> array = new Array<>(length());
		for (int i = 0; i < _length; ++i){
			array.set(i, mapper.apply(uncheckedGet(i)));
		}
		return array.toISeq();
	}

	@Override
	@Deprecated
	public <A> ISeq<A> upcast(ISeq<? extends A> seq) {
		throw new UnsupportedOperationException();
	}

	@Override
	public MSeq<BitGene> copy() {
		return null;
	}

}

class BitGeneMSeq extends BitGeneSeq implements MSeq<BitGene> {

	BitGeneMSeq(final BitGeneArrayRef genes, final int start, final int end) {
		super(genes, start, end);
	}

	@Override
	public MSeq<BitGene> subSeq(int start, int end) {
		return new BitGeneMSeq(_genes, start + _start, end + _start);
	}

	@Override
	public MSeq<BitGene> subSeq(int start) {
		return new BitGeneMSeq(_genes, start + _start, _end);
	}

	@Override
	public MSeq<BitGene> copy() {
		return null;
	}

	@Override
	public void set(int index, BitGene value) {

	}

	@Override
	public MSeq<BitGene> setAll(BitGene value) {
		return null;
	}

	@Override
	public MSeq<BitGene> setAll(Iterator<? extends BitGene> it) {
		return null;
	}

	@Override
	public MSeq<BitGene> setAll(Iterable<? extends BitGene> values) {
		return null;
	}

	@Override
	public MSeq<BitGene> setAll(BitGene[] values) {
		return null;
	}

	@Override
	public MSeq<BitGene> fill(Factory<? extends BitGene> factory) {
		return null;
	}

	@Override
	public void swap(int i, int j) {

	}

	@Override
	public void swap(int start, int end, MSeq<BitGene> other, int otherStart) {

	}


	@Override
	public <B> MSeq<B> map(Function<? super BitGene, ? extends B> mapper) {
		return null;
	}

	@Override
	public ISeq<BitGene> toISeq() {
		return null;
	}

}



class BitGeneSeqList extends AbstractList<BitGene>
	implements RandomAccess
{
	private final BitGeneSeq _seq;

	BitGeneSeqList(final BitGeneSeq seq) {
		_seq = seq;
	}

	@Override
	public BitGene get(final int index) {
		return _seq.get(index);
	}

	@Override
	public int size() {
		return _seq.length();
	}

	@Override
	public int indexOf(final Object element) {
		return _seq.indexOf(element);
	}

	@Override
	public boolean contains(final Object element) {
		return _seq.contains(element);
	}

	@Override
	public Object[] toArray() {
		final Object[] array = new Object[size()];
		for (int i = size(); --i >= 0;) {
			array[i] = _seq.uncheckedGet(i);
		}
		return array;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <E> E[] toArray(final E[] array) {
		if (array.length < size()) {
			final E[] copy = (E[])java.lang.reflect.Array.newInstance(
				array.getClass().getComponentType(), size()
			);
			for (int i = size(); --i >= 0;) {
				copy[i] = (E)_seq.uncheckedGet(i);
			}

			return copy;
		}

		for (int i = size(); --i >= 0;) {
			array[i] = (E)_seq.uncheckedGet(i);
		}
		return array;
	}

}

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since @__new_version__@
 * @version @__new_version__@ &mdash; <em>$Date: 2013-06-24 $</em>
 */
final class BitGeneArrayRef implements Cloneable {
	byte[] data;
	final int length;

	private boolean _sealed = false;

	BitGeneArrayRef(final byte[] data, final int length) {
		this.data = data;
		this.length = length;
	}

	BitGeneArrayRef(final int length) {
		this(new byte[toByteLength(length)], length);
	}

	private static int toByteLength(final int length) {
		return (length & 7) == 0 ? (length >>> 3) : (length >>> 3) + 1;
	}

	final void cloneIfSealed() {
		if (_sealed) {
			data = data.clone();
			_sealed = false;
		}
	}

	final BitGeneArrayRef seal() {
		_sealed = true;
		return this;
	}

}



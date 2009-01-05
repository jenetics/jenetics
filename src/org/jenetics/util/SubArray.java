package org.jenetics.util;

import java.util.ListIterator;

class SubArray<T> extends Array<T> {
	int _start;
	int _end;
	
	SubArray(final Object[] array, final int start, final int end, final boolean sealed) {
		_start = start;
		_end = end;
		_array = array;
		_sealed = sealed;
	}
	
	@Override
	public void set(int index, T value) {
		if (_sealed) {
			throw new UnsupportedOperationException("Array is sealed");
		}
		if (index >= _end) {
			throw new IndexOutOfBoundsException(String.format(
				"Index %s is out of bounds [%s, %s).", index, 0, (_end - _start)
			));
		}
		
		_array[index + _start] = value;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public T get(int index) {
		if (index >= _end) {
			throw new IndexOutOfBoundsException(String.format(
				"Index %s is out of bounds [%s, %s).", index, 0, (_end - _start)
			));
		}
		
		return (T)_array[index + _start];
	}
	
	@Override
	public int indexOf(Object element) {
		int index = ArrayUtils.indexOf(_array, _start, _end, element);
		if (index != -1) {
			index -= _start;
		}
		return index;
	}
	
	@Override
	public boolean contains(Object element) {
		return ArrayUtils.indexOf(_array, _start, element) != -1;
	}
	
	@Override
	public Array<T> seal() {
		_sealed = true;
		return this;
	}
	
	@Override
	public boolean isSealed() {
		return _sealed;
	}
	
	@Override
	public void clear() {
		if (_sealed) {
			throw new UnsupportedOperationException("Array is sealed.");
		}
		for (int i = _start; i < _end; ++i) {
			_array[i] = null;
		}
	}
	
	@Override
	public int length() {
		return _end - _start;
	}

	@Override
	public ListIterator<T> iterator() {
		return new ArrayIterator<T>(_array, _start, _end, _sealed);
	}
	
	@Override
	public Array<T> copy() {
		return copy(0, length());
	}

	@Override
	public Array<T> copy(int start) {
		return copy(start, length());
	}
	
	@Override
	public Array<T> copy(int start, int end) {
		if (start < 0 || end > length() || start > end) {
			throw new IndexOutOfBoundsException(String.format(
				"Invalid index range: [%d, %s]", start, end
			));
		}
		
		final Array<T> array = Array.newInstance(end - start);
		System.arraycopy(_array, start + _start, array._array, 0, end - start);
		return array;
	}

	public Array<T> subArray(final int start, final int end) {
		if (start < 0 || end > length() || start > end) {
			throw new IndexOutOfBoundsException(String.format(
				"Invalid index range: [%d, %s]", start, end
			));
		}
		
		return new SubArray<T>(_array, start + _start, end + _start, _sealed);
	}
	
	@Override
	public int hashCode() {
		int code = 17;
		for (int i = _start; i < _end; ++i) {
			final Object element = _array[i];
			if (element != null) {
				code += 37*element.hashCode() + 17;
			} else {
				code += 3;
			}
		}
		return code;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof Array)) {
			return false;
		}
		
		final Array<?> array = (Array<?>)obj;
		boolean equals = (length() == array.length());
		for (int i = _start; equals && i < _end; ++i) {
			if (_array[i] != null) {
				equals = _array[i].equals(array._array[i]);
			} else {
				equals = array._array[i] == null;
			}
		}
		return equals;
	}

	@Override
	public String toString() {
		return super.toString();
	}

}

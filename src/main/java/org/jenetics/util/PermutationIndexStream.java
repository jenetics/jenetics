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
package org.jenetics.util;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
class PermutationIndexStream implements IndexStream {
	
	private final int _length;
	private final int _stride;
	
	private int _start;
	private int _pos = 0;
	private int _calls = 0;
	
	
	public PermutationIndexStream(final int length) {
		this(length, prime(length), prime(length));
	}
	
	public PermutationIndexStream(
		final int length, 
		final int start, 
		final int stride
	) {
		if (length < 1) {
			throw new IllegalArgumentException();
		}
		if (start < 0) {
			throw new IllegalArgumentException();
		}
		if (stride < 1 || stride == length) {
			throw new IllegalArgumentException();
		}
		
		_length = length;
		_start = start%length;
		_pos = _start;
		_stride = stride;
	}
	
	private static int prime(final int length) {
		final Random random = new Random();
		int value = length;
		
		while (value == length) {
			value = BigInteger.probablePrime(31, random).intValue();
		}	
		
		return value;
	}
	
	@Override
	public int next() {
		int next = -1;
		
		if (_calls < _length) {
			if (_pos >= _length) {
				_start = (_start + 1)%_stride;
				_pos = _start;
			}

			next = _pos;
			_pos = (_pos + _stride)%_length;
			++_calls;
		}
		
		return next;
	}
	
	@Override
	public String toString() {
		return String.format(
				"Length: %d, start: %d, stride: %d", _length, _start, _stride
			);
	}
	
	
	public static void main(String[] args) {
		final int N = 1000000;
		final Set<Integer> values = new HashSet<Integer>(N);
		
		final IndexStream stream = new PermutationIndexStream(N);
//		final IndexStream stream = new PermutationIndexStream(N, N - 1, N - 1);
		
		int count = 0;
		for (int i = stream.next(); i != -1; i = stream.next()) {
			//System.out.println((count++) + " --> " + i);
			if (values.contains(i)) {
				System.out.println("double: " + i);
			}
			values.add(i);
		}
		
		System.out.println(values.size());
		for (int i = 0; i < N; ++i) {
			if (!values.contains(i)) {
				System.out.println("missing: " + i);
			}
		}
		
		System.out.println(stream);
	}

}


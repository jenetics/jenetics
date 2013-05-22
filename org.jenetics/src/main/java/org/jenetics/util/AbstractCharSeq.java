/*
 * Java Genetic Algorithm Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Author:
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 */
package org.jenetics.util;


/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 1.0 &mdash; <em>$Date: 2013-04-27 $</em>
 */
abstract class AbstractCharSeq extends ArrayISeq<Character> {
	private static final long serialVersionUID = 1L;

	final char[] _characters;

	AbstractCharSeq(final char[] characters) {
		super(toArrayRef(characters), 0, characters.length);
		_characters = characters;
	}

	private static ArrayRef toArrayRef(final char[] characters) {
		final Object[] values = new Object[characters.length];
		for (int i = 0; i < characters.length; ++i) {
			values[i] = characters[i];
		}
		final ArrayRef ref = new ArrayRef(values);
		ref._sealed = true;

		return ref;
	}


}

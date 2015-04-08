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
package org.jenetics.diagram;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.io.UncheckedIOException;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public class UncheckedAppendable implements Appendable {

	private final Appendable _out;

	public UncheckedAppendable(final Appendable out) {
		_out = requireNonNull(out);
	}

	@Override
	public UncheckedAppendable append(final CharSequence csq) {
		try {
			return new UncheckedAppendable(_out.append(csq));
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	@Override
	public UncheckedAppendable append(
		final CharSequence csq,
		final int start,
		final int end
	) {
		try {
			return new UncheckedAppendable(_out.append(csq, start, end));
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	@Override
	public UncheckedAppendable append(char c) {
		try {
			return new UncheckedAppendable(_out.append(c));
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
}

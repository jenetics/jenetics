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
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmail.com)
 */
package io.jenetics.incubator.csv;

import static java.util.Objects.requireNonNull;

/**
 * Combines the column values with the convert used for converting the strings
 * into concrete data types.
 *
 * @param columns the underlying column values of the row
 * @param converter the used column value converter
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 8.2
 * @since 8.2
 */
record ColumnsRow(String[] columns, Converter converter) implements Row {

	public ColumnsRow {
		requireNonNull(columns);
		requireNonNull(converter);
	}

	/**
	 * Wraps the given column values with the {@link Converter#DEFAULT} converter.
	 *
	 * @param columns the row values
	 */
	public ColumnsRow(String[] columns) {
		this(columns, Converter.DEFAULT);
	}

	@Override
	public int size() {
		return columns.length;
	}

	@Override
	public boolean isEmptyAt(final int index) {
		return columns[index] == null || columns[index].isEmpty();
	}

	@Override
	public String stringAt(final int index) {
		return columns[index];
	}

	@Override
	public byte byteAt(final int index, final byte defaultValue) {
		return isEmptyAt(index)
			? defaultValue
			: requireNonNull(converter.convert(columns[index], Byte.class));
	}

	@Override
	public short shortAt(final int index, final short defaultValue) {
		return isEmptyAt(index)
			? defaultValue
			: requireNonNull(converter.convert(columns[index], Short.class));
	}

	@Override
	public int intAt(final int index, final int defaultValue) {
		return isEmptyAt(index)
			? defaultValue
			: requireNonNull(converter.convert(columns[index], Integer.class));
	}

	@Override
	public long longAt(final int index, final long defaultValue) {
		return isEmptyAt(index)
			? defaultValue
			: requireNonNull(converter.convert(columns[index], Long.class));
	}

	@Override
	public float floatAt(final int index, final float defaultValue) {
		return isEmptyAt(index)
			? defaultValue
			: requireNonNull(converter.convert(columns[index], Float.class));
	}

	@Override
	public double doubleAt(final int index, final double defaultValue) {
		return isEmptyAt(index)
			? defaultValue
			: requireNonNull(converter.convert(columns[index], Double.class));
	}

	@Override
	public <T> T objectAt(final int index, final Class<T> type) {
		requireNonNull(type);
		if (isEmptyAt(index)) {
			return null;
		}

		return converter.convert(columns[index], type);
	}

}

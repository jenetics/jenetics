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
package org.jenetics.internal.util;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 3.0
 * @version 3.0 &mdash; <em>$Date: 2014-10-03 $</em>
 */
public final class Table {

	public static final class Row {
		private final List<Data> _data = new ArrayList<>();

		public Row(final String... data) {
			for (String datum : data) {
				_data.add(new Data(datum));
			}
		}
	}

	public static class Data {
		private final String _content;

		public Data(final String content) {
			_content = content;
		}
	}

	public static final class Header extends Data {
		public Header(final String content) {
			super(content);
		}
	}


	private final List<Row> _rows = new ArrayList<>();


	public Table header(final String... header) {
		_rows.add(new Row(header));
		return this;
	}

	public Table row(final String... row) {
		return this;
	}









	public static void main(final String[] args) {
		final Table table = new Table()
			.header("Speed", "Entry", "Foo")
			.row("23", "32", "33");
	}


}

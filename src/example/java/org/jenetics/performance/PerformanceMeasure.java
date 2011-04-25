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
package org.jenetics.performance;

import java.io.IOException;

import org.jenetics.util.Timer;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public class PerformanceMeasure {

	private final String _group;
	private final Timer[] _timers;
	
	public PerformanceMeasure(final String group, final Timer... timers) {
		_group = group;
		_timers = timers;
	}
	
	@Override
	public String toString() {
		final StringBuilder out = new StringBuilder();
		//"+------------------------------------------------------------------------------+"
		return out.toString();
	}
	
	
	static class TablePrinter {
		private final Appendable _printer;
		private final String[] _header;
		private final int[] _widths;
		
		public TablePrinter(
			final Appendable printer, 
			final String[] header, 
			final int[] widths
		) {
			_printer = printer;
			_header = header;
			_widths = widths;
		}
		
		public void print(final Object[][] data) {
			
		}
		
		private void printHeader() throws IOException {
			_printer.append("+");
			for (int i = 0; i < _widths.length; ++i) {
				for (int j = 0, n = _widths[i]; j < n; ++j) {
					_printer.append("-");
				}
				_printer.append("+");
			}
		}
	}
	
}












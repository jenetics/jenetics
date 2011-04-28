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

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import javax.measure.unit.SI;

import org.jenetics.util.Timer;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public abstract class PerfTest {
	
	protected int N = 1;

	private final String _group;
	private final List<Timer> _timers = new ArrayList<Timer>();
	
	public PerfTest(final String group) {
		_group = group;
	}
	
	protected abstract int calls();
	
	protected Timer newTimer(final String name) {
		final Timer timer = new Timer(name);
		_timers.add(timer);
		return timer;
	}
	
	protected abstract PerfTest measure();	
	
	public void print(final PrintStream out) {
		out.print(this);
	}
	
	@Override
	public String toString() {
		final int[] columns = new int[]{37, 16, 16, 16}; 
		final String hhline = hline(columns, '=');
		final String hline = hline(columns, '-');
		
		final String header = String.format(
				"| %%-%ds | %%-%ds | %%-%ds | %%-%ds |",
				columns[0] - 2, columns[1] - 2, columns[2] - 2, columns[3] - 2
			);
		final String row = String.format(
				"| %%-%ds | %%%df ns | %%%df ns | %%%df ns |",
				columns[0] - 2, columns[1] - 5, columns[2] - 5, columns[3] - 5
			);
		
		final StringBuilder out = new StringBuilder();
		out.append(hhline).append('\n');
		out.append(String.format(header, _group, "Mean", "Min", "Max")).append("\n");
		out.append(hhline).append('\n');
		
		for (Timer timer : _timers) {
			out.append(String.format(
					row, 
					timer.getLabel(), 
					timer.getMean().doubleValue(SI.NANO(SI.SECOND))/calls(),
					timer.getMin().doubleValue(SI.NANO(SI.SECOND))/calls(),
					timer.getMax().doubleValue(SI.NANO(SI.SECOND))/calls()
				));
			out.append("\n");
			out.append(hline).append('\n');
		}

		return out.toString();
	}
	
	private static String hline(final int[] columns, final char c) {
		final StringBuilder out = new StringBuilder();
		out.append('+');
		for (int i = 0; i < columns.length; ++i) {
			for (int j = 0; j < columns[i]; ++j) {
				out.append(c);
			}
			out.append('+');
		}
		
		return out.toString();
	}
	
	public static void main(final String[] args) {
		//System.getProperties().list(System.out);
		new ArrayTest().measure().print(System.out);
	}
	
}

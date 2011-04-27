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

import java.util.ArrayList;
import java.util.List;

import javax.measure.unit.SI;

import org.jenetics.util.Timer;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public abstract class PerfTest {

	private final String _group;
	private final List<Timer> _timers = new ArrayList<Timer>();
	
	public PerfTest(final String group) {
		_group = group;
	}
	
	protected Timer newTimer(final String name) {
		final Timer timer = new Timer(name);
		_timers.add(timer);
		return timer;
	}
	
	protected abstract void measure();
	
	
	@Override
	public String toString() {
		final String header = "| %-20s | %8s      | %8s      | %8s      |";
		final String row =    "| %-20s | %05.8f ms | %05.8f ms | %05.8f ms |";
		
		final StringBuilder out = new StringBuilder();
		out.append("+======================+===============+===============+===============+\n");
		out.append(String.format(header, _group, "Mean", "Min", "Max")).append("\n");
		out.append("+======================+===============+===============+===============+\n");
		
		for (Timer timer : _timers) {
			out.append(String.format(
					row, 
					timer.getLabel(), 
					timer.getMean().doubleValue(SI.MILLI(SI.SECOND)),
					timer.getMin().doubleValue(SI.MILLI(SI.SECOND)),
					timer.getMax().doubleValue(SI.MILLI(SI.SECOND))
				));
			out.append("\n");
			out.append("+----------------------+---------------+---------------+---------------+\n");
		}
		
		return out.toString();
	}
	
}

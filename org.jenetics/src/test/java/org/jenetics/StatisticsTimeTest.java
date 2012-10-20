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
package org.jenetics;

import java.util.Random;

import javax.measure.Measure;
import javax.measure.unit.SI;

import org.jenetics.Statistics.Time;
import org.jenetics.util.Factory;
import org.jenetics.util.ObjectTester;
import org.jenetics.util.RandomRegistry;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public class StatisticsTimeTest extends ObjectTester<Time> {

	final Factory<Time> _factory = new Factory<Time>() {
		@Override
		public Time newInstance() {
			final Random random = RandomRegistry.getRandom();
			
			final Time time = new Time();
			time.alter.set(Measure.valueOf(random.nextDouble()*1000, SI.SECOND));
			time.combine.set(Measure.valueOf(random.nextDouble()*1000, SI.SECOND));
			time.evaluation.set(Measure.valueOf(random.nextDouble()*1000, SI.SECOND));
			time.execution.set(Measure.valueOf(random.nextDouble()*1000, SI.SECOND));
			time.selection.set(Measure.valueOf(random.nextDouble()*1000, SI.SECOND));
			time.statistics.set(Measure.valueOf(random.nextDouble()*1000, SI.SECOND));
			
			return time;
		}
	};
	@Override
	protected Factory<Time> getFactory() {
		return _factory;
	}
	
}

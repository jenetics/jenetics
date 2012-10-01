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

import static java.util.FormattableFlags.LEFT_JUSTIFY;

import java.io.IOException;
import java.lang.reflect.Field;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Formattable;
import java.util.Formatter;
import java.util.List;

import javax.measure.Measurable;
import javax.measure.Measure;
import javax.measure.quantity.Duration;
import javax.measure.unit.SI;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public final class TestSuite {
	private final String _name;
	private final TestCase[] _tests;
	
	public TestSuite(final String name, final TestCase[] tests) {
		_name = name;
		_tests = tests;
	}
	
	public TestSuite(final Class<?> suite) {
		try {
			if (suite.isAnnotationPresent(Suite.class)) {
				_name = suite.getAnnotation(Suite.class).value();
				final Object object = suite.newInstance();
				
				final List<TestCase> tests = new ArrayList<>();

				for (Field field : suite.getFields()) {
					if (field.isAnnotationPresent(Test.class) &&
						field.getType().isAssignableFrom(TestCase.class))
					{
						final TestCase test = (TestCase)field.get(object);
						test.setOrdinal(field.getAnnotation(Test.class).value());
						
						tests.add(test);
					}
				}
				
				Collections.sort(tests);
				_tests = tests.toArray(new TestCase[0]);
			} else {
				_name = "<group>";
				_tests = new TestCase[0];
			}
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

	public String getName() {
		return _name;
	}
	
	public TestCase[] getTests() {
		return _tests;
	}
	
	public TestSuite run() {
		for (TestCase test : _tests) {
			test.run();
		}
		return this;
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
	
	private static Formattable FormattableDuration(final double nanos) {
		return new Formattable() {
			
			private final Measurable<Duration>
			_duration = Measure.valueOf(nanos, SI.NANO(SI.SECOND));
			
			@Override
			public void formatTo(
				final Formatter formatter,
				final int flags,
				final int width,
				final int precision
			) {
				final double nanos = _duration.doubleValue(SI.NANO(SI.SECOND));
				final double micros = _duration.doubleValue(SI.MICRO(SI.SECOND));
				final double millis = _duration.doubleValue(SI.MILLI(SI.SECOND));
				final double seconds = _duration.doubleValue(SI.SECOND);
				
				final NumberFormat nf = NumberFormat.getNumberInstance();
				nf.setMinimumFractionDigits(precision);
				nf.setMaximumFractionDigits(precision);
				
				String unit = "";
				String value = "";
				if ((long)seconds > 0) {
					unit = "s ";
					value = nf.format(seconds);
				} else if ((long)millis > 0) {
					unit = "ms";
					value = nf.format(millis);
				} else if ((long)micros > 0) {
					unit = "µs";
					value = nf.format(micros);
				} else {
					unit = "ns";
					value = nf.format(nanos);
				}
			
				String result = value + " " + unit;
				if (result.length() < width) {
					if ((flags & LEFT_JUSTIFY) == LEFT_JUSTIFY) {
						result = result + padding(width - result.length());
					} else {
						result = padding(width - result.length()) + result;
					}
				}
				
				formatter.format(result);
				
			}
			
			private String padding(final int width) {
				final char[] chars = new char[width];
				Arrays.fill(chars, ' ');
				return new String(chars);
			}
			
		};
	}
	
	public void print() {
		System.out.println(toString());
	}
	
	public void print(final Appendable appendable) throws IOException {
		appendable.append(toString());
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
				"| %%-%ds | %%%d.5s | %%%d.5s | %%%d.5s |",
				columns[0] - 2, columns[1] - 2, columns[2] - 2, columns[3] - 2
			);
		
		final NumberFormat nf = NumberFormat.getNumberInstance();
		
		final StringBuilder out = new StringBuilder();
		out.append(hhline).append('\n');
		out.append(String.format(header, _name, "Mean", "Min", "Max")).append("\n");
		out.append(hhline).append('\n');
		
		for (TestCase test : _tests) {
			out.append(String.format(
					row,
					String.format("%-20s 1/%s", test.getTimer().getLabel(), nf.format(test.getSize())),
					FormattableDuration(test.getVariance().getMean()),
					FormattableDuration(test.getMinMax().getMin()),
					FormattableDuration(test.getMinMax().getMax())
				));
			out.append("\n");
			out.append(hline).append('\n');	
		}
		out.deleteCharAt(out.length() - 1);

		return out.toString();
	}
	
}







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
package io.jenetics.tool.trial;

import static java.lang.String.format;
import static java.util.Collections.singletonList;
import static java.util.Objects.requireNonNull;
import static io.jenetics.tool.trial.SampleSummary.toSampleSummary;
import static io.jenetics.xml.stream.Writer.attr;
import static io.jenetics.xml.stream.Writer.elem;
import static io.jenetics.xml.stream.Writer.elems;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.jenetics.xml.stream.Reader;
import io.jenetics.xml.stream.Writer;

/**
 * This class collects a list of {@link Sample} result objects into on
 * {@code Data} object.
 *
 * @see Sample
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 4.0
 * @since 3.4
 */
public final class Data implements Serializable {

	private static final long serialVersionUID = 1L;

	private final String _name;
	private final List<Sample> _samples = new ArrayList<>();

	private Data(final String name, final List<Sample> samples) {
		if (samples.isEmpty()) {
			throw new IllegalArgumentException("Sample list must not be empty.");
		}
		if (!samples.stream().allMatch(s -> samples.get(0).size() == s.size())) {
			throw new IllegalArgumentException(
				"All sample object must have the same size."
			);
		}

		_name = requireNonNull(name);
		_samples.addAll(samples);
	}

	/**
	 * Return the name of the sample {@code Data} collections.
	 *
	 * @return the name of the sample {@code Data} collections
	 */
	public String getName() {
		return _name;
	}

	/**
	 * The number of {@link Sample} objects this {@code Data} class contains.
	 *
	 * @return the size of the data object
	 */
	public int dataSize() {
		return _samples.size();
	}

	/**
	 * The number of values of an {@link Sample} object.
	 *
	 * @see Sample#size()
	 *
	 * @return number of values of an {@link Sample} object
	 */
	public int sampleSize() {
		return _samples.get(0).size();
	}

	/**
	 * Return the current {@link Sample} object. A newly created object is
	 * returned on demand.
	 *
	 * @return the current {@link Sample} object
	 */
	public Sample currentSample() {
		Sample sample = _samples.get(_samples.size() - 1);
		if (sample.isFull()) {
			sample = sample.newSample();
			_samples.add(sample);
		}

		return sample;
	}

	/**
	 * Return the index of the next parameter index to calculate.
	 *
	 * @return the index of the next parameter index to calculate
	 */
	public int nextParamIndex() {
		return currentSample().nextIndex();
	}

	/**
	 * Calculate the sample summary of this data object.
	 *
	 * @return the sample summary of this data object
	 */
	public SampleSummary summary() {
		return _samples.stream()
			.filter(Sample::isFull)
			.collect(toSampleSummary(sampleSize()));
	}

	@Override
	public int hashCode() {
		return (37*_name.hashCode() + 17)*_samples.hashCode()*37 + 17;
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof Data &&
			_name.equals(((Data)obj)._name) &&
			_samples.equals(((Data)obj)._samples);
	}

	@Override
	public String toString() {
		return format("Data[name=%s, size=%d]", _name, dataSize());
	}

	/**
	 * Create a new {@code Data} object with the given parameters.
	 *
	 * @param name the name of the data object
	 * @param samples the sample list of the data object
	 * @throws NullPointerException if one of the parameters is {@code null}
	 * @return a new {@code Data} object with the given parameters
	 */
	public static Data of(final String name, final List<Sample> samples) {
		return new Data(name, samples);
	}

	/**
	 * Return a new {@code Data} object with the given name and the given number
	 * of parameters.
	 *
	 * @param name the name of the data object
	 * @param parameterCount the parameter count of the created samples
	 * @throws NullPointerException if the data {@code name} is {@code null}
	 * @throws IllegalArgumentException if the given {@code parameterCount} is
	 *         smaller then one
	 * @return a new {@code Data} object with the given parameters
	 */
	public static Data of(final String name, final int parameterCount) {
		return of(name, singletonList(Sample.of(parameterCount)));
	}


	/* *************************************************************************
	 *  XML reader/writer
	 * ************************************************************************/

	public static final Writer<Data> WRITER = elem(
		"data",
		attr("name").map(Data::getName),
		elems(Sample.WRITER).map(d -> d._samples)
	);

	@SuppressWarnings("unchecked")
	public static final Reader<Data> READER = Reader.elem(
		(Object[] v) -> Data.of((String)v[0], (List<Sample>)v[1]),
		"data",
		Reader.attr("name"),
		Reader.elems(Sample.READER)
	);

}

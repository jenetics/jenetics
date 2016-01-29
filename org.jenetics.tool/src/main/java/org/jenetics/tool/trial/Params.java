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
package org.jenetics.trial;

import static java.util.Objects.requireNonNull;

import java.util.Iterator;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.jenetics.util.ISeq;

/**
 * Collection of parameters the function under test is tested with.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
@XmlJavaTypeAdapter(Params.Model.Adapter.class)
public final class Params<T> implements Iterable<T> {

	private final String _name;
	private final ISeq<T> _params;

	private Params(final String name, final ISeq<T> params) {
		_name = requireNonNull(name);
		_params = requireNonNull(params);
	}

	/**
	 * Return the name of the parameter collection.
	 *
	 * @return the name of the parameter collection
	 */
	public String getName() {
		return _name;
	}

	public ISeq<T> get() {
		return _params;
	}

	/**
	 * Return the number of parameters this collection contains.
	 *
	 * @return the number of parameters
	 */
	public int size() {
		return _params.size();
	}

	@Override
	public Iterator<T> iterator() {
		return _params.iterator();
	}

	@Override
	public String toString() {
		return _params.toString();
	}

	public static <T> Params<T> of(
		final String name,
		final ISeq<T> params
	) {
		return new Params<>(name, params);
	}

	/* *************************************************************************
	 *  JAXB object serialization
	 * ************************************************************************/

	@XmlRootElement(name = "params")
	@XmlType(name = "org.jenetics.tool.Params")
	@XmlAccessorType(XmlAccessType.FIELD)
	@SuppressWarnings({"unchecked", "rawtypes"})
	static final class Model {

		@XmlAttribute(name = "name")
		public String name;

		@XmlElement(name = "param", required = true, nillable = false)
		public List params;

		public static final class Adapter extends XmlAdapter<Model, Params> {
			@Override
			public Model marshal(final Params params) {
				final Model model = new Model();
				model.name = params.getName();
				model.params = params.get().asList();
				return model;
			}

			@Override
			public Params unmarshal(final Model model) {
				return Params.of(
					model.name,
					ISeq.of(model.params)
				);
			}
		}
	}

}

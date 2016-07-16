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
package org.jenetics.example.tsp.gpx;

import static java.util.Objects.requireNonNull;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * An email address. Broken into two parts (id and domain) to help prevent email
 * harvesting.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
@XmlJavaTypeAdapter(Email.Model.Adapter.class)
public final class Email implements Serializable {

	private static final long serialVersionUID = 1L;

	private final String _id;
	private final String _domain;

	/**
	 * Create a new {@code Email} object with the given {@code id} and
	 * {@code domain}.
	 *
	 * @param id id half of email address (billgates2004)
	 * @param domain domain half of email address (hotmail.com)
	 * @throws NullPointerException if one of the argument is {@code null}
	 */
	private Email(final String id, final String domain) {
		_id = requireNonNull(id);
		_domain = requireNonNull(domain);
	}

	/**
	 * Return the id half of the email address.
	 *
	 * @return the id half of the email address
	 */
	public String getID() {
		return _id;
	}

	/**
	 * Return the domain half of the email address.
	 *
	 * @return the domain half of the email address
	 */
	public String getDomain() {
		return _domain;
	}

	@Override
	public int hashCode() {
		int hash = 37;
		hash += 17*_id.hashCode() + 31;
		hash += 17*_domain.hashCode() + 31;
		return hash;
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof Email &&
			((Email)obj)._id.equals(_id) &&
			((Email)obj)._domain.equals(_domain);
	}

	@Override
	public String toString() {
		return _id + "@" + _domain;
	}


	/* *************************************************************************
	 *  Static object creation methods
	 * ************************************************************************/

	/**
	 * Create a new {@code Email} object with the given {@code id} and
	 * {@code domain}.
	 *
	 * @param id id half of email address (billgates2004)
	 * @param domain domain half of email address (hotmail.com)
	 * @return a new {@code Email} object with the given values
	 * @throws NullPointerException if one of the argument is {@code null}
	 */
	public static Email of(final String id, final String domain) {
		return new Email(id, domain);
	}


	/* *************************************************************************
	 *  JAXB object serialization
	 * ************************************************************************/

	@XmlRootElement(name = "email")
	@XmlType(name = "gpx.Email")
	@XmlAccessorType(XmlAccessType.FIELD)
	static final class Model {

		@XmlAttribute(name = "id", required = true)
		public String id;

		@XmlAttribute(name = "domain", required = true)
		public String domain;

		public static final class Adapter
			extends XmlAdapter<Model, Email>
		{
			@Override
			public Model marshal(final Email email) {
				final Model model = new Model();
				model.id = email._id;
				model.domain = email._domain;
				return model;
			}

			@Override
			public Email unmarshal(final Model model) {
				return Email.of(model.id, model.domain);
			}
		}

		static final Adapter ADAPTER = new Adapter();

	}

}

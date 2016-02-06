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
package org.jenetics;

import static org.jenetics.internal.util.Equality.eq;
import static org.jenetics.util.ISeq.toISeq;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.jenetics.internal.util.Hash;
import org.jenetics.internal.util.jaxb;

import org.jenetics.util.ISeq;
import org.jenetics.util.Seq;

/**
 * Combines several alterers to one.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version !__version__!
 */
@XmlJavaTypeAdapter(CompositeAlterer.Model.Adapter.class)
public final class CompositeAlterer<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
	extends AbstractAlterer<G, C>
	implements Serializable
{

	private static final long serialVersionUID = 1L;

	private final ISeq<? extends Alterer<G, C>> _alterers;

	/**
	 * Combine the given alterers.
	 *
	 * @param alterers the alterers to combine.
	 * @throws NullPointerException if one of the alterers is {@code null}.
	 */
	public CompositeAlterer(final Seq<? extends Alterer<G, C>> alterers) {
		super(1.0);
		_alterers = normalize(alterers);
	}

	static <G extends Gene<?, G>, C extends Comparable<? super C>>
	ISeq<Alterer<G, C>> normalize(final Seq<? extends Alterer<G, C>> alterers) {
		final Function<Alterer<G, C>, Stream<? extends Alterer<G, C>>> mapper =
			a -> a instanceof CompositeAlterer<?, ?>
				? ((CompositeAlterer<G, C>)a).getAlterers().stream()
				: Stream.of(a);

		return alterers.stream()
			.flatMap(mapper)
			.filter(a -> !a.equals(Alterer.empty()))
			.collect(toISeq());
	}

	/**
	 * Return the number of alterers the {@code CompositeAlterer} consists of.
	 *
	 * @since !__version__!
	 *
	 * @return the number of alterers the {@code CompositeAlterer} consists of
	 */
	public int size() {
		return _alterers.size();
	}

	@Override
	public int alter(final Population<G, C> population, final long generation) {
		return _alterers.stream()
			.mapToInt(a -> a.alter(population, generation))
			.sum();
	}

	/**
	 * Return the alterers this alterer consists of. The returned array is sealed
	 * and cannot be changed.
	 *
	 * @return the alterers this alterer consists of.
	 */
	public ISeq<? extends Alterer<G, C>> getAlterers() {
		return _alterers;
	}

	@Override
	public int hashCode() {
		return Hash.of(getClass()).and(_alterers).value();
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof CompositeAlterer &&
			eq(((CompositeAlterer)obj)._alterers, _alterers);
	}

	@Override
	public String toString() {
		return _alterers.stream()
			.map(Objects::toString)
			.collect(Collectors.joining(",", "[", "]"));
	}

	/**
	 * Combine the given alterers.
	 *
	 * @param <G> the gene type
	 * @param <C> the fitness function result type
	 * @param alterers the alterers to combine.
	 * @return a new alterer which consists of the given one
	 * @throws NullPointerException if one of the alterers is {@code null}.
	 */
	@SafeVarargs
	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	CompositeAlterer<G, C> of(final Alterer<G, C>... alterers) {
		return new CompositeAlterer<>(ISeq.of(alterers));
	}

	/**
	 * Joins the given alterer and returns a new CompositeAlterer object. If one
	 * of the given alterers is a CompositeAlterer the sub alterers of it are
	 * unpacked and appended to the newly created CompositeAlterer.
	 *
	 * @param a1 the first alterer.
	 * @param a2 the second alterer.
	 * @return a new CompositeAlterer object.
	 * @param <T> the gene type of the alterers.
	 * @param <C> the fitness function result type
	 * @throws NullPointerException if one of the given alterer is {@code null}.
	 */
	public static <T extends Gene<?, T>, C extends Comparable<? super C>>
	CompositeAlterer<T, C> join(
		final Alterer<T, C> a1,
		final Alterer<T, C> a2
	) {
		return CompositeAlterer.of(a1, a2);
	}


	/* *************************************************************************
	 *  JAXB object serialization
	 * ************************************************************************/

	@XmlRootElement(name = "composite-alterer")
	@XmlType(name = "org.jenetics.CompositeAlterer")
	@XmlAccessorType(XmlAccessType.FIELD)
	@SuppressWarnings({"unchecked", "rawtypes"})
	static final class Model {

		@XmlAttribute(name = "length", required = true)
		public int length;

		@XmlElement(name = "alterers", required = true, nillable = false)
		public List alterers;

		public static final class Adapter
			extends XmlAdapter<Model, CompositeAlterer>
		{
			@Override
			public Model marshal(final CompositeAlterer ca) throws Exception {
				final Model model = new Model();
				model.length = ca.size();
				model.alterers = ca.getAlterers()
					.map(a -> jaxb.Marshaller(a).apply(a))
					.asList();

				return model;
			}

			@Override
			public CompositeAlterer unmarshal(final Model model) throws Exception {
				final ISeq alterers = (ISeq)model.alterers.stream()
					.map(a -> jaxb.Unmarshaller(a).apply(a))
					.collect(toISeq());

				return new CompositeAlterer(alterers);
			}
		}
	}

}

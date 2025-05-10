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
package io.jenetics.incubator.metamodel.type;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static org.assertj.core.api.Assertions.assertThat;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Comparator;
import java.util.stream.Stream;

import org.testng.annotations.Test;

import io.jenetics.incubator.metamodel.Path;
import io.jenetics.incubator.metamodel.PathValue;
import io.jenetics.incubator.metamodel.model.Author;
import io.jenetics.incubator.metamodel.model.Book;
import io.jenetics.incubator.metamodel.model.Library;

import io.jenetics.jpx.GPX;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class DescriptionTest {

	@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
	@Target({METHOD, TYPE})
	public @interface Anno_1 { }

	@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
	@Target({METHOD, TYPE})
	public @interface Anno_2 { }

	@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
	@Target({METHOD, TYPE})
	public @interface Anno_3 { }

//	@Test
//	public void annotations() {
//		interface Data1 { @Anno_1 String value(); }
//		interface Data2 { @Anno_2 String value(); }
//		record DataRecord(@Anno_3 String value) implements Data1, Data2 { }
//
//		final var desc = Description.list(DataRecord.class).toList().getFirst();
//		assertThat(desc.annotations()).hasSize(3);
//		assertThat(desc.annotations().map(Annotation::annotationType).toArray())
//			.containsAll(List.of(Anno_1.class, Anno_2.class, Anno_3.class));
//	}

	@Test
	public void extractLibrary() {
		final var descriptions = Description
			.list(PathValue.of(Path.of("library"), Library.class))
			.sorted(Comparator.comparing(Description::path))
			.map(Description::toString)
			.toArray(String[]::new);

		//Stream.of(descriptions).forEach(System.out::println);

		final var expected = """
			Description[path=library.books, type=ComponentType[name=books, type=java.util.List<io.jenetics.incubator.metamodel.model.Book>, enclosure=RecordType[io.jenetics.incubator.metamodel.model.Library]]]
			Description[path=library.name, type=ComponentType[name=name, type=java.lang.String, enclosure=RecordType[io.jenetics.incubator.metamodel.model.Library]]]
			""".split("\n");

		assertThat(descriptions).isEqualTo(expected);
	}

	@Test
	public void extractBook() {
		final var descriptions = Description
			.list(PathValue.of(Book.class))
			.sorted(Comparator.comparing(Description::path))
			.map(Description::toString)
			.peek(System.out::println)
			.toArray(String[]::new);

		//Stream.of(descriptions).forEach(System.out::println);

		final var expected = """
			Description[path=authors, type=ComponentType[name=authors, type=java.util.List<io.jenetics.incubator.metamodel.model.Author>, enclosure=RecordType[io.jenetics.incubator.metamodel.model.Book]]]
			Description[path=pages, type=ComponentType[name=pages, type=int, enclosure=RecordType[io.jenetics.incubator.metamodel.model.Book]]]
			Description[path=title, type=ComponentType[name=title, type=java.lang.String, enclosure=RecordType[io.jenetics.incubator.metamodel.model.Book]]]
			""".split("\n");

		assertThat(descriptions).isEqualTo(expected);
	}

	@Test
	public void extractAuthor() {
		final var descriptions = Description
			.list(PathValue.of(Author.class))
			.sorted(Comparator.comparing(Description::path))
			.map(Description::toString)
			.toArray(String[]::new);

		//Stream.of(descriptions).forEach(System.out::println);

		final var expected = """
			Description[path=birthDate, type=ComponentType[name=birthDate, type=java.time.LocalDate, enclosure=RecordType[io.jenetics.incubator.metamodel.model.Author]]]
			Description[path=books, type=ComponentType[name=books, type=java.util.List<io.jenetics.incubator.metamodel.model.Book>, enclosure=RecordType[io.jenetics.incubator.metamodel.model.Author]]]
			Description[path=forename, type=ComponentType[name=forename, type=java.lang.String, enclosure=RecordType[io.jenetics.incubator.metamodel.model.Author]]]
			Description[path=surname, type=ComponentType[name=surname, type=java.lang.String, enclosure=RecordType[io.jenetics.incubator.metamodel.model.Author]]]
			""".split("\n");

		assertThat(descriptions).isEqualTo(expected);
	}

	@Test
	public void extractGPX() {
		final var descriptions = Description
			.list(PathValue.of(GPX.class))
			.sorted(Comparator.comparing(Description::path))
			.map(Description::toString)
			.toArray(String[]::new);

		//Stream.of(descriptions).forEach(System.out::println);

		final var expected = """
			Description[path=creator, type=ComponentType[name=creator, type=java.lang.String, enclosure=BeanType[io.jenetics.jpx.GPX]]]
			Description[path=extensions, type=ComponentType[name=extensions, type=java.util.Optional<org.w3c.dom.Document>, enclosure=BeanType[io.jenetics.jpx.GPX]]]
			Description[path=metadata, type=ComponentType[name=metadata, type=java.util.Optional<io.jenetics.jpx.Metadata>, enclosure=BeanType[io.jenetics.jpx.GPX]]]
			Description[path=routes, type=ComponentType[name=routes, type=java.util.List<io.jenetics.jpx.Route>, enclosure=BeanType[io.jenetics.jpx.GPX]]]
			Description[path=tracks, type=ComponentType[name=tracks, type=java.util.List<io.jenetics.jpx.Track>, enclosure=BeanType[io.jenetics.jpx.GPX]]]
			Description[path=version, type=ComponentType[name=version, type=java.lang.String, enclosure=BeanType[io.jenetics.jpx.GPX]]]
			Description[path=wayPoints, type=ComponentType[name=wayPoints, type=java.util.List<io.jenetics.jpx.WayPoint>, enclosure=BeanType[io.jenetics.jpx.GPX]]]
			""".split("\n");

		assertThat(descriptions).isEqualTo(expected);
	}

	@Test
	public void walkLibrary() {
		final var descriptions = Description
			.walk(PathValue.of(Path.of("library"), Library.class))
			.sorted(Comparator.comparing(Description::path))
			.map(Description::toString)
			.toArray(String[]::new);

		//Stream.of(descriptions).forEach(System.out::println);

		final var expected = """
			Description[path=library.books, type=ComponentType[name=books, type=java.util.List<io.jenetics.incubator.metamodel.model.Book>, enclosure=RecordType[io.jenetics.incubator.metamodel.model.Library]]]
			Description[path=library.books[0], type=ListType[java.util.List[io.jenetics.incubator.metamodel.model.Book]]]
			Description[path=library.books[0].authors, type=ComponentType[name=authors, type=java.util.List<io.jenetics.incubator.metamodel.model.Author>, enclosure=RecordType[io.jenetics.incubator.metamodel.model.Book]]]
			Description[path=library.books[0].authors[0], type=ListType[java.util.List[io.jenetics.incubator.metamodel.model.Author]]]
			Description[path=library.books[0].authors[0].birthDate, type=ComponentType[name=birthDate, type=java.time.LocalDate, enclosure=RecordType[io.jenetics.incubator.metamodel.model.Author]]]
			Description[path=library.books[0].authors[0].books, type=ComponentType[name=books, type=java.util.List<io.jenetics.incubator.metamodel.model.Book>, enclosure=RecordType[io.jenetics.incubator.metamodel.model.Author]]]
			Description[path=library.books[0].authors[0].books[0], type=ListType[java.util.List[io.jenetics.incubator.metamodel.model.Book]]]
			Description[path=library.books[0].authors[0].forename, type=ComponentType[name=forename, type=java.lang.String, enclosure=RecordType[io.jenetics.incubator.metamodel.model.Author]]]
			Description[path=library.books[0].authors[0].surname, type=ComponentType[name=surname, type=java.lang.String, enclosure=RecordType[io.jenetics.incubator.metamodel.model.Author]]]
			Description[path=library.books[0].pages, type=ComponentType[name=pages, type=int, enclosure=RecordType[io.jenetics.incubator.metamodel.model.Book]]]
			Description[path=library.books[0].title, type=ComponentType[name=title, type=java.lang.String, enclosure=RecordType[io.jenetics.incubator.metamodel.model.Book]]]
			Description[path=library.name, type=ComponentType[name=name, type=java.lang.String, enclosure=RecordType[io.jenetics.incubator.metamodel.model.Library]]]
			""".split("\n");

		assertThat(descriptions).isEqualTo(expected);
	}

	@Test
	public void walkGPX() {
		final var descriptions = Description
			.walk(PathValue.of(Path.of("gpx"), GPX.class))
			.sorted(Comparator.comparing(Description::path))
			.map(Description::toString)
			.filter(d ->
				!d.contains("gpx.routes[0].points[0].time") &&
				!d.contains("gpx.routes[0].points[0].elevation"))
			.toArray(String[]::new);

		//Stream.of(descriptions).forEach(System.out::println);

		final var expected = """
			Description[path=gpx.creator, type=ComponentType[name=creator, type=java.lang.String, enclosure=BeanType[io.jenetics.jpx.GPX]]]
			Description[path=gpx.extensions, type=ComponentType[name=extensions, type=java.util.Optional<org.w3c.dom.Document>, enclosure=BeanType[io.jenetics.jpx.GPX]]]
			Description[path=gpx.extensions[0], type=OptionalType[org.w3c.dom.Document]]
			Description[path=gpx.extensions[0].doctype, type=ComponentType[name=doctype, type=org.w3c.dom.DocumentType, enclosure=BeanType[org.w3c.dom.Document]]]
			Description[path=gpx.extensions[0].doctype.entities, type=ComponentType[name=entities, type=org.w3c.dom.NamedNodeMap, enclosure=BeanType[org.w3c.dom.DocumentType]]]
			Description[path=gpx.extensions[0].doctype.entities.length, type=ComponentType[name=length, type=int, enclosure=BeanType[org.w3c.dom.NamedNodeMap]]]
			Description[path=gpx.extensions[0].doctype.internalSubset, type=ComponentType[name=internalSubset, type=java.lang.String, enclosure=BeanType[org.w3c.dom.DocumentType]]]
			Description[path=gpx.extensions[0].doctype.name, type=ComponentType[name=name, type=java.lang.String, enclosure=BeanType[org.w3c.dom.DocumentType]]]
			Description[path=gpx.extensions[0].doctype.notations, type=ComponentType[name=notations, type=org.w3c.dom.NamedNodeMap, enclosure=BeanType[org.w3c.dom.DocumentType]]]
			Description[path=gpx.extensions[0].doctype.publicId, type=ComponentType[name=publicId, type=java.lang.String, enclosure=BeanType[org.w3c.dom.DocumentType]]]
			Description[path=gpx.extensions[0].doctype.systemId, type=ComponentType[name=systemId, type=java.lang.String, enclosure=BeanType[org.w3c.dom.DocumentType]]]
			Description[path=gpx.extensions[0].documentElement, type=ComponentType[name=documentElement, type=org.w3c.dom.Element, enclosure=BeanType[org.w3c.dom.Document]]]
			Description[path=gpx.extensions[0].documentElement.schemaTypeInfo, type=ComponentType[name=schemaTypeInfo, type=org.w3c.dom.TypeInfo, enclosure=BeanType[org.w3c.dom.Element]]]
			Description[path=gpx.extensions[0].documentElement.schemaTypeInfo.typeName, type=ComponentType[name=typeName, type=java.lang.String, enclosure=BeanType[org.w3c.dom.TypeInfo]]]
			Description[path=gpx.extensions[0].documentElement.schemaTypeInfo.typeNamespace, type=ComponentType[name=typeNamespace, type=java.lang.String, enclosure=BeanType[org.w3c.dom.TypeInfo]]]
			Description[path=gpx.extensions[0].documentElement.tagName, type=ComponentType[name=tagName, type=java.lang.String, enclosure=BeanType[org.w3c.dom.Element]]]
			Description[path=gpx.extensions[0].documentURI, type=ComponentType[name=documentURI, type=java.lang.String, enclosure=BeanType[org.w3c.dom.Document]]]
			Description[path=gpx.extensions[0].domConfig, type=ComponentType[name=domConfig, type=org.w3c.dom.DOMConfiguration, enclosure=BeanType[org.w3c.dom.Document]]]
			Description[path=gpx.extensions[0].domConfig.parameterNames, type=ComponentType[name=parameterNames, type=org.w3c.dom.DOMStringList, enclosure=BeanType[org.w3c.dom.DOMConfiguration]]]
			Description[path=gpx.extensions[0].domConfig.parameterNames.length, type=ComponentType[name=length, type=int, enclosure=BeanType[org.w3c.dom.DOMStringList]]]
			Description[path=gpx.extensions[0].implementation, type=ComponentType[name=implementation, type=org.w3c.dom.DOMImplementation, enclosure=BeanType[org.w3c.dom.Document]]]
			Description[path=gpx.extensions[0].inputEncoding, type=ComponentType[name=inputEncoding, type=java.lang.String, enclosure=BeanType[org.w3c.dom.Document]]]
			Description[path=gpx.extensions[0].strictErrorChecking, type=ComponentType[name=strictErrorChecking, type=boolean, enclosure=BeanType[org.w3c.dom.Document]]]
			Description[path=gpx.extensions[0].xmlEncoding, type=ComponentType[name=xmlEncoding, type=java.lang.String, enclosure=BeanType[org.w3c.dom.Document]]]
			Description[path=gpx.extensions[0].xmlStandalone, type=ComponentType[name=xmlStandalone, type=boolean, enclosure=BeanType[org.w3c.dom.Document]]]
			Description[path=gpx.extensions[0].xmlVersion, type=ComponentType[name=xmlVersion, type=java.lang.String, enclosure=BeanType[org.w3c.dom.Document]]]
			Description[path=gpx.metadata, type=ComponentType[name=metadata, type=java.util.Optional<io.jenetics.jpx.Metadata>, enclosure=BeanType[io.jenetics.jpx.GPX]]]
			Description[path=gpx.metadata[0], type=OptionalType[io.jenetics.jpx.Metadata]]
			Description[path=gpx.metadata[0].author, type=ComponentType[name=author, type=java.util.Optional<io.jenetics.jpx.Person>, enclosure=BeanType[io.jenetics.jpx.Metadata]]]
			Description[path=gpx.metadata[0].author[0], type=OptionalType[io.jenetics.jpx.Person]]
			Description[path=gpx.metadata[0].author[0].email, type=ComponentType[name=email, type=java.util.Optional<io.jenetics.jpx.Email>, enclosure=BeanType[io.jenetics.jpx.Person]]]
			Description[path=gpx.metadata[0].author[0].email[0], type=OptionalType[io.jenetics.jpx.Email]]
			Description[path=gpx.metadata[0].author[0].email[0].ID, type=ComponentType[name=ID, type=java.lang.String, enclosure=BeanType[io.jenetics.jpx.Email]]]
			Description[path=gpx.metadata[0].author[0].email[0].address, type=ComponentType[name=address, type=java.lang.String, enclosure=BeanType[io.jenetics.jpx.Email]]]
			Description[path=gpx.metadata[0].author[0].email[0].domain, type=ComponentType[name=domain, type=java.lang.String, enclosure=BeanType[io.jenetics.jpx.Email]]]
			Description[path=gpx.metadata[0].author[0].empty, type=ComponentType[name=empty, type=boolean, enclosure=BeanType[io.jenetics.jpx.Person]]]
			Description[path=gpx.metadata[0].author[0].link, type=ComponentType[name=link, type=java.util.Optional<io.jenetics.jpx.Link>, enclosure=BeanType[io.jenetics.jpx.Person]]]
			Description[path=gpx.metadata[0].author[0].link[0], type=OptionalType[io.jenetics.jpx.Link]]
			Description[path=gpx.metadata[0].author[0].link[0].href, type=ComponentType[name=href, type=java.net.URI, enclosure=BeanType[io.jenetics.jpx.Link]]]
			Description[path=gpx.metadata[0].author[0].link[0].href.absolute, type=ComponentType[name=absolute, type=boolean, enclosure=BeanType[java.net.URI]]]
			Description[path=gpx.metadata[0].author[0].link[0].href.authority, type=ComponentType[name=authority, type=java.lang.String, enclosure=BeanType[java.net.URI]]]
			Description[path=gpx.metadata[0].author[0].link[0].href.fragment, type=ComponentType[name=fragment, type=java.lang.String, enclosure=BeanType[java.net.URI]]]
			Description[path=gpx.metadata[0].author[0].link[0].href.host, type=ComponentType[name=host, type=java.lang.String, enclosure=BeanType[java.net.URI]]]
			Description[path=gpx.metadata[0].author[0].link[0].href.opaque, type=ComponentType[name=opaque, type=boolean, enclosure=BeanType[java.net.URI]]]
			Description[path=gpx.metadata[0].author[0].link[0].href.path, type=ComponentType[name=path, type=java.lang.String, enclosure=BeanType[java.net.URI]]]
			Description[path=gpx.metadata[0].author[0].link[0].href.port, type=ComponentType[name=port, type=int, enclosure=BeanType[java.net.URI]]]
			Description[path=gpx.metadata[0].author[0].link[0].href.query, type=ComponentType[name=query, type=java.lang.String, enclosure=BeanType[java.net.URI]]]
			Description[path=gpx.metadata[0].author[0].link[0].href.rawAuthority, type=ComponentType[name=rawAuthority, type=java.lang.String, enclosure=BeanType[java.net.URI]]]
			Description[path=gpx.metadata[0].author[0].link[0].href.rawFragment, type=ComponentType[name=rawFragment, type=java.lang.String, enclosure=BeanType[java.net.URI]]]
			Description[path=gpx.metadata[0].author[0].link[0].href.rawPath, type=ComponentType[name=rawPath, type=java.lang.String, enclosure=BeanType[java.net.URI]]]
			Description[path=gpx.metadata[0].author[0].link[0].href.rawQuery, type=ComponentType[name=rawQuery, type=java.lang.String, enclosure=BeanType[java.net.URI]]]
			Description[path=gpx.metadata[0].author[0].link[0].href.rawSchemeSpecificPart, type=ComponentType[name=rawSchemeSpecificPart, type=java.lang.String, enclosure=BeanType[java.net.URI]]]
			Description[path=gpx.metadata[0].author[0].link[0].href.rawUserInfo, type=ComponentType[name=rawUserInfo, type=java.lang.String, enclosure=BeanType[java.net.URI]]]
			Description[path=gpx.metadata[0].author[0].link[0].href.scheme, type=ComponentType[name=scheme, type=java.lang.String, enclosure=BeanType[java.net.URI]]]
			Description[path=gpx.metadata[0].author[0].link[0].href.schemeSpecificPart, type=ComponentType[name=schemeSpecificPart, type=java.lang.String, enclosure=BeanType[java.net.URI]]]
			Description[path=gpx.metadata[0].author[0].link[0].href.userInfo, type=ComponentType[name=userInfo, type=java.lang.String, enclosure=BeanType[java.net.URI]]]
			Description[path=gpx.metadata[0].author[0].link[0].text, type=ComponentType[name=text, type=java.util.Optional<java.lang.String>, enclosure=BeanType[io.jenetics.jpx.Link]]]
			Description[path=gpx.metadata[0].author[0].link[0].text[0], type=OptionalType[java.lang.String]]
			Description[path=gpx.metadata[0].author[0].link[0].type, type=ComponentType[name=type, type=java.util.Optional<java.lang.String>, enclosure=BeanType[io.jenetics.jpx.Link]]]
			Description[path=gpx.metadata[0].author[0].link[0].type[0], type=OptionalType[java.lang.String]]
			Description[path=gpx.metadata[0].author[0].name, type=ComponentType[name=name, type=java.util.Optional<java.lang.String>, enclosure=BeanType[io.jenetics.jpx.Person]]]
			Description[path=gpx.metadata[0].author[0].name[0], type=OptionalType[java.lang.String]]
			Description[path=gpx.metadata[0].bounds, type=ComponentType[name=bounds, type=java.util.Optional<io.jenetics.jpx.Bounds>, enclosure=BeanType[io.jenetics.jpx.Metadata]]]
			Description[path=gpx.metadata[0].bounds[0], type=OptionalType[io.jenetics.jpx.Bounds]]
			Description[path=gpx.metadata[0].bounds[0].maxLatitude, type=ComponentType[name=maxLatitude, type=io.jenetics.jpx.Latitude, enclosure=BeanType[io.jenetics.jpx.Bounds]]]
			Description[path=gpx.metadata[0].bounds[0].maxLongitude, type=ComponentType[name=maxLongitude, type=io.jenetics.jpx.Longitude, enclosure=BeanType[io.jenetics.jpx.Bounds]]]
			Description[path=gpx.metadata[0].bounds[0].minLatitude, type=ComponentType[name=minLatitude, type=io.jenetics.jpx.Latitude, enclosure=BeanType[io.jenetics.jpx.Bounds]]]
			Description[path=gpx.metadata[0].bounds[0].minLongitude, type=ComponentType[name=minLongitude, type=io.jenetics.jpx.Longitude, enclosure=BeanType[io.jenetics.jpx.Bounds]]]
			Description[path=gpx.metadata[0].copyright, type=ComponentType[name=copyright, type=java.util.Optional<io.jenetics.jpx.Copyright>, enclosure=BeanType[io.jenetics.jpx.Metadata]]]
			Description[path=gpx.metadata[0].copyright[0], type=OptionalType[io.jenetics.jpx.Copyright]]
			Description[path=gpx.metadata[0].copyright[0].author, type=ComponentType[name=author, type=java.lang.String, enclosure=BeanType[io.jenetics.jpx.Copyright]]]
			Description[path=gpx.metadata[0].copyright[0].license, type=ComponentType[name=license, type=java.util.Optional<java.net.URI>, enclosure=BeanType[io.jenetics.jpx.Copyright]]]
			Description[path=gpx.metadata[0].copyright[0].license[0], type=OptionalType[java.net.URI]]
			Description[path=gpx.metadata[0].copyright[0].year, type=ComponentType[name=year, type=java.util.Optional<java.time.Year>, enclosure=BeanType[io.jenetics.jpx.Copyright]]]
			Description[path=gpx.metadata[0].copyright[0].year[0], type=OptionalType[java.time.Year]]
			Description[path=gpx.metadata[0].description, type=ComponentType[name=description, type=java.util.Optional<java.lang.String>, enclosure=BeanType[io.jenetics.jpx.Metadata]]]
			Description[path=gpx.metadata[0].description[0], type=OptionalType[java.lang.String]]
			Description[path=gpx.metadata[0].empty, type=ComponentType[name=empty, type=boolean, enclosure=BeanType[io.jenetics.jpx.Metadata]]]
			Description[path=gpx.metadata[0].extensions, type=ComponentType[name=extensions, type=java.util.Optional<org.w3c.dom.Document>, enclosure=BeanType[io.jenetics.jpx.Metadata]]]
			Description[path=gpx.metadata[0].extensions[0], type=OptionalType[org.w3c.dom.Document]]
			Description[path=gpx.metadata[0].keywords, type=ComponentType[name=keywords, type=java.util.Optional<java.lang.String>, enclosure=BeanType[io.jenetics.jpx.Metadata]]]
			Description[path=gpx.metadata[0].keywords[0], type=OptionalType[java.lang.String]]
			Description[path=gpx.metadata[0].links, type=ComponentType[name=links, type=java.util.List<io.jenetics.jpx.Link>, enclosure=BeanType[io.jenetics.jpx.Metadata]]]
			Description[path=gpx.metadata[0].links[0], type=ListType[java.util.List[io.jenetics.jpx.Link]]]
			Description[path=gpx.metadata[0].name, type=ComponentType[name=name, type=java.util.Optional<java.lang.String>, enclosure=BeanType[io.jenetics.jpx.Metadata]]]
			Description[path=gpx.metadata[0].name[0], type=OptionalType[java.lang.String]]
			Description[path=gpx.metadata[0].time, type=ComponentType[name=time, type=java.util.Optional<java.time.Instant>, enclosure=BeanType[io.jenetics.jpx.Metadata]]]
			Description[path=gpx.metadata[0].time[0], type=OptionalType[java.time.Instant]]
			Description[path=gpx.routes, type=ComponentType[name=routes, type=java.util.List<io.jenetics.jpx.Route>, enclosure=BeanType[io.jenetics.jpx.GPX]]]
			Description[path=gpx.routes[0], type=ListType[java.util.List[io.jenetics.jpx.Route]]]
			Description[path=gpx.routes[0].comment, type=ComponentType[name=comment, type=java.util.Optional<java.lang.String>, enclosure=BeanType[io.jenetics.jpx.Route]]]
			Description[path=gpx.routes[0].comment[0], type=OptionalType[java.lang.String]]
			Description[path=gpx.routes[0].description, type=ComponentType[name=description, type=java.util.Optional<java.lang.String>, enclosure=BeanType[io.jenetics.jpx.Route]]]
			Description[path=gpx.routes[0].description[0], type=OptionalType[java.lang.String]]
			Description[path=gpx.routes[0].empty, type=ComponentType[name=empty, type=boolean, enclosure=BeanType[io.jenetics.jpx.Route]]]
			Description[path=gpx.routes[0].extensions, type=ComponentType[name=extensions, type=java.util.Optional<org.w3c.dom.Document>, enclosure=BeanType[io.jenetics.jpx.Route]]]
			Description[path=gpx.routes[0].extensions[0], type=OptionalType[org.w3c.dom.Document]]
			Description[path=gpx.routes[0].links, type=ComponentType[name=links, type=java.util.List<io.jenetics.jpx.Link>, enclosure=BeanType[io.jenetics.jpx.Route]]]
			Description[path=gpx.routes[0].links[0], type=ListType[java.util.List[io.jenetics.jpx.Link]]]
			Description[path=gpx.routes[0].name, type=ComponentType[name=name, type=java.util.Optional<java.lang.String>, enclosure=BeanType[io.jenetics.jpx.Route]]]
			Description[path=gpx.routes[0].name[0], type=OptionalType[java.lang.String]]
			Description[path=gpx.routes[0].number, type=ComponentType[name=number, type=java.util.Optional<io.jenetics.jpx.UInt>, enclosure=BeanType[io.jenetics.jpx.Route]]]
			Description[path=gpx.routes[0].number[0], type=OptionalType[io.jenetics.jpx.UInt]]
			Description[path=gpx.routes[0].number[0].value, type=ComponentType[name=value, type=int, enclosure=BeanType[io.jenetics.jpx.UInt]]]
			Description[path=gpx.routes[0].points, type=ComponentType[name=points, type=java.util.List<io.jenetics.jpx.WayPoint>, enclosure=BeanType[io.jenetics.jpx.Route]]]
			Description[path=gpx.routes[0].points[0], type=ListType[java.util.List[io.jenetics.jpx.WayPoint]]]
			Description[path=gpx.routes[0].points[0].DGPSID, type=ComponentType[name=DGPSID, type=java.util.Optional<io.jenetics.jpx.DGPSStation>, enclosure=BeanType[io.jenetics.jpx.WayPoint]]]
			Description[path=gpx.routes[0].points[0].DGPSID[0], type=OptionalType[io.jenetics.jpx.DGPSStation]]
			Description[path=gpx.routes[0].points[0].ageOfGPSData, type=ComponentType[name=ageOfGPSData, type=java.util.Optional<java.time.Duration>, enclosure=BeanType[io.jenetics.jpx.WayPoint]]]
			Description[path=gpx.routes[0].points[0].ageOfGPSData[0], type=OptionalType[java.time.Duration]]
			Description[path=gpx.routes[0].points[0].ageOfGPSData[0].nano, type=ComponentType[name=nano, type=int, enclosure=BeanType[java.time.Duration]]]
			Description[path=gpx.routes[0].points[0].ageOfGPSData[0].negative, type=ComponentType[name=negative, type=boolean, enclosure=BeanType[java.time.Duration]]]
			Description[path=gpx.routes[0].points[0].ageOfGPSData[0].positive, type=ComponentType[name=positive, type=boolean, enclosure=BeanType[java.time.Duration]]]
			Description[path=gpx.routes[0].points[0].ageOfGPSData[0].seconds, type=ComponentType[name=seconds, type=long, enclosure=BeanType[java.time.Duration]]]
			Description[path=gpx.routes[0].points[0].ageOfGPSData[0].units, type=ComponentType[name=units, type=java.util.List<java.time.temporal.TemporalUnit>, enclosure=BeanType[java.time.Duration]]]
			Description[path=gpx.routes[0].points[0].ageOfGPSData[0].units[0], type=ListType[java.util.List[java.time.temporal.TemporalUnit]]]
			Description[path=gpx.routes[0].points[0].ageOfGPSData[0].units[0].dateBased, type=ComponentType[name=dateBased, type=boolean, enclosure=BeanType[java.time.temporal.TemporalUnit]]]
			Description[path=gpx.routes[0].points[0].ageOfGPSData[0].units[0].duration, type=ComponentType[name=duration, type=java.time.Duration, enclosure=BeanType[java.time.temporal.TemporalUnit]]]
			Description[path=gpx.routes[0].points[0].ageOfGPSData[0].units[0].durationEstimated, type=ComponentType[name=durationEstimated, type=boolean, enclosure=BeanType[java.time.temporal.TemporalUnit]]]
			Description[path=gpx.routes[0].points[0].ageOfGPSData[0].units[0].timeBased, type=ComponentType[name=timeBased, type=boolean, enclosure=BeanType[java.time.temporal.TemporalUnit]]]
			Description[path=gpx.routes[0].points[0].ageOfGPSData[0].zero, type=ComponentType[name=zero, type=boolean, enclosure=BeanType[java.time.Duration]]]
			Description[path=gpx.routes[0].points[0].comment, type=ComponentType[name=comment, type=java.util.Optional<java.lang.String>, enclosure=BeanType[io.jenetics.jpx.WayPoint]]]
			Description[path=gpx.routes[0].points[0].comment[0], type=OptionalType[java.lang.String]]
			Description[path=gpx.routes[0].points[0].course, type=ComponentType[name=course, type=java.util.Optional<io.jenetics.jpx.Degrees>, enclosure=BeanType[io.jenetics.jpx.WayPoint]]]
			Description[path=gpx.routes[0].points[0].course[0], type=OptionalType[io.jenetics.jpx.Degrees]]
			Description[path=gpx.routes[0].points[0].description, type=ComponentType[name=description, type=java.util.Optional<java.lang.String>, enclosure=BeanType[io.jenetics.jpx.WayPoint]]]
			Description[path=gpx.routes[0].points[0].description[0], type=OptionalType[java.lang.String]]
			Description[path=gpx.routes[0].points[0].extensions, type=ComponentType[name=extensions, type=java.util.Optional<org.w3c.dom.Document>, enclosure=BeanType[io.jenetics.jpx.WayPoint]]]
			Description[path=gpx.routes[0].points[0].extensions[0], type=OptionalType[org.w3c.dom.Document]]
			Description[path=gpx.routes[0].points[0].fix, type=ComponentType[name=fix, type=java.util.Optional<io.jenetics.jpx.Fix>, enclosure=BeanType[io.jenetics.jpx.WayPoint]]]
			Description[path=gpx.routes[0].points[0].fix[0], type=OptionalType[io.jenetics.jpx.Fix]]
			Description[path=gpx.routes[0].points[0].geoidHeight, type=ComponentType[name=geoidHeight, type=java.util.Optional<io.jenetics.jpx.Length>, enclosure=BeanType[io.jenetics.jpx.WayPoint]]]
			Description[path=gpx.routes[0].points[0].geoidHeight[0], type=OptionalType[io.jenetics.jpx.Length]]
			Description[path=gpx.routes[0].points[0].hdop, type=ComponentType[name=hdop, type=java.util.Optional<java.lang.Double>, enclosure=BeanType[io.jenetics.jpx.WayPoint]]]
			Description[path=gpx.routes[0].points[0].hdop[0], type=OptionalType[java.lang.Double]]
			Description[path=gpx.routes[0].points[0].latitude, type=ComponentType[name=latitude, type=io.jenetics.jpx.Latitude, enclosure=BeanType[io.jenetics.jpx.WayPoint]]]
			Description[path=gpx.routes[0].points[0].links, type=ComponentType[name=links, type=java.util.List<io.jenetics.jpx.Link>, enclosure=BeanType[io.jenetics.jpx.WayPoint]]]
			Description[path=gpx.routes[0].points[0].links[0], type=ListType[java.util.List[io.jenetics.jpx.Link]]]
			Description[path=gpx.routes[0].points[0].longitude, type=ComponentType[name=longitude, type=io.jenetics.jpx.Longitude, enclosure=BeanType[io.jenetics.jpx.WayPoint]]]
			Description[path=gpx.routes[0].points[0].magneticVariation, type=ComponentType[name=magneticVariation, type=java.util.Optional<io.jenetics.jpx.Degrees>, enclosure=BeanType[io.jenetics.jpx.WayPoint]]]
			Description[path=gpx.routes[0].points[0].magneticVariation[0], type=OptionalType[io.jenetics.jpx.Degrees]]
			Description[path=gpx.routes[0].points[0].name, type=ComponentType[name=name, type=java.util.Optional<java.lang.String>, enclosure=BeanType[io.jenetics.jpx.WayPoint]]]
			Description[path=gpx.routes[0].points[0].name[0], type=OptionalType[java.lang.String]]
			Description[path=gpx.routes[0].points[0].pdop, type=ComponentType[name=pdop, type=java.util.Optional<java.lang.Double>, enclosure=BeanType[io.jenetics.jpx.WayPoint]]]
			Description[path=gpx.routes[0].points[0].pdop[0], type=OptionalType[java.lang.Double]]
			Description[path=gpx.routes[0].points[0].sat, type=ComponentType[name=sat, type=java.util.Optional<io.jenetics.jpx.UInt>, enclosure=BeanType[io.jenetics.jpx.WayPoint]]]
			Description[path=gpx.routes[0].points[0].sat[0], type=OptionalType[io.jenetics.jpx.UInt]]
			Description[path=gpx.routes[0].points[0].source, type=ComponentType[name=source, type=java.util.Optional<java.lang.String>, enclosure=BeanType[io.jenetics.jpx.WayPoint]]]
			Description[path=gpx.routes[0].points[0].source[0], type=OptionalType[java.lang.String]]
			Description[path=gpx.routes[0].points[0].speed, type=ComponentType[name=speed, type=java.util.Optional<io.jenetics.jpx.Speed>, enclosure=BeanType[io.jenetics.jpx.WayPoint]]]
			Description[path=gpx.routes[0].points[0].speed[0], type=OptionalType[io.jenetics.jpx.Speed]]
			Description[path=gpx.routes[0].points[0].symbol, type=ComponentType[name=symbol, type=java.util.Optional<java.lang.String>, enclosure=BeanType[io.jenetics.jpx.WayPoint]]]
			Description[path=gpx.routes[0].points[0].symbol[0], type=OptionalType[java.lang.String]]
			Description[path=gpx.routes[0].points[0].type, type=ComponentType[name=type, type=java.util.Optional<java.lang.String>, enclosure=BeanType[io.jenetics.jpx.WayPoint]]]
			Description[path=gpx.routes[0].points[0].type[0], type=OptionalType[java.lang.String]]
			Description[path=gpx.routes[0].points[0].vdop, type=ComponentType[name=vdop, type=java.util.Optional<java.lang.Double>, enclosure=BeanType[io.jenetics.jpx.WayPoint]]]
			Description[path=gpx.routes[0].points[0].vdop[0], type=OptionalType[java.lang.Double]]
			Description[path=gpx.routes[0].source, type=ComponentType[name=source, type=java.util.Optional<java.lang.String>, enclosure=BeanType[io.jenetics.jpx.Route]]]
			Description[path=gpx.routes[0].source[0], type=OptionalType[java.lang.String]]
			Description[path=gpx.routes[0].type, type=ComponentType[name=type, type=java.util.Optional<java.lang.String>, enclosure=BeanType[io.jenetics.jpx.Route]]]
			Description[path=gpx.routes[0].type[0], type=OptionalType[java.lang.String]]
			Description[path=gpx.tracks, type=ComponentType[name=tracks, type=java.util.List<io.jenetics.jpx.Track>, enclosure=BeanType[io.jenetics.jpx.GPX]]]
			Description[path=gpx.tracks[0], type=ListType[java.util.List[io.jenetics.jpx.Track]]]
			Description[path=gpx.tracks[0].comment, type=ComponentType[name=comment, type=java.util.Optional<java.lang.String>, enclosure=BeanType[io.jenetics.jpx.Track]]]
			Description[path=gpx.tracks[0].comment[0], type=OptionalType[java.lang.String]]
			Description[path=gpx.tracks[0].description, type=ComponentType[name=description, type=java.util.Optional<java.lang.String>, enclosure=BeanType[io.jenetics.jpx.Track]]]
			Description[path=gpx.tracks[0].description[0], type=OptionalType[java.lang.String]]
			Description[path=gpx.tracks[0].empty, type=ComponentType[name=empty, type=boolean, enclosure=BeanType[io.jenetics.jpx.Track]]]
			Description[path=gpx.tracks[0].extensions, type=ComponentType[name=extensions, type=java.util.Optional<org.w3c.dom.Document>, enclosure=BeanType[io.jenetics.jpx.Track]]]
			Description[path=gpx.tracks[0].extensions[0], type=OptionalType[org.w3c.dom.Document]]
			Description[path=gpx.tracks[0].links, type=ComponentType[name=links, type=java.util.List<io.jenetics.jpx.Link>, enclosure=BeanType[io.jenetics.jpx.Track]]]
			Description[path=gpx.tracks[0].links[0], type=ListType[java.util.List[io.jenetics.jpx.Link]]]
			Description[path=gpx.tracks[0].name, type=ComponentType[name=name, type=java.util.Optional<java.lang.String>, enclosure=BeanType[io.jenetics.jpx.Track]]]
			Description[path=gpx.tracks[0].name[0], type=OptionalType[java.lang.String]]
			Description[path=gpx.tracks[0].number, type=ComponentType[name=number, type=java.util.Optional<io.jenetics.jpx.UInt>, enclosure=BeanType[io.jenetics.jpx.Track]]]
			Description[path=gpx.tracks[0].number[0], type=OptionalType[io.jenetics.jpx.UInt]]
			Description[path=gpx.tracks[0].segments, type=ComponentType[name=segments, type=java.util.List<io.jenetics.jpx.TrackSegment>, enclosure=BeanType[io.jenetics.jpx.Track]]]
			Description[path=gpx.tracks[0].segments[0], type=ListType[java.util.List[io.jenetics.jpx.TrackSegment]]]
			Description[path=gpx.tracks[0].segments[0].empty, type=ComponentType[name=empty, type=boolean, enclosure=BeanType[io.jenetics.jpx.TrackSegment]]]
			Description[path=gpx.tracks[0].segments[0].extensions, type=ComponentType[name=extensions, type=java.util.Optional<org.w3c.dom.Document>, enclosure=BeanType[io.jenetics.jpx.TrackSegment]]]
			Description[path=gpx.tracks[0].segments[0].extensions[0], type=OptionalType[org.w3c.dom.Document]]
			Description[path=gpx.tracks[0].segments[0].points, type=ComponentType[name=points, type=java.util.List<io.jenetics.jpx.WayPoint>, enclosure=BeanType[io.jenetics.jpx.TrackSegment]]]
			Description[path=gpx.tracks[0].segments[0].points[0], type=ListType[java.util.List[io.jenetics.jpx.WayPoint]]]
			Description[path=gpx.tracks[0].source, type=ComponentType[name=source, type=java.util.Optional<java.lang.String>, enclosure=BeanType[io.jenetics.jpx.Track]]]
			Description[path=gpx.tracks[0].source[0], type=OptionalType[java.lang.String]]
			Description[path=gpx.tracks[0].type, type=ComponentType[name=type, type=java.util.Optional<java.lang.String>, enclosure=BeanType[io.jenetics.jpx.Track]]]
			Description[path=gpx.tracks[0].type[0], type=OptionalType[java.lang.String]]
			Description[path=gpx.version, type=ComponentType[name=version, type=java.lang.String, enclosure=BeanType[io.jenetics.jpx.GPX]]]
			Description[path=gpx.wayPoints, type=ComponentType[name=wayPoints, type=java.util.List<io.jenetics.jpx.WayPoint>, enclosure=BeanType[io.jenetics.jpx.GPX]]]
			Description[path=gpx.wayPoints[0], type=ListType[java.util.List[io.jenetics.jpx.WayPoint]]]
			""".split("\n");

		assertThat(descriptions).isEqualTo(
			Stream.of(expected)
				.filter(d ->
					!d.contains("gpx.routes[0].points[0].time") &&
					!d.contains("gpx.routes[0].points[0].elevation"))
				.toArray(String[]::new)
		);
	}


}

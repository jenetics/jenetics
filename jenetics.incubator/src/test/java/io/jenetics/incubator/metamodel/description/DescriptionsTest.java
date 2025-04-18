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
package io.jenetics.incubator.metamodel.description;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static org.assertj.core.api.Assertions.assertThat;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Comparator;
import java.util.List;
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
public class DescriptionsTest {

	@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
	@Target({METHOD, TYPE})
	public @interface Anno_1 { }

	@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
	@Target({METHOD, TYPE})
	public @interface Anno_2 { }

	@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
	@Target({METHOD, TYPE})
	public @interface Anno_3 { }

	@Test
	public void annotations() {
		interface Data1 { @Anno_1 String value(); }
		interface Data2 { @Anno_2 String value(); }
		record DataRecord(@Anno_3 String value) implements Data1, Data2 { }

		final var desc = Descriptions.list(DataRecord.class).toList().getFirst();
		assertThat(desc.annotations()).hasSize(3);
		assertThat(desc.annotations().map(Annotation::annotationType).toArray())
			.containsAll(List.of(Anno_1.class, Anno_2.class, Anno_3.class));
	}

	@Test
	public void extractLibrary() {
		final var descriptions = Descriptions
			.list(PathValue.of(Path.of("library"), Library.class))
			.sorted(Comparator.comparing(Description::path))
			.map(Description::toString)
			.toArray(String[]::new);

		final var expected = """
			Description[path=library.books, type=java.util.List<io.jenetics.incubator.metamodel.model.Book>, enclosure=io.jenetics.incubator.metamodel.model.Library]
			Description[path=library.name, type=java.lang.String, enclosure=io.jenetics.incubator.metamodel.model.Library]
			""".split("\n");

		assertThat(descriptions).isEqualTo(expected);
	}

	@Test
	public void extractBook() {
		final var descriptions = Descriptions
			.list(PathValue.of(Book.class))
			.sorted(Comparator.comparing(Description::path))
			.map(Description::toString)
			.peek(System.out::println)
			.toArray(String[]::new);

		final var expected = """
			Description[path=authors, type=java.util.List<io.jenetics.incubator.metamodel.model.Author>, enclosure=io.jenetics.incubator.metamodel.model.Book]
			Description[path=pages, type=int, enclosure=io.jenetics.incubator.metamodel.model.Book]
			Description[path=title, type=java.lang.String, enclosure=io.jenetics.incubator.metamodel.model.Book]
			""".split("\n");

		assertThat(descriptions).isEqualTo(expected);
	}

	@Test
	public void extractAuthor() {
		final var descriptions = Descriptions
			.list(PathValue.of(Author.class))
			.sorted(Comparator.comparing(Description::path))
			.map(Description::toString)
			.toArray(String[]::new);

		final var expected = """
			Description[path=birthDate, type=java.time.LocalDate, enclosure=io.jenetics.incubator.metamodel.model.Author]
			Description[path=books, type=java.util.List<io.jenetics.incubator.metamodel.model.Book>, enclosure=io.jenetics.incubator.metamodel.model.Author]
			Description[path=forename, type=java.lang.String, enclosure=io.jenetics.incubator.metamodel.model.Author]
			Description[path=surname, type=java.lang.String, enclosure=io.jenetics.incubator.metamodel.model.Author]
			""".split("\n");

		assertThat(descriptions).isEqualTo(expected);
	}

	@Test
	public void extractGPX() {
		final var descriptions = Descriptions
			.list(PathValue.of(GPX.class))
			.sorted(Comparator.comparing(Description::path))
			.map(Description::toString)
			.toArray(String[]::new);

		final var expected = """
			Description[path=creator, type=java.lang.String, enclosure=io.jenetics.jpx.GPX]
			Description[path=extensions, type=java.util.Optional<org.w3c.dom.Document>, enclosure=io.jenetics.jpx.GPX]
			Description[path=metadata, type=java.util.Optional<io.jenetics.jpx.Metadata>, enclosure=io.jenetics.jpx.GPX]
			Description[path=routes, type=java.util.List<io.jenetics.jpx.Route>, enclosure=io.jenetics.jpx.GPX]
			Description[path=tracks, type=java.util.List<io.jenetics.jpx.Track>, enclosure=io.jenetics.jpx.GPX]
			Description[path=version, type=java.lang.String, enclosure=io.jenetics.jpx.GPX]
			Description[path=wayPoints, type=java.util.List<io.jenetics.jpx.WayPoint>, enclosure=io.jenetics.jpx.GPX]
			""".split("\n");

		assertThat(descriptions).isEqualTo(expected);
	}

	@Test
	public void walkLibrary() {
		final var descriptions = Descriptions
			.walk(PathValue.of(Path.of("library"), Library.class))
			.sorted(Comparator.comparing(Description::path))
			.map(Description::toString)
			.toArray(String[]::new);

		final var expected = """
			Description[path=library.books, type=java.util.List<io.jenetics.incubator.metamodel.model.Book>, enclosure=io.jenetics.incubator.metamodel.model.Library]
			Description[path=library.books[0], type=io.jenetics.incubator.metamodel.model.Book, enclosure=java.util.List]
			Description[path=library.books[0].authors, type=java.util.List<io.jenetics.incubator.metamodel.model.Author>, enclosure=io.jenetics.incubator.metamodel.model.Book]
			Description[path=library.books[0].authors[0], type=io.jenetics.incubator.metamodel.model.Author, enclosure=java.util.List]
			Description[path=library.books[0].authors[0].birthDate, type=java.time.LocalDate, enclosure=io.jenetics.incubator.metamodel.model.Author]
			Description[path=library.books[0].authors[0].books, type=java.util.List<io.jenetics.incubator.metamodel.model.Book>, enclosure=io.jenetics.incubator.metamodel.model.Author]
			Description[path=library.books[0].authors[0].books[0], type=io.jenetics.incubator.metamodel.model.Book, enclosure=java.util.List]
			Description[path=library.books[0].authors[0].forename, type=java.lang.String, enclosure=io.jenetics.incubator.metamodel.model.Author]
			Description[path=library.books[0].authors[0].surname, type=java.lang.String, enclosure=io.jenetics.incubator.metamodel.model.Author]
			Description[path=library.books[0].pages, type=int, enclosure=io.jenetics.incubator.metamodel.model.Book]
			Description[path=library.books[0].title, type=java.lang.String, enclosure=io.jenetics.incubator.metamodel.model.Book]
			Description[path=library.name, type=java.lang.String, enclosure=io.jenetics.incubator.metamodel.model.Library]
			""".split("\n");

		assertThat(descriptions).isEqualTo(expected);
	}

	@Test
	public void walkGPX() {
		final var descriptions = Descriptions
			.walk(PathValue.of(Path.of("gpx"), GPX.class))
			.sorted(Comparator.comparing(Description::path))
			.map(Description::toString)
			.filter(d ->
				!d.contains("gpx.routes[0].points[0].time") &&
				!d.contains("gpx.routes[0].points[0].elevation"))
			.toArray(String[]::new);

		final var expected = """
			Description[path=gpx.creator, type=java.lang.String, enclosure=io.jenetics.jpx.GPX]
			Description[path=gpx.extensions, type=java.util.Optional<org.w3c.dom.Document>, enclosure=io.jenetics.jpx.GPX]
			Description[path=gpx.extensions[0], type=org.w3c.dom.Document, enclosure=java.util.Optional]
			Description[path=gpx.extensions[0].doctype, type=org.w3c.dom.DocumentType, enclosure=org.w3c.dom.Document]
			Description[path=gpx.extensions[0].doctype.entities, type=org.w3c.dom.NamedNodeMap, enclosure=org.w3c.dom.DocumentType]
			Description[path=gpx.extensions[0].doctype.entities.length, type=int, enclosure=org.w3c.dom.NamedNodeMap]
			Description[path=gpx.extensions[0].doctype.internalSubset, type=java.lang.String, enclosure=org.w3c.dom.DocumentType]
			Description[path=gpx.extensions[0].doctype.name, type=java.lang.String, enclosure=org.w3c.dom.DocumentType]
			Description[path=gpx.extensions[0].doctype.notations, type=org.w3c.dom.NamedNodeMap, enclosure=org.w3c.dom.DocumentType]
			Description[path=gpx.extensions[0].doctype.publicId, type=java.lang.String, enclosure=org.w3c.dom.DocumentType]
			Description[path=gpx.extensions[0].doctype.systemId, type=java.lang.String, enclosure=org.w3c.dom.DocumentType]
			Description[path=gpx.extensions[0].documentElement, type=org.w3c.dom.Element, enclosure=org.w3c.dom.Document]
			Description[path=gpx.extensions[0].documentElement.schemaTypeInfo, type=org.w3c.dom.TypeInfo, enclosure=org.w3c.dom.Element]
			Description[path=gpx.extensions[0].documentElement.schemaTypeInfo.typeName, type=java.lang.String, enclosure=org.w3c.dom.TypeInfo]
			Description[path=gpx.extensions[0].documentElement.schemaTypeInfo.typeNamespace, type=java.lang.String, enclosure=org.w3c.dom.TypeInfo]
			Description[path=gpx.extensions[0].documentElement.tagName, type=java.lang.String, enclosure=org.w3c.dom.Element]
			Description[path=gpx.extensions[0].documentURI, type=java.lang.String, enclosure=org.w3c.dom.Document]
			Description[path=gpx.extensions[0].domConfig, type=org.w3c.dom.DOMConfiguration, enclosure=org.w3c.dom.Document]
			Description[path=gpx.extensions[0].domConfig.parameterNames, type=org.w3c.dom.DOMStringList, enclosure=org.w3c.dom.DOMConfiguration]
			Description[path=gpx.extensions[0].domConfig.parameterNames.length, type=int, enclosure=org.w3c.dom.DOMStringList]
			Description[path=gpx.extensions[0].implementation, type=org.w3c.dom.DOMImplementation, enclosure=org.w3c.dom.Document]
			Description[path=gpx.extensions[0].inputEncoding, type=java.lang.String, enclosure=org.w3c.dom.Document]
			Description[path=gpx.extensions[0].strictErrorChecking, type=boolean, enclosure=org.w3c.dom.Document]
			Description[path=gpx.extensions[0].xmlEncoding, type=java.lang.String, enclosure=org.w3c.dom.Document]
			Description[path=gpx.extensions[0].xmlStandalone, type=boolean, enclosure=org.w3c.dom.Document]
			Description[path=gpx.extensions[0].xmlVersion, type=java.lang.String, enclosure=org.w3c.dom.Document]
			Description[path=gpx.metadata, type=java.util.Optional<io.jenetics.jpx.Metadata>, enclosure=io.jenetics.jpx.GPX]
			Description[path=gpx.metadata[0], type=io.jenetics.jpx.Metadata, enclosure=java.util.Optional]
			Description[path=gpx.metadata[0].author, type=java.util.Optional<io.jenetics.jpx.Person>, enclosure=io.jenetics.jpx.Metadata]
			Description[path=gpx.metadata[0].author[0], type=io.jenetics.jpx.Person, enclosure=java.util.Optional]
			Description[path=gpx.metadata[0].author[0].email, type=java.util.Optional<io.jenetics.jpx.Email>, enclosure=io.jenetics.jpx.Person]
			Description[path=gpx.metadata[0].author[0].email[0], type=io.jenetics.jpx.Email, enclosure=java.util.Optional]
			Description[path=gpx.metadata[0].author[0].email[0].ID, type=java.lang.String, enclosure=io.jenetics.jpx.Email]
			Description[path=gpx.metadata[0].author[0].email[0].address, type=java.lang.String, enclosure=io.jenetics.jpx.Email]
			Description[path=gpx.metadata[0].author[0].email[0].domain, type=java.lang.String, enclosure=io.jenetics.jpx.Email]
			Description[path=gpx.metadata[0].author[0].empty, type=boolean, enclosure=io.jenetics.jpx.Person]
			Description[path=gpx.metadata[0].author[0].link, type=java.util.Optional<io.jenetics.jpx.Link>, enclosure=io.jenetics.jpx.Person]
			Description[path=gpx.metadata[0].author[0].link[0], type=io.jenetics.jpx.Link, enclosure=java.util.Optional]
			Description[path=gpx.metadata[0].author[0].link[0].href, type=java.net.URI, enclosure=io.jenetics.jpx.Link]
			Description[path=gpx.metadata[0].author[0].link[0].href.absolute, type=boolean, enclosure=java.net.URI]
			Description[path=gpx.metadata[0].author[0].link[0].href.authority, type=java.lang.String, enclosure=java.net.URI]
			Description[path=gpx.metadata[0].author[0].link[0].href.fragment, type=java.lang.String, enclosure=java.net.URI]
			Description[path=gpx.metadata[0].author[0].link[0].href.host, type=java.lang.String, enclosure=java.net.URI]
			Description[path=gpx.metadata[0].author[0].link[0].href.opaque, type=boolean, enclosure=java.net.URI]
			Description[path=gpx.metadata[0].author[0].link[0].href.path, type=java.lang.String, enclosure=java.net.URI]
			Description[path=gpx.metadata[0].author[0].link[0].href.port, type=int, enclosure=java.net.URI]
			Description[path=gpx.metadata[0].author[0].link[0].href.query, type=java.lang.String, enclosure=java.net.URI]
			Description[path=gpx.metadata[0].author[0].link[0].href.rawAuthority, type=java.lang.String, enclosure=java.net.URI]
			Description[path=gpx.metadata[0].author[0].link[0].href.rawFragment, type=java.lang.String, enclosure=java.net.URI]
			Description[path=gpx.metadata[0].author[0].link[0].href.rawPath, type=java.lang.String, enclosure=java.net.URI]
			Description[path=gpx.metadata[0].author[0].link[0].href.rawQuery, type=java.lang.String, enclosure=java.net.URI]
			Description[path=gpx.metadata[0].author[0].link[0].href.rawSchemeSpecificPart, type=java.lang.String, enclosure=java.net.URI]
			Description[path=gpx.metadata[0].author[0].link[0].href.rawUserInfo, type=java.lang.String, enclosure=java.net.URI]
			Description[path=gpx.metadata[0].author[0].link[0].href.scheme, type=java.lang.String, enclosure=java.net.URI]
			Description[path=gpx.metadata[0].author[0].link[0].href.schemeSpecificPart, type=java.lang.String, enclosure=java.net.URI]
			Description[path=gpx.metadata[0].author[0].link[0].href.userInfo, type=java.lang.String, enclosure=java.net.URI]
			Description[path=gpx.metadata[0].author[0].link[0].text, type=java.util.Optional<java.lang.String>, enclosure=io.jenetics.jpx.Link]
			Description[path=gpx.metadata[0].author[0].link[0].text[0], type=java.lang.String, enclosure=java.util.Optional]
			Description[path=gpx.metadata[0].author[0].link[0].type, type=java.util.Optional<java.lang.String>, enclosure=io.jenetics.jpx.Link]
			Description[path=gpx.metadata[0].author[0].link[0].type[0], type=java.lang.String, enclosure=java.util.Optional]
			Description[path=gpx.metadata[0].author[0].name, type=java.util.Optional<java.lang.String>, enclosure=io.jenetics.jpx.Person]
			Description[path=gpx.metadata[0].author[0].name[0], type=java.lang.String, enclosure=java.util.Optional]
			Description[path=gpx.metadata[0].bounds, type=java.util.Optional<io.jenetics.jpx.Bounds>, enclosure=io.jenetics.jpx.Metadata]
			Description[path=gpx.metadata[0].bounds[0], type=io.jenetics.jpx.Bounds, enclosure=java.util.Optional]
			Description[path=gpx.metadata[0].bounds[0].maxLatitude, type=io.jenetics.jpx.Latitude, enclosure=io.jenetics.jpx.Bounds]
			Description[path=gpx.metadata[0].bounds[0].maxLongitude, type=io.jenetics.jpx.Longitude, enclosure=io.jenetics.jpx.Bounds]
			Description[path=gpx.metadata[0].bounds[0].minLatitude, type=io.jenetics.jpx.Latitude, enclosure=io.jenetics.jpx.Bounds]
			Description[path=gpx.metadata[0].bounds[0].minLongitude, type=io.jenetics.jpx.Longitude, enclosure=io.jenetics.jpx.Bounds]
			Description[path=gpx.metadata[0].copyright, type=java.util.Optional<io.jenetics.jpx.Copyright>, enclosure=io.jenetics.jpx.Metadata]
			Description[path=gpx.metadata[0].copyright[0], type=io.jenetics.jpx.Copyright, enclosure=java.util.Optional]
			Description[path=gpx.metadata[0].copyright[0].author, type=java.lang.String, enclosure=io.jenetics.jpx.Copyright]
			Description[path=gpx.metadata[0].copyright[0].license, type=java.util.Optional<java.net.URI>, enclosure=io.jenetics.jpx.Copyright]
			Description[path=gpx.metadata[0].copyright[0].license[0], type=java.net.URI, enclosure=java.util.Optional]
			Description[path=gpx.metadata[0].copyright[0].year, type=java.util.Optional<java.time.Year>, enclosure=io.jenetics.jpx.Copyright]
			Description[path=gpx.metadata[0].copyright[0].year[0], type=java.time.Year, enclosure=java.util.Optional]
			Description[path=gpx.metadata[0].description, type=java.util.Optional<java.lang.String>, enclosure=io.jenetics.jpx.Metadata]
			Description[path=gpx.metadata[0].description[0], type=java.lang.String, enclosure=java.util.Optional]
			Description[path=gpx.metadata[0].empty, type=boolean, enclosure=io.jenetics.jpx.Metadata]
			Description[path=gpx.metadata[0].extensions, type=java.util.Optional<org.w3c.dom.Document>, enclosure=io.jenetics.jpx.Metadata]
			Description[path=gpx.metadata[0].extensions[0], type=org.w3c.dom.Document, enclosure=java.util.Optional]
			Description[path=gpx.metadata[0].keywords, type=java.util.Optional<java.lang.String>, enclosure=io.jenetics.jpx.Metadata]
			Description[path=gpx.metadata[0].keywords[0], type=java.lang.String, enclosure=java.util.Optional]
			Description[path=gpx.metadata[0].links, type=java.util.List<io.jenetics.jpx.Link>, enclosure=io.jenetics.jpx.Metadata]
			Description[path=gpx.metadata[0].links[0], type=io.jenetics.jpx.Link, enclosure=java.util.List]
			Description[path=gpx.metadata[0].name, type=java.util.Optional<java.lang.String>, enclosure=io.jenetics.jpx.Metadata]
			Description[path=gpx.metadata[0].name[0], type=java.lang.String, enclosure=java.util.Optional]
			Description[path=gpx.metadata[0].time, type=java.util.Optional<java.time.Instant>, enclosure=io.jenetics.jpx.Metadata]
			Description[path=gpx.metadata[0].time[0], type=java.time.Instant, enclosure=java.util.Optional]
			Description[path=gpx.routes, type=java.util.List<io.jenetics.jpx.Route>, enclosure=io.jenetics.jpx.GPX]
			Description[path=gpx.routes[0], type=io.jenetics.jpx.Route, enclosure=java.util.List]
			Description[path=gpx.routes[0].comment, type=java.util.Optional<java.lang.String>, enclosure=io.jenetics.jpx.Route]
			Description[path=gpx.routes[0].comment[0], type=java.lang.String, enclosure=java.util.Optional]
			Description[path=gpx.routes[0].description, type=java.util.Optional<java.lang.String>, enclosure=io.jenetics.jpx.Route]
			Description[path=gpx.routes[0].description[0], type=java.lang.String, enclosure=java.util.Optional]
			Description[path=gpx.routes[0].empty, type=boolean, enclosure=io.jenetics.jpx.Route]
			Description[path=gpx.routes[0].extensions, type=java.util.Optional<org.w3c.dom.Document>, enclosure=io.jenetics.jpx.Route]
			Description[path=gpx.routes[0].extensions[0], type=org.w3c.dom.Document, enclosure=java.util.Optional]
			Description[path=gpx.routes[0].links, type=java.util.List<io.jenetics.jpx.Link>, enclosure=io.jenetics.jpx.Route]
			Description[path=gpx.routes[0].links[0], type=io.jenetics.jpx.Link, enclosure=java.util.List]
			Description[path=gpx.routes[0].name, type=java.util.Optional<java.lang.String>, enclosure=io.jenetics.jpx.Route]
			Description[path=gpx.routes[0].name[0], type=java.lang.String, enclosure=java.util.Optional]
			Description[path=gpx.routes[0].number, type=java.util.Optional<io.jenetics.jpx.UInt>, enclosure=io.jenetics.jpx.Route]
			Description[path=gpx.routes[0].number[0], type=io.jenetics.jpx.UInt, enclosure=java.util.Optional]
			Description[path=gpx.routes[0].number[0].value, type=int, enclosure=io.jenetics.jpx.UInt]
			Description[path=gpx.routes[0].points, type=java.util.List<io.jenetics.jpx.WayPoint>, enclosure=io.jenetics.jpx.Route]
			Description[path=gpx.routes[0].points[0], type=io.jenetics.jpx.WayPoint, enclosure=java.util.List]
			Description[path=gpx.routes[0].points[0].DGPSID, type=java.util.Optional<io.jenetics.jpx.DGPSStation>, enclosure=io.jenetics.jpx.WayPoint]
			Description[path=gpx.routes[0].points[0].DGPSID[0], type=io.jenetics.jpx.DGPSStation, enclosure=java.util.Optional]
			Description[path=gpx.routes[0].points[0].ageOfGPSData, type=java.util.Optional<java.time.Duration>, enclosure=io.jenetics.jpx.WayPoint]
			Description[path=gpx.routes[0].points[0].ageOfGPSData[0], type=java.time.Duration, enclosure=java.util.Optional]
			Description[path=gpx.routes[0].points[0].ageOfGPSData[0].nano, type=int, enclosure=java.time.Duration]
			Description[path=gpx.routes[0].points[0].ageOfGPSData[0].negative, type=boolean, enclosure=java.time.Duration]
			Description[path=gpx.routes[0].points[0].ageOfGPSData[0].positive, type=boolean, enclosure=java.time.Duration]
			Description[path=gpx.routes[0].points[0].ageOfGPSData[0].seconds, type=long, enclosure=java.time.Duration]
			Description[path=gpx.routes[0].points[0].ageOfGPSData[0].units, type=java.util.List<java.time.temporal.TemporalUnit>, enclosure=java.time.Duration]
			Description[path=gpx.routes[0].points[0].ageOfGPSData[0].units[0], type=java.time.temporal.TemporalUnit, enclosure=java.util.List]
			Description[path=gpx.routes[0].points[0].ageOfGPSData[0].units[0].dateBased, type=boolean, enclosure=java.time.temporal.TemporalUnit]
			Description[path=gpx.routes[0].points[0].ageOfGPSData[0].units[0].duration, type=java.time.Duration, enclosure=java.time.temporal.TemporalUnit]
			Description[path=gpx.routes[0].points[0].ageOfGPSData[0].units[0].durationEstimated, type=boolean, enclosure=java.time.temporal.TemporalUnit]
			Description[path=gpx.routes[0].points[0].ageOfGPSData[0].units[0].timeBased, type=boolean, enclosure=java.time.temporal.TemporalUnit]
			Description[path=gpx.routes[0].points[0].ageOfGPSData[0].zero, type=boolean, enclosure=java.time.Duration]
			Description[path=gpx.routes[0].points[0].comment, type=java.util.Optional<java.lang.String>, enclosure=io.jenetics.jpx.WayPoint]
			Description[path=gpx.routes[0].points[0].comment[0], type=java.lang.String, enclosure=java.util.Optional]
			Description[path=gpx.routes[0].points[0].course, type=java.util.Optional<io.jenetics.jpx.Degrees>, enclosure=io.jenetics.jpx.WayPoint]
			Description[path=gpx.routes[0].points[0].course[0], type=io.jenetics.jpx.Degrees, enclosure=java.util.Optional]
			Description[path=gpx.routes[0].points[0].description, type=java.util.Optional<java.lang.String>, enclosure=io.jenetics.jpx.WayPoint]
			Description[path=gpx.routes[0].points[0].description[0], type=java.lang.String, enclosure=java.util.Optional]
			Description[path=gpx.routes[0].points[0].extensions, type=java.util.Optional<org.w3c.dom.Document>, enclosure=io.jenetics.jpx.WayPoint]
			Description[path=gpx.routes[0].points[0].extensions[0], type=org.w3c.dom.Document, enclosure=java.util.Optional]
			Description[path=gpx.routes[0].points[0].fix, type=java.util.Optional<io.jenetics.jpx.Fix>, enclosure=io.jenetics.jpx.WayPoint]
			Description[path=gpx.routes[0].points[0].fix[0], type=io.jenetics.jpx.Fix, enclosure=java.util.Optional]
			Description[path=gpx.routes[0].points[0].geoidHeight, type=java.util.Optional<io.jenetics.jpx.Length>, enclosure=io.jenetics.jpx.WayPoint]
			Description[path=gpx.routes[0].points[0].geoidHeight[0], type=io.jenetics.jpx.Length, enclosure=java.util.Optional]
			Description[path=gpx.routes[0].points[0].hdop, type=java.util.Optional<java.lang.Double>, enclosure=io.jenetics.jpx.WayPoint]
			Description[path=gpx.routes[0].points[0].hdop[0], type=java.lang.Double, enclosure=java.util.Optional]
			Description[path=gpx.routes[0].points[0].latitude, type=io.jenetics.jpx.Latitude, enclosure=io.jenetics.jpx.WayPoint]
			Description[path=gpx.routes[0].points[0].links, type=java.util.List<io.jenetics.jpx.Link>, enclosure=io.jenetics.jpx.WayPoint]
			Description[path=gpx.routes[0].points[0].links[0], type=io.jenetics.jpx.Link, enclosure=java.util.List]
			Description[path=gpx.routes[0].points[0].longitude, type=io.jenetics.jpx.Longitude, enclosure=io.jenetics.jpx.WayPoint]
			Description[path=gpx.routes[0].points[0].magneticVariation, type=java.util.Optional<io.jenetics.jpx.Degrees>, enclosure=io.jenetics.jpx.WayPoint]
			Description[path=gpx.routes[0].points[0].magneticVariation[0], type=io.jenetics.jpx.Degrees, enclosure=java.util.Optional]
			Description[path=gpx.routes[0].points[0].name, type=java.util.Optional<java.lang.String>, enclosure=io.jenetics.jpx.WayPoint]
			Description[path=gpx.routes[0].points[0].name[0], type=java.lang.String, enclosure=java.util.Optional]
			Description[path=gpx.routes[0].points[0].pdop, type=java.util.Optional<java.lang.Double>, enclosure=io.jenetics.jpx.WayPoint]
			Description[path=gpx.routes[0].points[0].pdop[0], type=java.lang.Double, enclosure=java.util.Optional]
			Description[path=gpx.routes[0].points[0].sat, type=java.util.Optional<io.jenetics.jpx.UInt>, enclosure=io.jenetics.jpx.WayPoint]
			Description[path=gpx.routes[0].points[0].sat[0], type=io.jenetics.jpx.UInt, enclosure=java.util.Optional]
			Description[path=gpx.routes[0].points[0].source, type=java.util.Optional<java.lang.String>, enclosure=io.jenetics.jpx.WayPoint]
			Description[path=gpx.routes[0].points[0].source[0], type=java.lang.String, enclosure=java.util.Optional]
			Description[path=gpx.routes[0].points[0].speed, type=java.util.Optional<io.jenetics.jpx.Speed>, enclosure=io.jenetics.jpx.WayPoint]
			Description[path=gpx.routes[0].points[0].speed[0], type=io.jenetics.jpx.Speed, enclosure=java.util.Optional]
			Description[path=gpx.routes[0].points[0].symbol, type=java.util.Optional<java.lang.String>, enclosure=io.jenetics.jpx.WayPoint]
			Description[path=gpx.routes[0].points[0].symbol[0], type=java.lang.String, enclosure=java.util.Optional]
			Description[path=gpx.routes[0].points[0].type, type=java.util.Optional<java.lang.String>, enclosure=io.jenetics.jpx.WayPoint]
			Description[path=gpx.routes[0].points[0].type[0], type=java.lang.String, enclosure=java.util.Optional]
			Description[path=gpx.routes[0].points[0].vdop, type=java.util.Optional<java.lang.Double>, enclosure=io.jenetics.jpx.WayPoint]
			Description[path=gpx.routes[0].points[0].vdop[0], type=java.lang.Double, enclosure=java.util.Optional]
			Description[path=gpx.routes[0].source, type=java.util.Optional<java.lang.String>, enclosure=io.jenetics.jpx.Route]
			Description[path=gpx.routes[0].source[0], type=java.lang.String, enclosure=java.util.Optional]
			Description[path=gpx.routes[0].type, type=java.util.Optional<java.lang.String>, enclosure=io.jenetics.jpx.Route]
			Description[path=gpx.routes[0].type[0], type=java.lang.String, enclosure=java.util.Optional]
			Description[path=gpx.tracks, type=java.util.List<io.jenetics.jpx.Track>, enclosure=io.jenetics.jpx.GPX]
			Description[path=gpx.tracks[0], type=io.jenetics.jpx.Track, enclosure=java.util.List]
			Description[path=gpx.tracks[0].comment, type=java.util.Optional<java.lang.String>, enclosure=io.jenetics.jpx.Track]
			Description[path=gpx.tracks[0].comment[0], type=java.lang.String, enclosure=java.util.Optional]
			Description[path=gpx.tracks[0].description, type=java.util.Optional<java.lang.String>, enclosure=io.jenetics.jpx.Track]
			Description[path=gpx.tracks[0].description[0], type=java.lang.String, enclosure=java.util.Optional]
			Description[path=gpx.tracks[0].empty, type=boolean, enclosure=io.jenetics.jpx.Track]
			Description[path=gpx.tracks[0].extensions, type=java.util.Optional<org.w3c.dom.Document>, enclosure=io.jenetics.jpx.Track]
			Description[path=gpx.tracks[0].extensions[0], type=org.w3c.dom.Document, enclosure=java.util.Optional]
			Description[path=gpx.tracks[0].links, type=java.util.List<io.jenetics.jpx.Link>, enclosure=io.jenetics.jpx.Track]
			Description[path=gpx.tracks[0].links[0], type=io.jenetics.jpx.Link, enclosure=java.util.List]
			Description[path=gpx.tracks[0].name, type=java.util.Optional<java.lang.String>, enclosure=io.jenetics.jpx.Track]
			Description[path=gpx.tracks[0].name[0], type=java.lang.String, enclosure=java.util.Optional]
			Description[path=gpx.tracks[0].number, type=java.util.Optional<io.jenetics.jpx.UInt>, enclosure=io.jenetics.jpx.Track]
			Description[path=gpx.tracks[0].number[0], type=io.jenetics.jpx.UInt, enclosure=java.util.Optional]
			Description[path=gpx.tracks[0].segments, type=java.util.List<io.jenetics.jpx.TrackSegment>, enclosure=io.jenetics.jpx.Track]
			Description[path=gpx.tracks[0].segments[0], type=io.jenetics.jpx.TrackSegment, enclosure=java.util.List]
			Description[path=gpx.tracks[0].segments[0].empty, type=boolean, enclosure=io.jenetics.jpx.TrackSegment]
			Description[path=gpx.tracks[0].segments[0].extensions, type=java.util.Optional<org.w3c.dom.Document>, enclosure=io.jenetics.jpx.TrackSegment]
			Description[path=gpx.tracks[0].segments[0].extensions[0], type=org.w3c.dom.Document, enclosure=java.util.Optional]
			Description[path=gpx.tracks[0].segments[0].points, type=java.util.List<io.jenetics.jpx.WayPoint>, enclosure=io.jenetics.jpx.TrackSegment]
			Description[path=gpx.tracks[0].segments[0].points[0], type=io.jenetics.jpx.WayPoint, enclosure=java.util.List]
			Description[path=gpx.tracks[0].source, type=java.util.Optional<java.lang.String>, enclosure=io.jenetics.jpx.Track]
			Description[path=gpx.tracks[0].source[0], type=java.lang.String, enclosure=java.util.Optional]
			Description[path=gpx.tracks[0].type, type=java.util.Optional<java.lang.String>, enclosure=io.jenetics.jpx.Track]
			Description[path=gpx.tracks[0].type[0], type=java.lang.String, enclosure=java.util.Optional]
			Description[path=gpx.version, type=java.lang.String, enclosure=io.jenetics.jpx.GPX]
			Description[path=gpx.wayPoints, type=java.util.List<io.jenetics.jpx.WayPoint>, enclosure=io.jenetics.jpx.GPX]
			Description[path=gpx.wayPoints[0], type=io.jenetics.jpx.WayPoint, enclosure=java.util.List]
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

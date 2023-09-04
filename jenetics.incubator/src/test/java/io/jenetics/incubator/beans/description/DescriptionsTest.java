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
package io.jenetics.incubator.beans.description;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Comparator;
import java.util.stream.Stream;

import org.testng.annotations.Test;

import io.jenetics.incubator.beans.Path;
import io.jenetics.incubator.beans.PathValue;
import io.jenetics.incubator.beans.model.Author;
import io.jenetics.incubator.beans.model.Book;
import io.jenetics.incubator.beans.model.Library;

import io.jenetics.jpx.GPX;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class DescriptionsTest {

	@Test
	public void extractLibrary() {
		final var descriptions = Descriptions
			.unapply(PathValue.of(Path.of("library"), Library.class))
			.sorted(Comparator.comparing(Description::path))
			.map(Description::toString)
			.toArray(String[]::new);

		final var expected = """
			Description[path=library.books, value=Single[value=java.util.List, enclosure=io.jenetics.incubator.beans.model.Library]]
			Description[path=library.name, value=Single[value=java.lang.String, enclosure=io.jenetics.incubator.beans.model.Library]]
			""".split("\n");

		assertThat(descriptions).isEqualTo(expected);
	}

	@Test
	public void extractBook() {
		final var descriptions = Descriptions
			.unapply(PathValue.of(Book.class))
			.sorted(Comparator.comparing(Description::path))
			.map(Description::toString)
			.toArray(String[]::new);

		final var expected = """
			Description[path=authors, value=Single[value=java.util.List, enclosure=io.jenetics.incubator.beans.model.Book]]
			Description[path=pages, value=Single[value=int, enclosure=io.jenetics.incubator.beans.model.Book]]
			Description[path=title, value=Single[value=java.lang.String, enclosure=io.jenetics.incubator.beans.model.Book]]
			""".split("\n");

		assertThat(descriptions).isEqualTo(expected);
	}

	@Test
	public void extractAuthor() {
		final var descriptions = Descriptions
			.unapply(PathValue.of(Author.class))
			.sorted(Comparator.comparing(Description::path))
			.map(Description::toString)
			.toArray(String[]::new);

		final var expected = """
			Description[path=birthDate, value=Single[value=java.time.LocalDate, enclosure=io.jenetics.incubator.beans.model.Author]]
			Description[path=books, value=Single[value=java.util.List, enclosure=io.jenetics.incubator.beans.model.Author]]
			Description[path=forename, value=Single[value=java.lang.String, enclosure=io.jenetics.incubator.beans.model.Author]]
			Description[path=surname, value=Single[value=java.lang.String, enclosure=io.jenetics.incubator.beans.model.Author]]
			""".split("\n");

		assertThat(descriptions).isEqualTo(expected);
	}

	@Test
	public void extractGPX() {
		final var descriptions = Descriptions
			.unapply(PathValue.of(GPX.class))
			.sorted(Comparator.comparing(Description::path))
			.map(Description::toString)
			.toArray(String[]::new);

		final var expected = """
			Description[path=creator, value=Single[value=java.lang.String, enclosure=io.jenetics.jpx.GPX]]
			Description[path=extensions, value=Single[value=java.util.Optional, enclosure=io.jenetics.jpx.GPX]]
			Description[path=metadata, value=Single[value=java.util.Optional, enclosure=io.jenetics.jpx.GPX]]
			Description[path=routes, value=Single[value=java.util.List, enclosure=io.jenetics.jpx.GPX]]
			Description[path=tracks, value=Single[value=java.util.List, enclosure=io.jenetics.jpx.GPX]]
			Description[path=version, value=Single[value=java.lang.String, enclosure=io.jenetics.jpx.GPX]]
			Description[path=wayPoints, value=Single[value=java.util.List, enclosure=io.jenetics.jpx.GPX]]
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
			Description[path=library.books, value=Single[value=java.util.List, enclosure=io.jenetics.incubator.beans.model.Library]]
			Description[path=library.books[0], value=Indexed[value=io.jenetics.incubator.beans.model.Book, enclosure=java.util.List]]
			Description[path=library.books[0].authors, value=Single[value=java.util.List, enclosure=io.jenetics.incubator.beans.model.Book]]
			Description[path=library.books[0].authors[0], value=Indexed[value=io.jenetics.incubator.beans.model.Author, enclosure=java.util.List]]
			Description[path=library.books[0].authors[0].birthDate, value=Single[value=java.time.LocalDate, enclosure=io.jenetics.incubator.beans.model.Author]]
			Description[path=library.books[0].authors[0].books, value=Single[value=java.util.List, enclosure=io.jenetics.incubator.beans.model.Author]]
			Description[path=library.books[0].authors[0].books[0], value=Indexed[value=io.jenetics.incubator.beans.model.Book, enclosure=java.util.List]]
			Description[path=library.books[0].authors[0].forename, value=Single[value=java.lang.String, enclosure=io.jenetics.incubator.beans.model.Author]]
			Description[path=library.books[0].authors[0].surname, value=Single[value=java.lang.String, enclosure=io.jenetics.incubator.beans.model.Author]]
			Description[path=library.books[0].pages, value=Single[value=int, enclosure=io.jenetics.incubator.beans.model.Book]]
			Description[path=library.books[0].title, value=Single[value=java.lang.String, enclosure=io.jenetics.incubator.beans.model.Book]]
			Description[path=library.name, value=Single[value=java.lang.String, enclosure=io.jenetics.incubator.beans.model.Library]]
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
			Description[path=gpx.creator, value=Single[value=java.lang.String, enclosure=io.jenetics.jpx.GPX]]
			Description[path=gpx.extensions, value=Single[value=java.util.Optional, enclosure=io.jenetics.jpx.GPX]]
			Description[path=gpx.extensions[0], value=Indexed[value=org.w3c.dom.Document, enclosure=java.util.Optional]]
			Description[path=gpx.extensions[0].doctype, value=Single[value=org.w3c.dom.DocumentType, enclosure=org.w3c.dom.Document]]
			Description[path=gpx.extensions[0].doctype.entities, value=Single[value=org.w3c.dom.NamedNodeMap, enclosure=org.w3c.dom.DocumentType]]
			Description[path=gpx.extensions[0].doctype.entities.length, value=Single[value=int, enclosure=org.w3c.dom.NamedNodeMap]]
			Description[path=gpx.extensions[0].doctype.internalSubset, value=Single[value=java.lang.String, enclosure=org.w3c.dom.DocumentType]]
			Description[path=gpx.extensions[0].doctype.name, value=Single[value=java.lang.String, enclosure=org.w3c.dom.DocumentType]]
			Description[path=gpx.extensions[0].doctype.notations, value=Single[value=org.w3c.dom.NamedNodeMap, enclosure=org.w3c.dom.DocumentType]]
			Description[path=gpx.extensions[0].doctype.publicId, value=Single[value=java.lang.String, enclosure=org.w3c.dom.DocumentType]]
			Description[path=gpx.extensions[0].doctype.systemId, value=Single[value=java.lang.String, enclosure=org.w3c.dom.DocumentType]]
			Description[path=gpx.extensions[0].documentElement, value=Single[value=org.w3c.dom.Element, enclosure=org.w3c.dom.Document]]
			Description[path=gpx.extensions[0].documentElement.schemaTypeInfo, value=Single[value=org.w3c.dom.TypeInfo, enclosure=org.w3c.dom.Element]]
			Description[path=gpx.extensions[0].documentElement.schemaTypeInfo.typeName, value=Single[value=java.lang.String, enclosure=org.w3c.dom.TypeInfo]]
			Description[path=gpx.extensions[0].documentElement.schemaTypeInfo.typeNamespace, value=Single[value=java.lang.String, enclosure=org.w3c.dom.TypeInfo]]
			Description[path=gpx.extensions[0].documentElement.tagName, value=Single[value=java.lang.String, enclosure=org.w3c.dom.Element]]
			Description[path=gpx.extensions[0].documentURI, value=Single[value=java.lang.String, enclosure=org.w3c.dom.Document]]
			Description[path=gpx.extensions[0].domConfig, value=Single[value=org.w3c.dom.DOMConfiguration, enclosure=org.w3c.dom.Document]]
			Description[path=gpx.extensions[0].domConfig.parameterNames, value=Single[value=org.w3c.dom.DOMStringList, enclosure=org.w3c.dom.DOMConfiguration]]
			Description[path=gpx.extensions[0].domConfig.parameterNames.length, value=Single[value=int, enclosure=org.w3c.dom.DOMStringList]]
			Description[path=gpx.extensions[0].implementation, value=Single[value=org.w3c.dom.DOMImplementation, enclosure=org.w3c.dom.Document]]
			Description[path=gpx.extensions[0].inputEncoding, value=Single[value=java.lang.String, enclosure=org.w3c.dom.Document]]
			Description[path=gpx.extensions[0].strictErrorChecking, value=Single[value=boolean, enclosure=org.w3c.dom.Document]]
			Description[path=gpx.extensions[0].xmlEncoding, value=Single[value=java.lang.String, enclosure=org.w3c.dom.Document]]
			Description[path=gpx.extensions[0].xmlStandalone, value=Single[value=boolean, enclosure=org.w3c.dom.Document]]
			Description[path=gpx.extensions[0].xmlVersion, value=Single[value=java.lang.String, enclosure=org.w3c.dom.Document]]
			Description[path=gpx.metadata, value=Single[value=java.util.Optional, enclosure=io.jenetics.jpx.GPX]]
			Description[path=gpx.metadata[0], value=Indexed[value=io.jenetics.jpx.Metadata, enclosure=java.util.Optional]]
			Description[path=gpx.metadata[0].author, value=Single[value=java.util.Optional, enclosure=io.jenetics.jpx.Metadata]]
			Description[path=gpx.metadata[0].author[0], value=Indexed[value=io.jenetics.jpx.Person, enclosure=java.util.Optional]]
			Description[path=gpx.metadata[0].author[0].email, value=Single[value=java.util.Optional, enclosure=io.jenetics.jpx.Person]]
			Description[path=gpx.metadata[0].author[0].email[0], value=Indexed[value=io.jenetics.jpx.Email, enclosure=java.util.Optional]]
			Description[path=gpx.metadata[0].author[0].email[0].ID, value=Single[value=java.lang.String, enclosure=io.jenetics.jpx.Email]]
			Description[path=gpx.metadata[0].author[0].email[0].address, value=Single[value=java.lang.String, enclosure=io.jenetics.jpx.Email]]
			Description[path=gpx.metadata[0].author[0].email[0].domain, value=Single[value=java.lang.String, enclosure=io.jenetics.jpx.Email]]
			Description[path=gpx.metadata[0].author[0].empty, value=Single[value=boolean, enclosure=io.jenetics.jpx.Person]]
			Description[path=gpx.metadata[0].author[0].link, value=Single[value=java.util.Optional, enclosure=io.jenetics.jpx.Person]]
			Description[path=gpx.metadata[0].author[0].link[0], value=Indexed[value=io.jenetics.jpx.Link, enclosure=java.util.Optional]]
			Description[path=gpx.metadata[0].author[0].link[0].href, value=Single[value=java.net.URI, enclosure=io.jenetics.jpx.Link]]
			Description[path=gpx.metadata[0].author[0].link[0].text, value=Single[value=java.util.Optional, enclosure=io.jenetics.jpx.Link]]
			Description[path=gpx.metadata[0].author[0].link[0].text[0], value=Indexed[value=java.lang.String, enclosure=java.util.Optional]]
			Description[path=gpx.metadata[0].author[0].link[0].type, value=Single[value=java.util.Optional, enclosure=io.jenetics.jpx.Link]]
			Description[path=gpx.metadata[0].author[0].link[0].type[0], value=Indexed[value=java.lang.String, enclosure=java.util.Optional]]
			Description[path=gpx.metadata[0].author[0].name, value=Single[value=java.util.Optional, enclosure=io.jenetics.jpx.Person]]
			Description[path=gpx.metadata[0].author[0].name[0], value=Indexed[value=java.lang.String, enclosure=java.util.Optional]]
			Description[path=gpx.metadata[0].bounds, value=Single[value=java.util.Optional, enclosure=io.jenetics.jpx.Metadata]]
			Description[path=gpx.metadata[0].bounds[0], value=Indexed[value=io.jenetics.jpx.Bounds, enclosure=java.util.Optional]]
			Description[path=gpx.metadata[0].bounds[0].maxLatitude, value=Single[value=io.jenetics.jpx.Latitude, enclosure=io.jenetics.jpx.Bounds]]
			Description[path=gpx.metadata[0].bounds[0].maxLongitude, value=Single[value=io.jenetics.jpx.Longitude, enclosure=io.jenetics.jpx.Bounds]]
			Description[path=gpx.metadata[0].bounds[0].minLatitude, value=Single[value=io.jenetics.jpx.Latitude, enclosure=io.jenetics.jpx.Bounds]]
			Description[path=gpx.metadata[0].bounds[0].minLongitude, value=Single[value=io.jenetics.jpx.Longitude, enclosure=io.jenetics.jpx.Bounds]]
			Description[path=gpx.metadata[0].copyright, value=Single[value=java.util.Optional, enclosure=io.jenetics.jpx.Metadata]]
			Description[path=gpx.metadata[0].copyright[0], value=Indexed[value=io.jenetics.jpx.Copyright, enclosure=java.util.Optional]]
			Description[path=gpx.metadata[0].copyright[0].author, value=Single[value=java.lang.String, enclosure=io.jenetics.jpx.Copyright]]
			Description[path=gpx.metadata[0].copyright[0].license, value=Single[value=java.util.Optional, enclosure=io.jenetics.jpx.Copyright]]
			Description[path=gpx.metadata[0].copyright[0].license[0], value=Indexed[value=java.net.URI, enclosure=java.util.Optional]]
			Description[path=gpx.metadata[0].copyright[0].year, value=Single[value=java.util.Optional, enclosure=io.jenetics.jpx.Copyright]]
			Description[path=gpx.metadata[0].copyright[0].year[0], value=Indexed[value=java.time.Year, enclosure=java.util.Optional]]
			Description[path=gpx.metadata[0].description, value=Single[value=java.util.Optional, enclosure=io.jenetics.jpx.Metadata]]
			Description[path=gpx.metadata[0].description[0], value=Indexed[value=java.lang.String, enclosure=java.util.Optional]]
			Description[path=gpx.metadata[0].empty, value=Single[value=boolean, enclosure=io.jenetics.jpx.Metadata]]
			Description[path=gpx.metadata[0].extensions, value=Single[value=java.util.Optional, enclosure=io.jenetics.jpx.Metadata]]
			Description[path=gpx.metadata[0].extensions[0], value=Indexed[value=org.w3c.dom.Document, enclosure=java.util.Optional]]
			Description[path=gpx.metadata[0].keywords, value=Single[value=java.util.Optional, enclosure=io.jenetics.jpx.Metadata]]
			Description[path=gpx.metadata[0].keywords[0], value=Indexed[value=java.lang.String, enclosure=java.util.Optional]]
			Description[path=gpx.metadata[0].links, value=Single[value=java.util.List, enclosure=io.jenetics.jpx.Metadata]]
			Description[path=gpx.metadata[0].links[0], value=Indexed[value=io.jenetics.jpx.Link, enclosure=java.util.List]]
			Description[path=gpx.metadata[0].name, value=Single[value=java.util.Optional, enclosure=io.jenetics.jpx.Metadata]]
			Description[path=gpx.metadata[0].name[0], value=Indexed[value=java.lang.String, enclosure=java.util.Optional]]
			Description[path=gpx.metadata[0].time, value=Single[value=java.util.Optional, enclosure=io.jenetics.jpx.Metadata]]
			Description[path=gpx.metadata[0].time[0], value=Indexed[value=java.time.Instant, enclosure=java.util.Optional]]
			Description[path=gpx.routes, value=Single[value=java.util.List, enclosure=io.jenetics.jpx.GPX]]
			Description[path=gpx.routes[0], value=Indexed[value=io.jenetics.jpx.Route, enclosure=java.util.List]]
			Description[path=gpx.routes[0].comment, value=Single[value=java.util.Optional, enclosure=io.jenetics.jpx.Route]]
			Description[path=gpx.routes[0].comment[0], value=Indexed[value=java.lang.String, enclosure=java.util.Optional]]
			Description[path=gpx.routes[0].description, value=Single[value=java.util.Optional, enclosure=io.jenetics.jpx.Route]]
			Description[path=gpx.routes[0].description[0], value=Indexed[value=java.lang.String, enclosure=java.util.Optional]]
			Description[path=gpx.routes[0].empty, value=Single[value=boolean, enclosure=io.jenetics.jpx.Route]]
			Description[path=gpx.routes[0].extensions, value=Single[value=java.util.Optional, enclosure=io.jenetics.jpx.Route]]
			Description[path=gpx.routes[0].extensions[0], value=Indexed[value=org.w3c.dom.Document, enclosure=java.util.Optional]]
			Description[path=gpx.routes[0].links, value=Single[value=java.util.List, enclosure=io.jenetics.jpx.Route]]
			Description[path=gpx.routes[0].links[0], value=Indexed[value=io.jenetics.jpx.Link, enclosure=java.util.List]]
			Description[path=gpx.routes[0].name, value=Single[value=java.util.Optional, enclosure=io.jenetics.jpx.Route]]
			Description[path=gpx.routes[0].name[0], value=Indexed[value=java.lang.String, enclosure=java.util.Optional]]
			Description[path=gpx.routes[0].number, value=Single[value=java.util.Optional, enclosure=io.jenetics.jpx.Route]]
			Description[path=gpx.routes[0].number[0], value=Indexed[value=io.jenetics.jpx.UInt, enclosure=java.util.Optional]]
			Description[path=gpx.routes[0].number[0].value, value=Single[value=int, enclosure=io.jenetics.jpx.UInt]]
			Description[path=gpx.routes[0].points, value=Single[value=java.util.List, enclosure=io.jenetics.jpx.Route]]
			Description[path=gpx.routes[0].points[0], value=Indexed[value=io.jenetics.jpx.WayPoint, enclosure=java.util.List]]
			Description[path=gpx.routes[0].points[0].DGPSID, value=Single[value=java.util.Optional, enclosure=io.jenetics.jpx.WayPoint]]
			Description[path=gpx.routes[0].points[0].DGPSID[0], value=Indexed[value=io.jenetics.jpx.DGPSStation, enclosure=java.util.Optional]]
			Description[path=gpx.routes[0].points[0].ageOfGPSData, value=Single[value=java.util.Optional, enclosure=io.jenetics.jpx.WayPoint]]
			Description[path=gpx.routes[0].points[0].ageOfGPSData[0], value=Indexed[value=java.time.Duration, enclosure=java.util.Optional]]
			Description[path=gpx.routes[0].points[0].comment, value=Single[value=java.util.Optional, enclosure=io.jenetics.jpx.WayPoint]]
			Description[path=gpx.routes[0].points[0].comment[0], value=Indexed[value=java.lang.String, enclosure=java.util.Optional]]
			Description[path=gpx.routes[0].points[0].course, value=Single[value=java.util.Optional, enclosure=io.jenetics.jpx.WayPoint]]
			Description[path=gpx.routes[0].points[0].course[0], value=Indexed[value=io.jenetics.jpx.Degrees, enclosure=java.util.Optional]]
			Description[path=gpx.routes[0].points[0].description, value=Single[value=java.util.Optional, enclosure=io.jenetics.jpx.WayPoint]]
			Description[path=gpx.routes[0].points[0].description[0], value=Indexed[value=java.lang.String, enclosure=java.util.Optional]]
			Description[path=gpx.routes[0].points[0].elevation, value=Single[value=java.util.Optional, enclosure=io.jenetics.jpx.WayPoint]]
			Description[path=gpx.routes[0].points[0].elevation[0], value=Indexed[value=io.jenetics.jpx.Length, enclosure=java.util.Optional]]
			Description[path=gpx.routes[0].points[0].extensions, value=Single[value=java.util.Optional, enclosure=io.jenetics.jpx.WayPoint]]
			Description[path=gpx.routes[0].points[0].extensions[0], value=Indexed[value=org.w3c.dom.Document, enclosure=java.util.Optional]]
			Description[path=gpx.routes[0].points[0].fix, value=Single[value=java.util.Optional, enclosure=io.jenetics.jpx.WayPoint]]
			Description[path=gpx.routes[0].points[0].fix[0], value=Indexed[value=io.jenetics.jpx.Fix, enclosure=java.util.Optional]]
			Description[path=gpx.routes[0].points[0].fix[0].value, value=Single[value=java.lang.String, enclosure=io.jenetics.jpx.Fix]]
			Description[path=gpx.routes[0].points[0].geoidHeight, value=Single[value=java.util.Optional, enclosure=io.jenetics.jpx.WayPoint]]
			Description[path=gpx.routes[0].points[0].geoidHeight[0], value=Indexed[value=io.jenetics.jpx.Length, enclosure=java.util.Optional]]
			Description[path=gpx.routes[0].points[0].hdop, value=Single[value=java.util.Optional, enclosure=io.jenetics.jpx.WayPoint]]
			Description[path=gpx.routes[0].points[0].hdop[0], value=Indexed[value=java.lang.Double, enclosure=java.util.Optional]]
			Description[path=gpx.routes[0].points[0].latitude, value=Single[value=io.jenetics.jpx.Latitude, enclosure=io.jenetics.jpx.WayPoint]]
			Description[path=gpx.routes[0].points[0].links, value=Single[value=java.util.List, enclosure=io.jenetics.jpx.WayPoint]]
			Description[path=gpx.routes[0].points[0].links[0], value=Indexed[value=io.jenetics.jpx.Link, enclosure=java.util.List]]
			Description[path=gpx.routes[0].points[0].longitude, value=Single[value=io.jenetics.jpx.Longitude, enclosure=io.jenetics.jpx.WayPoint]]
			Description[path=gpx.routes[0].points[0].magneticVariation, value=Single[value=java.util.Optional, enclosure=io.jenetics.jpx.WayPoint]]
			Description[path=gpx.routes[0].points[0].magneticVariation[0], value=Indexed[value=io.jenetics.jpx.Degrees, enclosure=java.util.Optional]]
			Description[path=gpx.routes[0].points[0].name, value=Single[value=java.util.Optional, enclosure=io.jenetics.jpx.WayPoint]]
			Description[path=gpx.routes[0].points[0].name[0], value=Indexed[value=java.lang.String, enclosure=java.util.Optional]]
			Description[path=gpx.routes[0].points[0].pdop, value=Single[value=java.util.Optional, enclosure=io.jenetics.jpx.WayPoint]]
			Description[path=gpx.routes[0].points[0].pdop[0], value=Indexed[value=java.lang.Double, enclosure=java.util.Optional]]
			Description[path=gpx.routes[0].points[0].sat, value=Single[value=java.util.Optional, enclosure=io.jenetics.jpx.WayPoint]]
			Description[path=gpx.routes[0].points[0].sat[0], value=Indexed[value=io.jenetics.jpx.UInt, enclosure=java.util.Optional]]
			Description[path=gpx.routes[0].points[0].source, value=Single[value=java.util.Optional, enclosure=io.jenetics.jpx.WayPoint]]
			Description[path=gpx.routes[0].points[0].source[0], value=Indexed[value=java.lang.String, enclosure=java.util.Optional]]
			Description[path=gpx.routes[0].points[0].speed, value=Single[value=java.util.Optional, enclosure=io.jenetics.jpx.WayPoint]]
			Description[path=gpx.routes[0].points[0].speed[0], value=Indexed[value=io.jenetics.jpx.Speed, enclosure=java.util.Optional]]
			Description[path=gpx.routes[0].points[0].symbol, value=Single[value=java.util.Optional, enclosure=io.jenetics.jpx.WayPoint]]
			Description[path=gpx.routes[0].points[0].symbol[0], value=Indexed[value=java.lang.String, enclosure=java.util.Optional]]
			Description[path=gpx.routes[0].points[0].time, value=Single[value=java.util.Optional, enclosure=io.jenetics.jpx.WayPoint]]
			Description[path=gpx.routes[0].points[0].time[0], value=Indexed[value=java.time.Instant, enclosure=java.util.Optional]]
			Description[path=gpx.routes[0].points[0].type, value=Single[value=java.util.Optional, enclosure=io.jenetics.jpx.WayPoint]]
			Description[path=gpx.routes[0].points[0].type[0], value=Indexed[value=java.lang.String, enclosure=java.util.Optional]]
			Description[path=gpx.routes[0].points[0].vdop, value=Single[value=java.util.Optional, enclosure=io.jenetics.jpx.WayPoint]]
			Description[path=gpx.routes[0].points[0].vdop[0], value=Indexed[value=java.lang.Double, enclosure=java.util.Optional]]
			Description[path=gpx.routes[0].source, value=Single[value=java.util.Optional, enclosure=io.jenetics.jpx.Route]]
			Description[path=gpx.routes[0].source[0], value=Indexed[value=java.lang.String, enclosure=java.util.Optional]]
			Description[path=gpx.routes[0].type, value=Single[value=java.util.Optional, enclosure=io.jenetics.jpx.Route]]
			Description[path=gpx.routes[0].type[0], value=Indexed[value=java.lang.String, enclosure=java.util.Optional]]
			Description[path=gpx.tracks, value=Single[value=java.util.List, enclosure=io.jenetics.jpx.GPX]]
			Description[path=gpx.tracks[0], value=Indexed[value=io.jenetics.jpx.Track, enclosure=java.util.List]]
			Description[path=gpx.tracks[0].comment, value=Single[value=java.util.Optional, enclosure=io.jenetics.jpx.Track]]
			Description[path=gpx.tracks[0].comment[0], value=Indexed[value=java.lang.String, enclosure=java.util.Optional]]
			Description[path=gpx.tracks[0].description, value=Single[value=java.util.Optional, enclosure=io.jenetics.jpx.Track]]
			Description[path=gpx.tracks[0].description[0], value=Indexed[value=java.lang.String, enclosure=java.util.Optional]]
			Description[path=gpx.tracks[0].empty, value=Single[value=boolean, enclosure=io.jenetics.jpx.Track]]
			Description[path=gpx.tracks[0].extensions, value=Single[value=java.util.Optional, enclosure=io.jenetics.jpx.Track]]
			Description[path=gpx.tracks[0].extensions[0], value=Indexed[value=org.w3c.dom.Document, enclosure=java.util.Optional]]
			Description[path=gpx.tracks[0].links, value=Single[value=java.util.List, enclosure=io.jenetics.jpx.Track]]
			Description[path=gpx.tracks[0].links[0], value=Indexed[value=io.jenetics.jpx.Link, enclosure=java.util.List]]
			Description[path=gpx.tracks[0].name, value=Single[value=java.util.Optional, enclosure=io.jenetics.jpx.Track]]
			Description[path=gpx.tracks[0].name[0], value=Indexed[value=java.lang.String, enclosure=java.util.Optional]]
			Description[path=gpx.tracks[0].number, value=Single[value=java.util.Optional, enclosure=io.jenetics.jpx.Track]]
			Description[path=gpx.tracks[0].number[0], value=Indexed[value=io.jenetics.jpx.UInt, enclosure=java.util.Optional]]
			Description[path=gpx.tracks[0].segments, value=Single[value=java.util.List, enclosure=io.jenetics.jpx.Track]]
			Description[path=gpx.tracks[0].segments[0], value=Indexed[value=io.jenetics.jpx.TrackSegment, enclosure=java.util.List]]
			Description[path=gpx.tracks[0].segments[0].empty, value=Single[value=boolean, enclosure=io.jenetics.jpx.TrackSegment]]
			Description[path=gpx.tracks[0].segments[0].extensions, value=Single[value=java.util.Optional, enclosure=io.jenetics.jpx.TrackSegment]]
			Description[path=gpx.tracks[0].segments[0].extensions[0], value=Indexed[value=org.w3c.dom.Document, enclosure=java.util.Optional]]
			Description[path=gpx.tracks[0].segments[0].points, value=Single[value=java.util.List, enclosure=io.jenetics.jpx.TrackSegment]]
			Description[path=gpx.tracks[0].segments[0].points[0], value=Indexed[value=io.jenetics.jpx.WayPoint, enclosure=java.util.List]]
			Description[path=gpx.tracks[0].source, value=Single[value=java.util.Optional, enclosure=io.jenetics.jpx.Track]]
			Description[path=gpx.tracks[0].source[0], value=Indexed[value=java.lang.String, enclosure=java.util.Optional]]
			Description[path=gpx.tracks[0].type, value=Single[value=java.util.Optional, enclosure=io.jenetics.jpx.Track]]
			Description[path=gpx.tracks[0].type[0], value=Indexed[value=java.lang.String, enclosure=java.util.Optional]]
			Description[path=gpx.version, value=Single[value=java.lang.String, enclosure=io.jenetics.jpx.GPX]]
			Description[path=gpx.wayPoints, value=Single[value=java.util.List, enclosure=io.jenetics.jpx.GPX]]
			Description[path=gpx.wayPoints[0], value=Indexed[value=io.jenetics.jpx.WayPoint, enclosure=java.util.List]]
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

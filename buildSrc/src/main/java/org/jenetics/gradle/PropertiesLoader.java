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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * Author:
 *   Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 *
 */
package org.jenetics.gradle;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static java.util.regex.Pattern.quote;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.gradle.api.GradleException;
import org.gradle.api.plugins.ExtraPropertiesExtension;

/**
 * Reads user properties from an property file and writes it to the given
 * {@code ExternalPropertiesExtension} object.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.2
 * @version 1.4 &mdash; <em>$Date: 2013-08-27 $</em>
 */
public final class PropertiesLoader {

	private final ExtraPropertiesExtension _ext;

	public PropertiesLoader(final ExtraPropertiesExtension ext) {
		_ext = requireNonNull(ext, "Properties must not be null.");
	}

	/**
	 * Loads the properties, stored in the given file, and writes it to the
	 * {@code ExternalPropertiesExtension} object.
	 *
	 * @param file the property file
	 */
	public void load(final File file) {
		final Properties properties = new Properties();
		try (final InputStream in = new FileInputStream(file)) {
			properties.load(in);
		} catch (IOException e) {
			throw new GradleException(format(
				"Error while reading propertie file '%s'", file), e
			);
		}

		for (final Map.Entry<Object, Object> entry : properties.entrySet()) {
			set(entry.getKey().toString(), entry.getValue());
		}
	}

	private void set(final String key, final Object value) {
		final String[] parts = key.split(quote("."));
		if (parts.length == 1) {
			_ext.set(key, value);
		} else {
			Map<String, Object> map = getOrElseCreateMap(parts[0]);
			for (int i = 1; i < parts.length - 1; ++i) {
				map = getOrElseCreateMap(map, parts[i]);
			}
			map.put(parts[parts.length - 1], value);
		}
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> getOrElseCreateMap(
		final Map<String, Object> parent,
		final String key
	) {
		Map<String, Object> map = (Map<String, Object>)parent.get(key);
		if (map == null) {
			map = new HashMap<>();
			parent.put(key, map);
		}
		return map;
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> getOrElseCreateMap(final String key) {
		Map<String, Object> map = null;
		if (_ext.has(key)) {
			map = (Map<String, Object>)_ext.get(key);
		} else {
			map = new HashMap<>();
			_ext.set(key,  map);
		}
		return map;
	}

}



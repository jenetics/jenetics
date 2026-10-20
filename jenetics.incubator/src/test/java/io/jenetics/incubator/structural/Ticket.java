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
package io.jenetics.incubator.structural;

import java.time.LocalDate;
import java.util.function.Consumer;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public interface Ticket {
	String ticketId();
	LocalDate ticketDate();
	String ticketType();
	Event event();

	interface Builder extends Ticket {
		Builder ticketId(String value);
		Builder ticketDate(LocalDate value);
		Builder ticketType(String value);
		Builder event(Event value);
		Builder event(Consumer<? super Event.Builder> builder);
	}

}

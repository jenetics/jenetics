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
package io.jenetics.prog.op;

import static java.util.Objects.requireNonNull;

import java.util.Optional;
import java.util.regex.Pattern;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 4.4
 * @since 4.4
 */
final class Numbers {
	private Numbers() {}

	// Regex for checking if parsable as double as described in
	// Double.valueOf Java documentation.

	private static final String DIGITS = "(\\p{Digit}+)";

	private static final String HEX_DIGITS = "(\\p{XDigit}+)";

	// an exponent is 'e' or 'E' followed by an optionally
	// signed decimal integer.
	private static final String EXP = "[eE][+-]?"+ DIGITS;

	private static final String FP_REGEX =
		"[\\x00-\\x20]*"+  // Optional leading "whitespace"
		"[+-]?(" +         // Optional sign character
		"NaN|" +           // "NaN" string
		"Infinity|" +      // "Infinity" string

		// A decimal floating-point string representing a finite positive
		// number without a leading sign has at most five basic pieces:
		// Digits . Digits ExponentPart FloatTypeSuffix
		//
		// Since this method allows integer-only strings as input
		// in addition to strings of floating-point literals, the
		// two sub-patterns below are simplifications of the grammar
		// productions from section 3.10.2 of
		// The Java Language Specification.

		// Digits ._opt Digits_opt ExponentPart_opt FloatTypeSuffix_opt
		"((("+ DIGITS +"(\\.)?("+ DIGITS +"?)("+ EXP +")?)|"+

		// . Digits ExponentPart_opt FloatTypeSuffix_opt
		"(\\.("+ DIGITS +")("+ EXP +")?)|"+

		// Hexadecimal strings
		"((" +
		// 0[xX] HexDigits ._opt BinaryExponent FloatTypeSuffix_opt
		"(0[xX]" + HEX_DIGITS + "(\\.)?)|" +

		// 0[xX] HexDigits_opt . HexDigits BinaryExponent FloatTypeSuffix_opt
		"(0[xX]" + HEX_DIGITS + "?(\\.)" + HEX_DIGITS + ")" +

		")[pP][+-]?" + DIGITS + "))" +
		"[fFdD]?))" +
		"[\\x00-\\x20]*";// Optional trailing "whitespace"

	private static final Pattern FP_PATTERN = Pattern.compile(FP_REGEX);

	static boolean isNumber(final String value) {
		requireNonNull(value);
		return FP_PATTERN.matcher(value).matches();
	}

	static Optional<Double> toDoubleOptional(final String value) {
		return isNumber(value)
			? Optional.of(Double.parseDouble(value))
			: Optional.empty();
	}

}

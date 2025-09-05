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
package io.jenetics.distassert.internal.screen;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 8.3
 * @since 8.3
 */
final class DrawChars {

	public static final char LIGHT_HORIZONTAL = '_';
	public static final char HEAVY_HORIZONTAL = '━';
	public static final char LIGHT_VERTICAL = '│';
	public static final char HEAVY_VERTICAL = '┃';
	public static final char LIGHT_TRIPLE_DASH_HORIZONTAL = '┄';
	public static final char HEAVY_TRIPLE_DASH_HORIZONTAL = '┅';
	public static final char LIGHT_TRIPLE_DASH_VERTICAL = '┆';
	public static final char HEAVY_TRIPLE_DASH_VERTICAL = '┇';
	public static final char LIGHT_QUADRUPLE_DASH_HORIZONTAL = '┈';
	public static final char HEAVY_QUADRUPLE_DASH_HORIZONTAL = '┉';
	public static final char LIGHT_QUADRUPLE_DASH_VERTICAL = '┊';
	public static final char HEAVY_QUADRUPLE_DASH_VERTICAL = '┋';
	public static final char LIGHT_DOWN_AND_RIGHT = '┌';
	public static final char DOWN_LIGHT_AND_RIGHT_HEAVY = '┍';
	public static final char DOWN_HEAVY_AND_RIGHT_LIGHT = '┎';
	public static final char HEAVY_DOWN_AND_RIGHT = '┏';
	public static final char LIGHT_DOWN_AND_LEFT = '┐';
	public static final char DOWN_LIGHT_AND_LEFT_HEAVY = '┑';
	public static final char DOWN_HEAVY_AND_LEFT_LIGHT = '┒';
	public static final char HEAVY_DOWN_AND_LEFT = '┓';
	public static final char LIGHT_UP_AND_RIGHT = '└';
	public static final char UP_LIGHT_AND_RIGHT_HEAVY = '┕';
	public static final char UP_HEAVY_AND_RIGHT_LIGHT = '┖';
	public static final char HEAVY_UP_AND_RIGHT = '┗';
	public static final char LIGHT_UP_AND_LEFT = '┘';
	public static final char UP_LIGHT_AND_LEFT_HEAVY = '┙';
	public static final char UP_HEAVY_AND_LEFT_LIGHT = '┚';
	public static final char HEAVY_UP_AND_LEFT = '┛';
	public static final char LIGHT_VERTICAL_AND_RIGHT = '├';
	public static final char VERTICAL_LIGHT_AND_RIGHT_HEAVY = '┝';
	public static final char UP_HEAVY_AND_RIGHT_DOWN_LIGHT = '┞';
	public static final char DOWN_HEAVY_AND_RIGHT_UP_LIGHT = '┟';
	public static final char VERTICAL_HEAVY_AND_RIGHT_LIGHT = '┠';
	public static final char DOWN_LIGHT_AND_RIGHT_UP_HEAVY = '┡';
	public static final char UP_LIGHT_AND_RIGHT_DOWN_HEAVY = '┢';
	public static final char HEAVY_VERTICAL_AND_RIGHT = '┣';
	public static final char LIGHT_VERTICAL_AND_LEFT = '┤';
	public static final char VERTICAL_LIGHT_AND_LEFT_HEAVY = '┥';
	public static final char UP_HEAVY_AND_LEFT_DOWN_LIGHT = '┦';
	public static final char DOWN_HEAVY_AND_LEFT_UP_LIGHT = '┧';
	public static final char VERTICAL_HEAVY_AND_LEFT_LIGHT = '┨';
	public static final char DOWN_LIGHT_AND_LEFT_UP_HEAVY = '┩';
	public static final char UP_LIGHT_AND_LEFT_DOWN_HEAVY = '┪';
	public static final char HEAVY_VERTICAL_AND_LEFT = '┫';
	public static final char LIGHT_DOWN_AND_HORIZONTAL = '┬';
	public static final char LEFT_HEAVY_AND_RIGHT_DOWN_LIGHT = '┭';
	public static final char RIGHT_HEAVY_AND_LEFT_DOWN_LIGHT = '┮';
	public static final char DOWN_LIGHT_AND_HORIZONTAL_HEAVY = '┯';
	public static final char DOWN_HEAVY_AND_HORIZONTAL_LIGHT = '┰';
	public static final char RIGHT_LIGHT_AND_LEFT_DOWN_HEAVY = '┱';
	public static final char LEFT_LIGHT_AND_RIGHT_DOWN_HEAVY = '┲';
	public static final char HEAVY_DOWN_AND_HORIZONTAL = '┳';
	public static final char LIGHT_UP_AND_HORIZONTAL = '┴';
	public static final char LEFT_HEAVY_AND_RIGHT_UP_LIGHT = '┵';
	public static final char RIGHT_HEAVY_AND_LEFT_UP_LIGHT = '┶';
	public static final char UP_LIGHT_AND_HORIZONTAL_HEAVY = '┷';
	public static final char UP_HEAVY_AND_HORIZONTAL_LIGHT = '┸';
	public static final char RIGHT_LIGHT_AND_LEFT_UP_HEAVY = '┹';
	public static final char LEFT_LIGHT_AND_RIGHT_UP_HEAVY = '┺';
	public static final char HEAVY_UP_AND_HORIZONTAL = '┻';
	public static final char LIGHT_VERTICAL_AND_HORIZONTAL = '┼';
	public static final char LEFT_HEAVY_AND_RIGHT_VERTICAL_LIGHT = '┽';
	public static final char RIGHT_HEAVY_AND_LEFT_VERTICAL_LIGHT = '┾';
	public static final char VERTICAL_LIGHT_AND_HORIZONTAL_HEAVY = '┿';
	public static final char UP_HEAVY_AND_DOWN_HORIZONTAL_LIGHT = '╀';
	public static final char DOWN_HEAVY_AND_UP_HORIZONTAL_LIGHT = '╁';
	public static final char VERTICAL_HEAVY_AND_HORIZONTAL_LIGHT = '╂';
	public static final char LEFT_UP_HEAVY_AND_RIGHT_DOWN_LIGHT = '╃';
	public static final char RIGHT_UP_HEAVY_AND_LEFT_DOWN_LIGHT = '╄';
	public static final char LEFT_DOWN_HEAVY_AND_RIGHT_UP_LIGHT = '╅';
	public static final char RIGHT_DOWN_HEAVY_AND_LEFT_UP_LIGHT = '╆';
	public static final char DOWN_LIGHT_AND_UP_HORIZONTAL_HEAVY = '╇';
	public static final char UP_LIGHT_AND_DOWN_HORIZONTAL_HEAVY = '╈';
	public static final char RIGHT_LIGHT_AND_LEFT_VERTICAL_HEAVY = '╉';
	public static final char LEFT_LIGHT_AND_RIGHT_VERTICAL_HEAVY = '╊';
	public static final char HEAVY_VERTICAL_AND_HORIZONTAL = '╋';
	public static final char LIGHT_DOUBLE_DASH_HORIZONTAL = '╌';
	public static final char HEAVY_DOUBLE_DASH_HORIZONTAL = '╍';
	public static final char LIGHT_DOUBLE_DASH_VERTICAL = '╎';
	public static final char HEAVY_DOUBLE_DASH_VERTICAL = '╏';
	public static final char DOUBLE_HORIZONTAL = '═';
	public static final char DOUBLE_VERTICAL = '║';
	public static final char DOWN_SINGLE_AND_RIGHT_DOUBLE = '╒';
	public static final char DOWN_DOUBLE_AND_RIGHT_SINGLE = '╓';
	public static final char DOUBLE_DOWN_AND_RIGHT = '╔';
	public static final char DOWN_SINGLE_AND_LEFT_DOUBLE = '╕';
	public static final char DOWN_DOUBLE_AND_LEFT_SINGLE = '╖';
	public static final char DOUBLE_DOWN_AND_LEFT = '╗';
	public static final char UP_SINGLE_AND_RIGHT_DOUBLE = '╘';
	public static final char UP_DOUBLE_AND_RIGHT_SINGLE = '╙';
	public static final char DOUBLE_UP_AND_RIGHT = '╚';
	public static final char UP_SINGLE_AND_LEFT_DOUBLE = '╛';
	public static final char UP_DOUBLE_AND_LEFT_SINGLE = '╜';
	public static final char DOUBLE_UP_AND_LEFT = '╝';
	public static final char VERTICAL_SINGLE_AND_RIGHT_DOUBLE = '╞';
	public static final char VERTICAL_DOUBLE_AND_RIGHT_SINGLE = '╟';
	public static final char DOUBLE_VERTICAL_AND_RIGHT = '╠';
	public static final char VERTICAL_SINGLE_AND_LEFT_DOUBLE = '╡';
	public static final char VERTICAL_DOUBLE_AND_LEFT_SINGLE = '╢';
	public static final char DOUBLE_VERTICAL_AND_LEFT = '╣';
	public static final char DOWN_SINGLE_AND_HORIZONTAL_DOUBLE = '╤';
	public static final char DOWN_DOUBLE_AND_HORIZONTAL_SINGLE = '╥';
	public static final char DOUBLE_DOWN_AND_HORIZONTAL = '╦';
	public static final char UP_SINGLE_AND_HORIZONTAL_DOUBLE = '╧';
	public static final char UP_DOUBLE_AND_HORIZONTAL_SINGLE = '╨';
	public static final char DOUBLE_UP_AND_HORIZONTAL = '╩';
	public static final char VERTICAL_SINGLE_AND_HORIZONTAL_DOUBLE = '╪';
	public static final char VERTICAL_DOUBLE_AND_HORIZONTAL_SINGLE = '╫';
	public static final char DOUBLE_VERTICAL_AND_HORIZONTAL = '╬';
	public static final char LIGHT_ARC_DOWN_AND_RIGHT = '╭';
	public static final char LIGHT_ARC_DOWN_AND_LEFT = '╮';
	public static final char LIGHT_ARC_UP_AND_LEFT = '╯';
	public static final char LIGHT_ARC_UP_AND_RIGHT = '╰';
	public static final char LIGHT_DIAGONAL_UPPER_RIGHT_TO_LOWER_LEFT = '╱';
	public static final char LIGHT_DIAGONAL_UPPER_LEFT_TO_LOWER_RIGHT = '╲';
	public static final char LIGHT_DIAGONAL_CROSS = '╳';
	public static final char LIGHT_LEFT = '╴';
	public static final char LIGHT_UP = '╵';
	public static final char LIGHT_RIGHT = '╶';
	public static final char LIGHT_DOWN = '╷';
	public static final char HEAVY_LEFT = '╸';
	public static final char HEAVY_UP = '╹';
	public static final char HEAVY_RIGHT = '╺';
	public static final char HEAVY_DOWN = '╻';
	public static final char LIGHT_LEFT_AND_HEAVY_RIGHT = '╼';
	public static final char LIGHT_UP_AND_HEAVY_DOWN = '╽';
	public static final char HEAVY_LEFT_AND_LIGHT_RIGHT = '╾';
	public static final char HEAVY_UP_AND_LIGHT_DOWN = '╿';

	//           0    1    2    3    4    5    6    7    8    9    A    B    C    D    E    F
	// U+250x    ─    ━    │    ┃    ┄    ┅    ┆    ┇    ┈    ┉    ┊    ┋    ┌    ┍    ┎    ┏
	// U+251x    ┐    ┑    ┒    ┓    └    ┕    ┖    ┗    ┘    ┙    ┚    ┛    ├    ┝    ┞    ┟
	// U+252x    ┠    ┡    ┢    ┣    ┤    ┥    ┦    ┧    ┨    ┩    ┪    ┫    ┬    ┭    ┮    ┯
	// U+253x    ┰    ┱    ┲    ┳    ┴    ┵    ┶    ┷    ┸    ┹    ┺    ┻    ┼    ┽    ┾    ┿
	// U+254x    ╀    ╁    ╂    ╃    ╄    ╅    ╆    ╇    ╈    ╉    ╊    ╋    ╌    ╍    ╎    ╏
	// U+255x    ═    ║    ╒    ╓    ╔    ╕    ╖    ╗    ╘    ╙    ╚    ╛    ╜    ╝    ╞    ╟
	// U+256x    ╠    ╡    ╢    ╣    ╤    ╥    ╦    ╧    ╨    ╩    ╪    ╫    ╬    ╭    ╮    ╯
	// U+257x    ╰    ╱    ╲    ╳    ╴    ╵    ╶    ╷    ╸    ╹    ╺    ╻    ╼    ╽    ╾    ╿
	static final char[] BOX_CHARS = {
		// Light and heavy solid lines
		'_', //   0: LIGHT HORIZONTAL
		'━', //   1: HEAVY HORIZONTAL
		'│', //   2: LIGHT VERTICAL
		'┃', //   3: HEAVY VERTICAL

		// Light and heavy dashed lines
		'┄', //   4: LIGHT TRIPLE DASH HORIZONTAL
		'┅', //   5: HEAVY TRIPLE DASH HORIZONTAL
		'┆', //   6: LIGHT TRIPLE DASH VERTICAL
		'┇', //   7: HEAVY TRIPLE DASH VERTICAL
		'┈', //   8: LIGHT QUADRUPLE DASH HORIZONTAL
		'┉', //   9: HEAVY QUADRUPLE DASH HORIZONTAL
		'┊', //  10: LIGHT QUADRUPLE DASH VERTICAL
		'┋', //  11: HEAVY QUADRUPLE DASH VERTICAL

		// Light and heavy line box components
		'┌', //  12: LIGHT DOWN AND RIGHT
		'┍', //  13: DOWN LIGHT AND RIGHT HEAVY
		'┎', //  14: DOWN HEAVY AND RIGHT LIGHT
		'┏', //  15: HEAVY DOWN AND RIGHT
		'┐', //  16: LIGHT DOWN AND LEFT
		'┑', //  17: DOWN LIGHT AND LEFT HEAVY
		'┒', //  18: DOWN HEAVY AND LEFT LIGHT
		'┓', //  19: HEAVY DOWN AND LEFT
		'└', //  20: LIGHT UP AND RIGHT
		'┕', //  21: UP LIGHT AND RIGHT HEAVY
		'┖', //  22: UP HEAVY AND RIGHT LIGHT
		'┗', //  23: HEAVY UP AND RIGHT
		'┘', //  24: LIGHT UP AND LEFT
		'┙', //  25: UP LIGHT AND LEFT HEAVY
		'┚', //  26: UP HEAVY AND LEFT LIGHT
		'┛', //  27: HEAVY UP AND LEFT
		'├', //  28: LIGHT VERTICAL AND RIGHT
		'┝', //  29: VERTICAL LIGHT AND RIGHT HEAVY
		'┞', //  30: UP HEAVY AND RIGHT DOWN LIGHT
		'┟', //  31: DOWN HEAVY AND RIGHT UP LIGHT
		'┠', //  32: VERTICAL HEAVY AND RIGHT LIGHT
		'┡', //  33: DOWN LIGHT AND RIGHT UP HEAVY
		'┢', //  34: UP LIGHT AND RIGHT DOWN HEAVY
		'┣', //  35: HEAVY VERTICAL AND RIGHT
		'┤', //  36: LIGHT VERTICAL AND LEFT
		'┥', //  37: VERTICAL LIGHT AND LEFT HEAVY
		'┦', //  38: UP HEAVY AND LEFT DOWN LIGHT
		'┧', //  39: DOWN HEAVY AND LEFT UP LIGHT
		'┨', //  40: VERTICAL HEAVY AND LEFT LIGHT
		'┩', //  41: DOWN LIGHT AND LEFT UP HEAVY
		'┪', //  42: UP LIGHT AND LEFT DOWN HEAVY
		'┫', //  43: HEAVY VERTICAL AND LEFT
		'┬', //  44: LIGHT DOWN AND HORIZONTAL
		'┭', //  45: LEFT HEAVY AND RIGHT DOWN LIGHT
		'┮', //  46: RIGHT HEAVY AND LEFT DOWN LIGHT
		'┯', //  47: DOWN LIGHT AND HORIZONTAL HEAVY
		'┰', //  48: DOWN HEAVY AND HORIZONTAL LIGHT
		'┱', //  49: RIGHT LIGHT AND LEFT DOWN HEAVY
		'┲', //  50: LEFT LIGHT AND RIGHT DOWN HEAVY
		'┳', //  51: HEAVY DOWN AND HORIZONTAL
		'┴', //  52: LIGHT UP AND HORIZONTAL
		'┵', //  53: LEFT HEAVY AND RIGHT UP LIGHT
		'┶', //  54: RIGHT HEAVY AND LEFT UP LIGHT
		'┷', //  55: UP LIGHT AND HORIZONTAL HEAVY
		'┸', //  56: UP HEAVY AND HORIZONTAL LIGHT
		'┹', //  57: RIGHT LIGHT AND LEFT UP HEAVY
		'┺', //  58: LEFT LIGHT AND RIGHT UP HEAVY
		'┻', //  59: HEAVY UP AND HORIZONTAL
		'┼', //  60: LIGHT VERTICAL AND HORIZONTAL
		'┽', //  61: LEFT HEAVY AND RIGHT VERTICAL LIGHT
		'┾', //  62: RIGHT HEAVY AND LEFT VERTICAL LIGHT
		'┿', //  63: VERTICAL LIGHT AND HORIZONTAL HEAVY
		'╀', //  64: UP HEAVY AND DOWN HORIZONTAL LIGHT
		'╁', //  65: DOWN HEAVY AND UP HORIZONTAL LIGHT
		'╂', //  66: VERTICAL HEAVY AND HORIZONTAL LIGHT
		'╃', //  67: LEFT UP HEAVY AND RIGHT DOWN LIGHT
		'╄', //  68: RIGHT UP HEAVY AND LEFT DOWN LIGHT
		'╅', //  69: LEFT DOWN HEAVY AND RIGHT UP LIGHT
		'╆', //  70: RIGHT DOWN HEAVY AND LEFT UP LIGHT
		'╇', //  71: DOWN LIGHT AND UP HORIZONTAL HEAVY
		'╈', //  72: UP LIGHT AND DOWN HORIZONTAL HEAVY
		'╉', //  73: RIGHT LIGHT AND LEFT VERTICAL HEAVY
		'╊', //  74: LEFT LIGHT AND RIGHT VERTICAL HEAVY
		'╋', //  75: HEAVY VERTICAL AND HORIZONTAL

		// Light and heavy dashed lines
		'╌', //  76: LIGHT DOUBLE DASH HORIZONTAL
		'╍', //  77: HEAVY DOUBLE DASH HORIZONTAL
		'╎', //  78: LIGHT DOUBLE DASH VERTICAL
		'╏', //  79: HEAVY DOUBLE DASH VERTICAL

		// Double lines
		'═', //  80: DOUBLE HORIZONTAL
		'║', //  81: DOUBLE VERTICAL

		// Light and double line box components
		'╒', //  82: DOWN SINGLE AND RIGHT DOUBLE
		'╓', //  83: DOWN DOUBLE AND RIGHT SINGLE
		'╔', //  84: DOUBLE DOWN AND RIGHT
		'╕', //  85: DOWN SINGLE AND LEFT DOUBLE
		'╖', //  86: DOWN DOUBLE AND LEFT SINGLE
		'╗', //  87: DOUBLE DOWN AND LEFT
		'╘', //  88: UP SINGLE AND RIGHT DOUBLE
		'╙', //  89: UP DOUBLE AND RIGHT SINGLE
		'╚', //  90: DOUBLE UP AND RIGHT
		'╛', //  91: UP SINGLE AND LEFT DOUBLE
		'╜', //  92: UP DOUBLE AND LEFT SINGLE
		'╝', //  93: DOUBLE UP AND LEFT
		'╞', //  94: VERTICAL SINGLE AND RIGHT DOUBLE
		'╟', //  95: VERTICAL DOUBLE AND RIGHT SINGLE
		'╠', //  96: DOUBLE VERTICAL AND RIGHT
		'╡', //  97: VERTICAL SINGLE AND LEFT DOUBLE
		'╢', //  98: VERTICAL DOUBLE AND LEFT SINGLE
		'╣', //  99: DOUBLE VERTICAL AND LEFT
		'╤', // 100: DOWN SINGLE AND HORIZONTAL DOUBLE
		'╥', // 101: DOWN DOUBLE AND HORIZONTAL SINGLE
		'╦', // 102: DOUBLE DOWN AND HORIZONTAL
		'╧', // 103: UP SINGLE AND HORIZONTAL DOUBLE
		'╨', // 104: UP DOUBLE AND HORIZONTAL SINGLE
		'╩', // 105: DOUBLE UP AND HORIZONTAL
		'╪', // 106: VERTICAL SINGLE AND HORIZONTAL DOUBLE
		'╫', // 107: VERTICAL DOUBLE AND HORIZONTAL SINGLE
		'╬', // 108: DOUBLE VERTICAL AND HORIZONTAL

		// Character cell arcs
		'╭', // 109: LIGHT ARC DOWN AND RIGHT
		'╮', // 110: LIGHT ARC DOWN AND LEFT
		'╯', // 111: LIGHT ARC UP AND LEFT
		'╰', // 112: LIGHT ARC UP AND RIGHT

		// Character cell diagonals
		'╱', // 113: LIGHT DIAGONAL UPPER RIGHT TO LOWER LEFT
		'╲', // 114: LIGHT DIAGONAL UPPER LEFT TO LOWER RIGHT
		'╳', // 115: LIGHT DIAGONAL CROSS

		// Light and heavy half lines
		'╴', // 116: LIGHT LEFT
		'╵', // 117: LIGHT UP
		'╶', // 118: LIGHT RIGHT
		'╷', // 119: LIGHT DOWN
		'╸', // 120: HEAVY LEFT
		'╹', // 121: HEAVY UP
		'╺', // 122: HEAVY RIGHT
		'╻', // 123: HEAVY DOWN

		// Mixed light and heavy lines
		'╼', // 124: LIGHT LEFT AND HEAVY RIGHT
		'╽', // 125: LIGHT UP AND HEAVY DOWN
		'╾', // 126: HEAVY LEFT AND LIGHT RIGHT
		'╿', // 127: HEAVY UP AND LIGHT DOWN
	};
	//           0    1    2    3    4    5    6    7    8    9    A    B    C    D    E    F
	// U+258x    ▀    ▁    ▂    ▃    ▄    ▅    ▆    ▇    █    ▉    ▊    ▋    ▌    ▍    ▎    ▏
	// U+259x    ▐    ░    ▒    ▓    ▔    ▕    ▖    ▗    ▘    ▙    ▚    ▛    ▜    ▝    ▞    ▟
	static final char[] BLOCK_CHARS = {
		// Block elements
		'▀', //  0: UPPER HALF BLOCK
		'▁', //  1: LOWER ONE EIGHTH BLOCK
		'▂', //  2: LOWER ONE QUARTER BLOCK
		'▃', //  3: LOWER THREE EIGHTHS BLOCK
		'▄', //  4: LOWER HALF BLOCK
		'▅', //  5: LOWER FIVE EIGHTHS BLOCK
		'▆', //  6: LOWER THREE QUARTERS BLOCK
		'▇', //  7: LOWER SEVEN EIGHTHS BLOCK
		'█', //  8: FULL BLOCK
		'▉', //  9: LEFT SEVEN EIGHTHS BLOCK
		'▊', // 10: LEFT THREE QUARTERS BLOCK
		'▋', // 11: LEFT FIVE EIGHTHS BLOCK
		'▌', // 12: LEFT HALF BLOCK
		'▍', // 13: LEFT THREE EIGHTHS BLOCK
		'▎', // 14: LEFT ONE QUARTER BLOCK
		'▏', // 15: LEFT ONE EIGHTH BLOCK
		'▐', // 16: RIGHT HALF BLOCK

		// Shade characters
		'░', // 17: LIGHT SHADE
		'▒', // 18: MEDIUM SHADE
		'▓', // 19: DARK SHADE

		// Block elements
		'▔', // 20: UPPER ONE EIGHTH BLOCK
		'▕', // 21: RIGHT ONE EIGHTH BLOCK

		// Terminal graphic characters
		'▖', // 22: QUADRANT LOWER LEFT
		'▗', // 23: QUADRANT LOWER RIGHT
		'▘', // 24: QUADRANT UPPER LEFT
		'▙', // 25: QUADRANT UPPER LEFT AND LOWER LEFT AND LOWER RIGHT
		'▚', // 26: QUADRANT UPPER LEFT AND LOWER RIGHT
		'▛', // 27: QUADRANT UPPER LEFT AND UPPER RIGHT AND LOWER LEFT
		'▜', // 28: QUADRANT UPPER LEFT AND UPPER RIGHT AND LOWER RIGHT
		'▝', // 29: QUADRANT UPPER RIGHT
		'▞', // 30: QUADRANT UPPER RIGHT AND LOWER LEFT
		'▟'  // 31: QUADRANT UPPER RIGHT AND LOWER LEFT AND LOWER RIGHT
	};

	private DrawChars() {
	}

}

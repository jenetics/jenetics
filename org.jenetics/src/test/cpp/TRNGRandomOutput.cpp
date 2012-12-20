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
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 *
 */

#include <cstdlib>
#include <iostream>
#include <iomanip>
#include <sstream>
#include <fstream>
#include <vector>
#include <trng/lcg64_shift.hpp>

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date: 2012-12-19 $</em>
 */
template<class Random>
class TRNGRandomOutput {
public:

	TRNGRandomOutput(
		unsigned long long seed,
		unsigned int splitp,
		unsigned int splits,
		unsigned long long jump,
		unsigned int jump2
	) {
		_random.seed(seed);
		_random.split(splitp, splits);
		_random.jump(jump);
		_random.jump2(jump2);

		std::stringstream name;
		name << seed << "-";
		name << splitp << "-" << splits << "-";
		name << jump << "-";
		name << jump2;
		_fileName = name.str();
	}

	~TRNGRandomOutput() {
	}

	std::string next() {
		std::stringstream out;
		out << static_cast<long long>(_random());
		return out.str();
	}

	std::string fileName() {
		return _fileName;
	}

private:
	Random _random;
	std::string _fileName;
};

template<class Random>
void write(const std::string& dir, TRNGRandomOutput<Random>& random, std::size_t numbers) {
	std::string file = dir + "/" + random.fileName();
	std::fstream out(file.c_str(), std::fstream::out);

	for (std::size_t i = 0; i < numbers; ++i) {
		out << random.next() << std::endl;
	}

	out.close();
}



int main(void) {

	int count = 0;

	for (unsigned long long seed = 0; seed < 2; ++seed) {
		for (unsigned int splitp = 5; splitp < 10; splitp += 3) {
			for (unsigned int splits = 0; splits < splitp; splits += 2) {
				for (unsigned long long jump = 0; jump < 2; ++jump) {
					for (unsigned int jump2 = 0; jump2 < 64; jump2 += 23) {

						std::cout <<
							"{new Long(" << static_cast<long long>(seed*74236788222246L) << "L), " <<
							"new Integer(" << static_cast<long long>(splitp) << "), " <<
							"new Integer(" << static_cast<long long>(splits) << "), " <<
							"new Long(" << static_cast<long long>(jump*948392782247324L) << "L), " <<
							"new Integer(" << static_cast<long long>(jump2) << ")}," << std::endl;


						TRNGRandomOutput<trng::lcg64_shift> random(
							seed*74236788222246L,
							splitp, splits,
							jump*948392782247324L, jump2
						);
						write<trng::lcg64_shift>("output", random, 150);
					}
				}
			}
		}
	}

	return 0;
}

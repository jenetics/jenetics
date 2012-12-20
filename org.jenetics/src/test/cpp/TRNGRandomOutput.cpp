#include <cstdlib>
#include <iostream>
#include <iomanip>
#include <sstream>
#include <fstream>
#include <vector>
#include <trng/lcg64_shift.hpp>


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

int main(void) {

	int count = 0;

	for (unsigned long long seed = 0; seed < 2; ++seed) {
		for (unsigned int splitp = 5; splitp < 10; splitp += 3) {
			for (unsigned int splits = 0; splits < splitp; splits += 2) {
				for (unsigned long long jump = 0; jump < 2; ++jump) {
					for (unsigned int jump2 = 0; jump2 < 64; jump2 += 23) {
						TRNGRandomOutput<trng::lcg64_shift> random(
							seed*74236788222246L,
							splitp, splits,
							jump*948392782247324L, jump2
						);
						std::cout << (++count) << ": " << random.fileName() << std::endl;
					}
				}
			}
		}
	}

	return 0;
}

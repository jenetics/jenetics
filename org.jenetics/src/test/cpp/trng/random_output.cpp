#include <cstdlib>
#include <iostream>
#include <iomanip>
#include <vector>
#include <trng/lcg64_shift.hpp>

unsigned long long pow(unsigned long long x, unsigned long long n) {
	unsigned long long result=1;
	while (n > 0) {
		if ((n&1) > 0) {
			result=result*x;
		}
		x = x*x;
		n >>= 1;
	}
	return result;
}

unsigned int log2_floor(unsigned long long x) {
    unsigned int y(0);
    while (x>0) {
      x>>=1;
      ++y;
    };
    --y;
    return y;
  }

int main(void) {

	trng::lcg64_shift random_default;
	trng::lcg64_shift random_seed_111(111);
	trng::lcg64_shift random_split_3_0;
	random_split_3_0.split(3, 0);

	trng::lcg64_shift random_split_3_1;
	random_split_3_1.split(3, 1);

	trng::lcg64_shift random_split_3_2;
	random_split_3_2.split(3, 2);

	trng::lcg64_shift random_jump_6361;
	trng::lcg64_shift random_jump2_5667;


	std::cout << "# default, seed 111, split 3-0, split 3-1, split 3-2, jump-6361, jump2-5657" << std::endl;
	for (int i = 0; i < 1009; ++i) {
		std::cout << static_cast<long long>(random_default()) << ',';
		std::cout << static_cast<long long>(random_seed_111()) << ',';
		std::cout << static_cast<long long>(random_split_3_0()) << ',';
		std::cout << static_cast<long long>(random_split_3_1()) << ',';
		std::cout << static_cast<long long>(random_split_3_2()) << ',';

		random_jump_6361.jump(i);
		std::cout << static_cast<long long>(random_jump_6361()) << ',';

		random_jump2_5667.jump2(i%64);
		std::cout << static_cast<long long>(random_jump2_5667()) << '\n';
	}

	return 0;
}

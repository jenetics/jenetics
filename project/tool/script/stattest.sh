#!/bin/sh

java org.jenetics.util.DieHarder org.jenetics.util.XORShiftRandom -a
java org.jenetics.util.DieHarder org.jenetics.util.HQ32Random -a
java org.jenetics.util.DieHarder org.jenetics.util.HQ64Random -a
java org.jenetics.util.DieHarder org.jenetics.util.ThreadLocalRandom -a
java org.jenetics.util.DieHarder java.util.Random -a
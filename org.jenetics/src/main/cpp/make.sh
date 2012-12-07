#!/bin/sh

gcc -O3 -shared -fpic -o libjenetics.so -I/home/fwilhelm/bin/java/include -I/home/fwilhelm/bin/java/include//linux org_jenetics_util_IndexStream.c
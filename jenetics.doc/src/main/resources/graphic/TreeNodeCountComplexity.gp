#!/usr/bin/gnuplot -p

set terminal svg size 700,300 font "Serif,16"
set output "TreeNodeCountComplexity.svg"

set grid
set xlabel "N(P)"
set ylabel "C(P)"
set xrange [0.0:30.0]

set samples 30
set style line 1 lt 1 lc rgb "#0087ca"
set style fill solid

min(a,b) = (a < b) ? a : b
sqr(a) = a*a

f(x) = 1 - sqrt(1 - sqr(min(x, 28))/sqr(28))


plot f(x) notitle with boxes linestyle 1

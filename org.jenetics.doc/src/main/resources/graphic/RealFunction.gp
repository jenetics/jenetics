#!/usr/bin/gnuplot -p

set terminal svg size 700,300 fname "Serif" fsize 16
set output "RealFunction.svg"

set grid
set xlabel "x"
set ylabel "y"
set xrange [0.0:2*pi]

f(x) = cos(0.5 + sin(x))*cos(x)

plot f(x) notitle with lines linestyle 1

#!/usr/bin/gnuplot -p

set terminal svg size 600,300 enhanced fname "Times Roman" fsize 11
set output "RealFunction2D.svg"

set grid
set xlabel "x"
set ylabel "y"
set xrange [0.0:2*pi]

f(x) = cos(0.5 + sin(x))*cos(x)

plot f(x) notitle with lines linestyle 1

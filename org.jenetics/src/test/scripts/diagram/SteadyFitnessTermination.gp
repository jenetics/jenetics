#!/usr/bin/gnuplot -p

data = "SteadyFitnessTermination.dat"

set terminal svg size 700,500 enhanced fname "Times Roman" fsize 11
set output "SteadyFitnessTermination.svg"

set grid
set logscale x
set logscale y
set yrange[1:]
set format y "   10^{%L}"
set key right bottom
set style fill empty

set multiplot
set size 1, 0.67
set origin 0, 0.33
set bmargin 0.1
set format x ""
set ylabel "Total generation" font ",12"

set lmargin 12
set rmargin 4

#f(x) = a*x + b
#fit f(x) data using 1:9 via a, b
#title_f(a,b) = sprintf('f(x) = %.2fx + %.2f', a, b)

plot data using 1:7:9:10:8 with candlesticks lt rgb "red" notitle axes x1y1 whiskerbars, \
     data using 1:6:6:6:6 with candlesticks lt rgb "red" lw 2 notitle axes x1y1, \
     data using 1:2 with lines notitle lt rgb "red" axes x1y1 #, \
     #f(x) title title_f(a, b)

unset title
unset logscale y
set autoscale y
set ylabel "Fitness"
set xlabel "Steady generation" font ",12"
set bmargin
set format x "10^{%L}"
set size 1.0, 0.33
set origin 0.0, 0.0
set tmargin 1
set format y " %2.1f"

plot data using 1:(0.001*$11) with lines lt rgb "blue" notitle axes x1y1

unset multiplot

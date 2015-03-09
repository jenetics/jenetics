#!/usr/bin/gnuplot -p

data = "steady_fitness_termination.dat"

set terminal svg size 850,500 enhanced fname "Times Roman" fsize 11
set output "steady_fitness_termination.svg"

set title "Steady fitness termination" font ",15"
set key center top title " "
set ylabel "Total generation"
set grid

set logscale x
set logscale y
set format y " 10^{%L}"
set key right bottom
set style fill empty

set multiplot
set size 1, 0.67
set origin 0, 0.33
set bmargin 0
set format x ""

set lmargin 9
set rmargin 4

plot data using 1:7:9:10:8 with candlesticks lt rgb "red" \
          title "Total generation" axes x1y1 whiskerbars, \
     data using 1:6:6:6:6 with candlesticks lt rgb "red" lw 2 notitle axes x1y1, \
     data using 1:2 with lines notitle lt rgb "red" axes x1y1


unset title
set ylabel ""
set xlabel "Steady generation"
set bmargin
set format x "10^{%L}"
set format y "        "
set size 1.0, 0.33
set origin 0.0, 0.0
set tmargin 0
unset logscale y
set format y ""
set origin 0.0, 0.0

plot data using 1:11 with lines lt rgb "blue" title "Fitness" axes x1y1

unset multiplot

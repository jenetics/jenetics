#!/usr/bin/gnuplot -p

set terminal svg size 650,400 fname "Verdana" fsize 11
set output "steady_fitness_termination.svg"

set title "Steady fitness termination" font ",15"
set key center top title " "
#set xlabel "Steady generation"
set ylabel "Total generation"
#set y2label "Fitness"
set grid

#set label 1 "Samples: 1000"
#set label 1 at graph 0.02, 0.94 tc lt 3

set autoscale y
set autoscale y2
set key right bottom
set style fill empty

data = "steady_fitness_termination.dat"

set multiplot
set size 1, 0.67
set origin 0, 0.33
set bmargin 0
set format x ""

set xrange [ 0.00000 : 33.0000 ]
#set x2range [ 0.00000 : 190.0000 ]
set lmargin 9
set rmargin 2

plot data using 1:7:9:10:8 with candlesticks lt rgb "red" \
          title "Total generation" axes x1y1 whiskerbars, \
     data using 1:6:6:6:6 with candlesticks lt rgb "red" lw 2 notitle axes x1y1, \
     data using 1:2 with lines notitle lt rgb "red" axes x1y1

unset title
set ylabel ""
set xlabel "Steady generation"
set bmargin
set format x
set size 1.0, 0.33
set origin 0.0, 0.0
set tmargin 0
unset logscale y
set autoscale y
#set format y "%1.0f"
#set ytics 500
set format y ""
#set lmargin  8.63
set origin 0.0, 0.0
#set logscale y

plot data using 1:11 with lines lt rgb "blue" title "Fitness" axes x1y1

unset multiplot

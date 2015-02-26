#!/usr/bin/gnuplot -p

set terminal svg size 650,400 fname "Verdana" fsize 11
set output "steady_fitness_termination.svg"

set title "Steady fitness termination" font ",15"
set key center top title " "
set xlabel "Steady generations"
set ylabel "Total generations"
set y2label "Fitness"
set autoscale y
set autoscale y2
set key right bottom
set style fill empty

data = "steady_fitness_termination.dat"

set xrange [ 0.00000 : 50.0000 ]
set x2range [ 0.00000 : 50.0000 ]
#set yrange [ 0.00000 : 210.0000 ] noreverse nowriteback

plot data using 1:7:9:10:8 with candlesticks title "Total generations" axes x1y1 whiskerbars, \
     data using 1:6:6:6:6 with candlesticks lw 2 notitle axes x1y1, \
     data using 1:2 with lines notitle axes x1y1, \
     data using 1:11 with lines title "Fitness" axes x2y2

#!/usr/bin/gnuplot -p

set title "Steady state termination" font ",15"
set key center top title " "

set xlabel "Steady generations"
set ylabel "Total generations"
set y2label "Fitness"

set grid x y
set ytics
set y2tics
set autoscale y
set autoscale y2

plot "data.txt" using 1:2 with linespoints title "Total generations" axes x1y1, \
     "data.txt" using 1:3 with linespoints title "Best fitness" axes x2y2



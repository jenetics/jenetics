#!/usr/bin/gnuplot -p

set terminal svg size 650,400 fname "Verdana" fsize 11
set output "steady_fitness_termination.svg"

set title "Steady fitness termination" font ",15"
set key center top title " "

set xlabel "Steady generations"
set ylabel "Total generations"
set y2label "Fitness"

#set grid x y
#set ytics
#set y2tics
set autoscale y
set autoscale y2
set key right bottom

set style fill empty
#set border linewidth 2
#set style line 1 linecolor rgb '#0060ad' linetype 1 linewidth 5
#set style line 2 linecolor rgb '#dd181f' linetype 1 linewidth 5

data = "steady_fitness_termination.dat"

#plot data using 1:2 with linespoints title "Total generations" axes x1y1, \
#     data using 1:3 with linespoints title "Best fitness" axes x2y2

#plot data using 1:2 with lines title "Total generations" axes x1y1, \
#     data using 1:3 with lines title "Best fitness" axes x2y2

plot data using 1:4:6:7:5 with candlesticks title "Total generations" axes x1y1 whiskerbars, \
     data using 1:2:2:2:2 with candlesticks lt -1 lw 2 notitle axes x1y1, \
     data using 1:3 with lines title "Best fitness" axes x2y2



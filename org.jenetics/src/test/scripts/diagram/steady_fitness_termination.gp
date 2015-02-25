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

set xrange [ 0.00000 : 55.0000 ]
set x2range [ 0.00000 : 55.0000 ]
#set yrange [ 0.00000 : 210.0000 ] noreverse nowriteback

#plot data using 1:2 with linespoints title "Total generations" axes x1y1, \
#     data using 1:7 with linespoints title "Best fitness" axes x2y2

#plot data using 1:2 with lines title "Total generations" axes x1y1, \
#     data using 1:3 with lines title "Best fitness" axes x2y2

plot data using 1:3:5:6:4 with candlesticks title "Total generations" axes x1y1 whiskerbars, \
     data using 1:2 with lines notitle axes x1y1, \
     data using 1:8:10:11:9 with candlesticks title "Best fitness" axes x2y2 whiskerbars, \
     data using 1:7 with lines notitle axes x2y2

#data using 1:2:2:2:2 with candlesticks lt -1 lw 2 notitle axes x1y1, \
#data using 1:8:10:11:9 with candlesticks title "Best fitness" axes x2y2 whiskerbars, \
#	gen, // 1
#	generations.getMean(), // 2
#	fitness.getMean(), // 3
#	quartileLower.getValue(), // 4
#	quartileUpper.getValue(), // 5
#	generations.getMin(), // 6
#	generations.getMax() // 7

#	1:4:6:7:5

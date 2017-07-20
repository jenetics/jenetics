#!/usr/bin/gnuplot -p

#set terminal svg size 600,500 fname "Serif" fsize 16
#set output "RastriginFunction.svg"

set terminal png transparent size 1200,1000 font "Serif" 16
set output "RastriginFunction.png"

set grid
set xlabel "x"
set ylabel "y"
set ticslevel 0
set isosample 250
#set contour
set nosurface
#set contour base
set view 65,30
set pm3d
set xrange [-5.12:5.12]
set yrange [-5.12:5.12]

#set cntrparam levels incremental -1, 0.2, 1
#set cntrparam levels discrete -0.2, -0.5, 0.2, 0.5

f(x,y) = 20 + x**2 - 10*cos(2*pi*x) + y**2 - 10*cos(2*pi*y)

splot f(x,y) notitle with lines linestyle 1 linewidth 0.01

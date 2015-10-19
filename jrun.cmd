@echo off

set MODULES=(org.jenetics org.jenetics.example)

for %%m in %MODULES% do (
    set CLASSPATH=%%m\build\classes\main;%%m\build\resources\main;%%m\build\classes\test;%%m\build\resources\test
)

java -cp %CLASSPATH% %*

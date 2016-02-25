@echo off
setlocal EnableDelayedExpansion 

set SCRIPT_DIR=%~dp0

set MODULES=(^
	%SCRIPT_DIR%org.jenetics^
	%SCRIPT_DIR%org.jenetics.example^
	%SCRIPT_DIR%org.jenetics.tool^
)

set CLASSPATH=.
for %%m in %MODULES% do (
	set CLASSPATH=!CLASSPATH!;%%m\build\classes\main
	set CLASSPATH=!CLASSPATH!;%%m\build\resources\main
	set CLASSPATH=!CLASSPATH!;%%m\build\classes\test
	set CLASSPATH=!CLASSPATH!;%%m\build\resources\test
)

java -cp %CLASSPATH% %*

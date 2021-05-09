@echo off
setlocal EnableDelayedExpansion

set SCRIPT_DIR=%~dp0

set MODULES=(^
	%SCRIPT_DIR%jenetics^
	%SCRIPT_DIR%jenetics.example^
	%SCRIPT_DIR%jenetics.ext^
	%SCRIPT_DIR%jenetics.incubator^
	%SCRIPT_DIR%jenetics.prog^
	%SCRIPT_DIR%jenetics.tool^
	%SCRIPT_DIR%jenetics.xml^
)

set CLASSPATH=.
for %%m in %MODULES% do (
	set CLASSPATH=!CLASSPATH!;%%m\build\classes\main
	set CLASSPATH=!CLASSPATH!;%%m\build\resources\main
	set CLASSPATH=!CLASSPATH!;%%m\build\classes\test
	set CLASSPATH=!CLASSPATH!;%%m\build\resources\test
)

java -cp %CLASSPATH% %*

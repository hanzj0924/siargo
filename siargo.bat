@echo off

:: -------------------------------------------------------------------------
:: name:         jfinal.bat
:: version:      1.0
:: author:       山东小木
:: email:        909854136@qq.com
:: 使用说明：
::
:: 1: 该脚本用于别的项目时只需要修改 MAIN_CLASS 即可运行
::
:: 2: JAVA_OPTS 可通过 -D 传入 undertow.port 与 undertow.host 这类参数覆盖
::    配置文件中的相同值此外还有 undertow.resourcePath, undertow.ioThreads
::    undertow.workerThreads 共五个参数可通过 -D 进行传入
::
:: 3: JAVA_OPTS 可传入标准的 java 命令行参数,例如 -Xms256m -Xmx1024m 这类常用参数
::
::
:: -------------------------------------------------------------------------

setlocal & pushd %~dp0

:: 创建 ESC 字符用于 ANSI 转义序列（使用 forfiles 将 0x1B 转为 ESC 字符）
for /f "delims=" %%a in ('forfiles /p "%~dp0." /m "%~nx0" /c "cmd /c echo 0x1B"') do set "ESC=%%a"
if not defined ESC set "ESC=["

:: 启动入口类,该脚本文件用于别的项目时要改这里
set MAIN_CLASS=cn.jbolt.starter.Starter

:: Java 命令行参数,根据需要开启下面的配置,改成自己需要的,注意等号前后不能有空格
:: set "JAVA_OPTS=-Xms256m -Xmx1024m -Dundertow.port=80 -Dundertow.host=0.0.0.0"
:: set "JAVA_OPTS=-Dundertow.port=80 -Dundertow.host=0.0.0.0"


:: 如果有命令行参数，直接执行对应操作
if "%1"=="start" ( call :start & goto end )
if "%1"=="stop" ( call :stop & goto end )
if "%1"=="restart" ( call :restart & goto end )
if not "%1"=="" goto usage


:menu
cls
echo.
echo   %ESC%[90m══════════════════════════════════════%ESC%[0m
echo.
echo        %ESC%[1;36mSiargo Server Console%ESC%[0m
echo.
echo   %ESC%[90m──────────────────────────────────────%ESC%[0m
echo.
echo     %ESC%[1;33m[1]%ESC%[0m  %ESC%[32mSTART%ESC%[0m     %ESC%[37m启动服务%ESC%[0m
echo     %ESC%[1;33m[2]%ESC%[0m  %ESC%[32mSTOP%ESC%[0m      %ESC%[37m停止服务%ESC%[0m
echo     %ESC%[1;33m[3]%ESC%[0m  %ESC%[32mRESTART%ESC%[0m   %ESC%[37m重启服务%ESC%[0m
echo     %ESC%[1;33m[0]%ESC%[0m  %ESC%[32mEXIT%ESC%[0m      %ESC%[37m退出%ESC%[0m
echo.
echo   %ESC%[90m══════════════════════════════════════%ESC%[0m
echo.
set "choice="
set /p choice=  %ESC%[36m^>^>^>%ESC%[0m %ESC%[37m请选择操作 [0-3]:%ESC%[0m 
if "%choice%"=="1" ( call :start & goto end )
if "%choice%"=="2" ( call :stop & goto end )
if "%choice%"=="3" ( call :restart & goto end )
if "%choice%"=="0" goto end
echo.
echo   %ESC%[31m[!] 无效选项，请重新输入%ESC%[0m
echo.
pause
goto menu


:usage
echo Usage: jfinal.bat start ^| stop ^| restart
goto :eof


:start
set APP_BASE_PATH=%~dp0
set CP=%APP_BASE_PATH%config;%APP_BASE_PATH%lib\*
echo   %ESC%[1;36m[*]%ESC%[0m %ESC%[32mStarting Siargo Server...%ESC%[0m
set JDK_OPTS=--add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.lang.reflect=ALL-UNNAMED --add-opens java.base/java.lang.invoke=ALL-UNNAMED --add-opens java.base/java.util=ALL-UNNAMED --add-opens java.base/java.io=ALL-UNNAMED --add-opens java.base/sun.reflect.annotation=ALL-UNNAMED
java -Xverify:none %JDK_OPTS% -Dfile.encoding=GBK %JAVA_OPTS% -cp %CP% %MAIN_CLASS%
goto :eof


:stop
set "PATH=%JAVA_HOME%\bin;%PATH%"
echo   %ESC%[1;33m[*]%ESC%[0m %ESC%[33mStopping Siargo Server...%ESC%[0m
for /f "tokens=1" %%i in ('jps -l ^| find "%MAIN_CLASS%"') do ( taskkill /F /PID %%i )
goto :eof


:restart
call :stop
call :start
goto :eof


:end
endlocal & popd
pause
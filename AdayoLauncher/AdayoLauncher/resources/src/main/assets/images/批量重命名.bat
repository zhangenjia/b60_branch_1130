@echo off & setlocal enableDelayedExpansion
rem 1 这是提示输入文件位置，直接打开文件夹复制路径、黏贴就行
set /p path=please paste/input your Excel Path :
set /p str=Please enter the character to be replaced:
set /p ChangeStr=Please enter the target character:

REM 当前路径，如果在当前文件解开下面注释即可，
REM set path=%~dp0

cd /d %path%

rem 2 这是通过循环去修改文件夹里面的文件名字
for /f "delims=!" %%i in ('dir /aa /b %path%') do (
::echo in progressing %%i_%%j_%%k
set str1=%%i
set str2=!str1:%str%=%ChangeStr%!
echo str1 is !str1! str2 is !str2!
ren "!str1!" "!str2!"

)

rem 3 结束，并打开文件位置
echo.
echo.
echo its ok now,the file locate at %path%

rem 打开文件位置
REM C:\Windows\explorer.exe %path%

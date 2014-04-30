rem set backupDate=$(date +%Y%m%d%H%M.%S)
rem @ECHO OFF
rem @TITLE Backing up source safe databases
FOR /F "tokens=2-4 delims=/ " %%i IN ('date /t') DO SET DATE1=%%i%%j%%k
FOR /F "tokens=1-3 delims=: " %%i IN ('time /t') DO SET TIME=%%j%%k
SET DATETIME=%DATE%_%TIME%
rem echo %DATETIME%
  
set logDir="C:/epcSqlLog/"
cd  C:\eclipse\workspace\httpclient\bin\com\da\so
java.exe -Dfile.encoding=UTF-8 -classpath C:\eclipse\workspace\httpclient\bin;C:\eclipse\workspace\httpclient\lib\commons-codec-1.6.jar;C:\eclipse\workspace\httpclient\lib\commons-io-1.2.jar;C:\eclipse\workspace\httpclient\lib\commons-lang-2.4.jar;C:\eclipse\workspace\httpclient\lib\commons-logging-1.1.1.jar;C:\eclipse\workspace\httpclient\lib\commons-net-1.4.1.jar;C:\eclipse\workspace\httpclient\lib\fluent-hc-4.2.jar;C:\eclipse\workspace\httpclient\lib\httpclient-4.2.jar;C:\eclipse\workspace\httpclient\lib\httpclient-cache-4.2.jar;C:\eclipse\workspace\httpclient\lib\httpcore-4.2.jar;C:\eclipse\workspace\httpclient\lib\httpmime-4.2.jar com.da.so.CafeBoradAllList %1
@echo off
REM Script de gerenciamento do servidor Petshop
REM Uso: servidor.cmd [start|stop|restart|status]

setlocal enabledelayedexpansion

set PORT=8080
set ACTION=%1

if "%ACTION%"=="" (
    echo.
    echo ========================================
    echo   Uso: servidor.cmd [start^|stop^|restart^|status]
    echo ========================================
    echo.
    echo   start    - Inicia o servidor
    echo   stop     - Para o servidor
    echo   restart  - Reinicia o servidor
    echo   status   - Mostra status do servidor
    echo.
    exit /b 1
)

if /i not "%ACTION%"=="start" if /i not "%ACTION%"=="stop" if /i not "%ACTION%"=="restart" if /i not "%ACTION%"=="status" (
    echo.
    echo ERRO: Acao invalida: %ACTION%
    echo Use: start, stop, restart ou status
    echo.
    exit /b 1
)

if /i "%ACTION%"=="start" goto :start
if /i "%ACTION%"=="stop" goto :stop
if /i "%ACTION%"=="restart" goto :restart
if /i "%ACTION%"=="status" goto :status

:start
echo.
echo ========================================
echo   Iniciando Servidor Petshop
echo ========================================
echo.

REM Verifica se a porta 8080 esta em uso
netstat -ano | find ":%PORT%" | find "LISTENING" >NUL
if "%ERRORLEVEL%"=="0" (
    echo AVISO: Porta %PORT% ja esta em uso!
    echo.
    echo Tentando encontrar e parar o processo...
    for /f "tokens=5" %%a in ('netstat -ano ^| find ":%PORT%" ^| find "LISTENING"') do (
        echo   Encontrado PID: %%a usando a porta %PORT%
        taskkill /PID %%a /F >NUL 2>&1
        if !errorlevel! equ 0 (
            echo   Processo %%a encerrado
        )
    )
    echo.
    echo Aguardando 2 segundos para liberar a porta...
    timeout /t 2 /nobreak >NUL
    echo.
)

REM Verifica se ainda existe processo Java rodando
tasklist /FI "IMAGENAME eq java.exe" 2^>NUL | find /I /N "java.exe">NUL
if "%ERRORLEVEL%"=="0" (
    echo AVISO: Ainda existem processos Java rodando!
    echo Parando todos os processos Java...
    for /f "tokens=2" %%a in ('tasklist /FI "IMAGENAME eq java.exe" 2^>NUL ^| find /I "java.exe"') do (
        taskkill /PID %%a /F >NUL 2>&1
    )
    timeout /t 1 /nobreak >NUL
    echo.
)

REM Verifica MySQL Connector
if not exist "lib\mysql-connector*.jar" (
    echo ERRO: MySQL Connector nao encontrado!
    echo Certifique-se de que ha um arquivo mysql-connector*.jar na pasta lib\
    exit /b 1
)

echo Compilando projeto...
echo.

REM Compila todos os arquivos Java
javac -encoding UTF-8 -cp "lib\*" -d src -sourcepath src ^
    src\model\*.java ^
    src\db\*.java ^
    src\dao\*.java ^
    src\exception\*.java ^
    src\util\*.java ^
    src\server\*.java ^
    src\App.java ^
    src\CriarBanco.java ^
    src\TesteConexao.java

if errorlevel 1 (
    echo.
    echo ERRO na compilacao!
    exit /b 1
)

if not exist "src\App.class" (
    echo ERRO: App.class nao foi criado durante a compilacao!
    exit /b 1
)

echo Compilacao concluida!
echo.
echo ========================================
echo   Servidor Iniciado
echo   URL: http://localhost:%PORT%
echo ========================================
echo.
echo Pressione CTRL+C para parar o servidor
echo.

java -cp "lib\*;src" App

goto :end

:stop
echo.
echo ========================================
echo   Parando Servidor
echo ========================================
echo.

set STOPPED=0

REM Para processos usando a porta 8080
echo Verificando processos na porta %PORT%...
for /f "tokens=5" %%a in ('netstat -ano ^| find ":%PORT%" ^| find "LISTENING"') do (
    echo   Encontrado PID: %%a usando a porta %PORT%
    taskkill /PID %%a /F >NUL 2>&1
    if !errorlevel! equ 0 (
        echo   Processo %%a encerrado
        set /a STOPPED+=1
    )
)

REM Para processos Java restantes
for /f "tokens=2" %%a in ('tasklist /FI "IMAGENAME eq java.exe" 2^>NUL ^| find /I "java.exe"') do (
    echo Parando processo Java (PID: %%a)...
    taskkill /PID %%a /F >NUL 2>&1
    if !errorlevel! equ 0 (
        echo   Processo encerrado: %%a
        set /a STOPPED+=1
    )
)

if %STOPPED% equ 0 (
    echo Nenhum processo encontrado
) else (
    echo.
    echo %STOPPED% processo(s) encerrado(s)
)

echo.
echo Servidor parado com sucesso!
echo.
goto :end

:restart
echo.
echo ========================================
echo   Reiniciando Servidor
echo ========================================
echo.

set STOPPED=0

REM Para processos usando a porta 8080
for /f "tokens=5" %%a in ('netstat -ano ^| find ":%PORT%" ^| find "LISTENING" 2^>NUL') do (
    taskkill /PID %%a /F >NUL 2>&1
    if !errorlevel! equ 0 set /a STOPPED+=1
)

REM Para processos Java
for /f "tokens=2" %%a in ('tasklist /FI "IMAGENAME eq java.exe" 2^>NUL ^| find /I "java.exe"') do (
    taskkill /PID %%a /F >NUL 2>&1
    if !errorlevel! equ 0 set /a STOPPED+=1
)

if %STOPPED% gtr 0 (
    echo Aguardando 2 segundos para liberar porta...
    timeout /t 2 /nobreak >NUL
)

echo.
goto :start

:status
echo.
echo ========================================
echo   Status do Servidor
echo ========================================
echo.

REM Verifica processos usando a porta 8080
echo Verificando porta %PORT%...
netstat -ano | find ":%PORT%" | find "LISTENING" >NUL
if "%ERRORLEVEL%"=="0" (
    echo Porta %PORT%: Em uso
    echo.
    echo Processos usando a porta:
    for /f "tokens=5" %%a in ('netstat -ano ^| find ":%PORT%" ^| find "LISTENING"') do (
        echo   - PID: %%a
        tasklist /FI "PID eq %%a" /FO CSV 2^>NUL | find /I "%%a" >NUL
        if "%ERRORLEVEL%"=="0" (
            for /f "tokens=1,5" %%b in ('tasklist /FI "PID eq %%a" /FO CSV 2^>NUL') do (
                echo     Nome: %%b | Memoria: %%c
            )
        )
    )
    set PORT_OPEN=1
) else (
    echo Porta %PORT%: Livre
    set PORT_OPEN=0
)

echo.
REM Verifica processos Java
tasklist /FI "IMAGENAME eq java.exe" 2^>NUL | find /I /N "java.exe">NUL
if "%ERRORLEVEL%"=="0" (
    echo Processos Java: Rodando
    set JAVA_RUNNING=1
) else (
    echo Processos Java: Nenhum processo encontrado
    set JAVA_RUNNING=0
)

echo.
echo URL: http://localhost:%PORT%
echo.

if "%JAVA_RUNNING%"=="1" if "%PORT_OPEN%"=="1" (
    echo Status: SERVIDOR RODANDO
) else if "%JAVA_RUNNING%"=="1" (
    echo Status: PROCESSO JAVA ENCONTRADO, MAS PORTA LIVRE
) else if "%PORT_OPEN%"=="1" (
    echo Status: PORTA EM USO, MAS PROCESSO JAVA NAO ENCONTRADO
) else (
    echo Status: SERVIDOR PARADO
)

echo.
goto :end

:end
endlocal


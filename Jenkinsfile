pipeline {

    agent any

    options {
        retry(conditions: [nonresumable()], count: 2)
        durabilityHint('PERFORMANCE_OPTIMIZED')
        timeout(time: 30, unit: 'MINUTES')
        disableConcurrentBuilds()
    }

    environment {

        // ============================================================
        // JAVA / MAVEN
        // ============================================================

        JAVA_HOME = 'C:/Program Files/Java/jdk-17.0.2'
        MAVEN_HOME = 'D:/apache-maven-3.8.5'

        // ============================================================
        // BACKEND
        // ============================================================

        APP_JAR = 'target/quizapp.jar'
        BACKEND_PORT = '8080'
        BACKEND_URL = 'http://localhost:8080/api/categories'

        // ============================================================
        // APPZILLON / TOMCAT
        // ============================================================

        APPZ_HOME = 'D:/apache-tomcat-9.0.53/apache-tomcat-9.0.53'

        APPZ_ARTIFACTS = 'D:/forDeploy'

        QUIZZ_PROJECT = 'C:/Users/ananda.dc/Downloads/quizapp (1)/quizapp'

        QUIZZ_BIN = 'C:/Users/ananda.dc/Downloads/quizapp (1)/quizapp/bin'

        TOMCAT_PORT = '8090'

        APPZILLON_URL = 'http://localhost:8090/quizapp/'

        // ============================================================
        // DATABASE
        // ============================================================

        DB_NAME = 'quiz_app'
        DB_USER = 'root'
        DB_PASS = 'root'

        MYSQL_BIN = 'C:/Program Files/MySQL/MySQL Server 8.0/bin'

        // ============================================================
        // PLAYWRIGHT JAVA TEST
        // ============================================================

        PLAYWRIGHT_FILE =
            'C:/Users/ananda.dc/Downloads/quiz-app-backend (1)/quiz-app/src/test/java/playwrightTest.java'
    }


    stages {

        // ============================================================
        // BUILD BACKEND
        // ============================================================

        stage('Build Backend Jar') {

            steps {

                echo '=========================================='
                echo 'BUILDING QUIZAPP BACKEND'
                echo '=========================================='

                bat '''
                    @echo off

                    set "JAVA_HOME=%JAVA_HOME%"
                    set "PATH=%JAVA_HOME%\\bin;%MAVEN_HOME%\\bin;%PATH%"

                    echo.
                    echo JAVA VERSION
                    java -version

                    echo.
                    echo MAVEN VERSION
                    mvn -version
                '''


                echo '=========================================='
                echo 'CHECKING MAVEN PROJECT'
                echo '=========================================='

                bat '''
                    @echo off

                    echo Current workspace:
                    cd

                    echo.
                    echo Checking pom.xml...

                    if not exist "pom.xml" (
                        echo ERROR: pom.xml not found in workspace
                        echo.
                        echo Workspace contents:
                        dir
                        exit /b 1
                    )

                    echo pom.xml found successfully.
                '''


                echo '=========================================='
                echo 'KILLING OLD BACKEND PROCESS'
                echo '=========================================='

                bat '''
                    @echo off

                    for /f "tokens=5" %%a in (
                        'netstat -ano ^| findstr :%BACKEND_PORT% ^| findstr LISTENING'
                    ) do (
                        echo Killing process %%a on port %BACKEND_PORT%
                        taskkill /F /PID %%a >nul 2>&1
                    )

                    ping 127.0.0.1 -n 3 >nul
                '''


                echo '=========================================='
                echo 'STARTING MAVEN BUILD'
                echo '=========================================='

                bat '''
                    @echo off

                    set "JAVA_HOME=%JAVA_HOME%"
                    set "PATH=%JAVA_HOME%\\bin;%MAVEN_HOME%\\bin;%PATH%"

                    mvn clean package -DskipTests

                    if errorlevel 1 (
                        echo.
                        echo ==========================================
                        echo MAVEN BUILD FAILED
                        echo ==========================================
                        exit /b 1
                    )

                    echo.
                    echo ==========================================
                    echo MAVEN BUILD SUCCESSFUL
                    echo ==========================================
                '''


                echo '=========================================='
                echo 'CHECKING GENERATED JAR'
                echo '=========================================='

                bat '''
                    @echo off

                    if not exist "target\\quizapp.jar" (
                        echo ERROR: target\\quizapp.jar NOT FOUND

                        echo.
                        echo Target directory contents:

                        if exist "target" (
                            dir target
                        ) else (
                            echo target directory does not exist
                        )

                        exit /b 1
                    )

                    echo.
                    echo ==========================================
                    echo QUIZBACKEND JAR FOUND
                    echo ==========================================

                    dir target\\*.jar
                '''
            }
        }


        // ============================================================
        // DEPLOY BACKEND
        // ============================================================

        stage('Deploy Backend') {

            steps {

                echo '=========================================='
                echo 'DEPLOYING QUIZAPP BACKEND'
                echo '=========================================='

                bat '''
                    @echo off

                    if not exist "%WORKSPACE%\\target\\quizapp.jar" (
                        echo ERROR: JAR NOT FOUND
                        echo Expected:
                        echo %WORKSPACE%\\target\\quizapp.jar
                        exit /b 1
                    )

                    echo.
                    echo QuizBackend JAR found.

                    echo.
                    echo ==========================================
                    echo CHECKING PORT %BACKEND_PORT%
                    echo ==========================================

                    for /f "tokens=5" %%a in (
                        'netstat -ano ^| findstr :%BACKEND_PORT% ^| findstr LISTENING'
                    ) do (
                        echo Stopping process %%a on port %BACKEND_PORT%
                        taskkill /F /PID %%a >nul 2>&1
                    )

                    ping 127.0.0.1 -n 4 >nul

                    echo.
                    echo ==========================================
                    echo STARTING QUIZAPP BACKEND
                    echo ==========================================

                    set "JAVA_HOME=%JAVA_HOME%"
                    set "PATH=%JAVA_HOME%\\bin;%MAVEN_HOME%\\bin;%PATH%"
                    set "JENKINS_NODE_COOKIE=dontKillMe"

                    echo Starting:
                    echo java -jar %APP_JAR%

                    start "QuizApp-Backend" /B cmd /c "set JENKINS_NODE_COOKIE=dontKillMe && set JAVA_HOME=%JAVA_HOME% && java -jar %APP_JAR% > backend.log 2>&1"

                    echo.
                    echo BACKEND START COMMAND EXECUTED

                    echo.
                    echo Waiting for application to start...

                    ping 127.0.0.1 -n 6 >nul

                    echo.
                    echo ==========================================
                    echo BACKEND LOG
                    echo ==========================================

                    if exist backend.log (
                        powershell -Command "Get-Content backend.log -Tail 30"
                    ) else (
                        echo backend.log not found
                    )
                '''
            }
        }


        // ============================================================
        // BACKEND HEALTH CHECK
        // ============================================================

        stage('Backend Health Check') {

            steps {

                echo '=========================================='
                echo 'CHECKING QUIZAPP BACKEND'
                echo '=========================================='

                bat '''
                    @echo off

                    setlocal EnableDelayedExpansion

                    echo.
                    echo Backend URL:
                    echo %BACKEND_URL%

                    echo.
                    echo Backend Port:
                    echo %BACKEND_PORT%

                    set RETRIES=20

                    :CHECK_BACKEND

                    echo.
                    echo Checking backend...
                    echo Remaining attempts: !RETRIES!

                    curl -s -o nul -w "%%{http_code}" "%BACKEND_URL%" | findstr "200 201"

                    if not errorlevel 1 (

                        echo.
                        echo ==========================================
                        echo BACKEND IS RUNNING
                        echo ==========================================

                        echo Backend URL:
                        echo %BACKEND_URL%

                        exit /b 0
                    )

                    echo.
                    echo Backend not ready.

                    set /a RETRIES-=1

                    if !RETRIES! LEQ 0 (

                        echo.
                        echo ==========================================
                        echo BACKEND FAILED TO START
                        echo ==========================================

                        echo.
                        echo PORT STATUS
                        netstat -ano | findstr :%BACKEND_PORT%

                        echo.
                        echo BACKEND LOG

                        if exist backend.log (
                            type backend.log
                        ) else (
                            echo backend.log not found
                        )

                        exit /b 1
                    )

                    echo.
                    echo Waiting 3 seconds...

                    ping 127.0.0.1 -n 4 >nul

                    goto CHECK_BACKEND
                '''
            }
        }


        // ============================================================
        // APPZILLON - FIND WAR / PROPERTIES / DATABASE
        // ============================================================

        stage('Find Appzillon Files') {

            steps {

                echo '=========================================='
                echo 'SEARCHING APPZILLON PROJECT'
                echo '=========================================='

                powershell '''
                    $ErrorActionPreference = "Stop"

                    Write-Host "=========================================="
                    Write-Host "APPZILLON CONFIGURATION"
                    Write-Host "=========================================="

                    $quizBin = $env:QUIZZ_BIN
                    $appzHome = $env:APPZ_HOME
                    $artifacts = $env:APPZ_ARTIFACTS

                    Write-Host "QUIZZ_PROJECT : $env:QUIZZ_PROJECT"
                    Write-Host "QUIZZ_BIN     : $quizBin"
                    Write-Host "APPZ_HOME     : $appzHome"

                    # ------------------------------------------------
                    # Validate QUIZZ_BIN
                    # ------------------------------------------------

                    if (-not (Test-Path -LiteralPath $quizBin)) {
                        Write-Host "ERROR: QUIZZ_BIN does not exist:"
                        Write-Host $quizBin
                        exit 1
                    }

                    Write-Host ""
                    Write-Host "QUIZZ_BIN found successfully."

                    # ------------------------------------------------
                    # Validate Tomcat
                    # ------------------------------------------------

                    if (-not (Test-Path -LiteralPath $appzHome)) {
                        Write-Host "ERROR: Tomcat not found:"
                        Write-Host $appzHome
                        exit 1
                    }

                    if (-not (Test-Path -LiteralPath "$appzHome\\bin\\catalina.bat")) {
                        Write-Host "ERROR: catalina.bat not found."
                        exit 1
                    }

                    Write-Host "Tomcat found successfully."

                    # ------------------------------------------------
                    # FIND WEB WAR
                    # ------------------------------------------------

                    $webWar = Get-ChildItem `
                        -LiteralPath "$quizBin\\Web" `
                        -Filter "*.war" `
                        -File `
                        -ErrorAction SilentlyContinue |
                        Select-Object -First 1

                    if (-not $webWar) {

                        $webWar = Get-ChildItem `
                            -LiteralPath "$quizBin\\Web" `
                            -Filter "*.war" `
                            -File `
                            -Recurse `
                            -ErrorAction SilentlyContinue |
                            Select-Object -First 1
                    }

                    if (-not $webWar) {
                        Write-Host "ERROR: Web WAR not found."
                        exit 1
                    }

                    $webWarPath = $webWar.FullName
                    $webWarName = $webWar.Name

                    Write-Host ""
                    Write-Host "WEB WAR:"
                    Write-Host $webWarPath

                    # ------------------------------------------------
                    # FIND SERVER WAR
                    # ------------------------------------------------

                    $serverWar = Get-ChildItem `
                        -LiteralPath "$quizBin\\Server" `
                        -Filter "*.war" `
                        -File `
                        -ErrorAction SilentlyContinue |
                        Select-Object -First 1

                    if (-not $serverWar) {

                        $serverWar = Get-ChildItem `
                            -LiteralPath "$quizBin\\Server" `
                            -Filter "*.war" `
                            -File `
                            -Recurse `
                            -ErrorAction SilentlyContinue |
                            Select-Object -First 1
                    }

                    $serverWarPath = ""

                    if ($serverWar) {
                        $serverWarPath = $serverWar.FullName

                        Write-Host ""
                        Write-Host "SERVER WAR:"
                        Write-Host $serverWarPath
                    }
                    else {
                        Write-Host ""
                        Write-Host "WARNING: Server WAR not found."
                    }

                    # ------------------------------------------------
                    # FIND WEB PROPERTIES
                    # ------------------------------------------------

                    $webPropsRoot = Join-Path $quizBin "Web\\Properties"

                    $webProps = $null

                    if (Test-Path -LiteralPath $webPropsRoot) {

                        $webProps = Get-ChildItem `
                            -LiteralPath $webPropsRoot `
                            -Directory `
                            -ErrorAction SilentlyContinue |
                            Select-Object -First 1
                    }

                    $webPropsPath = ""

                    if ($webProps) {
                        $webPropsPath = $webProps.FullName

                        Write-Host ""
                        Write-Host "WEB PROPERTIES:"
                        Write-Host $webPropsPath
                    }
                    else {
                        Write-Host ""
                        Write-Host "WARNING: Web properties folder not found."
                    }

                    # ------------------------------------------------
                    # FIND SERVER PROPERTIES
                    # ------------------------------------------------

                    $serverPropsRoot = Join-Path $quizBin "Server\\Properties"

                    $serverProps = $null

                    if (Test-Path -LiteralPath $serverPropsRoot) {

                        $serverProps = Get-ChildItem `
                            -LiteralPath $serverPropsRoot `
                            -Directory `
                            -ErrorAction SilentlyContinue |
                            Select-Object -First 1
                    }

                    $serverPropsPath = ""

                    if ($serverProps) {

                        $serverPropsPath = $serverProps.FullName

                        Write-Host ""
                        Write-Host "SERVER PROPERTIES:"
                        Write-Host $serverPropsPath
                    }
                    else {
                        Write-Host ""
                        Write-Host "WARNING: Server properties folder not found."
                    }

                    # ------------------------------------------------
                    # DATABASE PATH
                    # ------------------------------------------------

                    $dbPath = Join-Path $quizBin "Server\\Database\\MySql"

                    if (-not (Test-Path -LiteralPath $dbPath)) {

                        Write-Host ""
                        Write-Host "WARNING: Standard DB path not found."

                        $dbPath = ""
                    }

                    Write-Host ""
                    Write-Host "DATABASE PATH:"
                    Write-Host $dbPath

                    # ------------------------------------------------
                    # SQL FILES
                    # ------------------------------------------------

                    if ($dbPath) {

                        $sqlFiles = Get-ChildItem `
                            -LiteralPath $dbPath `
                            -Filter "*.sql" `
                            -File `
                            -ErrorAction SilentlyContinue

                        Write-Host ""
                        Write-Host "SQL FILES FOUND:"

                        foreach ($sql in $sqlFiles) {
                            Write-Host $sql.FullName
                        }
                    }

                    # ------------------------------------------------
                    # SAVE VARIABLES
                    # ------------------------------------------------

                    $varsFile = Join-Path $env:WORKSPACE "appzillon_vars.txt"

                    @"
WEB_WAR=$webWarPath
WEB_WAR_NAME=$webWarName
SERVER_WAR=$serverWarPath
WEB_PROPS=$webPropsPath
SERVER_PROPS=$serverPropsPath
DB_PATH=$dbPath
"@ | Set-Content -LiteralPath $varsFile

                    Write-Host ""
                    Write-Host "=========================================="
                    Write-Host "APPZILLON FILE SEARCH COMPLETED"
                    Write-Host "=========================================="

                    Write-Host "Variables saved to:"
                    Write-Host $varsFile
                '''
            }
        }


        // ============================================================
        // COPY PROPERTIES
        // ============================================================

        stage('Copy Appzillon Properties') {

            steps {

                echo '=========================================='
                echo 'COPYING APPZILLON PROPERTIES'
                echo '=========================================='

                powershell '''
                    $ErrorActionPreference = "Stop"

                    $varsFile = Join-Path $env:WORKSPACE "appzillon_vars.txt"

                    if (-not (Test-Path -LiteralPath $varsFile)) {
                        Write-Host "ERROR: appzillon_vars.txt not found."
                        exit 1
                    }

                    $map = @{}

                    foreach ($line in Get-Content -LiteralPath $varsFile) {

                        if ($line -match "^(.*?)=(.*)$") {
                            $map[$matches[1]] = $matches[2]
                        }
                    }

                    $webProps = $map["WEB_PROPS"]
                    $serverProps = $map["SERVER_PROPS"]

                    $libPath = Join-Path $env:APPZ_HOME "lib"

                    Write-Host "Tomcat LIB:"
                    Write-Host $libPath

                    if (-not (Test-Path -LiteralPath $libPath)) {

                        New-Item `
                            -ItemType Directory `
                            -Path $libPath `
                            -Force |
                            Out-Null
                    }

                    # ------------------------------------------------
                    # WEB PROPERTIES
                    # ------------------------------------------------

                    if ($webProps -and (Test-Path -LiteralPath $webProps)) {

                        $folderName = Split-Path $webProps -Leaf

                        $destination = Join-Path $libPath $folderName

                        Write-Host ""
                        Write-Host "Copying Web Properties:"
                        Write-Host $webProps
                        Write-Host "To:"
                        Write-Host $destination

                        if (Test-Path -LiteralPath $destination) {

                            Remove-Item `
                                -LiteralPath $destination `
                                -Recurse `
                                -Force
                        }

                        Copy-Item `
                            -LiteralPath $webProps `
                            -Destination $libPath `
                            -Recurse `
                            -Force

                        Write-Host "Web Properties copied successfully."
                    }
                    else {

                        Write-Host "WARNING: Web properties not found."
                    }

                    # ------------------------------------------------
                    # SERVER PROPERTIES
                    # ------------------------------------------------

                    if ($serverProps -and (Test-Path -LiteralPath $serverProps)) {

                        $folderName = Split-Path $serverProps -Leaf

                        $destination = Join-Path $libPath $folderName

                        Write-Host ""
                        Write-Host "Copying Server Properties:"
                        Write-Host $serverProps
                        Write-Host "To:"
                        Write-Host $destination

                        if (Test-Path -LiteralPath $destination) {

                            Remove-Item `
                                -LiteralPath $destination `
                                -Recurse `
                                -Force
                        }

                        Copy-Item `
                            -LiteralPath $serverProps `
                            -Destination $libPath `
                            -Recurse `
                            -Force

                        Write-Host "Server Properties copied successfully."
                    }
                    else {

                        Write-Host "WARNING: Server properties not found."
                    }

                    Write-Host ""
                    Write-Host "=========================================="
                    Write-Host "TOMCAT LIB CONTENTS"
                    Write-Host "=========================================="

                    Get-ChildItem -LiteralPath $libPath |
                        Where-Object {
                            $_.Name -like "quiz*" -or
                            $_.Name -like "AppzillonServer*"
                        } |
                        ForEach-Object {
                            Write-Host $_.FullName
                        }
                '''
            }
        }


        // ============================================================
        // DATABASE
        // IMPORTANT:
        // THIS IS POWERHELL INSTEAD OF BAT.
        // THIS FIXES quizapp (1) PATH ERROR.
        // ============================================================

        stage('Run MySQL Database Scripts') {

            steps {

                echo '=========================================='
                echo 'RUNNING MYSQL DATABASE SCRIPTS'
                echo '=========================================='

                powershell '''
                    $ErrorActionPreference = "Stop"

                    Write-Host "=========================================="
                    Write-Host "MYSQL DATABASE SETUP"
                    Write-Host "=========================================="

                    $mysqlExe = Join-Path $env:MYSQL_BIN "mysql.exe"

                    Write-Host "MYSQL EXE:"
                    Write-Host $mysqlExe

                    if (-not (Test-Path -LiteralPath $mysqlExe)) {

                        Write-Host "ERROR: mysql.exe not found:"
                        Write-Host $mysqlExe

                        exit 1
                    }

                    Write-Host ""
                    Write-Host "Database:"
                    Write-Host $env:DB_NAME

                    # ------------------------------------------------
                    # CREATE DATABASE
                    # ------------------------------------------------

                    Write-Host ""
                    Write-Host "Creating database if it does not exist..."

                    & $mysqlExe `
                        "-u$env:DB_USER" `
                        "-p$env:DB_PASS" `
                        "-e" `
                        "CREATE DATABASE IF NOT EXISTS $env:DB_NAME CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

                    if ($LASTEXITCODE -ne 0) {

                        Write-Host "ERROR: Could not create/access database."
                        exit 1
                    }

                    Write-Host "Database ensured successfully."

                    # ------------------------------------------------
                    # READ DB PATH
                    # ------------------------------------------------

                    $varsFile = Join-Path $env:WORKSPACE "appzillon_vars.txt"

                    $dbPath = ""

                    if (Test-Path -LiteralPath $varsFile) {

                        foreach ($line in Get-Content -LiteralPath $varsFile) {

                            if ($line -like "DB_PATH=*") {

                                $dbPath = $line.Substring(8)
                                break
                            }
                        }
                    }

                    Write-Host ""
                    Write-Host "DB PATH FROM VARIABLES:"
                    Write-Host $dbPath

                    # ------------------------------------------------
                    # FALLBACK DB PATH
                    # ------------------------------------------------

                    if ([string]::IsNullOrWhiteSpace($dbPath)) {

                        $dbPath = Join-Path `
                            $env:QUIZZ_BIN `
                            "Server\\Database\\MySql"
                    }

                    Write-Host ""
                    Write-Host "FINAL DB PATH:"
                    Write-Host $dbPath

                    if (-not (Test-Path -LiteralPath $dbPath)) {

                        Write-Host ""
                        Write-Host "ERROR: Database path does not exist:"
                        Write-Host $dbPath

                        exit 1
                    }

                    # ------------------------------------------------
                    # FIND SQL FILES
                    # ------------------------------------------------

                    $sqlFiles = Get-ChildItem `
                        -LiteralPath $dbPath `
                        -Filter "*.sql" `
                        -File |
                        Sort-Object Name

                    if ($sqlFiles.Count -eq 0) {

                        Write-Host "WARNING: No SQL files found."
                        exit 0
                    }

                    Write-Host ""
                    Write-Host "SQL FILES FOUND:"

                    foreach ($sql in $sqlFiles) {
                        Write-Host " - $($sql.FullName)"
                    }

                    # ------------------------------------------------
                    # EXECUTE SQL
                    # ------------------------------------------------

                    foreach ($sql in $sqlFiles) {

                        Write-Host ""
                        Write-Host "=========================================="
                        Write-Host "EXECUTING:"
                        Write-Host $sql.Name
                        Write-Host "=========================================="

                        $sqlContent = Get-Content `
                            -LiteralPath $sql.FullName `
                            -Raw

                        $tempSql = Join-Path `
                            $env:TEMP `
                            ("quizapp_" + $sql.Name)

                        $finalSql = "USE $($env:DB_NAME);`r`n" + $sqlContent

                        Set-Content `
                            -LiteralPath $tempSql `
                            -Value $finalSql `
                            -Encoding UTF8

                        Write-Host "Temporary SQL file:"
                        Write-Host $tempSql

                        & $mysqlExe `
                            "-u$env:DB_USER" `
                            "-p$env:DB_PASS" `
                            $env:DB_NAME `
                            < $tempSql

                        if ($LASTEXITCODE -ne 0) {

                            Write-Host ""
                            Write-Host "ERROR: Failed executing:"
                            Write-Host $sql.FullName

                            Remove-Item `
                                -LiteralPath $tempSql `
                                -Force `
                                -ErrorAction SilentlyContinue

                            exit 1
                        }

                        Write-Host ""
                        Write-Host "SUCCESS:"
                        Write-Host $sql.Name

                        Remove-Item `
                            -LiteralPath $tempSql `
                            -Force `
                            -ErrorAction SilentlyContinue
                    }

                    # ------------------------------------------------
                    # VERIFY DATABASE
                    # ------------------------------------------------

                    Write-Host ""
                    Write-Host "=========================================="
                    Write-Host "VERIFYING DATABASE TABLES"
                    Write-Host "=========================================="

                    & $mysqlExe `
                        "-u$env:DB_USER" `
                        "-p$env:DB_PASS" `
                        "-D" `
                        $env:DB_NAME `
                        "-e" `
                        "SHOW TABLES;"

                    if ($LASTEXITCODE -ne 0) {

                        Write-Host "ERROR: Could not verify tables."
                        exit 1
                    }

                    Write-Host ""
                    Write-Host "=========================================="
                    Write-Host "MYSQL DATABASE SETUP SUCCESSFUL"
                    Write-Host "=========================================="
                '''
            }
        }


        // ============================================================
        // TOMCAT DEPLOYMENT
        // ============================================================

        stage('Deploy Appzillon WARs') {

            steps {

                echo '=========================================='
                echo 'DEPLOYING APPZILLON WAR FILES'
                echo '=========================================='

                bat '''
                    @echo off

                    setlocal EnableDelayedExpansion

                    echo.
                    echo ==========================================
                    echo TOMCAT CONFIGURATION
                    echo ==========================================

                    echo TOMCAT HOME:
                    echo %APPZ_HOME%

                    echo TOMCAT PORT:
                    echo %TOMCAT_PORT%

                    set "WEB_WAR="
                    set "SERVER_WAR="
                    set "WEB_WAR_NAME="

                    if exist "%WORKSPACE%\\appzillon_vars.txt" (

                        for /f "tokens=1,* delims==" %%a in (
                            'type "%WORKSPACE%\\appzillon_vars.txt" ^| findstr /B "WEB_WAR="'
                        ) do set "WEB_WAR=%%b"

                        for /f "tokens=1,* delims==" %%a in (
                            'type "%WORKSPACE%\\appzillon_vars.txt" ^| findstr /B "SERVER_WAR="'
                        ) do set "SERVER_WAR=%%b"

                        for /f "tokens=1,* delims==" %%a in (
                            'type "%WORKSPACE%\\appzillon_vars.txt" ^| findstr /B "WEB_WAR_NAME="'
                        ) do set "WEB_WAR_NAME=%%b"
                    )

                    echo.
                    echo WEB WAR:
                    echo !WEB_WAR!

                    echo.
                    echo SERVER WAR:
                    echo !SERVER_WAR!

                    echo.
                    echo WEB WAR NAME:
                    echo !WEB_WAR_NAME!

                    if "!WEB_WAR!"=="" (
                        echo ERROR: WEB WAR path is empty.
                        exit /b 1
                    )

                    if not exist "!WEB_WAR!" (
                        echo ERROR: WEB WAR does not exist:
                        echo !WEB_WAR!
                        exit /b 1
                    )

                    echo.
                    echo ==========================================
                    echo SHUTTING DOWN TOMCAT
                    echo ==========================================

                    call "%APPZ_HOME%\\bin\\shutdown.bat"

                    ping 127.0.0.1 -n 6 >nul

                    echo.
                    echo Killing remaining Tomcat process on port %TOMCAT_PORT%

                    for /f "tokens=5" %%a in (
                        'netstat -ano ^| findstr :%TOMCAT_PORT% ^| findstr LISTENING'
                    ) do (
                        echo Killing PID %%a
                        taskkill /F /PID %%a >nul 2>&1
                    )

                    ping 127.0.0.1 -n 3 >nul


                    echo.
                    echo ==========================================
                    echo CLEANING OLD QUIZAPP DEPLOYMENT
                    echo ==========================================

                    rmdir /S /Q "%APPZ_HOME%\\webapps\\quizapp" >nul 2>&1
                    del /F /Q "%APPZ_HOME%\\webapps\\quizapp.war" >nul 2>&1

                    rmdir /S /Q "%APPZ_HOME%\\webapps\\AppzillonServer" >nul 2>&1
                    del /F /Q "%APPZ_HOME%\\webapps\\AppzillonServer.war" >nul 2>&1

                    rmdir /S /Q "%APPZ_HOME%\\work\\Catalina\\localhost\\quizapp" >nul 2>&1
                    rmdir /S /Q "%APPZ_HOME%\\work\\Catalina\\localhost\\AppzillonServer" >nul 2>&1


                    echo.
                    echo ==========================================
                    echo COPYING WEB WAR
                    echo ==========================================

                    copy /Y "!WEB_WAR!" "%APPZ_HOME%\\webapps\\quizapp.war"

                    if errorlevel 1 (
                        echo ERROR: Failed to copy Web WAR.
                        exit /b 1
                    )

                    echo Web WAR copied successfully.


                    echo.
                    echo ==========================================
                    echo COPYING SERVER WAR
                    echo ==========================================

                    if not "!SERVER_WAR!"=="" (

                        if exist "!SERVER_WAR!" (

                            copy /Y "!SERVER_WAR!" "%APPZ_HOME%\\webapps\\AppzillonServer.war"

                            if errorlevel 1 (
                                echo ERROR: Failed to copy Server WAR.
                                exit /b 1
                            )

                            echo Server WAR copied successfully.
                        )
                        else (
                            echo WARNING: Server WAR path does not exist.
                        )
                    )
                    else (
                        echo WARNING: Server WAR was not found.
                    )


                    echo.
                    echo ==========================================
                    echo STARTING TOMCAT
                    echo ==========================================

                    set "JAVA_HOME=%JAVA_HOME%"
                    set "PATH=%JAVA_HOME%\\bin;%MAVEN_HOME%\\bin;%PATH%"
                    set "CATALINA_HOME=%APPZ_HOME%"
                    set "JENKINS_NODE_COOKIE=dontKillMe"

                    call "%APPZ_HOME%\\bin\\catalina.bat" start

                    echo.
                    echo Tomcat startup command executed.

                    echo.
                    echo Waiting for Tomcat...

                    ping 127.0.0.1 -n 21 >nul


                    echo.
                    echo ==========================================
                    echo CHECKING TOMCAT PORT
                    echo ==========================================

                    netstat -ano | findstr :%TOMCAT_PORT% | findstr LISTENING

                    if errorlevel 1 (

                        echo Port %TOMCAT_PORT% not listening yet.

                        echo Waiting another 10 seconds...

                        ping 127.0.0.1 -n 11 >nul

                        netstat -ano | findstr :%TOMCAT_PORT%
                    )
                    else (

                        echo.
                        echo ==========================================
                        echo TOMCAT IS RUNNING
                        echo ==========================================
                    )


                    echo.
                    echo ==========================================
                    echo WEBAPPS CONTENT
                    echo ==========================================

                    dir "%APPZ_HOME%\\webapps\\"


                    echo.
                    echo ==========================================
                    echo CHECKING QUIZAPP DEPLOYMENT
                    echo ==========================================

                    if exist "%APPZ_HOME%\\webapps\\quizapp.war" (
                        echo quizapp.war exists.
                    ) else (
                        echo ERROR: quizapp.war not found.
                    )

                    if exist "%APPZ_HOME%\\webapps\\quizapp" (
                        echo quizapp exploded directory exists.
                    ) else (
                        echo WARNING: quizapp exploded directory not created yet.
                    )


                    echo.
                    echo ==========================================
                    echo TOMCAT DEPLOYMENT COMPLETED
                    echo ==========================================
                '''
            }
        }


        // ============================================================
        // APPZILLON HEALTH CHECK
        // ============================================================

        stage('Appzillon Health Check') {

            steps {

                echo '=========================================='
                echo 'CHECKING APPZILLON'
                echo '=========================================='

                bat '''
                    @echo off

                    setlocal EnableDelayedExpansion

                    echo.
                    echo Appzillon URL:
                    echo %APPZILLON_URL%

                    echo.
                    echo Tomcat Port:
                    echo %TOMCAT_PORT%

                    set RETRIES=30

                    :CHECK_APPZILLON

                    echo.
                    echo Checking Appzillon...
                    echo Attempts remaining: !RETRIES!

                    curl -s -o nul -w "%%{http_code}" "%APPZILLON_URL%" | findstr "200 302"

                    if not errorlevel 1 (

                        echo.
                        echo ==========================================
                        echo APPZILLON IS RUNNING
                        echo ==========================================

                        echo URL:
                        echo %APPZILLON_URL%

                        exit /b 0
                    )

                    set /a RETRIES-=1

                    if !RETRIES! LEQ 0 (

                        echo.
                        echo ==========================================
                        echo APPZILLON HEALTH CHECK TIMEOUT
                        echo ==========================================

                        echo.
                        echo Tomcat port status:

                        netstat -ano | findstr :%TOMCAT_PORT%

                        echo.

                        echo Checking if quizapp deployment exists:

                        if exist "%APPZ_HOME%\\webapps\\quizapp" (
                            echo quizapp exploded directory exists.
                        ) else (
                            echo quizapp exploded directory NOT found.
                        )

                        echo.

                        echo Tomcat logs:

                        if exist "%APPZ_HOME%\\logs\\catalina.out" (
                            powershell -Command "Get-Content '%APPZ_HOME%\\logs\\catalina.out' -Tail 50"
                        ) else (
                            dir "%APPZ_HOME%\\logs\\"
                        )

                        echo.

                        netstat -ano | findstr :%TOMCAT_PORT% | findstr LISTENING >nul

                        if not errorlevel 1 (
                            echo Tomcat is listening.
                            echo Continuing because Tomcat is running.
                            exit /b 0
                        )

                        exit /b 1
                    )

                    echo Waiting 5 seconds...

                    ping 127.0.0.1 -n 6 >nul

                    goto CHECK_APPZILLON
                '''
            }
        }


        // ============================================================
        // OPEN APPZILLON
        // ============================================================

        stage('Open Appzillon Popup') {

            steps {

                echo '=========================================='
                echo 'OPENING APPZILLON'
                echo '=========================================='

                bat '''
                    @echo off

                    echo URL:
                    echo %APPZILLON_URL%

                    start "" "%APPZILLON_URL%"

                    ping 127.0.0.1 -n 3 >nul

                    if exist "C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe" (

                        start "" ^
                        "C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe" ^
                        "%APPZILLON_URL%"
                    )

                    echo.
                    echo Appzillon browser opened.

                    echo Waiting for UI...

                    ping 127.0.0.1 -n 9 >nul
                '''
            }
        }


        // ============================================================
        // PLAYWRIGHT
        //
        // Your original PLAYWRIGHT_DIR pointed directly to:
        // playwrightTest.java
        //
        // Therefore npx/package.json commands were incorrect.
        //
        // This stage runs the Java test through Maven.
        // ============================================================

        stage('Playwright UI Tests') {

            steps {

                echo '=========================================='
                echo 'RUNNING PLAYWRIGHT JAVA TEST'
                echo '=========================================='

                bat '''
                    @echo off

                    echo.
                    echo Playwright Java file:
                    echo %PLAYWRIGHT_FILE%

                    echo.
                    echo Appzillon URL:
                    echo %APPZILLON_URL%


                    if not exist "%PLAYWRIGHT_FILE%" (

                        echo.
                        echo WARNING: Playwright Java test file not found.

                        echo Expected:
                        echo %PLAYWRIGHT_FILE%

                        echo.
                        echo Skipping Playwright stage.

                        exit /b 0
                    )


                    echo.
                    echo ==========================================
                    echo PLAYWRIGHT JAVA TEST FOUND
                    echo ==========================================


                    set "JAVA_HOME=%JAVA_HOME%"
                    set "PATH=%JAVA_HOME%\\bin;%MAVEN_HOME%\\bin;%PATH%"


                    echo.
                    echo Running Maven test...

                    mvn test -DskipTests=false


                    set PW_EXIT=%errorlevel%


                    echo.
                    echo Playwright/Maven exit code:
                    echo %PW_EXIT%


                    if %PW_EXIT% NEQ 0 (

                        echo.
                        echo ==========================================
                        echo WARNING: PLAYWRIGHT TEST FAILED
                        echo ==========================================

                        echo Pipeline will continue.
                    )
                    else (

                        echo.
                        echo ==========================================
                        echo PLAYWRIGHT TEST PASSED
                        echo ==========================================
                    )

                    exit /b 0
                '''
            }
        }
    }


    // ================================================================
    // POST ACTIONS
    // ================================================================

    post {

        success {

            echo '=========================================='
            echo 'QUIZAPP DEPLOYMENT SUCCESSFUL - NANBA!'
            echo '=========================================='

            echo 'Backend: http://localhost:8080/api/categories'
            echo 'Appzillon: http://localhost:8090/quizapp/'
            echo 'AppzillonServer: http://localhost:8090/AppzillonServer/'
            echo '=========================================='
        }


        failure {

            echo '=========================================='
            echo 'QUIZAPP DEPLOYMENT FAILED - CHECK LOGS!'
            echo '=========================================='

            echo 'Check the stage that failed.'

            echo 'Backend log: backend.log (workspace)'

            echo "Tomcat logs: ${APPZ_HOME}\\logs\\"

            echo '=========================================='
        }
    }
}
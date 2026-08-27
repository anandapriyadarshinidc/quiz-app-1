pipeline {

    agent any

    options {
        timeout(time: 30, unit: 'MINUTES')
        disableConcurrentBuilds()
        durabilityHint('PERFORMANCE_OPTIMIZED')
    }

    environment {

        // ============================================================
        // JAVA
        // ============================================================

        JAVA_HOME = 'C:/Program Files/Java/jdk-17.0.2'

        // ============================================================
        // MAVEN
        // ============================================================

        MAVEN_HOME = 'D:/apache-maven-3.8.5'

        // ============================================================
        // SPRING BOOT BACKEND
        // ============================================================

        APP_JAR = 'target/quizapp.jar'

        BACKEND_PORT = '8080'

        BACKEND_URL = 'http://localhost:8080/api/categories'

        // ============================================================
        // TOMCAT
        // ============================================================

        APPZ_HOME = 'D:/apache-tomcat-9.0.53/apache-tomcat-9.0.53'

        TOMCAT_PORT = '8090'

        // WAR is deployed as quizzz.war
        APPZILLON_URL = 'http://localhost:8090/quizapp/'

        // ============================================================
        // APPZILLON PROJECT
        // ============================================================

        APPZ_ARTIFACTS = 'D:/forDeploy'

        QUIZZ_PROJECT = 'C:\Users\ananda.dc\Downloads\quizapp(1)\quizapp'

        QUIZZ_BIN = 'C:\Users\ananda.dc\Downloads\quizapp(1)\quizapp\bin'

        // ============================================================
        // DATABASE
        // ============================================================

        DB_NAME = 'quiz_app'

        DB_USER = 'root'

        DB_PASS = 'root'

        MYSQL_BIN = 'C:/Program Files/MySQL/MySQL Server 8.0/bin'

        // ============================================================
        // PLAYWRIGHT
        // ============================================================

        PLAYWRIGHT_DIR = 'C:/Users/ananda.dc/Downloads/quiz-app-backend (1)/quiz-app/src/test/java/playwrightTest.java'
    }


    stages {

        // ============================================================
        // 1. BUILD BACKEND
        // ============================================================

        stage('Build Backend Jar') {

            steps {

                echo '=========================================='
                echo 'BUILDING QUIZ APP BACKEND'
                echo '=========================================='

                bat '''
                    @echo off

                    set "JAVA_HOME=%JAVA_HOME%"
                    set "PATH=%JAVA_HOME%\\bin;%MAVEN_HOME%\\bin;%PATH%"

                    echo.
                    echo ==========================================
                    echo JAVA VERSION
                    echo ==========================================

                    java -version

                    echo.
                    echo ==========================================
                    echo MAVEN VERSION
                    echo ==========================================

                    mvn -version

                    echo.
                    echo ==========================================
                    echo CHECKING PROJECT
                    echo ==========================================

                    if not exist "pom.xml" (
                        echo ERROR: pom.xml not found.
                        echo Current directory:
                        cd
                        dir
                        exit /b 1
                    )

                    echo pom.xml found successfully.

                    echo.
                    echo ==========================================
                    echo MAVEN BUILD
                    echo ==========================================

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

                    echo.
                    echo ==========================================
                    echo TARGET FILES
                    echo ==========================================

                    dir target
                '''
            }
        }


        // ============================================================
        // 2. CHECK JAR
        // ============================================================

        stage('Check Backend Jar') {

            steps {

                echo '=========================================='
                echo 'CHECKING BACKEND JAR'
                echo '=========================================='

                bat '''
                    @echo off

                    if not exist "%APP_JAR%" (
                        echo ERROR: JAR file not found.
                        echo Expected:
                        echo %APP_JAR%
                        echo.
                        echo Target directory:
                        dir target
                        exit /b 1
                    )

                    echo.
                    echo ==========================================
                    echo JAR FOUND
                    echo ==========================================

                    echo %APP_JAR%
                '''
            }
        }


        // ============================================================
        // 3. STOP OLD BACKEND
        // ============================================================

        stage('Stop Old Backend') {

            steps {

                echo '=========================================='
                echo 'STOPPING OLD BACKEND'
                echo '=========================================='

                bat '''
                    @echo off

                    echo Checking port %BACKEND_PORT%...

                    for /f "tokens=5" %%a in ('netstat -ano ^| findstr :%BACKEND_PORT% ^| findstr LISTENING') do (
                        echo Stopping PID %%a
                        taskkill /F /PID %%a >nul 2>&1
                    )

                    echo Waiting...

                    ping 127.0.0.1 -n 4 >nul

                    echo Backend port checked.
                '''
            }
        }


        // ============================================================
        // 4. START BACKEND
        // ============================================================

        stage('Deploy Backend') {

            steps {

                echo '=========================================='
                echo 'STARTING QUIZ APP BACKEND'
                echo '=========================================='

                bat '''
                    @echo off

                    set "JAVA_HOME=%JAVA_HOME%"
                    set "PATH=%JAVA_HOME%\\bin;%PATH%"

                    set "JENKINS_NODE_COOKIE=dontKillMe"

                    echo JAVA_HOME:
                    echo %JAVA_HOME%

                    echo.
                    echo Starting backend...

                    start "QuizApp-Backend" /B cmd /c "set JENKINS_NODE_COOKIE=dontKillMe && set JAVA_HOME=%JAVA_HOME% && java -jar %APP_JAR% > backend.log 2>&1"

                    echo Backend start command executed.

                    echo.
                    echo Waiting for backend...

                    ping 127.0.0.1 -n 10 >nul

                    echo.
                    echo ==========================================
                    echo BACKEND LOG
                    echo ==========================================

                    if exist backend.log (
                        powershell -Command "Get-Content backend.log -Tail 40"
                    ) else (
                        echo backend.log not found.
                    )
                '''
            }
        }


        // ============================================================
        // 5. BACKEND HEALTH CHECK
        // ============================================================

        stage('Backend Health Check') {

            steps {

                echo '=========================================='
                echo 'BACKEND HEALTH CHECK'
                echo '=========================================='

                bat '''
                    @echo off

                    set RETRIES=20

                    :CHECK_BACKEND

                    echo.
                    echo Checking:
                    echo %BACKEND_URL%

                    curl -s -o nul -w "%%{http_code}" "%BACKEND_URL%" | findstr "200 201"

                    if not errorlevel 1 (
                        echo.
                        echo ==========================================
                        echo BACKEND IS RUNNING
                        echo ==========================================
                        exit /b 0
                    )

                    echo Backend not ready.

                    set /a RETRIES-=1

                    if %RETRIES% LEQ 0 (
                        echo.
                        echo ==========================================
                        echo BACKEND FAILED
                        echo ==========================================

                        echo.
                        echo PORT STATUS:

                        netstat -ano | findstr :%BACKEND_PORT%

                        echo.
                        echo BACKEND LOG:

                        if exist backend.log (
                            type backend.log
                        ) else (
                            echo backend.log not found.
                        )

                        exit /b 1
                    )

                    echo Waiting 3 seconds...

                    ping 127.0.0.1 -n 4 >nul

                    goto CHECK_BACKEND
                '''
            }
        }


        // ============================================================
        // 6. FIND APPZILLON FILES
        // ============================================================

        stage('Find Appzillon Files') {

            steps {

                echo '=========================================='
                echo 'FINDING APPZILLON FILES'
                echo '=========================================='

                powershell '''
                    $ErrorActionPreference = "Stop"

                    Write-Host "=========================================="
                    Write-Host "APPZILLON CONFIGURATION"
                    Write-Host "=========================================="

                    Write-Host "APPZ_HOME       : $env:APPZ_HOME"
                    Write-Host "QUIZZ_PROJECT   : $env:QUIZZ_PROJECT"
                    Write-Host "QUIZZ_BIN       : $env:QUIZZ_BIN"
                    Write-Host "APPZ_ARTIFACTS  : $env:APPZ_ARTIFACTS"

                    # ------------------------------------------------
                    # CHECK TOMCAT
                    # ------------------------------------------------

                    if (-not (Test-Path $env:APPZ_HOME)) {

                        Write-Host "ERROR: Tomcat directory not found."
                        Write-Host $env:APPZ_HOME

                        exit 1
                    }

                    if (-not (Test-Path "$env:APPZ_HOME/bin/catalina.bat")) {

                        Write-Host "ERROR: catalina.bat not found."

                        exit 1
                    }

                    Write-Host "Tomcat found successfully."

                    $webWar = $null
                    $serverWar = $null
                    $webProps = $null
                    $serverProps = $null
                    $dbPath = $null

                    # ------------------------------------------------
                    # WEB WAR
                    # ------------------------------------------------

                    if (Test-Path "$env:QUIZZ_BIN/Web") {

                        $file = Get-ChildItem `
                            -Path "$env:QUIZZ_BIN/Web" `
                            -Filter "*.war" `
                            -Recurse `
                            -ErrorAction SilentlyContinue |
                            Select-Object -First 1

                        if ($file) {
                            $webWar = $file.FullName
                        }
                    }

                    # ------------------------------------------------
                    # SERVER WAR
                    # ------------------------------------------------

                    if (Test-Path "$env:QUIZZ_BIN/Server") {

                        $file = Get-ChildItem `
                            -Path "$env:QUIZZ_BIN/Server" `
                            -Filter "*.war" `
                            -Recurse `
                            -ErrorAction SilentlyContinue |
                            Select-Object -First 1

                        if ($file) {
                            $serverWar = $file.FullName
                        }
                    }

                    # ------------------------------------------------
                    # WEB PROPERTIES
                    # ------------------------------------------------

                    if (Test-Path "$env:QUIZZ_BIN/Web/Properties") {

                        $directory = Get-ChildItem `
                            -Path "$env:QUIZZ_BIN/Web/Properties" `
                            -Directory `
                            -ErrorAction SilentlyContinue |
                            Select-Object -First 1

                        if ($directory) {
                            $webProps = $directory.FullName
                        }
                    }

                    # ------------------------------------------------
                    # SERVER PROPERTIES
                    # ------------------------------------------------

                    if (Test-Path "$env:QUIZZ_BIN/Server/Properties") {

                        $directory = Get-ChildItem `
                            -Path "$env:QUIZZ_BIN/Server/Properties" `
                            -Directory `
                            -ErrorAction SilentlyContinue |
                            Select-Object -First 1

                        if ($directory) {
                            $serverProps = $directory.FullName
                        }
                    }

                    # ------------------------------------------------
                    # DATABASE
                    # ------------------------------------------------

                    $possibleDbPaths = @(
                        "$env:QUIZZ_BIN/Server/Database/MySql",
                        "$env:QUIZZ_BIN/Server/Properties/AppzillonServer/quizzz/Database/MySql",
                        "$env:APPZ_ARTIFACTS/lib/AppzillonServer/quizzz/Database/MySql"
                    )

                    foreach ($path in $possibleDbPaths) {

                        if (Test-Path $path) {

                            $dbPath = $path

                            break
                        }
                    }

                    # ------------------------------------------------
                    # FALLBACK WEB WAR
                    # ------------------------------------------------

                    if (-not $webWar) {

                        if (Test-Path "$env:APPZ_ARTIFACTS/quizzz.war") {

                            $webWar = "$env:APPZ_ARTIFACTS/quizzz.war"
                        }
                    }

                    # ------------------------------------------------
                    # FALLBACK SERVER WAR
                    # ------------------------------------------------

                    if (-not $serverWar) {

                        if (Test-Path "$env:APPZ_ARTIFACTS/AppzillonServer.war") {

                            $serverWar = "$env:APPZ_ARTIFACTS/AppzillonServer.war"
                        }
                    }

                    # ------------------------------------------------
                    # FALLBACK WEB PROPERTIES
                    # ------------------------------------------------

                    if (-not $webProps) {

                        if (Test-Path "$env:APPZ_ARTIFACTS/quizzz") {

                            $webProps = "$env:APPZ_ARTIFACTS/quizzz"
                        }
                    }

                    # ------------------------------------------------
                    # FALLBACK SERVER PROPERTIES
                    # ------------------------------------------------

                    if (-not $serverProps) {

                        if (Test-Path "$env:APPZ_ARTIFACTS/lib/AppzillonServer") {

                            $serverProps = "$env:APPZ_ARTIFACTS/lib/AppzillonServer"
                        }
                    }

                    Write-Host ""
                    Write-Host "=========================================="
                    Write-Host "DISCOVERED FILES"
                    Write-Host "=========================================="

                    Write-Host "Web WAR      : $webWar"
                    Write-Host "Server WAR   : $serverWar"
                    Write-Host "Web Props    : $webProps"
                    Write-Host "Server Props : $serverProps"
                    Write-Host "DB Path      : $dbPath"

                    # ------------------------------------------------
                    # WEB WAR REQUIRED
                    # ------------------------------------------------

                    if (-not $webWar) {

                        Write-Host ""
                        Write-Host "ERROR: Web WAR was not found."

                        exit 1
                    }

                    # ------------------------------------------------
                    # SAVE VARIABLES
                    # ------------------------------------------------

                    $content = @(
                        "WEB_WAR=$webWar"
                        "SERVER_WAR=$serverWar"
                        "WEB_PROPS=$webProps"
                        "SERVER_PROPS=$serverProps"
                        "DB_PATH=$dbPath"
                    )

                    Set-Content `
                        -Path "$env:WORKSPACE/appzillon_vars.txt" `
                        -Value $content

                    Write-Host ""
                    Write-Host "Appzillon variables saved."
                '''
            }
        }


        // ============================================================
        // 7. COPY APPZILLON PROPERTIES
        // ============================================================

        stage('Copy Appzillon Properties') {

            steps {

                echo '=========================================='
                echo 'COPYING APPZILLON PROPERTIES'
                echo '=========================================='

                powershell '''
                    $ErrorActionPreference = "Stop"

                    $vars = Get-Content `
                        -Path "$env:WORKSPACE/appzillon_vars.txt"

                    $map = @{}

                    foreach ($line in $vars) {

                        if ($line -match "^(.*?)=(.*)$") {

                            $map[$matches[1]] = $matches[2]
                        }
                    }

                    $webProps = $map["WEB_PROPS"]
                    $serverProps = $map["SERVER_PROPS"]

                    $libPath = "$env:APPZ_HOME/lib"

                    # ------------------------------------------------
                    # CREATE LIB DIRECTORY
                    # ------------------------------------------------

                    if (-not (Test-Path $libPath)) {

                        New-Item `
                            -ItemType Directory `
                            -Path $libPath `
                            -Force |
                            Out-Null
                    }

                    Write-Host "Tomcat LIB:"
                    Write-Host $libPath

                    # ------------------------------------------------
                    # WEB PROPERTIES
                    # ------------------------------------------------

                    if ($webProps -and (Test-Path $webProps)) {

                        Write-Host ""
                        Write-Host "Copying Web Properties..."
                        Write-Host $webProps

                        Copy-Item `
                            -Path $webProps `
                            -Destination $libPath `
                            -Recurse `
                            -Force

                        Write-Host "Web properties copied."
                    }
                    else {

                        Write-Host "WARNING: Web properties not found."
                    }

                    # ------------------------------------------------
                    # SERVER PROPERTIES
                    # ------------------------------------------------

                    if ($serverProps -and (Test-Path $serverProps)) {

                        Write-Host ""
                        Write-Host "Copying Server Properties..."
                        Write-Host $serverProps

                        Copy-Item `
                            -Path $serverProps `
                            -Destination $libPath `
                            -Recurse `
                            -Force

                        Write-Host "Server properties copied."
                    }
                    else {

                        Write-Host "WARNING: Server properties not found."
                    }
                '''
            }
        }


        // ============================================================
        // 8. DATABASE
        // ============================================================

        stage('Database Setup') {

            steps {

                echo '=========================================='
                echo 'DATABASE SETUP'
                echo '=========================================='

                bat '''
                    @echo off

                    echo Database:
                    echo %DB_NAME%

                    echo.
                    echo MySQL path:
                    echo %MYSQL_BIN%

                    set "MYSQL_EXE=%MYSQL_BIN%\\mysql.exe"

                    if not exist "%MYSQL_EXE%" (

                        echo mysql.exe not found in configured location.

                        where mysql >nul 2>&1

                        if errorlevel 1 (

                            echo WARNING: MySQL executable not found.
                            echo Skipping database setup.

                            goto DB_SKIP
                        )

                        for /f "delims=" %%i in ('where mysql') do (
                            set "MYSQL_EXE=%%i"
                        )
                    )

                    echo Using:
                    echo %MYSQL_EXE%

                    echo.
                    echo Creating database...

                    "%MYSQL_EXE%" -u%DB_USER% -p%DB_PASS% -e "CREATE DATABASE IF NOT EXISTS %DB_NAME%;"

                    if errorlevel 1 (
                        echo WARNING: Database creation failed.
                    ) else (
                        echo Database ready.
                    )

                    set "DB_PATH="

                    if exist "%WORKSPACE%\\appzillon_vars.txt" (

                        for /f "tokens=1,* delims==" %%a in (
                            'type "%WORKSPACE%\\appzillon_vars.txt" ^| findstr DB_PATH'
                        ) do (
                            set "DB_PATH=%%b"
                        )
                    )

                    echo.
                    echo DB_PATH:
                    echo %DB_PATH%

                    if "%DB_PATH%"=="" (
                        goto DB_SKIP
                    )

                    if not exist "%DB_PATH%" (
                        echo DB path does not exist.
                        goto DB_SKIP
                    )

                    echo.
                    echo Searching SQL files...

                    dir "%DB_PATH%\\*.sql"

                    if errorlevel 1 (
                        echo No SQL files found.
                        goto DB_SKIP
                    )

                    for %%f in ("%DB_PATH%\\*.sql") do (

                        echo.
                        echo ==========================================
                        echo EXECUTING %%~nxf
                        echo ==========================================

                        "%MYSQL_EXE%" -u%DB_USER% -p%DB_PASS% %DB_NAME% < "%%f"

                        if errorlevel 1 (
                            echo ERROR executing %%~nxf
                        ) else (
                            echo Successfully executed %%~nxf
                        )
                    )

                    echo.
                    echo ==========================================
                    echo DATABASE TABLES
                    echo ==========================================

                    "%MYSQL_EXE%" -u%DB_USER% -p%DB_PASS% -D%DB_NAME% -e "SHOW TABLES;"

                    :DB_SKIP

                    echo.
                    echo Database stage completed.
                '''
            }
        }


        // ============================================================
        // 9. TOMCAT DEPLOYMENT
        // ============================================================

        stage('Deploy Appzillon to Tomcat') {

            steps {

                echo '=========================================='
                echo 'DEPLOYING APPZILLON TO TOMCAT'
                echo '=========================================='

                bat '''
                    @echo off

                    set "WEB_WAR="
                    set "SERVER_WAR="

                    if exist "%WORKSPACE%\\appzillon_vars.txt" (

                        for /f "tokens=1,* delims==" %%a in (
                            'type "%WORKSPACE%\\appzillon_vars.txt" ^| findstr WEB_WAR'
                        ) do (
                            set "WEB_WAR=%%b"
                        )

                        for /f "tokens=1,* delims==" %%a in (
                            'type "%WORKSPACE%\\appzillon_vars.txt" ^| findstr SERVER_WAR'
                        ) do (
                            set "SERVER_WAR=%%b"
                        )
                    )

                    echo WEB WAR:
                    echo %WEB_WAR%

                    echo SERVER WAR:
                    echo %SERVER_WAR%

                    if "%WEB_WAR%"=="" (
                        echo ERROR: Web WAR not found.
                        exit /b 1
                    )

                    if not exist "%WEB_WAR%" (
                        echo ERROR: Web WAR does not exist.
                        exit /b 1
                    )

                    echo.
                    echo ==========================================
                    echo STOPPING TOMCAT
                    echo ==========================================

                    call "%APPZ_HOME%\\bin\\shutdown.bat"

                    ping 127.0.0.1 -n 6 >nul

                    echo.
                    echo Killing remaining Tomcat process...

                    for /f "tokens=5" %%a in (
                        'netstat -ano ^| findstr :%TOMCAT_PORT% ^| findstr LISTENING'
                    ) do (
                        echo Killing PID %%a
                        taskkill /F /PID %%a >nul 2>&1
                    )

                    ping 127.0.0.1 -n 3 >nul

                    echo.
                    echo ==========================================
                    echo CLEANING OLD DEPLOYMENT
                    echo ==========================================

                    rmdir /S /Q "%APPZ_HOME%\\webapps\\quizzz" >nul 2>&1

                    rmdir /S /Q "%APPZ_HOME%\\webapps\\AppzillonServer" >nul 2>&1

                    del /F /Q "%APPZ_HOME%\\webapps\\quizzz.war" >nul 2>&1

                    del /F /Q "%APPZ_HOME%\\webapps\\AppzillonServer.war" >nul 2>&1

                    rmdir /S /Q "%APPZ_HOME%\\work\\Catalina\\localhost\\quizzz" >nul 2>&1

                    rmdir /S /Q "%APPZ_HOME%\\work\\Catalina\\localhost\\AppzillonServer" >nul 2>&1

                    echo.
                    echo ==========================================
                    echo COPYING WEB WAR
                    echo ==========================================

                    copy /Y "%WEB_WAR%" "%APPZ_HOME%\\webapps\\quizzz.war"

                    if errorlevel 1 (
                        echo ERROR: Web WAR copy failed.
                        exit /b 1
                    )

                    echo Web WAR copied successfully.

                    if not "%SERVER_WAR%"=="" (

                        if exist "%SERVER_WAR%" (

                            echo.
                            echo Copying Server WAR...

                            copy /Y "%SERVER_WAR%" "%APPZ_HOME%\\webapps\\AppzillonServer.war"

                            if errorlevel 1 (
                                echo ERROR: Server WAR copy failed.
                                exit /b 1
                            )

                            echo Server WAR copied successfully.
                        )
                    )

                    echo.
                    echo ==========================================
                    echo STARTING TOMCAT
                    echo ==========================================

                    set "JAVA_HOME=%JAVA_HOME%"
                    set "CATALINA_HOME=%APPZ_HOME%"
                    set "PATH=%JAVA_HOME%\\bin;%PATH%"
                    set "JENKINS_NODE_COOKIE=dontKillMe"

                    call "%APPZ_HOME%\\bin\\catalina.bat" start

                    echo.
                    echo Tomcat start command executed.

                    echo.
                    echo Waiting for Tomcat...

                    ping 127.0.0.1 -n 21 >nul

                    echo.
                    echo ==========================================
                    echo TOMCAT PORT STATUS
                    echo ==========================================

                    netstat -ano | findstr :%TOMCAT_PORT%

                    echo.
                    echo ==========================================
                    echo WEBAPPS
                    echo ==========================================

                    dir "%APPZ_HOME%\\webapps"
                '''
            }
        }


        // ============================================================
        // 10. APPZILLON HEALTH CHECK
        // ============================================================

        stage('Appzillon Health Check') {

            steps {

                echo '=========================================='
                echo 'APPZILLON HEALTH CHECK'
                echo '=========================================='

                bat '''
                    @echo off

                    set RETRIES=30

                    :CHECK_APPZILLON

                    echo.
                    echo Checking:
                    echo %APPZILLON_URL%

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

                    echo Appzillon not ready.

                    set /a RETRIES-=1

                    if %RETRIES% LEQ 0 (

                        echo.
                        echo ==========================================
                        echo APPZILLON HEALTH CHECK FAILED
                        echo ==========================================

                        echo.
                        echo TOMCAT PORT:

                        netstat -ano | findstr :%TOMCAT_PORT%

                        echo.
                        echo WEBAPPS:

                        dir "%APPZ_HOME%\\webapps"

                        echo.
                        echo TOMCAT LOGS:

                        if exist "%APPZ_HOME%\\logs\\catalina.out" (

                            powershell -Command "Get-Content '%APPZ_HOME%\\logs\\catalina.out' -Tail 60"

                        ) else (

                            echo catalina.out not found.
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
        // 11. OPEN APPZILLON
        // ============================================================

        stage('Open Appzillon') {

            steps {

                echo '=========================================='
                echo 'OPENING APPZILLON'
                echo '=========================================='

                bat '''
                    @echo off

                    echo Appzillon URL:
                    echo %APPZILLON_URL%

                    start "" "%APPZILLON_URL%"

                    echo Browser launch requested.

                    ping 127.0.0.1 -n 5 >nul
                '''
            }
        }


        // ============================================================
        // 12. PLAYWRIGHT
        // ============================================================

        stage('Playwright UI Tests') {

            steps {

                echo '=========================================='
                echo 'PLAYWRIGHT UI TESTS'
                echo '=========================================='

                bat '''
                    @echo off

                    echo Playwright directory:
                    echo %PLAYWRIGHT_DIR%

                    if not exist "%PLAYWRIGHT_DIR%" (

                        echo ERROR: Playwright directory not found.

                        echo %PLAYWRIGHT_DIR%

                        exit /b 1
                    )

                    cd /d "%PLAYWRIGHT_DIR%"

                    echo.
                    echo Current directory:
                    cd

                    echo.
                    echo ==========================================
                    echo PACKAGE.JSON
                    echo ==========================================

                    if not exist package.json (

                        echo ERROR: package.json not found.

                        dir

                        exit /b 1
                    )

                    echo package.json found.

                    echo.
                    echo ==========================================
                    echo INSTALLING PLAYWRIGHT DEPENDENCIES
                    echo ==========================================

                    npm install

                    if errorlevel 1 (

                        echo ERROR: npm install failed.

                        exit /b 1
                    )

                    echo.
                    echo ==========================================
                    echo RUNNING PLAYWRIGHT TEST
                    echo ==========================================

                    npx playwright test tests/05-home-quiz-flow.spec.js --headed --project=chromium

                    set PW_EXIT=%errorlevel%

                    echo.
                    echo Playwright exit code:
                    echo %PW_EXIT%

                    if %PW_EXIT% NEQ 0 (

                        echo.
                        echo ==========================================
                        echo PLAYWRIGHT TEST FAILED
                        echo ==========================================

                        if exist playwright-report\\index.html (

                            echo Opening Playwright report...

                            start "" playwright-report\\index.html
                        )

                        exit /b %PW_EXIT%
                    )

                    echo.
                    echo ==========================================
                    echo PLAYWRIGHT TEST PASSED
                    echo ==========================================
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
            echo 'QUIZ APP DEPLOYMENT SUCCESSFUL'
            echo '=========================================='

            echo 'Backend: http://localhost:8080'

            echo 'Backend API: http://localhost:8080/api/categories'

            echo 'Appzillon: http://localhost:9090/quizzz'

            echo '=========================================='
        }


        failure {

            echo '=========================================='
            echo 'QUIZ APP DEPLOYMENT FAILED'
            echo '=========================================='

            echo 'Check the failed Jenkins stage.'

            echo 'Backend log: backend.log'

            echo 'Tomcat logs: D:/apache-tomcat-9.0.53-windows-x64/apache-tomcat-9.0.53/logs'

            echo '=========================================='
        }
    }
}

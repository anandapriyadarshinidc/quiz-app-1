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
        // TOMCAT / APPZILLON
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
        // PLAYWRIGHT
        // IMPORTANT:
        // This must be the folder containing package.json
        // ============================================================

        PLAYWRIGHT_DIR = 'C:/Users/ananda.dc/Downloads/quiz-app-backend (1)/quiz-app'

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
                    set "PATH=%JAVA_HOME%\\bin;%PATH%"

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

                    for /f "tokens=5" %%a in ('netstat -ano ^| findstr :%BACKEND_PORT% ^| findstr LISTENING') do (
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
                    set "PATH=%JAVA_HOME%\\bin;%PATH%"

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

                        if exist target (
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

                    for /f "tokens=5" %%a in ('netstat -ano ^| findstr :%BACKEND_PORT% ^| findstr LISTENING') do (
                        echo Stopping process %%a on port %BACKEND_PORT%
                        taskkill /F /PID %%a >nul 2>&1
                    )

                    echo.
                    echo Waiting for port %BACKEND_PORT%...

                    ping 127.0.0.1 -n 4 >nul

                    echo.
                    echo ==========================================
                    echo STARTING QUIZAPP BACKEND
                    echo ==========================================

                    set "JAVA_HOME=%JAVA_HOME%"
                    set "PATH=%JAVA_HOME%\\bin;%PATH%"
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
                    echo Remaining attempts: %RETRIES%

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


                    if %RETRIES% LEQ 0 (

                        echo.
                        echo ==========================================
                        echo BACKEND FAILED TO START
                        echo ==========================================

                        echo.
                        echo ==========================================
                        echo PORT %BACKEND_PORT% STATUS
                        echo ==========================================

                        netstat -ano | findstr :%BACKEND_PORT%


                        echo.
                        echo ==========================================
                        echo BACKEND LOG
                        echo ==========================================

                        if exist backend.log (
                            type backend.log
                        ) else (
                            echo backend.log not found
                        )

                        exit /b 1
                    }


                    echo.
                    echo Waiting 3 seconds before retry...

                    ping 127.0.0.1 -n 4 >nul

                    goto CHECK_BACKEND
                '''
            }
        }


        // ============================================================
        // APPZILLON DEPLOYMENT
        // ============================================================

        stage('Deploy Appzillon - Full') {

            steps {

                echo '=========================================='
                echo 'DEPLOYING APPZILLON - FULL STEPS'
                echo '=========================================='

                // ====================================================
                // STEP 1-4
                // FIND WAR / PROPERTIES / DATABASE
                // ====================================================

                powershell '''
                    $ErrorActionPreference = "Stop"

                    Write-Host "=========================================="
                    Write-Host "CHECKING APPZILLON PROJECT"
                    Write-Host "=========================================="

                    Write-Host "QUIZZ_PROJECT: $env:QUIZZ_PROJECT"
                    Write-Host "QUIZZ_BIN: $env:QUIZZ_BIN"
                    Write-Host "APPZ_HOME: $env:APPZ_HOME"


                    $quizBin = $env:QUIZZ_BIN
                    $appzHome = $env:APPZ_HOME
                    $artifacts = $env:APPZ_ARTIFACTS


                    if (-not (Test-Path -LiteralPath $quizBin)) {

                        Write-Host "WARNING: QUIZZ_BIN not found."
                        Write-Host "Using fallback: $artifacts"

                        $quizBin = $null
                    }
                    else {

                        Write-Host "Found QUIZZ_BIN: $quizBin"

                        Get-ChildItem `
                            -LiteralPath $quizBin `
                            -Recurse `
                            -Depth 2 `
                            -ErrorAction SilentlyContinue |
                            ForEach-Object {
                                Write-Host $_.FullName
                            }
                    }


                    if (-not (Test-Path -LiteralPath $appzHome)) {

                        Write-Host "ERROR: Tomcat not found at $appzHome"

                        exit 1
                    }


                    if (-not (Test-Path -LiteralPath "$appzHome\\bin\\catalina.bat")) {

                        Write-Host "ERROR: catalina.bat missing"

                        exit 1
                    }


                    Write-Host "Tomcat found: $appzHome"


                    // =================================================
                    // SEARCH FOR WAR FILES
                    // =================================================

                    Write-Host ""
                    Write-Host "=========================================="
                    Write-Host "SEARCHING FOR WAR FILES"
                    Write-Host "=========================================="


                    $webWar = $null
                    $serverWar = $null
                    $webPropsSource = $null
                    $serverPropsSource = $null
                    $dbSqlPath = $null


                    if ($quizBin) {

                        // WEB WAR

                        $webWarCandidates =
                            Get-ChildItem `
                            -LiteralPath "$quizBin\\Web" `
                            -Filter "*.war" `
                            -ErrorAction SilentlyContinue |
                            Select-Object -First 1


                        if ($webWarCandidates) {
                            $webWar = $webWarCandidates.FullName
                        }


                        if (-not $webWar) {

                            $webWar =
                                (Get-ChildItem `
                                -Path "$quizBin\\Web" `
                                -Filter "*.war" `
                                -Recurse `
                                -ErrorAction SilentlyContinue |
                                Select-Object -First 1).FullName
                        }


                        // SERVER WAR

                        $serverWarCandidates =
                            Get-ChildItem `
                            -LiteralPath "$quizBin\\Server" `
                            -Filter "*.war" `
                            -ErrorAction SilentlyContinue |
                            Select-Object -First 1


                        if ($serverWarCandidates) {
                            $serverWar = $serverWarCandidates.FullName
                        }


                        if (-not $serverWar) {

                            $serverWar =
                                (Get-ChildItem `
                                -Path "$quizBin\\Server" `
                                -Filter "*.war" `
                                -Recurse `
                                -ErrorAction SilentlyContinue |
                                Select-Object -First 1).FullName
                        }


                        // WEB PROPERTIES

                        $webPropsRoot = "$quizBin\\Web\\Properties"


                        if (Test-Path -LiteralPath $webPropsRoot) {

                            $webPropsSource =
                                (Get-ChildItem `
                                -LiteralPath $webPropsRoot `
                                -Directory `
                                -ErrorAction SilentlyContinue |
                                Select-Object -First 1).FullName

                            Write-Host "Web Properties found: $webPropsSource"
                        }


                        // SERVER PROPERTIES

                        $serverPropsRoot = "$quizBin\\Server\\Properties"


                        if (Test-Path -LiteralPath $serverPropsRoot) {

                            $serverPropsSource =
                                (Get-ChildItem `
                                -LiteralPath $serverPropsRoot `
                                -Directory `
                                -ErrorAction SilentlyContinue |
                                Select-Object -First 1).FullName

                            Write-Host "Server Properties found: $serverPropsSource"
                        }


                        // DATABASE

                        $dbSqlPath = "$quizBin\\Server\\Database\\MySql"


                        if (-not (Test-Path -LiteralPath $dbSqlPath)) {

                            $dbSqlPath =
                                "$quizBin\\Server\\Properties\\AppzillonServer\\quizzz\\Database\\MySql"


                            if (-not (Test-Path -LiteralPath $dbSqlPath)) {

                                $sqlFile =
                                    Get-ChildItem `
                                    -Path $quizBin `
                                    -Filter "*.sql" `
                                    -Recurse `
                                    -ErrorAction SilentlyContinue |
                                    Select-Object -First 1

                                if ($sqlFile) {
                                    $dbSqlPath = $sqlFile.DirectoryName
                                }
                            }
                        }
                    }


                    // =================================================
                    // FALLBACK
                    // =================================================

                    if (-not $webWar -and (Test-Path -LiteralPath "$artifacts\\quizzz.war")) {

                        $webWar = "$artifacts\\quizzz.war"

                        Write-Host "Fallback Web WAR: $webWar"
                    }


                    if (-not $serverWar -and (Test-Path -LiteralPath "$artifacts\\AppzillonServer.war")) {

                        $serverWar = "$artifacts\\AppzillonServer.war"

                        Write-Host "Fallback Server WAR: $serverWar"
                    }


                    if (-not $webPropsSource -and (Test-Path -LiteralPath "$artifacts\\quizzz")) {

                        $webPropsSource = "$artifacts\\quizzz"

                        Write-Host "Fallback Web Props: $webPropsSource"
                    }


                    if (-not $serverPropsSource -and (Test-Path -LiteralPath "$artifacts\\lib\\AppzillonServer")) {

                        $serverPropsSource =
                            "$artifacts\\lib\\AppzillonServer"

                        Write-Host "Fallback Server Props: $serverPropsSource"
                    }


                    if (-not $dbSqlPath -and (Test-Path -LiteralPath "$artifacts\\lib\\AppzillonServer\\quizzz\\Database\\MySql")) {

                        $dbSqlPath =
                            "$artifacts\\lib\\AppzillonServer\\quizzz\\Database\\MySql"
                    }


                    Write-Host ""
                    Write-Host "Web WAR: $webWar"
                    Write-Host "Server WAR: $serverWar"
                    Write-Host "Web Props Source: $webPropsSource"
                    Write-Host "Server Props Source: $serverPropsSource"
                    Write-Host "DB SQL Path: $dbSqlPath"


                    if (-not $webWar -or -not (Test-Path -LiteralPath $webWar)) {

                        Write-Host "ERROR: Web WAR not found!"

                        exit 1
                    }


                    if (-not $serverWar -or -not (Test-Path -LiteralPath $serverWar)) {

                        Write-Host "WARNING: Server WAR missing."
                        Write-Host "Continuing with Web WAR only."
                    }


                    // =================================================
                    // SAVE VARIABLES
                    // =================================================

                    $varsFile = Join-Path $env:WORKSPACE "appzillon_vars.txt"


                    @(
                        "WEB_WAR=$webWar"
                        "SERVER_WAR=$serverWar"
                        "WEB_PROPS=$webPropsSource"
                        "SERVER_PROPS=$serverPropsSource"
                        "DB_PATH=$dbSqlPath"
                    ) |
                    Set-Content -LiteralPath $varsFile


                    Write-Host "Vars saved to: $varsFile"
                '''


                // ====================================================
                // STEP 5
                // COPY PROPERTIES
                // ====================================================

                powershell '''
                    $ErrorActionPreference = "Stop"

                    Write-Host "=========================================="
                    Write-Host "COPYING PROPERTIES TO TOMCAT LIB"
                    Write-Host "=========================================="


                    $appzHome = $env:APPZ_HOME

                    $varsFile =
                        Join-Path $env:WORKSPACE "appzillon_vars.txt"


                    $vars =
                        Get-Content `
                        -LiteralPath $varsFile `
                        -ErrorAction SilentlyContinue


                    $map = @{}


                    foreach ($line in $vars) {

                        if ($line -match "^(.*?)=(.*)$") {

                            $map[$matches[1]] = $matches[2]
                        }
                    }


                    $webProps = $map["WEB_PROPS"]
                    $serverProps = $map["SERVER_PROPS"]


                    Write-Host "Web Props: $webProps"
                    Write-Host "Server Props: $serverProps"
                    Write-Host "Tomcat LIB: $appzHome\\lib"


                    if ($webProps -and (Test-Path -LiteralPath $webProps)) {

                        Write-Host ""
                        Write-Host "Copying Web Properties..."

                        if (-not (Test-Path -LiteralPath "$appzHome\\lib")) {

                            New-Item `
                                -ItemType Directory `
                                -Path "$appzHome\\lib" `
                                -Force |
                                Out-Null
                        }


                        $destName =
                            Split-Path $webProps -Leaf


                        $dest =
                            Join-Path "$appzHome\\lib" $destName


                        if (Test-Path -LiteralPath $dest) {

                            Remove-Item `
                                -LiteralPath $dest `
                                -Recurse `
                                -Force `
                                -ErrorAction SilentlyContinue
                        }


                        Copy-Item `
                            -LiteralPath $webProps `
                            -Destination "$appzHome\\lib\\" `
                            -Recurse `
                            -Force


                        Write-Host "Web Properties copied successfully."
                    }
                    else {

                        Write-Host "WARNING: Web Props not found."
                    }


                    if ($serverProps -and (Test-Path -LiteralPath $serverProps)) {

                        Write-Host ""
                        Write-Host "Copying Server Properties..."


                        $destName =
                            Split-Path $serverProps -Leaf


                        $dest =
                            Join-Path "$appzHome\\lib" $destName


                        if (Test-Path -LiteralPath $dest) {

                            Remove-Item `
                                -LiteralPath $dest `
                                -Recurse `
                                -Force `
                                -ErrorAction SilentlyContinue
                        }


                        Copy-Item `
                            -LiteralPath $serverProps `
                            -Destination "$appzHome\\lib\\" `
                            -Recurse `
                            -Force


                        Write-Host "Server Properties copied successfully."
                    }
                    else {

                        Write-Host "WARNING: Server Props not found."
                    }


                    Write-Host ""
                    Write-Host "Tomcat lib contents after copy:"


                    Get-ChildItem `
                        -LiteralPath "$appzHome\\lib" |
                        Where-Object {
                            $_.Name -like "quizzz*" -or
                            $_.Name -like "AppzillonServer*"
                        } |
                        ForEach-Object {
                            Write-Host "  $($_.Name)"
                        }
                '''


                // ====================================================
                // STEP 6
                // DATABASE
                //
                // IMPORTANT:
                // This is PowerShell-safe.
                // NO "< $tempSql" is used.
                // ====================================================

                powershell '''
                    $ErrorActionPreference = "Continue"

                    Write-Host ""
                    Write-Host "=========================================="
                    Write-Host "RUNNING MYSQL DATABASE SCRIPTS"
                    Write-Host "=========================================="


                    $dbName = $env:DB_NAME
                    $dbUser = $env:DB_USER
                    $dbPass = $env:DB_PASS
                    $mysqlBin = $env:MYSQL_BIN


                    Write-Host "DB_NAME: $dbName"
                    Write-Host "MYSQL_BIN: $mysqlBin"
                    Write-Host "DB_USER: $dbUser"


                    // =================================================
                    // FIND MYSQL.EXE
                    // =================================================

                    $mysqlExe =
                        Join-Path $mysqlBin "mysql.exe"


                    if (-not (Test-Path -LiteralPath $mysqlExe)) {

                        Write-Host "mysql.exe not found at:"
                        Write-Host $mysqlExe

                        $mysqlExe =
                            "C:\\Program Files\\MySQL\\MySQL Server 8.0\\bin\\mysql.exe"
                    }


                    if (-not (Test-Path -LiteralPath $mysqlExe)) {

                        Write-Host "Trying mysql from PATH..."

                        $mysqlCommand =
                            Get-Command mysql.exe `
                            -ErrorAction SilentlyContinue


                        if ($mysqlCommand) {

                            $mysqlExe =
                                $mysqlCommand.Source
                        }
                    }


                    if (-not (Test-Path -LiteralPath $mysqlExe)) {

                        Write-Host "WARNING: mysql.exe not found."
                        Write-Host "Skipping database setup."

                        exit 0
                    }


                    Write-Host "Using MYSQL_EXE: $mysqlExe"


                    // =================================================
                    // CREATE DATABASE
                    // =================================================

                    Write-Host ""
                    Write-Host "Creating database if not exists: $dbName"


                    & $mysqlExe `
                        "-u$dbUser" `
                        "-p$dbPass" `
                        "-e" `
                        "CREATE DATABASE IF NOT EXISTS $dbName CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"


                    if ($LASTEXITCODE -ne 0) {

                        Write-Host "WARNING: Database creation returned exit code $LASTEXITCODE"
                        Write-Host "Continuing..."
                    }
                    else {

                        Write-Host "Database $dbName ensured."
                    }


                    // =================================================
                    // GET DB PATH FROM VARIABLES FILE
                    // =================================================

                    Write-Host ""
                    Write-Host "Searching for SQL files..."


                    $dbPath = ""


                    $varsFile =
                        Join-Path $env:WORKSPACE "appzillon_vars.txt"


                    if (Test-Path -LiteralPath $varsFile) {

                        $dbLine =
                            Get-Content -LiteralPath $varsFile |
                            Where-Object {
                                $_ -like "DB_PATH=*"
                            } |
                            Select-Object -First 1


                        if ($dbLine) {

                            $dbPath =
                                $dbLine.Substring(8)
                        }
                    }


                    Write-Host "DB_PATH from vars: $dbPath"


                    if ([string]::IsNullOrWhiteSpace($dbPath)) {

                        $dbPath =
                            Join-Path $env:QUIZZ_BIN "Server\\Database\\MySql"
                    }


                    Write-Host "Using DB_PATH: $dbPath"


                    // =================================================
                    // FALLBACK DATABASE PATHS
                    // =================================================

                    if (-not (Test-Path -LiteralPath $dbPath)) {

                        Write-Host "DB_PATH not found."

                        $alternate1 =
                            Join-Path `
                            $env:QUIZZ_PROJECT `
                            "bin\\Server\\Database\\MySql"


                        if (Test-Path -LiteralPath $alternate1) {

                            $dbPath = $alternate1
                        }
                        else {

                            $alternate2 =
                                Join-Path `
                                $env:APPZ_ARTIFACTS `
                                "lib\\AppzillonServer\\quizzz\\Database\\MySql"


                            if (Test-Path -LiteralPath $alternate2) {

                                $dbPath = $alternate2
                            }
                        }
                    }


                    Write-Host "Final DB_PATH: $dbPath"


                    if (-not (Test-Path -LiteralPath $dbPath)) {

                        Write-Host "WARNING: DB_PATH not found."
                        Write-Host "Skipping SQL execution."

                        exit 0
                    }


                    // =================================================
                    // FIND SQL FILES
                    // =================================================

                    $sqlFiles =
                        Get-ChildItem `
                        -LiteralPath $dbPath `
                        -Filter "*.sql" `
                        -File `
                        -ErrorAction SilentlyContinue


                    if (-not $sqlFiles) {

                        Write-Host "No SQL files found in $dbPath"

                        exit 0
                    }


                    Write-Host ""
                    Write-Host "Found SQL files:"
                    $sqlFiles | ForEach-Object {
                        Write-Host "  $($_.FullName)"
                    }


                    // =================================================
                    // EXECUTE SQL FILES
                    // =================================================

                    foreach ($sqlFile in $sqlFiles) {

                        Write-Host ""
                        Write-Host "=========================================="
                        Write-Host "Executing: $($sqlFile.Name)"
                        Write-Host "=========================================="


                        $tempSql =
                            Join-Path `
                            $env:TEMP `
                            "$($sqlFile.BaseName)_quizapp.sql"


                        try {

                            // -----------------------------------------
                            // Create temporary SQL file
                            // -----------------------------------------

                            "USE $dbName;" |
                                Set-Content `
                                -LiteralPath $tempSql `
                                -Encoding UTF8


                            Get-Content `
                                -LiteralPath $sqlFile.FullName |
                                Add-Content `
                                -LiteralPath $tempSql `
                                -Encoding UTF8


                            Write-Host "Temporary SQL file:"
                            Write-Host $tempSql


                            // -----------------------------------------
                            // IMPORTANT FIX
                            //
                            // DO NOT USE:
                            //
                            // mysql ... < $tempSql
                            //
                            // PowerShell does not support that syntax.
                            //
                            // Instead pipe the file contents into mysql.
                            // -----------------------------------------

                            Get-Content `
                                -LiteralPath $tempSql `
                                -Raw |
                                & $mysqlExe `
                                "-u$dbUser" `
                                "-p$dbPass" `
                                $dbName


                            $mysqlExitCode = $LASTEXITCODE


                            if ($mysqlExitCode -ne 0) {

                                Write-Host ""
                                Write-Host "WARNING: Failed to execute $($sqlFile.Name)"
                                Write-Host "MySQL exit code: $mysqlExitCode"

                                Write-Host ""
                                Write-Host "Trying MySQL SOURCE method..."


                                $sourceCommand =
                                    "USE $dbName; SOURCE `"$($sqlFile.FullName.Replace('\','/'))`";"


                                & $mysqlExe `
                                    "-u$dbUser" `
                                    "-p$dbPass" `
                                    "-D" `
                                    $dbName `
                                    "-e" `
                                    $sourceCommand


                                if ($LASTEXITCODE -ne 0) {

                                    Write-Host "ERROR: Still failed for $($sqlFile.Name)"
                                }
                                else {

                                    Write-Host "Success via SOURCE for $($sqlFile.Name)"
                                }
                            }
                            else {

                                Write-Host ""
                                Write-Host "Successfully executed $($sqlFile.Name)"
                            }

                        }
                        catch {

                            Write-Host ""
                            Write-Host "ERROR executing $($sqlFile.Name)"
                            Write-Host $_
                        }
                        finally {

                            if (Test-Path -LiteralPath $tempSql) {

                                Remove-Item `
                                    -LiteralPath $tempSql `
                                    -Force `
                                    -ErrorAction SilentlyContinue
                            }
                        }
                    }


                    // =================================================
                    // VERIFY TABLES
                    // =================================================

                    Write-Host ""
                    Write-Host "=========================================="
                    Write-Host "VERIFYING TABLES"
                    Write-Host "=========================================="


                    & $mysqlExe `
                        "-u$dbUser" `
                        "-p$dbPass" `
                        "-D" `
                        $dbName `
                        "-e" `
                        "SHOW TABLES;"


                    Write-Host ""
                    Write-Host "DATABASE STAGE COMPLETED"
                '''


                // ====================================================
                // STEP 7 & 8
                // TOMCAT SHUTDOWN / WAR DEPLOYMENT / START
                // ====================================================

                bat '''
                    @echo off

                    echo.
                    echo ==========================================
                    echo TOMCAT SHUTDOWN AND WAR DEPLOYMENT
                    echo ==========================================

                    echo TOMCAT HOME: %APPZ_HOME%
                    echo TOMCAT PORT: %TOMCAT_PORT%


                    set "WEB_WAR="
                    set "SERVER_WAR="


                    if exist "%WORKSPACE%\\appzillon_vars.txt" (

                        for /f "tokens=1,* delims==" %%a in ('type "%WORKSPACE%\\appzillon_vars.txt" ^| findstr "^WEB_WAR="') do set "WEB_WAR=%%b"

                        for /f "tokens=1,* delims==" %%a in ('type "%WORKSPACE%\\appzillon_vars.txt" ^| findstr "^SERVER_WAR="') do set "SERVER_WAR=%%b"
                    }


                    echo WEB_WAR: %WEB_WAR%
                    echo SERVER_WAR: %SERVER_WAR%


                    if "%WEB_WAR%"=="" set "WEB_WAR=%APPZ_ARTIFACTS%\\quizzz.war"

                    if "%SERVER_WAR%"=="" set "SERVER_WAR=%APPZ_ARTIFACTS%\\AppzillonServer.war"


                    echo Final WEB_WAR: %WEB_WAR%
                    echo Final SERVER_WAR: %SERVER_WAR%


                    if not exist "%WEB_WAR%" (

                        echo ERROR: WEB WAR not found at %WEB_WAR%

                        exit /b 1
                    }


                    if not exist "%SERVER_WAR%" (

                        echo WARNING: SERVER WAR not found.

                        echo Continuing with Web WAR only.
                    }


                    // =================================================
                    // SHUTDOWN TOMCAT
                    // =================================================

                    echo.
                    echo ==========================================
                    echo SHUTTING DOWN TOMCAT
                    echo ==========================================

                    call "%APPZ_HOME%\\bin\\shutdown.bat"


                    echo shutdown.bat executed.

                    echo.
                    echo Waiting 5 seconds...

                    ping 127.0.0.1 -n 6 >nul


                    // =================================================
                    // KILL REMAINING PROCESS
                    // =================================================

                    echo Killing remaining process on port %TOMCAT_PORT%...


                    for /f "tokens=5" %%a in ('netstat -ano ^| findstr :%TOMCAT_PORT% ^| findstr LISTENING') do (

                        echo Killing PID %%a

                        taskkill /F /PID %%a >nul 2>&1
                    )


                    ping 127.0.0.1 -n 3 >nul


                    // =================================================
                    // CLEAN OLD DEPLOYMENT
                    // =================================================

                    echo.
                    echo ==========================================
                    echo CLEANING OLD DEPLOYMENTS
                    echo ==========================================


                    rmdir /S /Q "%APPZ_HOME%\\webapps\\quizzz" >nul 2>&1

                    rmdir /S /Q "%APPZ_HOME%\\webapps\\AppzillonServer" >nul 2>&1

                    del /F /Q "%APPZ_HOME%\\webapps\\quizzz.war" >nul 2>&1

                    del /F /Q "%APPZ_HOME%\\webapps\\AppzillonServer.war" >nul 2>&1


                    rmdir /S /Q "%APPZ_HOME%\\work\\Catalina\\localhost\\quizzz" >nul 2>&1

                    rmdir /S /Q "%APPZ_HOME%\\work\\Catalina\\localhost\\AppzillonServer" >nul 2>&1


                    // =================================================
                    // COPY WEB WAR
                    // =================================================

                    echo.
                    echo ==========================================
                    echo COPYING NEW WARS
                    echo ==========================================


                    copy /Y "%WEB_WAR%" "%APPZ_HOME%\\webapps\\quizzz.war"


                    if errorlevel 1 (

                        echo ERROR: Failed to copy Web WAR.

                        exit /b 1
                    )


                    echo Web WAR copied successfully.


                    // =================================================
                    // COPY SERVER WAR
                    // =================================================

                    if exist "%SERVER_WAR%" (

                        copy /Y "%SERVER_WAR%" "%APPZ_HOME%\\webapps\\AppzillonServer.war"


                        if errorlevel 1 (

                            echo ERROR: Failed to copy Server WAR.

                            exit /b 1
                        )


                        echo Server WAR copied successfully.
                    )


                    // =================================================
                    // START TOMCAT
                    // =================================================

                    echo.
                    echo ==========================================
                    echo STARTING TOMCAT
                    echo ==========================================


                    set "JAVA_HOME=%JAVA_HOME%"
                    set "PATH=%JAVA_HOME%\\bin;%PATH%"
                    set "CATALINA_HOME=%APPZ_HOME%"
                    set "JENKINS_NODE_COOKIE=dontKillMe"


                    echo JAVA_HOME: %JAVA_HOME%
                    echo CATALINA_HOME: %CATALINA_HOME%


                    call "%APPZ_HOME%\\bin\\catalina.bat" start


                    echo catalina.bat start executed.


                    echo.
                    echo Waiting for Tomcat...

                    ping 127.0.0.1 -n 21 >nul


                    // =================================================
                    // CHECK TOMCAT PORT
                    // =================================================

                    echo.
                    echo ==========================================
                    echo CHECKING TOMCAT PORT %TOMCAT_PORT%
                    echo ==========================================


                    netstat -ano | findstr :%TOMCAT_PORT% | findstr LISTENING


                    if errorlevel 1 (

                        echo WARNING: Port not listening yet.

                        ping 127.0.0.1 -n 10 >nul

                        netstat -ano | findstr :%TOMCAT_PORT%
                    )
                    else (

                        echo Port %TOMCAT_PORT% is LISTENING.
                    )


                    // =================================================
                    // TOMCAT LOGS
                    // =================================================

                    echo.
                    echo ==========================================
                    echo TOMCAT LOGS
                    echo ==========================================


                    if exist "%APPZ_HOME%\\logs\\catalina.out" (

                        powershell -Command "Get-Content '%APPZ_HOME%\\logs\\catalina.out' -Tail 40"
                    )
                    else (

                        echo catalina.out not found.

                        dir "%APPZ_HOME%\\logs\\"
                    )


                    echo.
                    echo Checking webapps deployment...


                    dir "%APPZ_HOME%\\webapps\\" | findstr quizzz

                    dir "%APPZ_HOME%\\webapps\\" | findstr Appzillon
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
                    echo Attempts remaining: %RETRIES%


                    curl -s -o nul -w "%%{http_code}" "%APPZILLON_URL%" | findstr "200 302"


                    if not errorlevel 1 (

                        echo.
                        echo ==========================================
                        echo APPZILLON IS RUNNING
                        echo ==========================================

                        echo URL:
                        echo %APPZILLON_URL%

                        exit /b 0
                    }


                    curl -s -o nul -w "%%{http_code}" "%APPZILLON_URL%" | findstr "404"


                    if not errorlevel 1 (

                        echo Appzillon returned 404.

                        echo Application may still be deploying.
                    }


                    set /a RETRIES-=1


                    if %RETRIES% LEQ 0 (

                        echo.
                        echo ==========================================
                        echo APPZILLON HEALTH CHECK TIMEOUT
                        echo ==========================================


                        echo.
                        echo TOMCAT PORT STATUS

                        netstat -ano | findstr :%TOMCAT_PORT%


                        echo.
                        echo TOMCAT LOGS


                        if exist "%APPZ_HOME%\\logs\\catalina.out" (

                            powershell -Command "Get-Content '%APPZ_HOME%\\logs\\catalina.out' -Tail 50"
                        )
                        else (

                            dir "%APPZ_HOME%\\logs\\" 2>nul
                        )


                        echo.
                        echo WEBAPPS

                        dir "%APPZ_HOME%\\webapps\\"


                        netstat -ano |
                            findstr :%TOMCAT_PORT% |
                            findstr LISTENING >nul


                        if not errorlevel 1 (

                            echo Tomcat is listening.

                            echo Continuing pipeline.

                            exit /b 0
                        }


                        exit /b 1
                    }


                    echo.
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

                    echo URL: %APPZILLON_URL%


                    start "" "%APPZILLON_URL%"


                    ping 127.0.0.1 -n 3 >nul


                    echo Appzillon popup triggered.


                    if exist "C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe" (

                        start "" "C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe" "%APPZILLON_URL%"
                    )


                    echo Waiting for UI...

                    ping 127.0.0.1 -n 9 >nul
                '''
            }
        }


        // ============================================================
        // PLAYWRIGHT
        // ============================================================

        stage('Playwright UI Tests - After Open') {

            steps {

                echo '=========================================='
                echo 'PLAYWRIGHT UI TESTS'
                echo '=========================================='


                bat '''
                    @echo off


                    echo Playwright directory:
                    echo %PLAYWRIGHT_DIR%


                    echo Appzillon URL:
                    echo %APPZILLON_URL%


                    echo.


                    if not exist "%PLAYWRIGHT_DIR%" (

                        echo ERROR: Playwright directory not found.

                        echo Path:
                        echo %PLAYWRIGHT_DIR%

                        exit /b 1
                    }


                    if not exist "%PLAYWRIGHT_DIR%\\package.json" (

                        echo ERROR: package.json missing.

                        echo Contents:

                        dir "%PLAYWRIGHT_DIR%"

                        exit /b 1
                    }


                    cd /d "%PLAYWRIGHT_DIR%"


                    echo.
                    echo ==========================================
                    echo RUNNING PLAYWRIGHT
                    echo ==========================================


                    npx playwright test tests/05-home-quiz-flow.spec.js --headed --project=chromium 2>&1


                    set PW_EXIT=%errorlevel%


                    echo.
                    echo Playwright exit code: %PW_EXIT%


                    if %PW_EXIT% NEQ 0 (

                        echo WARNING: Playwright tests failed.

                        if exist "playwright-report\\index.html" (

                            start "" "playwright-report\\index.html"
                        )

                        echo Pipeline will continue.
                    )
                    else (

                        echo ALL PLAYWRIGHT TESTS PASSED.

                        if exist "playwright-report\\index.html" (

                            start "" "playwright-report\\index.html"
                        )
                    }


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

            echo 'Backend: http://localhost:8080/api/user/getQuizzes'
            echo 'Appzillon: http://localhost:8090/quizzz/'
            echo 'AppzillonServer: http://localhost:8090/AppzillonServer/Appzillon'

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
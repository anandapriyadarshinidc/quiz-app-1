pipeline {

    agent any

    environment {

        // ============================================================
        // JAVA
        // ============================================================
        JAVA_HOME = 'C:/Program Files/Java/jdk-21.0.11'

        // ============================================================
        // MAVEN
        // ============================================================
        MAVEN_HOME = 'D:/apache-maven-3.8.5'

        // ============================================================
        // BACKEND
        // ============================================================
        APP_DIR = 'project'
        APP_JAR = 'target/quiz-bg-1.0.0.jar'

        // ============================================================
        // APPZILLON / TOMCAT
        // ============================================================
        APPZ_HOME = 'D:/tom/apache-tomcat-9.0.53'

        APPZ_ARTIFACTS =
            'D:/MONTH-2/Week-4/wednesday/appzillon-artifacts'

        // ============================================================
        // PORTS
        // ============================================================
        BACKEND_PORT = '8080'
        TOMCAT_PORT = '8090'

        // ============================================================
        // URLS
        // ============================================================
        BACKEND_URL =
            'http://localhost:8080/api/user/getQuizzes'

        APPZILLON_URL =
            'http://localhost:8090/quizzz/'
    }

    stages {

        // ============================================================
        // CHECKOUT
        // ============================================================
        stage('Checkout') {

            steps {

                echo '=========================================='
                echo 'CHECKING OUT SOURCE CODE'
                echo '=========================================='

                checkout scm
            }
        }


        // ============================================================
        // BUILD BACKEND
        // ============================================================
        stage('Build Backend Jar') {

            steps {

                bat '''
                    @echo off

                    echo ==========================================
                    echo BUILDING SPRING BOOT BACKEND
                    echo ==========================================

                    set "JAVA_HOME=%JAVA_HOME%"
                    set "MAVEN_HOME=%MAVEN_HOME%"

                    set "PATH=%JAVA_HOME%\\bin;%MAVEN_HOME%\\bin;%PATH%"

                    echo.
                    echo JAVA_HOME:
                    echo %JAVA_HOME%

                    echo.
                    echo JAVA VERSION:
                    java -version

                    echo.
                    echo MAVEN VERSION:
                    mvn -version

                    echo.
                    echo ==========================================
                    echo RUNNING MAVEN BUILD
                    echo ==========================================

                    cd /d "%WORKSPACE%\\%APP_DIR%"

                    mvn clean package -DskipTests

                    if errorlevel 1 (
                        echo.
                        echo ==========================================
                        echo BACKEND BUILD FAILED
                        echo ==========================================
                        exit /b 1
                    )

                    echo.
                    echo ==========================================
                    echo BACKEND BUILD SUCCESSFUL
                    echo ==========================================

                    echo.
                    echo CHECKING JAR:

                    if not exist "%APP_JAR%" (
                        echo.
                        echo ==========================================
                        echo ERROR: JAR FILE NOT FOUND
                        echo ==========================================
                        echo Expected:
                        echo %WORKSPACE%\\%APP_DIR%\\%APP_JAR%
                        echo.
                        dir target
                        exit /b 1
                    )

                    echo.
                    echo JAR FOUND:
                    echo %WORKSPACE%\\%APP_DIR%\\%APP_JAR%

                    echo.
                    dir target
                '''
            }
        }


        // ============================================================
        // STOP OLD BACKEND
        // ============================================================
        stage('Stop Old Backend') {

            steps {

                bat '''
                    @echo off

                    echo ==========================================
                    echo STOPPING OLD BACKEND
                    echo ==========================================

                    echo Checking port %BACKEND_PORT%...

                    for /f "tokens=5" %%a in ('netstat -ano ^| findstr :%BACKEND_PORT% ^| findstr LISTENING') do (
                        echo Found process: %%a
                        echo Stopping process %%a...

                        taskkill /F /PID %%a >nul 2>&1
                    )

                    echo.
                    echo OLD BACKEND PROCESS CHECK COMPLETED.
                '''
            }
        }


        // ============================================================
        // START BACKEND
        // ============================================================
        stage('Deploy Backend') {

            steps {

                bat '''
                    @echo off

                    echo ==========================================
                    echo STARTING SPRING BOOT BACKEND
                    echo ==========================================

                    set "JAVA_HOME=%JAVA_HOME%"
                    set "PATH=%JAVA_HOME%\\bin;%MAVEN_HOME%\\bin;%PATH%"

                    cd /d "%WORKSPACE%\\%APP_DIR%"

                    echo.
                    echo BACKEND DIRECTORY:
                    cd

                    echo.
                    echo JAR:
                    echo %APP_JAR%

                    if not exist "%APP_JAR%" (
                        echo.
                        echo ERROR: BACKEND JAR DOES NOT EXIST
                        exit /b 1
                    )

                    echo.
                    echo STARTING BACKEND ON PORT %BACKEND_PORT%...

                    set "JENKINS_NODE_COOKIE=dontKillMe"

                    start "Quiz Backend" /B cmd /c ^
                    "java -jar "%APP_JAR%" --server.port=%BACKEND_PORT% > backend.log 2>&1"

                    echo.
                    echo ==========================================
                    echo BACKEND START COMMAND EXECUTED
                    echo ==========================================
                    echo BACKEND PORT: %BACKEND_PORT%
                    echo BACKEND URL: %BACKEND_URL%
                    echo LOG FILE:
                    echo %WORKSPACE%\\%APP_DIR%\\backend.log
                    echo ==========================================
                '''
            }
        }


        // ============================================================
        // BACKEND HEALTH CHECK
        // ============================================================
        stage('Backend Health Check') {

            steps {

                bat '''
                    @echo off

                    echo ==========================================
                    echo BACKEND HEALTH CHECK
                    echo ==========================================

                    echo.
                    echo URL:
                    echo %BACKEND_URL%

                    echo.
                    echo PORT:
                    echo %BACKEND_PORT%

                    echo.
                    echo Waiting for backend...

                    powershell -NoProfile -ExecutionPolicy Bypass -Command "Start-Sleep -Seconds 5"

                    set "RETRIES=20"

                    :CHECK_BACKEND

                    echo.
                    echo Checking backend...
                    echo Remaining attempts: %RETRIES%

                    curl.exe --silent --show-error --output nul --write-out "%%{http_code}" "%BACKEND_URL%" > backend_status.txt 2>nul

                    set /p STATUS=<backend_status.txt

                    echo HTTP STATUS: %STATUS%

                    if "%STATUS%"=="200" (
                        echo.
                        echo ==========================================
                        echo BACKEND IS RUNNING SUCCESSFULLY
                        echo ==========================================
                        echo URL: %BACKEND_URL%
                        echo PORT: %BACKEND_PORT%
                        echo ==========================================
                        exit /b 0
                    )

                    set /a RETRIES-=1

                    if %RETRIES% LEQ 0 (
                        echo.
                        echo ==========================================
                        echo BACKEND HEALTH CHECK FAILED
                        echo ==========================================
                        echo URL: %BACKEND_URL%
                        echo PORT: %BACKEND_PORT%
                        echo.
                        echo ==========================================
                        echo BACKEND LOG
                        echo ==========================================

                        if exist "%WORKSPACE%\\%APP_DIR%\\backend.log" (
                            type "%WORKSPACE%\\%APP_DIR%\\backend.log"
                        ) else (
                            echo backend.log was not found.
                        )

                        echo.
                        echo ==========================================
                        echo END BACKEND LOG
                        echo ==========================================

                        exit /b 1
                    )

                    echo Backend is not ready.
                    echo Waiting 3 seconds...

                    powershell -NoProfile -ExecutionPolicy Bypass -Command "Start-Sleep -Seconds 3"

                    goto CHECK_BACKEND
                '''
            }
        }


        // ============================================================
        // DEPLOY APPZILLON
        // ============================================================
        stage('Deploy Appzillon') {

            steps {

                bat '''
                    @echo off

                    echo ==========================================
                    echo DEPLOYING APPZILLON
                    echo ==========================================

                    echo.
                    echo APPZILLON HOME:
                    echo %APPZ_HOME%

                    echo.
                    echo APPZILLON ARTIFACTS:
                    echo %APPZ_ARTIFACTS%

                    echo.
                    echo TOMCAT PORT:
                    echo %TOMCAT_PORT%

                    echo.
                    echo ==========================================
                    echo CHECKING APPZILLON DIRECTORIES
                    echo ==========================================

                    if not exist "%APPZ_HOME%" (
                        echo ERROR: Tomcat directory does not exist:
                        echo %APPZ_HOME%
                        exit /b 1
                    )

                    if not exist "%APPZ_ARTIFACTS%" (
                        echo ERROR: Appzillon artifacts directory does not exist:
                        echo %APPZ_ARTIFACTS%
                        exit /b 1
                    )

                    echo.
                    echo Appzillon directories found.

                    echo.
                    echo ==========================================
                    echo APPZILLON DEPLOYMENT
                    echo ==========================================

                    REM ==================================================
                    REM PUT YOUR ACTUAL APPZILLON DEPLOYMENT COMMANDS HERE
                    REM ==================================================

                    echo.
                    echo APPZILLON DEPLOYMENT STEP COMPLETED.
                '''
            }
        }


        // ============================================================
        // APPZILLON HEALTH CHECK
        // ============================================================
        stage('Appzillon Health Check') {

            steps {

                bat '''
                    @echo off

                    echo ==========================================
                    echo APPZILLON HEALTH CHECK
                    echo ==========================================

                    echo.
                    echo URL:
                    echo %APPZILLON_URL%

                    echo.
                    echo Waiting for Tomcat...

                    powershell -NoProfile -ExecutionPolicy Bypass -Command "Start-Sleep -Seconds 5"

                    curl.exe --silent --show-error --fail "%APPZILLON_URL%" >nul 2>&1

                    if errorlevel 1 (
                        echo.
                        echo ==========================================
                        echo APPZILLON HEALTH CHECK FAILED
                        echo ==========================================
                        echo URL: %APPZILLON_URL%
                        exit /b 1
                    )

                    echo.
                    echo ==========================================
                    echo APPZILLON IS RUNNING
                    echo ==========================================
                    echo URL: %APPZILLON_URL%
                    echo ==========================================
                '''
            }
        }
    }


    // ============================================================
    // POST ACTIONS
    // ============================================================
    post {

        success {

            echo '''
==========================================
PIPELINE SUCCESS
==========================================
Java Version : 21
Backend Port : 8080
Backend URL  : http://localhost:8080/api/user/getQuizzes
Tomcat Port  : 8090
Appzillon URL: http://localhost:8090/quizzz/
==========================================
'''
        }

        failure {

            echo '''
==========================================
PIPELINE FAILED
==========================================
Check:
1. Jenkins console output
2. project/backend.log
3. Maven build output
4. Port 8080
5. Tomcat/Appzillon configuration
==========================================
'''
        }

        always {

            echo 'Pipeline execution completed.'
        }
    }
}
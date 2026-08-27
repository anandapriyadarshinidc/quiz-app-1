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
        // BACKEND JAR
        // ============================================================

        APP_JAR = 'target/quiz-bg-1.0.0.jar'

        // ============================================================
        // BACKEND
        // ============================================================

        BACKEND_PORT = '8080'

        BACKEND_URL = 'http://localhost:8080/api/user/getQuizzes'

        // ============================================================
        // TOMCAT / APPZILLON
        // ============================================================

        APPZ_HOME = 'D:/tom/apache-tomcat-9.0.53'

        TOMCAT_PORT = '8090'

        // ============================================================
        // APPZILLON ARTIFACTS
        // ============================================================

        APPZ_ARTIFACTS = 'D:/MONTH-2/Week-4/wednesday/appzillon-artifacts'

        // ============================================================
        // APPZILLON URL
        // ============================================================

        APPZILLON_URL = 'http://localhost:8090/quizzz/'
    }


    stages {

        // ============================================================
        // 1. CHECKOUT
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
        // 2. VERIFY ENVIRONMENT
        // ============================================================

        stage('Verify Environment') {

            steps {

                bat '''
                    @echo off

                    echo ==========================================
                    echo VERIFYING ENVIRONMENT
                    echo ==========================================

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
                    echo BACKEND PORT:
                    echo %BACKEND_PORT%

                    echo.
                    echo BACKEND URL:
                    echo %BACKEND_URL%

                    echo.
                    echo TOMCAT HOME:
                    echo %APPZ_HOME%

                    echo.
                    echo TOMCAT PORT:
                    echo %TOMCAT_PORT%

                    echo.
                    echo ==========================================
                    echo ENVIRONMENT CHECK COMPLETED
                    echo ==========================================
                '''
            }
        }


        // ============================================================
        // 3. BUILD BACKEND JAR
        // ============================================================

        stage('Build Backend Jar') {

            steps {

                bat '''
                    @echo off

                    echo ==========================================
                    echo BUILDING SPRING BOOT BACKEND
                    echo ==========================================

                    cd /d "%WORKSPACE%\\project"

                    echo.
                    echo PROJECT DIRECTORY:
                    cd

                    echo.
                    echo JAVA_HOME:
                    echo %JAVA_HOME%

                    set "PATH=%JAVA_HOME%\\bin;%MAVEN_HOME%\\bin;%PATH%"

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
                    echo MAVEN BUILD SUCCESSFUL
                    echo ==========================================

                    echo.
                    echo GENERATED FILES:
                    dir target

                    echo.
                    echo EXPECTED JAR:
                    echo %APP_JAR%

                    if not exist "%APP_JAR%" (
                        echo.
                        echo ==========================================
                        echo ERROR: JAR FILE NOT FOUND
                        echo ==========================================
                        echo Expected:
                        echo %APP_JAR%
                        echo.
                        echo Contents of target:
                        dir target
                        exit /b 1
                    )

                    echo.
                    echo ==========================================
                    echo JAR FILE FOUND
                    echo ==========================================
                '''
            }
        }


        // ============================================================
        // 4. STOP OLD BACKEND
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

                        echo Found process:
                        echo %%a

                        echo Stopping process %%a...

                        taskkill /F /PID %%a >nul 2>&1

                        if errorlevel 1 (
                            echo Process %%a could not be stopped.
                        ) else (
                            echo Process %%a stopped successfully.
                        )
                    )

                    echo.
                    echo Waiting for port to be released...

                    timeout /t 3 /nobreak >nul

                    echo.
                    echo ==========================================
                    echo OLD BACKEND STOP COMPLETE
                    echo ==========================================
                '''
            }
        }


        // ============================================================
        // 5. START BACKEND
        // ============================================================

        stage('Deploy Backend') {

            steps {

                bat '''
                    @echo off

                    echo ==========================================
                    echo STARTING SPRING BOOT BACKEND
                    echo ==========================================

                    cd /d "%WORKSPACE%\\project"

                    echo.
                    echo PROJECT DIRECTORY:
                    cd

                    echo.
                    echo JAR:
                    echo %APP_JAR%

                    if not exist "%APP_JAR%" (
                        echo.
                        echo ERROR: JAR FILE NOT FOUND
                        echo %WORKSPACE%\\%APP_JAR%
                        exit /b 1
                    )

                    echo.
                    echo JAVA_HOME:
                    echo %JAVA_HOME%

                    set "PATH=%JAVA_HOME%\\bin;%MAVEN_HOME%\\bin;%PATH%"

                    echo.
                    echo Starting application...

                    set "JENKINS_NODE_COOKIE=dontKillMe"

                    start "Quiz Backend" /MIN cmd /c "java -jar "%WORKSPACE%\\project\\%APP_JAR%" --server.port=%BACKEND_PORT% > "%WORKSPACE%\\project\\backend.log" 2>&1"

                    echo.
                    echo ==========================================
                    echo BACKEND START COMMAND EXECUTED
                    echo ==========================================

                    echo Backend Port:
                    echo %BACKEND_PORT%

                    echo Backend URL:
                    echo %BACKEND_URL%

                    echo.
                    echo Log file:
                    echo %WORKSPACE%\\project\\backend.log
                '''
            }
        }


        // ============================================================
        // 6. BACKEND HEALTH CHECK
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

                    set RETRIES=30

                    :backend_wait

                    echo Checking backend...
                    echo Remaining attempts: %RETRIES%

                    curl -s -o nul -w "%%{http_code}" "%BACKEND_URL%" > "%TEMP%\\backend_status.txt"

                    set /p STATUS=<"%TEMP%\\backend_status.txt"

                    echo HTTP STATUS:
                    echo %STATUS%

                    if "%STATUS%"=="200" (

                        echo.
                        echo ==========================================
                        echo BACKEND IS RUNNING
                        echo ==========================================
                        echo URL:
                        echo %BACKEND_URL%
                        echo ==========================================

                        exit /b 0
                    )

                    set /a RETRIES-=1

                    if %RETRIES% LEQ 0 (

                        echo.
                        echo ==========================================
                        echo BACKEND HEALTH CHECK FAILED
                        echo ==========================================

                        echo Backend did not respond on port %BACKEND_PORT%.

                        echo.
                        echo Checking port %BACKEND_PORT%...

                        netstat -ano | findstr :%BACKEND_PORT%

                        echo.
                        echo ==========================================
                        echo BACKEND LOG
                        echo ==========================================

                        if exist "%WORKSPACE%\\project\\backend.log" (
                            type "%WORKSPACE%\\project\\backend.log"
                        ) else (
                            echo backend.log not found.
                        )

                        echo.
                        echo ==========================================
                        echo END BACKEND LOG
                        echo ==========================================

                        exit /b 1
                    )

                    echo Backend not ready.
                    echo Waiting 3 seconds...

                    timeout /t 3 /nobreak >nul

                    goto backend_wait
                '''
            }
        }


        // ============================================================
        // 7. DEPLOY APPZILLON / TOMCAT
        // ============================================================

        stage('Deploy Appzillon') {

            steps {

                bat '''
                    @echo off

                    echo ==========================================
                    echo DEPLOYING APPZILLON
                    echo ==========================================

                    echo.
                    echo TOMCAT HOME:
                    echo %APPZ_HOME%

                    echo.
                    echo APPZILLON ARTIFACTS:
                    echo %APPZ_ARTIFACTS%

                    echo.

                    if not exist "%APPZ_HOME%" (
                        echo.
                        echo ERROR: TOMCAT HOME NOT FOUND
                        echo %APPZ_HOME%
                        exit /b 1
                    )

                    if not exist "%APPZ_HOME%\\bin\\catalina.bat" (
                        echo.
                        echo ERROR: catalina.bat NOT FOUND
                        echo %APPZ_HOME%\\bin\\catalina.bat
                        exit /b 1
                    )

                    if not exist "%APPZ_ARTIFACTS%" (
                        echo.
                        echo ERROR: APPZILLON ARTIFACTS FOLDER NOT FOUND
                        echo %APPZ_ARTIFACTS%
                        exit /b 1
                    )

                    if not exist "%APPZ_ARTIFACTS%\\AppzillonServer.war" (
                        echo.
                        echo ERROR: AppzillonServer.war NOT FOUND
                        echo %APPZ_ARTIFACTS%\\AppzillonServer.war
                        exit /b 1
                    )

                    if not exist "%APPZ_ARTIFACTS%\\quizzz.war" (
                        echo.
                        echo ERROR: quizzz.war NOT FOUND
                        echo %APPZ_ARTIFACTS%\\quizzz.war
                        exit /b 1
                    )

                    echo.
                    echo ==========================================
                    echo STOPPING TOMCAT
                    echo ==========================================

                    for /f "tokens=5" %%a in ('netstat -ano ^| findstr :%TOMCAT_PORT% ^| findstr LISTENING') do (

                        echo Stopping Tomcat process %%a

                        taskkill /F /PID %%a >nul 2>&1
                    )

                    timeout /t 5 /nobreak >nul

                    echo.
                    echo ==========================================
                    echo REMOVING OLD APPLICATIONS
                    echo ==========================================

                    rmdir /S /Q "%APPZ_HOME%\\webapps\\AppzillonServer" >nul 2>&1

                    rmdir /S /Q "%APPZ_HOME%\\webapps\\quizzz" >nul 2>&1

                    del /F /Q "%APPZ_HOME%\\webapps\\AppzillonServer.war" >nul 2>&1

                    del /F /Q "%APPZ_HOME%\\webapps\\quizzz.war" >nul 2>&1

                    echo.
                    echo ==========================================
                    echo COPYING APPZILLON WAR FILES
                    echo ==========================================

                    copy /Y "%APPZ_ARTIFACTS%\\AppzillonServer.war" "%APPZ_HOME%\\webapps\\AppzillonServer.war"

                    if errorlevel 1 (
                        echo.
                        echo ERROR: FAILED TO COPY AppzillonServer.war
                        exit /b 1
                    )

                    copy /Y "%APPZ_ARTIFACTS%\\quizzz.war" "%APPZ_HOME%\\webapps\\quizzz.war"

                    if errorlevel 1 (
                        echo.
                        echo ERROR: FAILED TO COPY quizzz.war
                        exit /b 1
                    )

                    echo.
                    echo ==========================================
                    echo STARTING TOMCAT
                    echo ==========================================

                    set "JENKINS_NODE_COOKIE=dontKillMe"

                    start "Tomcat Appzillon" /MIN cmd /c "call "%APPZ_HOME%\\bin\\catalina.bat" run > "%APPZ_HOME%\\logs\\jenkins-run.log" 2>&1"

                    echo.
                    echo ==========================================
                    echo TOMCAT START COMMAND EXECUTED
                    echo ==========================================

                    echo Tomcat Port:
                    echo %TOMCAT_PORT%

                    echo Appzillon URL:
                    echo %APPZILLON_URL%
                '''
            }
        }


        // ============================================================
        // 8. APPZILLON HEALTH CHECK
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

                    set RETRIES=30

                    :appzillon_wait

                    echo Checking Appzillon...
                    echo Remaining attempts: %RETRIES%

                    curl -s -o nul -w "%%{http_code}" "%APPZILLON_URL%" > "%TEMP%\\appzillon_status.txt"

                    set /p STATUS=<"%TEMP%\\appzillon_status.txt"

                    echo HTTP STATUS:
                    echo %STATUS%

                    if "%STATUS%"=="200" (

                        echo.
                        echo ==========================================
                        echo APPZILLON IS RUNNING
                        echo ==========================================
                        echo URL:
                        echo %APPZILLON_URL%
                        echo ==========================================

                        exit /b 0
                    )

                    set /a RETRIES-=1

                    if %RETRIES% LEQ 0 (

                        echo.
                        echo ==========================================
                        echo APPZILLON HEALTH CHECK FAILED
                        echo ==========================================

                        echo.
                        echo Checking Tomcat port:

                        netstat -ano | findstr :%TOMCAT_PORT%

                        echo.
                        echo Checking Tomcat log:

                        if exist "%APPZ_HOME%\\logs\\jenkins-run.log" (
                            type "%APPZ_HOME%\\logs\\jenkins-run.log"
                        ) else (
                            echo Tomcat log not found.
                        )

                        exit /b 1
                    )

                    echo Appzillon not ready.
                    echo Waiting 5 seconds...

                    timeout /t 5 /nobreak >nul

                    goto appzillon_wait
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

Java Version : 21.0.11

Backend:
http://localhost:8080

Backend API:
http://localhost:8080/api/user/getQuizzes

Tomcat:
http://localhost:8090

Appzillon:
http://localhost:8090/quizzz/

==========================================
DEPLOYMENT COMPLETED SUCCESSFULLY
==========================================
'''
        }

        failure {

            echo '''
==========================================
PIPELINE FAILED
==========================================

Backend Port : 8080
Tomcat Port  : 8090

Check:

1. backend.log
2. Jenkins Console Output
3. Port 8080
4. Port 8090
5. Tomcat logs
6. Appzillon WAR files

==========================================
'''
        }
    }
}
pipeline {

    agent any

    environment {

        // ============================================================
        // JAVA
        // ============================================================

        JAVA_HOME = 'C:/Program Files/Java/jdk-17.0.2'

        // ============================================================
        // BACKEND
        // ============================================================

        APP_JAR = 'target/quizapp.jar'
        BACKEND_PORT = '8080'
        BACKEND_URL = 'http://localhost:8080/api/categories'

        // ============================================================
        // APPZILLON UI
        // ============================================================

        FRONTEND_PORT = '8090'
        FRONTEND_URL = 'http://localhost:8090/quizapp/'
    }


    stages {

        // ============================================================
        // 1. CHECKOUT
        // ============================================================

        stage('Checkout') {

            steps {

                echo '=========================================='
                echo 'CHECKING OUT QUIZ APPLICATION'
                echo '=========================================='

                git branch: 'main',
                    url: 'https://github.com/anandapriyadarshinidc/quiz-app-1.git'
            }
        }


        // ============================================================
        // 2. CHECK WORKSPACE
        // ============================================================

        stage('Check Workspace') {

            steps {

                bat '''
                    @echo off

                    echo ==========================================
                    echo WORKSPACE CHECK
                    echo ==========================================

                    echo Workspace:
                    echo %WORKSPACE%

                    echo.
                    echo Workspace contents:
                    dir

                    echo.
                    echo Checking pom.xml...

                    if not exist "pom.xml" (
                        echo ERROR: pom.xml not found.
                        exit /b 1
                    )

                    echo pom.xml found successfully.

                    echo.
                    echo Checking src directory...

                    if not exist "src" (
                        echo ERROR: src directory not found.
                        exit /b 1
                    )

                    echo src directory found successfully.

                    echo.
                    echo Workspace check completed.
                '''
            }
        }


        // ============================================================
        // 3. CHECK JAVA AND MAVEN
        // ============================================================

        stage('Check Java and Maven') {

            steps {

                bat '''
                    @echo off

                    echo ==========================================
                    echo JAVA VERSION
                    echo ==========================================

                    set "JAVA_HOME=C:\\Program Files\\Java\\jdk-17.0.2"
                    set "PATH=%JAVA_HOME%\\bin;%PATH%"

                    java -version

                    if errorlevel 1 (
                        echo ERROR: Java is not working.
                        exit /b 1
                    )

                    echo.
                    echo ==========================================
                    echo MAVEN VERSION
                    echo ==========================================

                    mvn -version

                    if errorlevel 1 (
                        echo ERROR: Maven is not working.
                        exit /b 1
                    )

                    echo.
                    echo Java and Maven are working successfully.
                '''
            }
        }


        // ============================================================
        // 4. BUILD BACKEND
        // ============================================================

        stage('Build Backend') {

            steps {

                bat '''
                    @echo off

                    echo ==========================================
                    echo BUILDING QUIZAPP BACKEND
                    echo ==========================================

                    set "JAVA_HOME=C:\\Program Files\\Java\\jdk-17.0.2"
                    set "PATH=%JAVA_HOME%\\bin;%PATH%"

                    echo.
                    echo Starting Maven build...

                    call mvn -B clean package -DskipTests

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
                    echo Target directory:

                    dir target

                    echo.
                    echo Checking JAR...

                    if not exist "target\\quizapp.jar" (
                        echo ERROR: target\\quizapp.jar was not generated.
                        exit /b 1
                    )

                    echo.
                    echo quizapp.jar found successfully.
                '''
            }
        }


        // ============================================================
        // 5. STOP OLD BACKEND ON 8090
        // ============================================================

        stage('Stop Old Backend') {

            steps {

                bat '''
                    @echo off

                    echo ==========================================
                    echo CHECKING PORT 8080
                    echo ==========================================

                    netstat -ano | findstr LISTENING | findstr ":8080"

                    echo.

                    for /f "tokens=5" %%a in ('netstat -ano ^| findstr LISTENING ^| findstr ":8080"') do (

                        echo Found process %%a using port 8080.

                        echo Stopping process %%a...

                        taskkill /F /PID %%a >nul 2>&1
                    )

                    echo.
                    echo Waiting for port 8080...

                    ping -n 4 127.0.0.1 >nul

                    echo.
                    echo Port 8090 cleanup completed.
                '''
            }
        }


        // ============================================================
        // 6. START SPRING BOOT ON 8080
        // ============================================================

        stage('Start Backend') {

            steps {

                bat '''
                    @echo off

                    echo ==========================================
                    echo STARTING QUIZAPP BACKEND
                    echo ==========================================

                    set "JAVA_HOME=C:\\Program Files\\Java\\jdk-17.0.2"
                    set "PATH=%JAVA_HOME%\\bin;%PATH%"

                    if exist "%WORKSPACE%\\backend.log" (
                        del /F /Q "%WORKSPACE%\\backend.log"
                    )

                    if exist "%WORKSPACE%\\backend-err.log" (
                        del /F /Q "%WORKSPACE%\\backend-err.log"
                    )

                    echo.
                    echo JAR:
                    echo %WORKSPACE%\\target\\quizapp.jar

                    echo.
                    echo PORT:
                    echo 8080

                    echo.
                    echo Starting Spring Boot...

                    powershell -NoProfile -Command "$env:JENKINS_NODE_COOKIE='dontKillMe'; Start-Process -FilePath '%JAVA_HOME%\\bin\\java.exe' -ArgumentList '-jar','%WORKSPACE%\\target\\quizapp.jar','--server.port=8090' -RedirectStandardOutput '%WORKSPACE%\\backend.log' -RedirectStandardError '%WORKSPACE%\\backend-err.log' -WindowStyle Hidden"

                    echo.
                    echo Backend start command executed.

                    echo.
                    echo Waiting for Spring Boot...

                    ping -n 11 127.0.0.1 >nul

                    echo.
                    echo ==========================================
                    echo PORT 8080 STATUS
                    echo ==========================================

                    netstat -ano | findstr LISTENING | findstr ":8080"

                    echo.
                    echo ==========================================
                    echo BACKEND LOG
                    echo ==========================================

                    if exist "%WORKSPACE%\\backend.log" (
                        powershell -NoProfile -Command "Get-Content '%WORKSPACE%\\backend.log' -Tail 100"
                    ) else (
                        echo backend.log not found.
                    )

                    echo.
                    echo ==========================================
                    echo BACKEND ERROR LOG
                    echo ==========================================

                    if exist "%WORKSPACE%\\backend-err.log" (
                        powershell -NoProfile -Command "Get-Content '%WORKSPACE%\\backend-err.log' -Tail 100"
                    ) else (
                        echo backend-err.log not found.
                    )
                '''
            }
        }


        // ============================================================
        // 7. BACKEND HEALTH CHECK
        // ============================================================

        stage('Backend Health Check') {

            steps {

                bat '''
                    @echo off

                    echo ==========================================
                    echo BACKEND HEALTH CHECK
                    echo ==========================================

                    echo Backend:
                    echo http://localhost:8080

                    echo.
                    echo API:
                    echo http://localhost:8080/api/categories

                    set RETRIES=30

                    :CHECK_BACKEND

                    echo.
                    echo Attempts remaining: %RETRIES%

                    curl -s -o nul -w "HTTP Status: %%{http_code}" "http://localhost:8080/api/categories"

                    echo.

                    netstat -ano | findstr LISTENING | findstr ":8080" >nul

                    if not errorlevel 1 (

                        echo.
                        echo ==========================================
                        echo BACKEND IS RUNNING
                        echo ==========================================

                        echo Port 8080 is listening.

                        exit /b 0
                    )

                    echo.
                    echo Backend is not ready yet.

                    set /a RETRIES-=1

                    if %RETRIES% LEQ 0 (

                        echo.
                        echo ==========================================
                        echo BACKEND FAILED TO START
                        echo ==========================================

                        echo.
                        echo PORT STATUS:

                        netstat -ano | findstr ":8080"

                        echo.
                        echo ==========================================
                        echo BACKEND LOG
                        echo ==========================================

                        if exist "%WORKSPACE%\\backend.log" (
                            type "%WORKSPACE%\\backend.log"
                        ) else (
                            echo backend.log not found.
                        )

                        echo.
                        echo ==========================================
                        echo BACKEND ERROR LOG
                        echo ==========================================

                        if exist "%WORKSPACE%\\backend-err.log" (
                            type "%WORKSPACE%\\backend-err.log"
                        ) else (
                            echo backend-err.log not found.
                        )

                        exit /b 1
                    )

                    echo Waiting 2 seconds...

                    ping -n 3 127.0.0.1 >nul

                    goto CHECK_BACKEND
                '''
            }
        }


        // ============================================================
        // 8. CHECK APPZILLON UI
        // ============================================================

        stage('Check Appzillon UI') {

            steps {

                bat '''
                    @echo off

                    echo ==========================================
                    echo CHECKING APPZILLON UI
                    echo ==========================================

                    echo.
                    echo Appzillon URL:
                    echo http://localhost:8090/quizapp/

                    echo.
                    echo Checking port 8090...

                    netstat -ano | findstr LISTENING | findstr ":8090"

                    if errorlevel 1 (

                        echo.
                        echo ==========================================
                        echo ERROR: APPZILLON IS NOT RUNNING
                        echo ==========================================

                        echo Port 8090 is not listening.

                        echo.
                        echo Start your Appzillon application on:

                        echo http://localhost:8090/quizapp/

                        exit /b 1
                    )

                    echo.
                    echo Port 8090 is listening.

                    echo.
                    echo Testing Appzillon URL...

                    curl -I -s "http://localhost:8090/quizapp/"

                    if errorlevel 1 (

                        echo.
                        echo ERROR: Appzillon UI could not be reached.
                        exit /b 1
                    )

                    echo.
                    echo ==========================================
                    echo APPZILLON UI IS AVAILABLE
                    echo ==========================================
                '''
            }
        }


        // ============================================================
        // 9. INSTALL PLAYWRIGHT CHROMIUM
        // ============================================================

        stage('Install Playwright Browser') {

            steps {

                bat '''
                    @echo off

                    echo ==========================================
                    echo INSTALLING PLAYWRIGHT CHROMIUM
                    echo ==========================================

                    set "JAVA_HOME=C:\\Program Files\\Java\\jdk-17.0.2"
                    set "PATH=%JAVA_HOME%\\bin;%PATH%"

                    call mvn -B -Dexec.classpathScope=test -Dexec.mainClass=com.microsoft.playwright.CLI -Dexec.args="install chromium" org.codehaus.mojo:exec-maven-plugin:3.5.0:java

                    if errorlevel 1 (
                        echo.
                        echo ERROR: Playwright Chromium installation failed.
                        exit /b 1
                    )

                    echo.
                    echo Playwright Chromium installed successfully.
                '''
            }
        }


        // ============================================================
        // 10. RUN PLAYWRIGHT TEST
        // ============================================================

        stage('Run Playwright Tests') {

            steps {

                bat '''
                    @echo off

                    echo ==========================================
                    echo RUNNING PLAYWRIGHT TESTS
                    echo ==========================================

                    set "JAVA_HOME=C:\\Program Files\\Java\\jdk-17.0.2"
                    set "PATH=%JAVA_HOME%\\bin;%PATH%"

                    echo.
                    echo Appzillon UI:
                    echo http://localhost:8111/quizapp/

                    echo.
                    echo Backend:
                    echo http://localhost:8090

                    echo.
                    echo Running Maven tests...

                    call mvn -B test

                    if errorlevel 1 (
                        echo.
                        echo ==========================================
                        echo PLAYWRIGHT TEST FAILED
                        echo ==========================================
                        exit /b 1
                    )

                    echo.
                    echo ==========================================
                    echo PLAYWRIGHT TEST PASSED
                    echo ==========================================
                '''
            }
        }


        // ============================================================
        // 11. ARCHIVE JAR
        // ============================================================

        stage('Archive JAR') {

            steps {

                archiveArtifacts artifacts: 'target/*.jar',
                    fingerprint: true
            }
        }
    }


    // ================================================================
    // POST ACTIONS
    // ================================================================

    post {

        success {

            echo '=========================================='
            echo 'QUIZAPP PIPELINE SUCCESSFUL'
            echo '=========================================='

            echo 'Backend:'
            echo 'http://localhost:8080'

            echo 'Quiz API:'
            echo 'http://localhost:8080/api/categories'

            echo 'Appzillon UI:'
            echo 'http://localhost:8090/quizapp/'

            echo '=========================================='
        }

        failure {

            echo '=========================================='
            echo 'QUIZAPP PIPELINE FAILED'
            echo '=========================================='

            echo 'Check the failed Jenkins stage.'
            echo 'Check backend.log.'
            echo 'Check backend-err.log.'
            echo 'Check Playwright test output.'

            echo '=========================================='
        }

        always {

            echo '=========================================='
            echo 'JENKINS PIPELINE COMPLETED'
            echo '=========================================='
        }
    }
}

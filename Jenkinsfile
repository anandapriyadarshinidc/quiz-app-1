pipeline {

    agent any

    environment {
        JAVA_HOME = 'C:\\Program Files\\Java\\jdk-21.0.11'
        MAVEN_HOME = 'D:\\apache-maven-3.8.5'

        BACKEND_PORT = '8080'
        BACKEND_URL = 'http://localhost:8080/api/user/getQuizzes'

        PROJECT_DIR = 'project'
        JAR_FILE = 'target\\quiz-bg-1.0.0.jar'
    }

    stages {

        stage('Checkout') {
            steps {
                echo '=========================================='
                echo 'CHECKING OUT PROJECT'
                echo '=========================================='

                checkout scm

                bat '''
                    echo Current directory:
                    cd

                    echo.
                    echo Project files:
                    dir
                '''
            }
        }

        stage('Build Backend Jar') {
            steps {
                echo '=========================================='
                echo 'BUILDING SPRING BOOT BACKEND'
                echo '=========================================='

                bat '''
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
                    echo Moving to backend project...
                    cd /d "%WORKSPACE%\\project"

                    echo.
                    echo Backend project directory:
                    cd

                    echo.
                    echo Building application...

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
                    echo Generated JAR files:
                    dir target\\*.jar
                '''
            }
        }

        stage('Stop Old Backend') {
            steps {
                echo '=========================================='
                echo 'STOPPING OLD BACKEND ON PORT 8080'
                echo '=========================================='

                bat '''
                    powershell -NoProfile -ExecutionPolicy Bypass -Command ^
                    "$connections = Get-NetTCPConnection -LocalPort 8080 -State Listen -ErrorAction SilentlyContinue; ^
                    if ($connections) { ^
                        foreach ($connection in $connections) { ^
                            Write-Host ('Stopping PID ' + $connection.OwningProcess); ^
                            Stop-Process -Id $connection.OwningProcess -Force -ErrorAction SilentlyContinue ^
                        } ^
                    } else { ^
                        Write-Host 'No application is currently using port 8080.' ^
                    }"
                '''

                echo 'Old backend process handled.'
            }
        }

        stage('Deploy Backend') {
            steps {
                echo '=========================================='
                echo 'STARTING SPRING BOOT BACKEND'
                echo '=========================================='

                bat '''
                    set "PATH=%JAVA_HOME%\\bin;%PATH%"

                    cd /d "%WORKSPACE%\\project"

                    if not exist "%JAR_FILE%" (
                        echo.
                        echo ==========================================
                        echo JAR FILE NOT FOUND
                        echo ==========================================
                        echo Expected:
                        echo %WORKSPACE%\\project\\%JAR_FILE%
                        exit /b 1
                    )

                    echo.
                    echo Starting:
                    echo %JAR_FILE%

                    echo.
                    echo Backend will run on:
                    echo http://localhost:8080

                    echo.
                    echo Starting application...

                    start "QuizBackend" /MIN cmd /c "java -jar %JAR_FILE% > backend.log 2>&1"

                    echo.
                    echo Backend process started.

                    timeout /t 5 /nobreak >nul

                    echo.
                    echo Checking backend log...

                    if exist backend.log (
                        type backend.log
                    )
                '''
            }
        }

        stage('Backend Health Check') {
            steps {
                echo '=========================================='
                echo 'CHECKING BACKEND'
                echo '=========================================='

                bat '''
                    powershell -NoProfile -ExecutionPolicy Bypass -Command ^
                    "$url = 'http://localhost:8080/api/user/getQuizzes'; ^
                    $maxRetries = 20; ^
                    $success = $false; ^
                    Write-Host ('Health check URL: ' + $url); ^
                    for ($i = 1; $i -le $maxRetries; $i++) { ^
                        try { ^
                            $response = Invoke-WebRequest -Uri $url -UseBasicParsing -TimeoutSec 5 -ErrorAction Stop; ^
                            Write-Host ('Attempt ' + $i + ': HTTP ' + $response.StatusCode); ^
                            if ($response.StatusCode -ge 200 -and $response.StatusCode -lt 500) { ^
                                Write-Host 'Backend is responding.'; ^
                                $success = $true; ^
                                break ^
                            } ^
                        } catch { ^
                            Write-Host ('Attempt ' + $i + ': Backend not ready.'); ^
                        } ^
                        Start-Sleep -Seconds 3 ^
                    } ^
                    if (-not $success) { ^
                        Write-Host ''; ^
                        Write-Host '==========================================' ; ^
                        Write-Host 'BACKEND HEALTH CHECK FAILED' ; ^
                        Write-Host '==========================================' ; ^
                        exit 1 ^
                    }"

                    if errorlevel 1 (
                        echo.
                        echo ==========================================
                        echo BACKEND HEALTH CHECK FAILED
                        echo ==========================================
                        echo.
                        echo Backend log:
                        if exist "%WORKSPACE%\\project\\backend.log" (
                            type "%WORKSPACE%\\project\\backend.log"
                        )
                        exit /b 1
                    )

                    echo.
                    echo ==========================================
                    echo BACKEND HEALTH CHECK PASSED
                    echo ==========================================
                    echo Backend is running on:
                    echo http://localhost:8080
                '''
            }
        }

        stage('Deploy Appzillon') {
            steps {
                echo '=========================================='
                echo 'DEPLOYING APPZILLON APPLICATION'
                echo '=========================================='

                /*
                 * Put your existing Appzillon deployment commands here.
                 *
                 * Example:
                 *
                 * bat '''
                 *     call deploy.bat
                 * '''
                 */

                echo 'Appzillon deployment stage reached successfully.'
            }
        }

        stage('Appzillon Health Check') {
            steps {
                echo '=========================================='
                echo 'APPZILLON HEALTH CHECK'
                echo '=========================================='

                /*
                 * Put your existing Appzillon health-check URL here.
                 */

                echo 'Appzillon health check stage completed.'
            }
        }
    }

    post {

        success {
            echo '''
==========================================
PIPELINE SUCCESS
==========================================

Backend:
http://localhost:8080

API:
http://localhost:8080/api/user/getQuizzes

==========================================
'''
        }

        failure {
            echo '''
==========================================
PIPELINE FAILED
==========================================

Check:
1. Maven build
2. backend.log
3. Port 8080
4. Spring Boot configuration
5. Backend API URL

==========================================
'''
        }

        always {
            echo 'Pipeline execution completed.'
        }
    }
}
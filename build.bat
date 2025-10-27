@echo off
echo Building Smart Document Scanner SDK...
echo.

echo Building SDK module...
call gradlew :smart-document-scanner-sdk:assembleDebug
if %errorlevel% neq 0 (
    echo SDK build failed!
    pause
    exit /b 1
)

echo.
echo Building Demo App...
call gradlew :sdk-demo-app:assembleDebug
if %errorlevel% neq 0 (
    echo Demo app build failed!
    pause
    exit /b 1
)

echo.
echo Build completed successfully!
echo SDK AAR: smart-document-scanner-sdk\build\outputs\aar\
echo Demo APK: sdk-demo-app\build\outputs\apk\debug\
echo.
pause

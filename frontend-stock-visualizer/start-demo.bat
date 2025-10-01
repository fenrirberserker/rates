@echo off
echo ========================================
echo    Stock Visualizer Demo Launcher
echo ========================================
echo.
echo Starting backend server in demo mode...
echo This will generate random stock data (1-100) every second
echo.

cd server
start "Backend Server" cmd /k "npm start"
echo Backend server started!
echo.

timeout /t 3 /nobreak >nul

echo Starting React frontend...
cd ..
start "React Frontend" cmd /k "npm start"
echo.

echo ========================================
echo Demo is starting!
echo ========================================
echo Backend: http://localhost:3001
echo Frontend: http://localhost:3000
echo Health Check: http://localhost:3001/health
echo.
echo Both windows will open automatically.
echo Wait a few seconds for everything to load.
echo ========================================
pause
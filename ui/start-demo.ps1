# Stock Visualizer Demo Launcher
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "    Stock Visualizer Demo Launcher" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

Write-Host "Starting backend server in demo mode..." -ForegroundColor Yellow
Write-Host "This will generate random stock data (1-100) every second" -ForegroundColor Green
Write-Host ""

# Check if server dependencies are installed
if (!(Test-Path "server\node_modules")) {
    Write-Host "Installing server dependencies..." -ForegroundColor Yellow
    Set-Location server
    npm install
    Set-Location ..
}

# Check if frontend dependencies are installed
if (!(Test-Path "node_modules")) {
    Write-Host "Installing frontend dependencies..." -ForegroundColor Yellow
    npm install
}

# Start backend server
Write-Host "Starting backend server..." -ForegroundColor Yellow
Start-Process -FilePath "powershell.exe" -ArgumentList "-NoExit", "-Command", "cd '$PWD\server'; npm start" -WindowStyle Normal

Start-Sleep -Seconds 3

# Start frontend
Write-Host "Starting React frontend..." -ForegroundColor Yellow
Start-Process -FilePath "powershell.exe" -ArgumentList "-NoExit", "-Command", "cd '$PWD'; npm start" -WindowStyle Normal

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Demo is starting!" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Backend: http://localhost:3001" -ForegroundColor White
Write-Host "Frontend: http://localhost:3000" -ForegroundColor White
Write-Host "Health Check: http://localhost:3001/health" -ForegroundColor White
Write-Host ""
Write-Host "Both windows will open automatically." -ForegroundColor Yellow
Write-Host "Wait a few seconds for everything to load." -ForegroundColor Yellow
Write-Host "========================================" -ForegroundColor Cyan

# Open browser after a delay
Start-Sleep -Seconds 5
Write-Host "Opening browser..." -ForegroundColor Green
Start-Process "http://localhost:3000"

Read-Host "Press Enter to continue..."
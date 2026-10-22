@echo off
REM ========================================
REM Script de lancement du projet PFA
REM Dashboard Admin Edition
REM ========================================

echo.
echo =========================================
echo    PFA - Projet Plateforme de Candidatures
echo    Dashboard Admin - Lancement
echo =========================================
echo.

REM Déterminer le chemin du projet
set PROJECT_PATH=%~dp0
cd /d "%PROJECT_PATH%"

echo [INFO] Chemin du projet: %PROJECT_PATH%
echo.

REM Menu de sélection
echo Que voulez-vous faire?
echo.
echo 1 - Compiler le backend
echo 2 - Compiler le frontend
echo 3 - Lancer le backend
echo 4 - Lancer le frontend
echo 5 - Compiler ET lancer les deux
echo 6 - Vérifier l'installation
echo 7 - Accéder au dashboard (navigateur)
echo 8 - Quitter
echo.

set /p choice="Sélectionnez une option (1-8): "

if "%choice%"=="1" goto compile_backend
if "%choice%"=="2" goto compile_frontend
if "%choice%"=="3" goto run_backend
if "%choice%"=="4" goto run_frontend
if "%choice%"=="5" goto compile_and_run
if "%choice%"=="6" goto check_install
if "%choice%"=="7" goto open_dashboard
if "%choice%"=="8" goto end

echo Choix invalide!
goto end

:compile_backend
echo.
echo [BACKEND] Compilation en cours...
cd "%PROJECT_PATH%backend"
start cmd /k ".\mvnw clean compile -q && echo [OK] Backend compile avec succes! && pause"
goto end

:compile_frontend
echo.
echo [FRONTEND] Compilation en cours...
cd "%PROJECT_PATH%frontend"
start cmd /k "npm run build && echo [OK] Frontend compile avec succes! && pause"
goto end

:run_backend
echo.
echo [BACKEND] Demarrage du serveur...
cd "%PROJECT_PATH%backend"
start cmd /k "echo Demarrage du backend Spring Boot... && .\mvnw spring-boot:run"
echo [INFO] Backend should start on http://localhost:8080
timeout /t 3
goto end

:run_frontend
echo.
echo [FRONTEND] Demarrage du serveur Angular...
cd "%PROJECT_PATH%frontend"
set FullPath=%cd%\node_modules\.bin\ng.cmd
start cmd /k "echo Demarrage du frontend Angular... && "%FullPath%" serve"
echo [INFO] Frontend should start on http://localhost:4200
timeout /t 2
goto end

:compile_and_run
echo.
echo [BACKEND] Compilation en cours...
cd "%PROJECT_PATH%backend"
call .\mvnw clean compile -q
if %errorlevel% neq 0 (
    echo [ERREUR] Compilation backend echouee!
    pause
    goto end
)
echo [OK] Backend compile!

echo.
echo [FRONTEND] Compilation en cours...
cd "%PROJECT_PATH%frontend"
call npm run build
if %errorlevel% neq 0 (
    echo [ERREUR] Compilation frontend echouee!
    pause
    goto end
)
echo [OK] Frontend compile!

echo.
echo =========================================
echo    Les deux sont prets a etre lances!
echo =========================================
echo.
echo Demarrage du backend en premier...
cd "%PROJECT_PATH%backend"
start cmd /k ".\mvnw spring-boot:run"

timeout /t 5

echo.
echo Demarrage du frontend...
cd "%PROJECT_PATH%frontend"
set FullPath=%cd%\node_modules\.bin\ng.cmd
start cmd /k ""%FullPath%" serve"

echo.
echo =========================================
echo    Servers demarres!
echo =========================================
echo Backend: http://localhost:8080
echo Frontend: http://localhost:4200
echo Dashboard: http://localhost:4200/admin/dashboard
echo.
echo [INFO] En attente de fin des serveurs...
pause

goto end

:check_install
echo.
echo [INFO] Verification de l'installation...
echo.

echo [1] Verification Java...
java -version 2>&1 | findstr /i "version" && echo [OK] Java installe! || echo [ERREUR] Java non installe!

echo.
echo [2] Verification Maven...
cd "%PROJECT_PATH%backend"
.\mvnw -version 2>&1 | findstr /i "Maven" && echo [OK] Maven OK! || echo [ERREUR] Maven non fonctionnel!

echo.
echo [3] Verification Node.js...
node --version && echo [OK] Node.js installe! || echo [ERREUR] Node.js non installe!

echo.
echo [4] Verification npm...
npm --version && echo [OK] npm installe! || echo [ERREUR] npm non installe!

echo.
echo [5] Verification Angular CLI...
cd "%PROJECT_PATH%frontend"
if exist "node_modules\.bin\ng.cmd" (
    echo [OK] Angular CLI installe!
) else (
    echo [ERREUR] Angular CLI non installe!
    echo Installation necessaire: npm install dans le dossier frontend
)

echo.
echo [6] Verification de Port 8080 (Backend)...
netstat -ano | findstr :8080 && echo [ATTENTION] Port 8080 est deja utilise! || echo [OK] Port 8080 disponible!

echo.
echo [7] Verification de Port 4200 (Frontend)...
netstat -ano | findstr :4200 && echo [ATTENTION] Port 4200 est deja utilise! || echo [OK] Port 4200 disponible!

echo.
echo =========================================
echo    Verification terminee!
echo =========================================
pause
goto end

:open_dashboard
echo.
echo [INFO] Ouverture du dashboard...
echo.
echo Instruction: Assurez-vous que:
echo - Backend tourne sur http://localhost:8080
echo - Frontend tourne sur http://localhost:4200
echo - Vous etes connecte en tant qu'ADMIN
echo.
start http://localhost:4200/admin/dashboard
echo Dashboard ouvert dans le navigateur!
goto end

:end
echo.
echo [INFO] Fin du script
exit /b 0

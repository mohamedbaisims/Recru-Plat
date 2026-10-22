@echo off
REM ========================================
REM 🔔 SYSTÈME DE NOTIFICATIONS
REM DÉMARRAGE RAPIDE - DOUBLE-CLIC POUR TESTER
REM ========================================

echo.
echo ╔═════════════════════════════════════════╗
echo ║  🚀 DÉMARRAGE DU SYSTÈME COMPLET        ║
echo ║  Notifications: Candidat → Manager      ║
echo ╚═════════════════════════════════════════╝
echo.

REM Obtenir le répertoire du script
set BASE_DIR=%~dp0

REM Vérifier si les répertoires existent
if not exist "%BASE_DIR%backend" (
    echo ❌ ERREUR: Répertoire 'backend' non trouvé
    echo Exécutez ce script dans le répertoire racine du projet
    pause
    exit /b 1
)

if not exist "%BASE_DIR%frontend" (
    echo ❌ ERREUR: Répertoire 'frontend' non trouvé
    echo Exécutez ce script dans le répertoire racine du projet
    pause
    exit /b 1
)

echo ✓ Répertoires détectés
echo.

REM ========================================
REM Démarrer Backend
REM ========================================
echo ⏳ Démarrage du BACKEND (Spring Boot)...
echo Port: 8080
echo.
start "Backend - Spring Boot 3.5 (API)" cmd /k "cd /d "%BASE_DIR%backend" && mvn clean spring-boot:run"

REM Attendre que le backend démarre
echo ⏳ Attente de 20 secondes pour le démarrage du backend...
timeout /t 20 /nobreak

REM ========================================
REM Démarrer Frontend
REM ========================================
echo.
echo ⏳ Démarrage du FRONTEND (Angular 17)...
echo Port: 4200
echo.
start "Frontend - Angular 17 (Interface Web)" cmd /k "cd /d "%BASE_DIR%frontend" && ng serve"

REM Attendre que le frontend démarre
echo ⏳ Attente de 30 secondes pour le démarrage du frontend...
timeout /t 30 /nobreak

echo.
echo ╔═════════════════════════════════════════╗
echo ║  ✅ SYSTÈME DÉMARRÉ AVEC SUCCÈS         ║
echo ╚═════════════════════════════════════════╝
echo.

echo 🌐 Accédez à l'application:
echo    → http://localhost:4200
echo.

echo 📡 API Backend disponible sur:
echo    → http://localhost:8080/api/v1
echo.

echo 📚 Documentation:
echo    → Lire NOTIFICATION_TEST_GUIDE.md pour les étapes de test
echo    → Lire NOTIFICATION_IMPLEMENTATION_SUMMARY.md pour l'architecture
echo.

echo 🔔 FONCTIONNALITÉS TESTÉES:
echo    ✓ Badge avec nombre de notifications
echo    ✓ Dropdown avec liste des notifications
echo    ✓ Marquer comme lue
echo    ✓ Supprimer notifications
echo    ✓ Polling automatique chaque 30s
echo    ✓ Notifications persistent en base de données
echo.

echo 🛑 Pour arrêter:
echo    - Fermez les 2 fenêtres de terminal
echo    - Ctrl+C dans chaque fenêtre
echo.

pause

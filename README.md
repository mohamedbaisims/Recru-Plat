# PFA — Plateforme de gestion des candidatures

Application web de recrutement développée dans le cadre d'un Projet de Fin d'Année (PFA).
Les candidats déposent leur candidature (CV, lettre de motivation), le système analyse le CV
pour suggérer un domaine métier, et les managers / administrateurs traitent les dossiers.

## Fonctionnalités

- **Authentification sécurisée** : JWT (access + refresh token), double authentification TOTP, réinitialisation du mot de passe par e-mail
- **Rôles** : `USER` (candidat), `MANAGER`, `ADMIN` avec permissions fines
- **Candidatures** : formulaire avec upload de fichiers (PDF), modification, suppression, suivi par le candidat
- **Analyse de CV** : extraction du texte des PDF (PDFBox) et suggestion automatique du domaine métier par correspondance de mots-clés
- **Espace manager** : candidatures filtrées par domaine, affectation à un domaine, rejet, planification d'entretien (notification e-mail au candidat)
- **Espace admin** : tableau de bord statistique, gestion des managers et des domaines métier
- **Notifications** in-app pour managers et candidats (lu / non lu, compteur)
- **Documentation API** Swagger / OpenAPI

## Stack technique

| Couche | Technologies |
|---|---|
| Backend | Java 17, Spring Boot 3.5 (Web, Data JPA, Security, Validation, Mail), JJWT, Lombok, PDFBox, springdoc-openapi |
| Base de données | MySQL |
| Frontend | Angular 17, Angular Material, Bootstrap 5, ng-select, lucide-angular, jwt-decode |

## Structure du projet

```
PFA/
├── backend/                 # API REST Spring Boot
│   └── src/main/java/com/example/backend/
│       ├── ai/              # Analyse de CV et suggestion de domaine
│       ├── appForm/         # Candidatures (CRUD, fichiers, statuts)
│       ├── auth/ reset/     # Inscription, connexion, 2FA, mot de passe oublié
│       ├── config/          # Sécurité, filtre JWT, OpenAPI
│       ├── dashboard/       # Statistiques admin
│       ├── manager/         # Gestion des managers
│       ├── notification/    # Notifications
│       ├── workdomain/      # Domaines métier
│       └── user/ token/     # Utilisateurs, rôles, tokens
├── frontend/                # Application Angular
│   └── src/app/
│       ├── components/      # Navbar, sidebar
│       ├── pages/           # Login, register, dashboards, formulaires…
│       └── serives/         # Services HTTP et auth
└── *.md                     # Documentation détaillée des modules
```

## Prérequis

- Java 17+
- Node.js 18+ et Angular CLI 17 (`npm install -g @angular/cli@17`)
- MySQL 8 avec une base `pfa`

## Installation et lancement

### 1. Base de données

```sql
CREATE DATABASE pfa;
```

Les tables sont créées automatiquement par Hibernate (`ddl-auto=update`).

### 2. Backend

Les secrets ne sont pas versionnés. Créez le fichier de configuration locale :

```bash
cd backend/src/main/resources
cp application-local.properties.example application-local.properties
```

Renseignez la clé JWT et les identifiants Gmail (mot de passe d'application), puis :

```bash
cd backend
./mvnw spring-boot:run
```

API disponible sur http://localhost:8080 — Swagger UI : http://localhost:8080/swagger-ui/index.html

### 3. Frontend

```bash
cd frontend
npm install
ng serve
```

Application disponible sur http://localhost:4200

> Sous Windows, `START_SERVERS.bat` lance les deux serveurs.

## Principaux endpoints

| Préfixe | Description |
|---|---|
| `/api/v1/auth` | register, authenticate, refresh-token, verify, forgot/reset-password |
| `/api/v1/applications` | candidatures, fichiers, affectation, rejet, entretien |
| `/api/ai/analyser` | analyse d'un CV |
| `/api/v1/work-domains` | domaines métier |
| `/api/v1/manager` | gestion des managers |
| `/api/v1/admin/dashboard/statistics` | statistiques |
| `/api/v1/notifications` | notifications |

## Documentation complémentaire

- [Dashboard admin](ADMIN_DASHBOARD_DOCUMENTATION.md)
- [Système de notifications](NOTIFICATIONS_README.md)
- [Actions manager](MANAGER_ACTIONS_SYSTEM.md)
- [Résumé exécutif](RESUME_EXECUTIF.md)

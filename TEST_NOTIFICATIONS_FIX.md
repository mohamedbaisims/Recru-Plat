# 🧪 TEST DES NOTIFICATIONS - APRÈS CORRECTION

## 📋 Checklist de Vérification

### Step 1: Vérifier la Compilation ✅
```bash
cd backend
mvn clean compile -q
# Résultat attendu: Pas d'erreur (sortie vide)
```

### Step 2: Builder et Démarrer le Backend
```bash
# Terminal 1: Backend
cd c:\Users\asus\Desktop\PFA\backend
mvn clean spring-boot:run
# Attendre: "Tomcat started on port(s): 8080"
```

### Step 3: Démarrer le Frontend
```bash
# Terminal 2: Frontend
cd c:\Users\asus\Desktop\PFA\frontend
ng serve
# Attendre: "Angular Live Development Server is listening on localhost:4200"
```

### Step 4: Test Manual Flow

#### 4.1 Login comme Candidate (CANDIDAT)
1. Aller sur http://localhost:4200
2. Login avec email: `candidat@test.com` / password: test123
3. Vérifier que vous êtes authentifié

#### 4.2 Submit une Nouvelle Candidature
1. Cliquer sur "Application Form" ou "Soumettre Candidature"
2. Remplir le formulaire:
   - Full Name: "Jean Dupont"
   - Email: "jean@test.com"
   - Education: "Master"
   - Experience: "5 ans"
   - Etc.
3. Uploader 3 fichiers PDF:
   - CV
   - Motivation Letter
   - Diploma
4. Cliquer **"Submit"** / **"Soumettre"**

**Points de contrôle:**
- ✅ Pas d'erreur 403 Forbidden
- ✅ Pas d'erreur "Cannot read properties of undefined"
- ✅ Formulaire accepte la soumission
- ✅ Backend montre: "[INFO] New application submitted by Jean Dupont"

#### 4.3 Login comme Manager
1. Dans un **NOUVEL ONGLET** (pas de logout)
2. Aller sur http://localhost:4200
3. Effacer le localStorage: `localStorage.clear()` dans console
4. Login avec email: `manager@test.com` / password: manager123
5. Attendre 2-3 secondes

**Points de contrôle:**
- ✅ Badge 🔔 montre un chiffre (ex: "1", "2", etc.)
- ✅ Pas d'erreur 403 Forbidden
- ✅ Badge pulsates (animation)
- ✅ Notifications apparaissent dans le dropdown

#### 4.4 Vérifier la Notification
1. Cliquer sur la cloche 🔔
2. Voir la notification: **"Nouvelle candidature soumise"**
3. Contenu: `Jean Dupont (jean@test.com) a soumis une nouvelle candidature`

**Points de contrôle:**
- ✅ Notification visible
- ✅ Titre et message corrects
- ✅ Email du candidat affiché
- ✅ Pas d'erreurs 403

#### 4.5 Tester les Actions
1. **Mark as read** 
   - Cliquer ✓ sur la notification
   - Badge diminue de 1
   - Notification devient grise (read state)
   
2. **Delete**
   - Cliquer ✕ sur une notification
   - Notification disparaît
   - Badge diminue

3. **Mark all as read**
   - Cliquer "Marquer tout comme lu"
   - Toutes les notifications deviennent grises
   - Badge devient "0"

---

## 🔍 Monitoring Backend

### Logs à Chercher

1. **Lors de la soumission:**
```
✅ Notification créée pour tous les managers
```

2. **Lors de la récupération:**
```
Récupération des notifications pour le manager: ...
Count: 1 (ou le nombre de notifications)
```

3. **Erreurs:**
```
❌ Erreur lors de la création de la notification
OR
HTTP 403 Forbidden
OR
Cannot read properties of undefined
```

---

## 📊 Expected Database State

### Après une soumission:

```sql
-- Vérifier dans la DB
SELECT * FROM notification WHERE canditat_email = 'jean@test.com';

-- Résultat attendu:
id  | manager_id | title | message | canditat_name | canditat_email | type | is_read | created_at
1   | (manager)  | "Nouvelle candidature soumise" | "Jean Dupont ..." | "Jean Dupont" | "jean@test.com" | NEW_APPLICATION | false | 2026-04-10T14:30:00
```

---

## 🚨 Troubleshooting

### Problème: Badge ne montre pas le count
**Solution:**
1. Check console (F12): Chercher 403 errors
2. Vérifier que le token JWT est dans localStorage
3. Logout et re-login
4. Vérifier que `Authorization` header est envoyé

### Problème: Notification ne crée pas
**Solution:**
1. Vérifier les logs backend
2. Check si `notifyManagersOfNewApplication()` est appelé
3. Vérifier s'il y a des MANAGER users dans la DB
4. Check NotificationRepository.findByManagerOrderByCreatedAtDesc()

### Problème: HTTP 403
**Solution:**
1. Check si l'utilisateur est MANAGER
2. Vérifier le token JWT dans localStorage
3. Check `@PreAuthorize("hasRole('MANAGER')")` sur les endpoints

---

## ✅ Success Criteria

Tous les critères DOIVENT être met:

- [ ] Backend compile sans erreurs
- [ ] Frontend compile sans erreurs
- [ ] Candidat peut soumettre une candidature sans erreur 403
- [ ] Pas d'erreur "Cannot read properties of undefined"
- [ ] Manager voit le badge 🔔 avec le bon count
- [ ] Manager voit la notification dans le dropdown
- [ ] Notification contient le nom du candidat
- [ ] Actions (mark read, delete) fonctionnent
- [ ] Base de données crée l'enregistrement notification
- [ ] Badge se met à jour en temps réel (polling 30s)

---

## 📸 Debugging Visuals

### Frontend Inspector:
1. F12 → Network → Filtre "notifications"
2. Voir les appels:
   - `GET /api/v1/notifications/unread-count` (polling)
   - `GET /api/v1/notifications/recent` (on click)
   - `PUT /api/v1/notifications/{id}/read` (action)

### Backend Console:
```
Should see:
✅ Notification créée pour tous les managers
[http-nio-8080-exec-X] [...] Application saved successfully
[http-nio-8080-exec-X] [...] Notifications endpoint called
```

---

**Créé:** April 10, 2026  
**Version:** 1.0  
**Status:** Guide de Test Complet

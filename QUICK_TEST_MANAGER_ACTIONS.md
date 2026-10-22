# ⚡ QUICK START - TESTER LE SYSTÈME

## 🚀 Démarrage Rapide

### Step 1: Build et Lancer les Serveurs
```bash
# Terminal 1: Backend
cd c:\Users\asus\Desktop\PFA\backend
mvn clean install -DskipTests
java -jar target/backend-0.0.1-SNAPSHOT.jar

# Attendre: "Tomcat started on port(s): 8080" ✅
```

```bash
# Terminal 2: Frontend
cd c:\Users\asus\Desktop\PFA\frontend
ng serve --open=false

# Attendre: "Angular Live Development Server is listening..." ✅
```

### Step 2: Ouvrir l'application
```
http://localhost:4200
```

---

## 🧪 Test Immédiat

### Teste 1️⃣: MANAGER REFUSE UNE CANDIDATURE

```
1. Login comme: manager@test.com / manager123
2. Aller sur page: "Candidates by Domain"
3. Sélectionner un domaine (ex: "Java Developer")
4. Table montre les candidatures
5. Cliquer bouton ROUGE "✕ Refuser"

✅ VÉRIFIER:
   - Modal s'ouvre
   - Titre: "Refuser une candidature"
   - Champ texte pour raison
   - Nom du candidat affiché

6. Entrer raison: "Compétences insuffisantes pour ce poste"
7. Cliquer "Confirmer le refus"

✅ VÉRIFIER:
   - Message vert: "Candidature refusée et notifiée" 
   - Modal ferme après 1.5s
   - Table se rafraîchit
```

### Teste 2️⃣: MANAGER PLANIFIE UN ENTRETIEN

```
1. Cliquer bouton BLEU "📅 Entretien"

✅ VÉRIFIER:
   - Modal s'ouvre
   - Titre: "Planifier un entretien"
   - Champs: Date*, Heure*, Type, Lien (optional)

2. Remplir:
   - Date: 2026-04-20
   - Heure: 14:30
   - Type: Technique
   - Lien: https://zoom.us/meeting/example

3. Cliquer "Planifier l'entretien"

✅ VÉRIFIER:
   - Message vert: "Entretien planifié et candidat notifié"
   - Modal ferme après 1.5s
   - Table se rafraîchit
```

### Test 3️⃣: CANDIDAT REÇOIT NOTIFICATION

```
1. Logout du manager (nouveau tab)
2. Login comme candidat: candidat@test.com / test123

✅ VÉRIFIER:
   - Badge 🔔 affiche count (1, 2, etc.)
   - Badge pulsate (animation)

3. Cliquer sur cloche 🔔

✅ VÉRIFIER:
   - Dropdown s'ouvre
   - 2 notifications visibles:
     1. "Candidature refusée - Votre candidature a été refusée..."
     2. "Entretien planifié - Félicitations! Un entretien..."

4. Actions:
   - Cliquer ✓ → Notification become grey (read)
   - Cliquer ✕ → Notification disappears
   - Compte diminue
```

---

## 🔍 Database Verification

```sql
-- 🆕 Vérifier les notifications au candidat
SELECT 
    n.id,
    n.title,
    n.message,
    n.type,
    u.email as recipient_email,
    n.created_at
FROM notification n
LEFT JOIN user u ON n.recipient_user_id = u.id
WHERE n.recipient_user_id IS NOT NULL
ORDER BY n.created_at DESC
LIMIT 5;

-- Résultat attendu: 2 rows (rejet + entretien)
```

---

## 🐛 Dépannage

| Problème | Solution |
|----------|----------|
| 🔴 Button ne montre rien | Rechargement page (F5) |
| 🔴 Modal pas d'animation | Vérifier les imports Angular |
| 🔴 Notification n'apparaît pas | Check logs backend console |
| 🔴 Erreur 403 | Verify JWT token / logout + re-login |

---

## ✅ Checklist de Succès

- [ ] Backend démarre sur port 8080
- [ ] Frontend démarre sur port 4200
- [ ] Manager voit colonne "Actions" dans la table
- [ ] Boutons "✕ Refuser" et "📅 Entretien" visibles
- [ ] Modal "Refuser" s'ouvre et fonctionne
- [ ] Modal "Entretien" s'ouvre et fonctionne
- [ ] Messages de succès s'affichent
- [ ] Candidat reçoit notifications
- [ ] Notifications apparaissent dans dropdown
- [ ] Actions (read/delete) fonctionnent

---

**Estimated Time:** 15 minutes  
**Status:** Ready to Test ✅

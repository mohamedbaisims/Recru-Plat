# 🧪 **GUIDE DE TEST - DASHBOARD ADMIN**

## ⚡ **TEST RAPIDE (5 minutes)**

### **Étape 1: Vérifier la Compilation ✅**

```powershell
# Backend
cd c:\Users\asus\Desktop\PFA\backend
.\mvnw clean compile -q

# Frontend
cd c:\Users\asus\Desktop\PFA\frontend
npm run build --prod
```

**Résultat attendu**: Aucune erreur critique

---

### **Étape 2: Lancer les Serveurs 🚀**

**Terminal 1 - Backend**:
```powershell
cd c:\Users\asus\Desktop\PFA\backend
.\mvnw spring-boot:run
```

**Terminal 2 - Frontend**:
```powershell
cd c:\Users\asus\Desktop\PFA\frontend
$FullPath = "c:\Users\asus\Desktop\PFA\frontend\node_modules\.bin\ng.cmd"
& $FullPath serve
```

**Résultat attendu**:
- Backend: `Started BackendApplication in XX seconds`
- Frontend: `Angular Live Development Server is listening on localhost:4200`

---

### **Étape 3: Accéder à l'Application 🌐**

1. Ouvrez: http://localhost:4200
2. Page de login s'affiche

---

### **Étape 4: Se Connecter en tant qu'ADMIN 👤**

Utilisez les credentials ADMIN:
```
Email: admin@example.com
Password: (votre mot de passe admin)
```

**Résultat attendu**: Redirection vers /welcome

---

### **Étape 5: Accéder au Dashboard 📊**

**Option A - Via Sidebar**:
1. Regardez le menu à gauche
2. Section "Administration"
3. Cliquez sur "📊 Dashboard"

**Option B - URL Directe**:
1. Allez à: http://localhost:4200/admin/dashboard

**Résultat attendu**: 
- Page dashboard se charge
- Spinner de chargement visible
- Statistiques s'affichent après ~1 seconde

---

### **Étape 6: Vérifier les Statistiques 📈**

Vous devriez voir:

```
📊 TABLEAU DE BORD ADMINISTRATEUR
├─ View d'ensemble en temps réel du système
│
├─ 📋 STATISTIQUES PRINCIPALES
│  ├─ Candidatures: [nombre]
│  ├─ Candidats: [nombre]
│  ├─ Managers: [nombre]
│  └─ Domaines: [nombre]
│
├─ ⏳ ÉTAT DES CANDIDATURES
│  ├─ En Attente: [nombre]
│  ├─ Acceptées: [nombre]
│  └─ Rejetées: [nombre]
│
└─ 📊 CANDIDATURES PAR DOMAINE
   ├─ Graphique en barres
   └─ Tableau détaillé
```

---

## ✅ **CHECKLIST DE VÉRIFICATION**

- [ ] Backend compile sans erreur
- [ ] Frontend compile sans erreur
- [ ] Backend démarre sur port 8080
- [ ] Frontend démarre sur port 4200
- [ ] Page login accessible
- [ ] Connexion ADMIN réussie
- [ ] Sidebar affiche menu Administration
- [ ] Lien Dashboard présent
- [ ] Dashboard charge sans erreur
- [ ] Statistiques affichées
- [ ] Cartes statistiques visibles
- [ ] Graphique domaines visible
- [ ] Tableau domaines visible
- [ ] Bouton "Rafraîchir" fonctionne

---

## 🔍 **TESTS DÉTAILLÉS**

### **Test 1: Chargement des Données ✅**

**Action**: Ouvrir le dashboard  
**Résultat attendu**: Les statistiques se chargent en ~1 seconde

**Inspection**:
```
Ouvrir Developer Tools (F12)
→ Network tab
→ Chercher la requête "statistics"
→ Vérifier Status 200 OK
```

---

### **Test 2: Affichage des Cartes 📊**

**Action**: Scroller pour voir tous les éléments  
**Points de vérification**:

- [ ] Carte "Candidatures" affiche un nombre
- [ ] Carte "Candidats" affiche un nombre
- [ ] Carte "Managers" affiche un nombre
- [ ] Carte "Domaines" affiche un nombre
- [ ] Icônes emoji visibles
- [ ] Couleurs différentes par carte

---

### **Test 3: Statut des Candidatures 📋**

**Points de vérification**:

- [ ] Carte "En Attente" affiche un nombre
- [ ] Carte "Acceptées" affiche un nombre
- [ ] Carte "Rejetées" affiche un nombre
- [ ] Couleurs: 🟨 Jaune (En attente), 🟩 Vert (Acceptée), 🟥 Rouge (Rejectée)

---

### **Test 4: Graphique des Domaines 📈**

**Points de vérification**:

- [ ] Entre 0 et N domaines visibles
- [ ] Chaque domaine a une barre
- [ ] Les barres ont des couleurs différentes
- [ ] Le nombre affichée = hauteur barre

**Cas particulier**: Si 0 domaines
- [ ] Message "Aucun domaine créé"

---

### **Test 5: Tableau des Domaines 📋**

**Colonnes visibles**:
- [ ] Domaine (nom)
- [ ] Candidatures (nombre)
- [ ] Pourcentage (avec barre)

**Vérifications**:
- [ ] Nombres cohérents avec le graphique
- [ ] Pourcentages corrects
- [ ] Barre de progression visuelle

---

### **Test 6: Bouton Rafraîchir 🔄**

**Action**: Cliquez le bouton "Rafraîchir les statistiques"

**Résultat attendu**:
- [ ] Loader spinner visible
- [ ] Les données se rafraîchissent
- [ ] Pas d'erreur en console

---

### **Test 7: Responsivité 📱**

**Desktop** (1920px):
- [ ] Layout 4 colonnes pour les cartes
- [ ] Bien espacé
- [ ] Lisible

**Tablette** (768px):
- [ ] Layout adapté
- [ ] Texte lisible
- [ ] Pas de débordement

**Mobile** (375px):
- [ ] Layout 1 colonne
- [ ] Pas de débordement horizontal
- [ ] Boutons cliquables
- [ ] Texte lisible

---

### **Test 8: Erreurs & Exceptions 🚨**

**Ouvrir Console (F12)**:
- [ ] Aucune erreur JavaScript (en rouge)
- [ ] Possibles warnings (en jaune) - OK
- [ ] Pas d'erreur réseau 4xx/5xx

---

### **Test 9: Sécurité 🔐**

**Action**: Se déconnecter et rafraîchir

**Résultat attendu**:
- [ ] Redirection vers login
- [ ] Pas d'accès au dashboard
- [ ] Pas d'erreur 401/403

---

### **Test 10: Performance ⚡**

**Vérifier l'heure d'accès**:
- Page charge en < 3 secondes
- Données affichées en < 1 seconde
- Bouton rafraîchir répond rapidement

**Vérifier Network**:
- [ ] Requête API < 500ms
- [ ] Taille < 10KB

---

## 🐛 **DÉPANNAGE**

### **Problème: Dashboard ne charge pas**

**Solution**:
1. Vérifier que backend est en cours d'exécution
2. Vérifier la console (F12) pour les erreurs
3. Vérifier que le token est valide
4. Essayer de rafraîchir (F5)

### **Problème: Données vides**

**Solutions**:
1. Vérifier qu'il y a des données en BD
2. Vérifier que les requêtes API réussissent (Network tab)
3. Consulter les logs backend

### **Problème: Erreur 401/403**

**Solution**:
1. Se reconnecter
2. Vérifier que c'est un compte ADMIN
3. Vérifier le token JWT

### **Problème: Style mal appliqué**

**Solution**:
1. Effacer le cache: Ctrl+Shift+R
2. Vérifier que Bootstrap est chargé
3. Vérifier la console pour les erreurs CSS

---

## 📊 **RÉSULTATS ATTENDUS PAR DB STATE**

### **Cas 1: Base vide**
```
Candidatures: 0
Candidats: 0
Managers: X (dépend de config)
Domaines: 0
```

### **Cas 2: Avec données**
```
Candidatures: 5+
Candidats: 2+
Managers: 1+
Domaines: 1+
```

---

## ✨ **TESTS DE BONUS**

### **Test Dark Mode** (futur)
- [ ] UI lisible en dark mode

### **Test Internationalisation** (futur)
- [ ] Textes traduits (EN/FR/ES)

### **Test Export** (futur)
- [ ] Export PDF possible
- [ ] Export CSV possible

---

## 📞 **SUPPORT**

Si vous rencontrez des problèmes:

1. **Vérifier les logs backend**:
   ```bash
   # Dans le terminal backend
   # Chercher les erreurs en rouge
   ```

2. **Vérifier la console frontend** (F12):
   ```javascript
   // Chercher les erreurs
   console.log() calls
   ```

3. **Vérifier Network tab** (F12):
   - Chercher l'appel GET `/api/v1/admin/dashboard/statistics`
   - Vérifier Status 200
   - Vérifier Response JSON

---

**✅ Si tous les tests passent, le dashboard est prêt!**

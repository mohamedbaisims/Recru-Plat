# 🔔 GUIDE DE TEST - SYSTÈME DE NOTIFICATIONS

## 🎯 Objectif
Ce guide teste le système de notifications complet pour les managers quand les candidats soumettent une candidature.

---

## ✅ ÉTAPES DE TEST

### **1️⃣ Démarrer les Serveurs**

#### Backend
```bash
cd c:\Users\asus\Desktop\PFA\backend
mvn spring-boot:run
```
✅ Attendez: `Tomcat started on port(s): 8080`

#### Frontend
```bash
cd c:\Users\asus\Desktop\PFA\frontend
ng serve
```
✅ Attendez: `Application bundle generation complete`

---

### **2️⃣ Se Connecter en tant que MANAGER**

1. Ouvrez `http://localhost:4200`
2. Login avec un compte manager:
   - **Email**: `manager@example.com` (ou tout manager existant)
   - **Password**: `password123`
3. ✅ Attendez d'être redirigé vers la page d'accueil

---

### **3️⃣ Vérifier le Bouton Notifications dans la Navbar**

1. Regardez la **barre de navigation** en haut
2. Vous devez voir une **cloche** 🔔 à côté de vos infos utilisateur
3. **Vérifier**:
   - ✓ La cloche est visible et cliquable
   - ✓ Un badge rouge avec `0` s'affiche (aucune notification pour le moment)

---

### **4️⃣ Se Connecter en tant que CANDIDAT (autre navigateur ou incognito)**

1. **Ouvrir une nouvelle fenêtre de navigateur** (ou mode incognito)
2. Accédez à `http://localhost:4200`
3. Login avec un compte candidat:
   - **Email**: `candidate@example.com` (ou tout candidat existant)
   - **Password**: `password123`
4. Attendez d'être sur la page d'accueil

---

### **5️⃣ Soumettre une Candidature (Candidat)**

1. **Cherchez le lien** "Soumettre une Candidature" dans la navbar ou le menu
2. **Remplissez le formulaire** avec vos infos:
   - Nom complet
   - Email
   - Numéro de téléphone
   - Date de naissance
   - Lieu de naissance
   - Nationalité
   - etc...
3. **Chargez les fichiers**:
   - CV (fichier PDF ou Word)
   - Lettre de motivation
   - Diplôme
4. **Cliquez** "Soumettre"
5. ✅ Vous devez voir un message de confirmation

---

### **6️⃣ Vérifier la Notification (Manager - Autre fenêtre)**

1. **Retournez à la fenêtre manager**
2. **Rafraîchissez le navigateur** ou attendez 30 secondes
3. **Cherchez le badge** 🔔 dans la navbar
4. **Vérifiez**:
   - ✓ Le badge affiche maintenant `1` (1 notification non lue)
   - ✓ La cloche est surlignée/animée

---

### **7️⃣ Ouvrir le Dropdown de Notifications**

1. **Cliquez** sur la cloche 🔔
2. Un **dropdown** doit s'ouvrir avec:
   - ✓ Titre: "Notifications"
   - ✓ Bouton "✓ Marquer tout comme lu"
   - ✓ **Liste avec 1 notification**:
     - **Titre**: "Nouvelle candidature soumise"
     - **Candidat**: Nom du candidat + email
     - **Message**: Description de la candidature
     - **TimeStamp**: Date/heure de soumission

---

### **8️⃣ Marquer comme Lue**

1. **Hover** sur la notification
2. Vous devez voir 2 boutons à droite:
   - ✓ **Checkmark** (✓) pour marquer comme lu
   - ✓ **X** pour supprimer
3. **Cliquez** sur le checkmark ✓
4. **Vérifiez**:
   - ✓ La notification change de style (plus pâle/gris)
   - ✓ Le bouton checkmark disparaît

---

### **9️⃣ Marquer Toutes comme Lues**

1. **Rouvrez** le dropdown 🔔 (s'il s'était fermé)
2. **Cliquez** sur "✓ Marquer tout comme lu"
3. **Vérifiez**:
   - ✓ Le badge 🔔 revient à `0`
   - ✓ La notification apparaît grisée

---

### **🔟 Supprimer une Notification**

1. **Rouvrez** le dropdown 🔔
2. **Hover** sur la notification
3. **Cliquez** sur le **X** rouge
4. **Vérifiez**:
   - ✓ La notification disparaît de la liste
   - ✓ Le message "Pas de notifications" s'affiche

---

## 🧪 TESTS AVANCÉS

### **Test 1: Notifications Multiples**
1. Soumettez **3-4 candidatures** depuis le compte candidat
2. Vérifiez que le badge manager affiche `3-4`
3. Vérifiez que toutes les notifications apparaissent dans le dropdown

### **Test 2: Polling en Temps Réel**
1. Laissez le dropdown **fermé**
2. Soumettez une candidature depuis le compte candidat
3. **Attendez 30 secondes** (polling interval)
4. Vérifiez que le badge se met à jour **automatiquement**

### **Test 3: Persistance après Refresh**
1. Ouvrez les notifications
2. **Rafraîchissez la page** (F5)
3. Vérifiez que les notifications **sont toujours là**

### **Test 4: Plusieurs Managers**
1. **Connectez-vous comme manager 2** (autre compte manager)
2. Vérifiez qu'il reçoit aussi les notifications
3. Marquez comme lue pour manager 1
4. Vérifiez que manager 2 les voit **toujours non lues**

---

## 🐛 DÉPANNAGE

### ❌ Badge n'affiche pas de nombre
- **Solution**: Vérifiez que le token JWT est valide
- Reconnectez-vous

### ❌ Dropdown ne s'ouvre pas
- **Solution**: Vérifiez la console (F12 → Console)
- Cherchez les erreurs CORS ou HTTP

### ❌ Notification apparaît mais reste vide
- **Solution**: Vérifiez que la candidature a un ID
- Vérifiez les logs du backend

### ❌ Notifications ne se mettent pas à jour automatiquement
- **Solution**: Attendez 30 secondes (polling interval)
- Ou cliquez sur la cloche pour forcer le refresh

---

## 📊 RÉSULTATS ATTENDUS

| Test | Résultat | Status |
|------|----------|--------|
| Badge affiche le nombre de notifications | ✓ | ✅ |
| Dropdown s'ouvre/ferme | ✓ | ✅ |
| Notification affiche titre, candidat, message | ✓ | ✅ |
| Marquer comme lu fonctionne | ✓ | ✅ |
| Marquer tous comme lus fonctionne | ✓ | ✅ |
| Supprimer notification fonctionne | ✓ | ✅ |
| Badge se met à jour après 30s | ✓ | ✅ |
| Notifications persistent après refresh | ✓ | ✅ |

---

## 🚀 PROCHAINES ÉTAPES

1. **Son de notification** - Ajouter une alerte sonore
2. **Web Push Notifications** - Notifications système (même navigateur fermé)
3. **Email de notification** - Envoyer email au manager
4. **Filtrer notifications** - Par domaine, par statut, etc.
5. **Archiver notifications** - Au lieu de supprimer

---

**Document créé**: 2024
**Version**: 1.0

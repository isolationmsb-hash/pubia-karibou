# PubIA Karibou — App Android de génération de pubs Facebook IA

App Android Kotlin/Compose qui orchestre plusieurs APIs d'IA (HeyGen, ElevenLabs, Flux, Claude) pour générer des publicités vidéo photoréalistes pour les bougies **Bougie Karibou**, prêtes à publier sur Facebook Ads.

> **Status actuel : Phase 1 — Setup terminé.** Le projet compile, la navigation entre les 8 écrans fonctionne, le thème Karibou est appliqué. Aucun appel API encore.

---

## 🎯 Aperçu du parcours

1. **Accueil** — liste des projets + bouton "Nouvelle pub"
2. **Avatar** — photo de la personne qui apparaît dans la pub
3. **Produit** — photo de la bougie
4. **Script** — texte de ce que dit/fait la personne
5. **Options** — durée (15/30/60s), format (1:1, 9:16, 16:9), voix
6. **Génération** — barre de progression pendant les appels API
7. **Aperçu** — lecture vidéo + télécharger / régénérer / publier
8. **Publication FB** — OAuth Meta + lancement campagne

---

## 🛠️ Installation (premier setup)

### 1. Installer Android Studio

Télécharge **Android Studio Koala (2024.1.x)** ou plus récent :
👉 https://developer.android.com/studio

Pendant l'installation, accepte tous les composants par défaut :
- Android SDK
- Android SDK Platform
- Android Virtual Device

### 2. Ouvrir le projet

1. Lance Android Studio
2. **File → Open...** → sélectionne le dossier `PubIA-Karibou/`
3. Android Studio va automatiquement :
   - Télécharger Gradle 8.9 (~150 Mo)
   - Télécharger les dépendances (~500 Mo : Compose, Hilt, Retrofit, etc.)
   - Indexer le projet
4. Attends que la barre de statut affiche **"Gradle sync finished"**
5. Si une popup demande d'installer le SDK Android 34, accepte

⏱️ **Premier sync** : 5-15 minutes selon la connexion.

### 3. Lancer l'app

#### Option A — Émulateur (le plus simple)
1. **Tools → Device Manager** → **Create Device**
2. Choisis un Pixel 7 / Pixel 8
3. Système : **Android 14 (API 34)**
4. Une fois créé, clique ▶ sur la barre du haut pour lancer

#### Option B — Téléphone Android réel
1. Sur ton Android : **Réglages → À propos du téléphone** → tape 7 fois sur "Numéro de build" pour activer le mode développeur
2. **Réglages → Options pour les développeurs** → active **Débogage USB**
3. Branche le téléphone en USB → autorise l'ordi sur le téléphone
4. Dans Android Studio, ton téléphone apparaît dans la liste des devices
5. Clique ▶

---

## 📂 Structure du projet

```
PubIA-Karibou/
├── app/
│   ├── build.gradle.kts                # Configuration du module app
│   ├── proguard-rules.pro              # Règles ProGuard pour le release
│   └── src/main/
│       ├── AndroidManifest.xml         # Permissions + déclaration MainActivity
│       ├── java/com/karibou/pubia/
│       │   ├── PubIAApplication.kt     # @HiltAndroidApp (racine DI)
│       │   ├── MainActivity.kt         # Activity unique + NavHost Compose
│       │   ├── data/                   # Couche données (Phase 2-3)
│       │   │   ├── remote/             # Services Retrofit
│       │   │   ├── local/              # Room + DataStore
│       │   │   └── repository/         # Repositories
│       │   ├── domain/                 # Logique métier (Phase 2+)
│       │   │   ├── model/              # Entités métier
│       │   │   └── usecase/            # Use cases
│       │   ├── presentation/
│       │   │   ├── navigation/         # NavGraph + Routes
│       │   │   ├── theme/              # Material 3 + couleurs Karibou
│       │   │   ├── ui/screens/         # 8 écrans Compose
│       │   │   └── viewmodel/          # ViewModels (Phase 2+)
│       │   ├── di/                     # Modules Hilt (Network, Database)
│       │   └── util/                   # Constantes, helpers
│       └── res/
│           ├── values/                 # strings.xml (FR), colors, themes
│           ├── xml/                    # network_security_config, backup_rules
│           └── drawable/               # Icônes vectorielles
├── build.gradle.kts                    # Build script racine
├── settings.gradle.kts                 # Modules + repositories
├── gradle.properties                   # Config Gradle
└── gradle/
    ├── libs.versions.toml              # Catalogue de versions (single source of truth)
    └── wrapper/                        # Gradle wrapper
```

---

## 🔐 Variables d'environnement (Phase 3+)

⚠️ **Aucune clé API ne doit jamais figurer dans l'app Android.** Toutes les clés sont détenues par le **backend Node.js** (Phase 3).

Quand on créera le backend, voici les variables `.env` qui seront nécessaires :

```bash
# Backend .env (Phase 3+)
HEYGEN_API_KEY=...           # https://app.heygen.com/settings/api-keys
ELEVENLABS_API_KEY=...       # https://elevenlabs.io/app/settings/api-keys
REPLICATE_API_TOKEN=...      # https://replicate.com/account/api-tokens
ANTHROPIC_API_KEY=...        # Déjà existant (MSB Learner / Mecanix)
META_APP_ID=...              # https://developers.facebook.com/apps
META_APP_SECRET=...

PORT=3000
NODE_ENV=development
```

---

## 📋 Comptes / clés API à créer avant Phase 3

| Service | URL | Coût démarrage |
|---|---|---|
| HeyGen | https://app.heygen.com/api | ~24$/mois Plan Creator |
| ElevenLabs | https://elevenlabs.io | 5$/mois Starter |
| Replicate (Flux) | https://replicate.com | Pay-per-use (~0$ au repos) |
| Anthropic | ✅ existe (clé MSB Learner) | — |
| Meta for Developers | https://developers.facebook.com | Gratuit |
| Railway (hébergement backend) | https://railway.app | ~5$/mois |

---

## 🚧 Roadmap

- [x] **Phase 1 — Setup (j 1-2)** : projet compile, 8 écrans stubs, navigation, thème Karibou
- [ ] **Phase 2 — Upload (j 3-4)** : sélection photos, compression, Room
- [ ] **Phase 3 — Backend minimal (j 5-7)** : Node.js + HeyGen → URL vidéo
- [ ] **Phase 4 — Intégration (j 8-10)** : ExoPlayer + téléchargement galerie
- [ ] **Phase 5 — Améliorations (sem 3)** : ElevenLabs + Flux + Claude rewriting
- [ ] **Phase 6 — Facebook (sem 4)** : OAuth Meta + Marketing API + mention IA obligatoire

---

## ⚖️ Conformité légale (à intégrer en Phase 6)

- ✅ Écran de consentement obligatoire avant chaque génération
- ✅ Mention auto "Cette publicité contient du contenu généré par IA" (règles Meta 2024+)
- ✅ Purge des photos serveur après génération
- ✅ HTTPS strict en production (déjà configuré dans `network_security_config.xml`)

---

## 🆘 Dépannage

**"Gradle sync failed: Could not resolve all files"**
→ Vérifie ta connexion internet et relance **File → Sync Project with Gradle Files**

**"SDK location not found"**
→ Android Studio → **File → Project Structure → SDK Location** → définis le chemin du SDK

**L'app crashe au lancement**
→ Vérifie que **API 34** est bien installé (SDK Manager) et que `minSdk=26`

---

## 📝 Notes développement

- **Code commenté en français** (préférence utilisateur)
- **Langue UI** : français uniquement (pas de traduction EN en Phase 1)
- **Material 3** avec couleurs custom Karibou (palette ambre/cire/sage)
- **Mono-utilisateur** : pas de login, pas de multi-tenant. Pour usage personnel Bougie Karibou uniquement.

---

*Projet créé le 2026-05-15 par Sylvain Hébert (Gestion Sylvain Hébert inc.)*
*Brief original : `OneDrive/Downloads/Brief App Pub IA ClaudeCode.pdf`*

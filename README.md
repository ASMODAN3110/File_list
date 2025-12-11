# File List Generator

Générateur de liste de fichiers en PDF - Projet Java avec Maven

## Prérequis

1. **Java JDK 11 ou supérieur**
   - Vérifiez l'installation : `java -version`
   - Téléchargez depuis : https://www.oracle.com/java/technologies/downloads/

2. **Apache Maven**
   - Vérifiez l'installation : `mvn --version`
   - Téléchargez depuis : https://maven.apache.org/download.cgi
   - Instructions d'installation : https://maven.apache.org/install.html

## Compilation du projet

### Option 1 : Compilation simple

```bash
mvn compile
```

Cette commande compile les fichiers Java dans le dossier `target/classes`.

### Option 2 : Compilation et création du JAR

```bash
mvn package
```

Cette commande :
- Compile le projet
- Exécute les tests (s'il y en a)
- Crée un JAR dans `target/file-list-generator-1.0.0.jar`
- Crée également un JAR "fat" (avec toutes les dépendances) : `target/file-list-generator-1.0.0-shaded.jar`

### Option 3 : Nettoyage et compilation complète

```bash
mvn clean package
```

Cette commande supprime le dossier `target` et recompile tout depuis le début.

## Exécution du projet

### Avec Maven (sans créer de JAR)

```bash
mvn exec:java -Dexec.mainClass="com.filelist.FileListGenerator"
```

### Avec le JAR créé

```bash
java -jar target/file-list-generator-1.0.0-shaded.jar
```

**Note :** Utilisez le JAR "shaded" (avec `-shaded` dans le nom) car il contient toutes les dépendances nécessaires (Apache PDFBox).

## Structure du projet

```
files_list/
├── .gitignore                        # Fichiers ignorés par Git
├── pom.xml                          # Configuration Maven
├── README.md                        # Documentation du projet
├── src/
│   └── main/
│       └── java/
│           └── com/
│               └── filelist/
│                   ├── FileListGenerator.java    # Point d'entrée
│                   ├── FileScanner.java          # Scanner de répertoires
│                   ├── FileInfo.java             # Modèle de données
│                   ├── MimeTypeDetector.java     # Détection MIME
│                   └── PdfGenerator.java         # Génération PDF
└── target/                          # Dossier de compilation (créé par Maven, ignoré par Git)
```

## Contrôle de version (Git)

Le projet inclut un fichier `.gitignore` qui exclut automatiquement :
- Le dossier `target/` (fichiers compilés par Maven)
- Les fichiers de configuration des IDE (Eclipse, IntelliJ, VS Code, Cursor)
- Les fichiers temporaires et logs
- Les fichiers PDF générés par l'application
- Les fichiers système (Windows, macOS, Linux)

Pour initialiser un dépôt Git :
```bash
git init
git add .
git commit -m "Initial commit"
```

## Utilisation

1. Lancez l'application
2. Entrez le chemin du répertoire à analyser
3. Entrez le nom du fichier PDF de sortie
4. Le PDF sera généré dans le répertoire courant

## Dépendances

- **Apache PDFBox 3.0.0** : Pour la génération de fichiers PDF

## Résolution de problèmes

### Erreur : "mvn n'est pas reconnu"

Cette erreur signifie que Maven n'est pas installé ou n'est pas dans le PATH système.

#### Installation de Maven sur Windows

**Méthode 1 : Installation manuelle**

1. **Téléchargez Maven** :
   - Allez sur https://maven.apache.org/download.cgi
   - Téléchargez le fichier `apache-maven-X.X.X-bin.zip` (version binaire)

2. **Extrayez l'archive** :
   - Extrayez le fichier ZIP dans un dossier (ex: `C:\Program Files\Apache\maven`)
   - Notez le chemin complet (ex: `C:\Program Files\Apache\maven\apache-maven-3.9.5`)

3. **Ajoutez Maven au PATH** :
   - Ouvrez les **Paramètres système** → **Variables d'environnement**
   - Dans **Variables système**, trouvez `Path` et cliquez sur **Modifier**
   - Cliquez sur **Nouveau** et ajoutez le chemin vers le dossier `bin` de Maven
     - Exemple : `C:\Program Files\Apache\maven\apache-maven-3.9.5\bin`
   - Cliquez sur **OK** pour fermer toutes les fenêtres

4. **Créez la variable MAVEN_HOME** (optionnel mais recommandé) :
   - Dans **Variables système**, cliquez sur **Nouveau**
   - Nom : `MAVEN_HOME`
   - Valeur : Le chemin vers le dossier Maven (sans `\bin`)
     - Exemple : `C:\Program Files\Apache\maven\apache-maven-3.9.5`
   - Cliquez sur **OK**

5. **Redémarrez PowerShell** :
   - Fermez complètement PowerShell et rouvrez-le
   - Vérifiez avec : `mvn --version`

**Méthode 2 : Avec Chocolatey (si installé)**

```powershell
choco install maven
```

**Méthode 3 : Avec winget (Windows 10/11)**

```powershell
winget install Apache.Maven
```

**Vérification après installation** :
```powershell
mvn --version
```

Si la commande fonctionne, vous devriez voir la version de Maven affichée.

### Erreur : "javac n'est pas reconnu"
- Vérifiez que Java JDK est installé (pas seulement JRE)
- Vérifiez que JAVA_HOME est configuré
- Téléchargez le JDK depuis : https://www.oracle.com/java/technologies/downloads/

### Erreur de compilation
- Vérifiez que vous êtes dans le répertoire racine du projet (où se trouve `pom.xml`)
- Exécutez `mvn clean` puis `mvn compile` pour nettoyer et recompiler



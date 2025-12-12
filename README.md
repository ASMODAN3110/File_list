# File List Generator

Générateur de liste de fichiers en PDF - Projet Java avec Maven

## Prérequis

- **Java JDK 11 ou supérieur** : `java -version`
- **Apache Maven** : `mvn --version`

## Compilation

```bash
mvn package
```

Crée un JAR exécutable dans `target/file-list-generator-1.0.0-shaded.jar` (avec toutes les dépendances).

## Exécution

### Avec Maven (recommandé)

```bash
mvn exec:java
```

### Avec le JAR

```bash
java -jar target/file-list-generator-1.0.0-shaded.jar
```

## Utilisation

1. Lancez l'application
2. Entrez le chemin du répertoire à analyser
3. Entrez le nom du fichier PDF de sortie
4. Le PDF sera généré dans le répertoire courant

## Structure du projet

```
src/
└── main/
    └── java/
        └── com/
            └── filelist/
                ├── FileListGenerator.java    # Point d'entrée
                ├── FileScanner.java          # Scanner de répertoires
                ├── FileInfo.java             # Modèle de données
                ├── MimeTypeDetector.java     # Détection MIME
                └── PdfGenerator.java         # Génération PDF
```

## Dépendances

- **Apache PDFBox 3.0.0** : Génération de fichiers PDF



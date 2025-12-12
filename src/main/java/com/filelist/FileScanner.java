package com.filelist;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Classe pour scanner un répertoire et collecter les informations des fichiers
 */
public class FileScanner {
    private static final Set<String> EXTENSIONS_INUTILES = new HashSet<>();
    private static final Set<String> NOMS_INUTILES = new HashSet<>();

    static {
        // Extensions de fichiers inutiles
        EXTENSIONS_INUTILES.add("tmp");
        EXTENSIONS_INUTILES.add("temp");
        EXTENSIONS_INUTILES.add("swp");
        EXTENSIONS_INUTILES.add("bak");
        EXTENSIONS_INUTILES.add("log");
        EXTENSIONS_INUTILES.add("~");
        
        // Noms de fichiers inutiles
        NOMS_INUTILES.add("Thumbs.db");
        NOMS_INUTILES.add(".DS_Store");
        NOMS_INUTILES.add("desktop.ini");
        NOMS_INUTILES.add(".git");
        NOMS_INUTILES.add(".svn");
        NOMS_INUTILES.add(".idea");
        NOMS_INUTILES.add(".vscode");
    }

    /**
     * Scanne récursivement un répertoire et retourne la liste des fichiers valides
     * @param repertoire Le répertoire à scanner
     * @param profondeurMax La profondeur maximale de scan (1 = seulement le répertoire racine, 2 = racine + 1 niveau, etc.)
     */
    public List<FileInfo> scannerRepertoire(Path repertoire, int profondeurMax) throws IOException {
        List<FileInfo> fichiers = new ArrayList<>();
        List<Path> dossiers = new ArrayList<>();
        
        if (!Files.exists(repertoire) || !Files.isDirectory(repertoire)) {
            throw new IllegalArgumentException("Le répertoire spécifié n'existe pas ou n'est pas un répertoire");
        }

        if (profondeurMax < 1) {
            throw new IllegalArgumentException("La profondeur maximale doit être au moins 1");
        }

        // Étape 1 : Collecter tous les fichiers et dossiers jusqu'à la profondeur maximale
        // (nécessaire pour calculer les tailles des dossiers)
        Set<Path> fichiersScannes = new HashSet<>();
        try {
            Files.walk(repertoire, profondeurMax)
                .filter(path -> !path.equals(repertoire)) // Exclure le répertoire racine lui-même
                .forEach(path -> {
                    try {
                        if (Files.isRegularFile(path) && !estFichierInutile(path)) {
                            // Normaliser le chemin pour garantir une comparaison correcte
                            fichiersScannes.add(path.normalize().toAbsolutePath());
                        }
                    } catch (Exception e) {
                        // Ignorer les fichiers/dossiers qui ne peuvent pas être lus
                        System.err.println("Erreur lors de la lecture de " + path + ": " + e.getMessage());
                    }
                });
        } catch (IOException e) {
            throw new IOException("Erreur lors du scan du répertoire : " + e.getMessage(), e);
        }
        
        // Étape 1b : Collecter les fichiers et dossiers jusqu'à la profondeur demandée
        try {
            Files.walk(repertoire, profondeurMax)
                .filter(path -> !path.equals(repertoire)) // Exclure le répertoire racine lui-même
                .filter(path -> {
                    // Calculer la profondeur du chemin
                    int profondeur = calculerProfondeur(path, repertoire);
                    // Garder tous les fichiers et dossiers jusqu'à la profondeur demandée
                    return profondeur <= profondeurMax;
                })
                .forEach(path -> {
                    try {
                        if (Files.isRegularFile(path) && !estFichierInutile(path)) {
                            FileInfo info = creerFileInfo(path, false);
                            fichiers.add(info);
                        } else if (Files.isDirectory(path) && !estDossierInutile(path)) {
                            dossiers.add(path); // Collecter les dossiers pour traitement ultérieur
                        }
                    } catch (Exception e) {
                        // Ignorer les fichiers/dossiers qui ne peuvent pas être lus
                        System.err.println("Erreur lors de la lecture de " + path + ": " + e.getMessage());
                    }
                });
        } catch (IOException e) {
            throw new IOException("Erreur lors du scan du répertoire : " + e.getMessage(), e);
        }

        // Étape 2 : Créer un set des dossiers listés pour exclure leurs fichiers du calcul de taille
        Set<Path> dossiersListes = new HashSet<>();
        for (Path dossier : dossiers) {
            dossiersListes.add(dossier.normalize().toAbsolutePath());
        }
        
        // Étape 3 : Calculer la taille des dossiers en excluant les fichiers déjà scannés
        for (Path dossier : dossiers) {
            FileInfo info = creerFileInfoDossier(dossier, repertoire, profondeurMax, fichiersScannes, dossiersListes);
            fichiers.add(info);
        }

        return fichiers;
    }

    /**
     * Vérifie si un fichier est considéré comme inutile
     */
    private boolean estFichierInutile(Path fichier) {
        String nomFichier = fichier.getFileName().toString();
        
        // Vérifier les noms de fichiers inutiles
        if (NOMS_INUTILES.contains(nomFichier)) {
            return true;
        }

        // Vérifier les fichiers cachés (commençant par .)
        if (nomFichier.startsWith(".") && !nomFichier.equals(".gitignore") && 
            !nomFichier.equals(".gitattributes") && !nomFichier.equals(".editorconfig")) {
            return true;
        }

        // Vérifier les fichiers commençant par ._ (fichiers de métadonnées macOS)
        if (nomFichier.startsWith("._")) {
            return true;
        }

        // Vérifier les extensions inutiles
        String extension = MimeTypeDetector.extraireExtension(fichier);
        if (!extension.isEmpty() && EXTENSIONS_INUTILES.contains(extension.toLowerCase())) {
            return true;
        }

        return false;
    }

    /**
     * Vérifie si un dossier est considéré comme inutile
     */
    private boolean estDossierInutile(Path dossier) {
        String nomDossier = dossier.getFileName().toString();
        
        // Vérifier les noms de dossiers inutiles
        if (NOMS_INUTILES.contains(nomDossier)) {
            return true;
        }

        // Vérifier les dossiers cachés (commençant par .)
        if (nomDossier.startsWith(".") && !nomDossier.equals(".git")) {
            return true;
        }

        return false;
    }

    /**
     * Crée un objet FileInfo à partir d'un Path (fichier)
     */
    private FileInfo creerFileInfo(Path fichier, boolean estDossier) throws IOException {
        String nom = fichier.getFileName().toString();
        String extension = MimeTypeDetector.extraireExtension(fichier);
        String typeMime = MimeTypeDetector.detecterTypeMime(fichier);
        String categorie = MimeTypeDetector.determinerCategorie(typeMime);
        long taille = Files.size(fichier);

        return new FileInfo(fichier, nom, extension, typeMime, categorie, taille, estDossier);
    }

    /**
     * Crée un objet FileInfo à partir d'un Path (dossier)
     * @param dossier Le dossier
     * @param repertoireRacine Le répertoire racine du scan
     * @param profondeurMax La profondeur maximale du scan initial
     * @param fichiersScannes Set des fichiers déjà scannés individuellement (à exclure du calcul)
     * @param dossiersListes Set des dossiers listés (pour exclure leurs fichiers du calcul)
     */
    private FileInfo creerFileInfoDossier(Path dossier, Path repertoireRacine, int profondeurMax, Set<Path> fichiersScannes, Set<Path> dossiersListes) {
        String nom = dossier.getFileName().toString();
        String extension = ""; // Les dossiers n'ont pas d'extension
        String typeMime = "inode/directory";
        String categorie = "Dossier";
        long taille = calculerTailleDossier(dossier, fichiersScannes, dossiersListes);

        return new FileInfo(dossier, nom, extension, typeMime, categorie, taille, true);
    }

    /**
     * Calcule la profondeur d'un chemin par rapport au répertoire racine
     */
    private int calculerProfondeur(Path chemin, Path racine) {
        int profondeur = 0;
        Path current = chemin;
        while (current != null && !current.equals(racine)) {
            profondeur++;
            current = current.getParent();
        }
        return profondeur;
    }

    /**
     * Calcule la taille totale d'un dossier en additionnant la taille de tous les fichiers qu'il contient,
     * en excluant les fichiers déjà listés individuellement dans le scan ET les fichiers des sous-dossiers listés
     * @param dossier Le dossier dont on veut calculer la taille
     * @param fichiersScannes Set des fichiers déjà scannés individuellement (à exclure)
     * @param dossiersListes Set des dossiers listés (pour exclure leurs fichiers)
     * @return La taille totale en octets (sans compter les fichiers déjà scannés)
     */
    private long calculerTailleDossier(Path dossier, Set<Path> fichiersScannes, Set<Path> dossiersListes) {
        try {
            Path dossierNormalise = dossier.normalize().toAbsolutePath();
            
            return Files.walk(dossier)
                .filter(Files::isRegularFile)
                .filter(path -> !estFichierInutile(path))
                .filter(path -> {
                    // Normaliser le chemin pour garantir une comparaison correcte
                    Path pathNormalise = path.normalize().toAbsolutePath();
                    
                    // Exclure les fichiers déjà scannés individuellement
                    if (fichiersScannes.contains(pathNormalise)) {
                        return false;
                    }
                    
                    // Exclure les fichiers qui sont dans des sous-dossiers listés (mais pas le dossier lui-même)
                    Path parent = pathNormalise.getParent();
                    while (parent != null && !parent.equals(dossierNormalise)) {
                        if (dossiersListes.contains(parent.normalize().toAbsolutePath())) {
                            return false; // Ce fichier est dans un sous-dossier listé
                        }
                        parent = parent.getParent();
                    }
                    
                    return true;
                })
                .mapToLong(path -> {
                    try {
                        return Files.size(path);
                    } catch (IOException e) {
                        // Ignorer les fichiers qui ne peuvent pas être lus
                        return 0L;
                    }
                })
                .sum();
        } catch (IOException e) {
            // En cas d'erreur, retourner 0
            return 0L;
        }
    }
}


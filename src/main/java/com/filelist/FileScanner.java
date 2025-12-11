package com.filelist;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
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
     */
    public List<FileInfo> scannerRepertoire(Path repertoire) throws IOException {
        List<FileInfo> fichiers = new ArrayList<>();
        
        if (!Files.exists(repertoire) || !Files.isDirectory(repertoire)) {
            throw new IllegalArgumentException("Le répertoire spécifié n'existe pas ou n'est pas un répertoire");
        }

        Files.walkFileTree(repertoire, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path fichier, BasicFileAttributes attrs) throws IOException {
                if (attrs.isRegularFile() && !estFichierInutile(fichier)) {
                    try {
                        FileInfo info = creerFileInfo(fichier);
                        fichiers.add(info);
                    } catch (Exception e) {
                        // Ignorer les fichiers qui ne peuvent pas être lus
                        System.err.println("Erreur lors de la lecture de " + fichier + ": " + e.getMessage());
                    }
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path fichier, IOException exc) throws IOException {
                // Ignorer les fichiers/dossiers inaccessibles
                return FileVisitResult.CONTINUE;
            }
        });

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
     * Crée un objet FileInfo à partir d'un Path
     */
    private FileInfo creerFileInfo(Path fichier) throws IOException {
        String nom = fichier.getFileName().toString();
        String extension = MimeTypeDetector.extraireExtension(fichier);
        String typeMime = MimeTypeDetector.detecterTypeMime(fichier);
        String categorie = MimeTypeDetector.determinerCategorie(typeMime);
        long taille = Files.size(fichier);

        return new FileInfo(fichier, nom, extension, typeMime, categorie, taille);
    }
}


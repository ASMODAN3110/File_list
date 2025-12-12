package com.filelist;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

/**
 * Point d'entrée principal de l'application
 */
public class FileListGenerator {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("=== Générateur de liste de fichiers en PDF ===");
        System.out.println();

        // Saisie interactive du répertoire
        Path repertoire = null;
        while (repertoire == null) {
            System.out.print("Veuillez entrer le chemin du répertoire à analyser : ");
            String chemin = scanner.nextLine().trim();
            
            if (chemin.isEmpty()) {
                System.out.println("Erreur : Le chemin ne peut pas être vide.");
                continue;
            }

            try {
                repertoire = Paths.get(chemin);
                if (!Files.exists(repertoire)) {
                    System.out.println("Erreur : Le répertoire spécifié n'existe pas.");
                    repertoire = null;
                } else if (!Files.isDirectory(repertoire)) {
                    System.out.println("Erreur : Le chemin spécifié n'est pas un répertoire.");
                    repertoire = null;
                }
            } catch (Exception e) {
                System.out.println("Erreur : Chemin invalide - " + e.getMessage());
                repertoire = null;
            }
        }

        // Saisie du nom du fichier de sortie
        System.out.print("Veuillez entrer le nom du fichier PDF de sortie (sans extension) : ");
        String nomFichier = scanner.nextLine().trim();
        if (nomFichier.isEmpty()) {
            nomFichier = "liste_fichiers";
        }
        if (!nomFichier.endsWith(".pdf")) {
            nomFichier += ".pdf";
        }

        Path fichierSortie = Paths.get(nomFichier);
        
        scanner.close();

        try {
            System.out.println();
            System.out.println("Analyse du répertoire en cours...");
            
            // Scanner le répertoire
            FileScanner fileScanner = new FileScanner();
            List<FileInfo> fichiers = fileScanner.scannerRepertoire(repertoire);
            
            if (fichiers.isEmpty()) {
                System.out.println("Aucun fichier trouvé dans le répertoire spécifié.");
                return;
            }

            System.out.println(fichiers.size() + " fichier(s) trouvé(s).");
            System.out.println("Génération du PDF en cours...");

            // Générer le PDF
            PdfGenerator pdfGenerator = new PdfGenerator();
            pdfGenerator.genererPdf(fichiers, repertoire, fichierSortie);

            System.out.println();
            System.out.println("✓ PDF généré avec succès : " + fichierSortie.toAbsolutePath());
            System.out.println("  Nombre de fichiers listés : " + fichiers.size());
            
            long tailleTotale = fichiers.stream().mapToLong(FileInfo::getTailleOctets).sum();
            System.out.println("  Taille totale : " + formaterTaille(tailleTotale));

        } catch (IOException e) {
            System.err.println("Erreur lors de la génération du PDF : " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Erreur : " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Formate la taille en octets
     */
    private static String formaterTaille(long octets) {
        if (octets < 1024) {
            return octets + " o";
        } else if (octets < 1024 * 1024) {
            return String.format("%.2f Ko", octets / 1024.0);
        } else if (octets < 1024 * 1024 * 1024) {
            return String.format("%.2f Mo", octets / (1024.0 * 1024.0));
        } else {
            return String.format("%.2f Go", octets / (1024.0 * 1024.0 * 1024.0));
        }
    }
}






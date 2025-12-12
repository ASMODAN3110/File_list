package com.filelist;

import java.nio.file.Path;

/**
 * Classe représentant les informations d'un fichier
 */
public class FileInfo {
    private Path cheminComplet;
    private String nom;
    private String extension;
    private String typeMime;
    private String categorie;
    private long tailleOctets;
    private String tailleLisible;
    private boolean estDossier;

    public FileInfo(Path cheminComplet, String nom, String extension, String typeMime, 
                   String categorie, long tailleOctets, boolean estDossier) {
        this.cheminComplet = cheminComplet;
        this.nom = nom;
        this.extension = extension;
        this.typeMime = typeMime != null ? typeMime : "inconnu";
        this.categorie = categorie;
        this.tailleOctets = tailleOctets;
        this.tailleLisible = estDossier ? formaterTaille(tailleOctets) : formaterTaille(tailleOctets);
        this.estDossier = estDossier;
    }

    /**
     * Formate la taille en octets en format lisible (Ko, Mo, Go)
     */
    private String formaterTaille(long octets) {
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

    // Getters
    public Path getCheminComplet() {
        return cheminComplet;
    }

    public String getNom() {
        return nom;
    }

    public String getExtension() {
        return extension;
    }

    public String getTypeMime() {
        return typeMime;
    }

    public String getCategorie() {
        return categorie;
    }

    public long getTailleOctets() {
        return tailleOctets;
    }

    public String getTailleLisible() {
        return tailleLisible;
    }

    public boolean estDossier() {
        return estDossier;
    }

    @Override
    public String toString() {
        return String.format("%s | %s | %s | %s | %s", 
            nom, extension, typeMime, categorie, tailleLisible);
    }
}


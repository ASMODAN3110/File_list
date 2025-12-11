package com.filelist;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

/**
 * Classe pour générer un PDF contenant la liste des fichiers
 */
public class PdfGenerator {
    private static final float MARGIN = 50;
    private static final float FONT_SIZE_TITLE = 16;
    private static final float FONT_SIZE_HEADER = 10;
    private static final float FONT_SIZE_BODY = 8;
    private static final float ROW_HEIGHT = 15;
    private static final float HEADER_HEIGHT = 20;

    /**
     * Génère un PDF avec la liste des fichiers
     */
    public void genererPdf(List<FileInfo> fichiers, Path repertoire, Path fichierSortie) throws IOException {
        // Trier les fichiers par nom
        fichiers.sort(Comparator.comparing(FileInfo::getNom));

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                float yPosition = PDRectangle.A4.getHeight() - MARGIN;
                
                // En-tête
                yPosition = dessinerEnTete(contentStream, yPosition, repertoire);
                
                // Tableau
                yPosition = dessinerTableau(contentStream, yPosition, fichiers, page);
                
                // Statistiques
                dessinerStatistiques(contentStream, yPosition, fichiers);
            }

            document.save(fichierSortie.toFile());
        }
    }

    /**
     * Dessine l'en-tête du document
     */
    private float dessinerEnTete(PDPageContentStream contentStream, float yPosition, Path repertoire) 
            throws IOException {
        PDType1Font fontTitle = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
        PDType1Font fontHeader = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

        // Titre
        contentStream.beginText();
        contentStream.setFont(fontTitle, FONT_SIZE_TITLE);
        contentStream.newLineAtOffset(MARGIN, yPosition);
        contentStream.showText("Liste des Fichiers");
        contentStream.endText();
        yPosition -= 25;

        // Date et heure
        String dateHeure = LocalDateTime.now().format(
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        contentStream.beginText();
        contentStream.setFont(fontHeader, FONT_SIZE_HEADER);
        contentStream.newLineAtOffset(MARGIN, yPosition);
        contentStream.showText("Date : " + dateHeure);
        contentStream.endText();
        yPosition -= 15;

        // Répertoire analysé
        String cheminRepertoire = repertoire.toAbsolutePath().toString();
        // Tronquer si trop long
        if (cheminRepertoire.length() > 100) {
            cheminRepertoire = "..." + cheminRepertoire.substring(cheminRepertoire.length() - 97);
        }
        contentStream.beginText();
        contentStream.setFont(fontHeader, FONT_SIZE_HEADER);
        contentStream.newLineAtOffset(MARGIN, yPosition);
        contentStream.showText("Répertoire : " + cheminRepertoire);
        contentStream.endText();
        yPosition -= 30;

        return yPosition;
    }

    /**
     * Dessine le tableau avec les fichiers
     */
    private float dessinerTableau(PDPageContentStream contentStream, float yPosition, 
                                  List<FileInfo> fichiers, PDPage page) throws IOException {
        float pageWidth = page.getMediaBox().getWidth();
        float tableWidth = pageWidth - 2 * MARGIN;
        
        // Largeurs des colonnes
        float colNom = tableWidth * 0.35f;
        float colExtension = tableWidth * 0.12f;
        float colMime = tableWidth * 0.25f;
        float colCategorie = tableWidth * 0.13f;
        float colTaille = tableWidth * 0.15f;

        float xStart = MARGIN;
        float yStart = yPosition;
        float currentY = yStart;

        PDType1Font fontHeader = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
        PDType1Font fontBody = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

        // En-tête du tableau
        contentStream.setLineWidth(1.5f);
        contentStream.moveTo(xStart, currentY);
        contentStream.lineTo(xStart + tableWidth, currentY);
        contentStream.stroke();

        currentY -= HEADER_HEIGHT;

        // Colonnes d'en-tête
        String[] headers = {"Nom", "Extension", "Type MIME", "Catégorie", "Taille"};
        float[] colWidths = {colNom, colExtension, colMime, colCategorie, colTaille};
        float xPos = xStart + 5;

        contentStream.beginText();
        contentStream.setFont(fontHeader, FONT_SIZE_BODY);
        contentStream.newLineAtOffset(xPos, currentY + HEADER_HEIGHT - 5);
        for (int i = 0; i < headers.length; i++) {
            String header = headers[i];
            if (header.length() > 15) {
                header = header.substring(0, 12) + "...";
            }
            contentStream.showText(header);
            if (i < headers.length - 1) {
                contentStream.newLineAtOffset(colWidths[i], 0);
            }
        }
        contentStream.endText();

        // Ligne de séparation
        contentStream.setLineWidth(1f);
        contentStream.moveTo(xStart, currentY);
        contentStream.lineTo(xStart + tableWidth, currentY);
        contentStream.stroke();

        currentY -= 5;

        // Lignes de données
        for (FileInfo fichier : fichiers) {
            // Vérifier si on a besoin d'une nouvelle page
            if (currentY < MARGIN + 50) {
                // Nouvelle page (simplifié - dans une vraie implémentation, on créerait une nouvelle page)
                break;
            }

            contentStream.beginText();
            contentStream.setFont(fontBody, FONT_SIZE_BODY);
            contentStream.newLineAtOffset(xStart + 5, currentY);
            
            // Nom (tronqué si nécessaire)
            String nom = fichier.getNom();
            if (nom.length() > 40) {
                nom = nom.substring(0, 37) + "...";
            }
            contentStream.showText(nom);
            contentStream.newLineAtOffset(colNom, 0);

            // Extension
            contentStream.showText(fichier.getExtension().isEmpty() ? "-" : fichier.getExtension());
            contentStream.newLineAtOffset(colExtension, 0);

            // Type MIME (tronqué si nécessaire)
            String mime = fichier.getTypeMime();
            if (mime.length() > 30) {
                mime = mime.substring(0, 27) + "...";
            }
            contentStream.showText(mime);
            contentStream.newLineAtOffset(colMime, 0);

            // Catégorie
            contentStream.showText(fichier.getCategorie());
            contentStream.newLineAtOffset(colCategorie, 0);

            // Taille
            contentStream.showText(fichier.getTailleLisible());
            
            contentStream.endText();

            // Ligne de séparation
            currentY -= ROW_HEIGHT;
            contentStream.setLineWidth(0.5f);
            contentStream.moveTo(xStart, currentY);
            contentStream.lineTo(xStart + tableWidth, currentY);
            contentStream.stroke();
        }

        // Bordures verticales du tableau
        contentStream.setLineWidth(1f);
        contentStream.moveTo(xStart, yStart);
        contentStream.lineTo(xStart, currentY);
        contentStream.stroke();
        contentStream.moveTo(xStart + tableWidth, yStart);
        contentStream.lineTo(xStart + tableWidth, currentY);
        contentStream.stroke();

        return currentY;
    }

    /**
     * Dessine les statistiques en bas du document
     */
    private void dessinerStatistiques(PDPageContentStream contentStream, float yPosition, 
                                      List<FileInfo> fichiers) throws IOException {
        if (yPosition < MARGIN + 30) {
            return; // Pas assez de place
        }

        PDType1Font font = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
        
        long tailleTotale = fichiers.stream().mapToLong(FileInfo::getTailleOctets).sum();
        String tailleTotaleLisible = formaterTaille(tailleTotale);

        yPosition -= 20;
        contentStream.beginText();
        contentStream.setFont(font, FONT_SIZE_HEADER);
        contentStream.newLineAtOffset(MARGIN, yPosition);
        contentStream.showText("Statistiques :");
        contentStream.endText();

        yPosition -= 15;
        font = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
        contentStream.beginText();
        contentStream.setFont(font, FONT_SIZE_BODY);
        contentStream.newLineAtOffset(MARGIN + 10, yPosition);
        contentStream.showText(String.format("Nombre total de fichiers : %d", fichiers.size()));
        contentStream.endText();

        yPosition -= 15;
        contentStream.beginText();
        contentStream.setFont(font, FONT_SIZE_BODY);
        contentStream.newLineAtOffset(MARGIN + 10, yPosition);
        contentStream.showText("Taille totale : " + tailleTotaleLisible);
        contentStream.endText();
    }

    /**
     * Formate la taille en octets
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
}


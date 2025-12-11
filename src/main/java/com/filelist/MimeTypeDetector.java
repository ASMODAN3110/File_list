package com.filelist;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Classe pour détecter le type MIME et catégoriser les fichiers
 */
public class MimeTypeDetector {
    private static final Map<String, String> extensionToMime = new HashMap<>();
    private static final Map<String, String> mimeToCategory = new HashMap<>();

    static {
        // Mapping extensions -> MIME types
        extensionToMime.put("pdf", "application/pdf");
        extensionToMime.put("doc", "application/msword");
        extensionToMime.put("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        extensionToMime.put("xls", "application/vnd.ms-excel");
        extensionToMime.put("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        extensionToMime.put("ppt", "application/vnd.ms-powerpoint");
        extensionToMime.put("pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation");
        extensionToMime.put("txt", "text/plain");
        extensionToMime.put("html", "text/html");
        extensionToMime.put("htm", "text/html");
        extensionToMime.put("css", "text/css");
        extensionToMime.put("xml", "application/xml");
        extensionToMime.put("json", "application/json");
        
        // Images
        extensionToMime.put("jpg", "image/jpeg");
        extensionToMime.put("jpeg", "image/jpeg");
        extensionToMime.put("png", "image/png");
        extensionToMime.put("gif", "image/gif");
        extensionToMime.put("bmp", "image/bmp");
        extensionToMime.put("svg", "image/svg+xml");
        extensionToMime.put("ico", "image/x-icon");
        
        // Vidéos
        extensionToMime.put("mp4", "video/mp4");
        extensionToMime.put("avi", "video/x-msvideo");
        extensionToMime.put("mov", "video/quicktime");
        extensionToMime.put("wmv", "video/x-ms-wmv");
        extensionToMime.put("flv", "video/x-flv");
        extensionToMime.put("mkv", "video/x-matroska");
        
        // Audio
        extensionToMime.put("mp3", "audio/mpeg");
        extensionToMime.put("wav", "audio/wav");
        extensionToMime.put("ogg", "audio/ogg");
        extensionToMime.put("flac", "audio/flac");
        extensionToMime.put("aac", "audio/aac");
        
        // Archives
        extensionToMime.put("zip", "application/zip");
        extensionToMime.put("rar", "application/x-rar-compressed");
        extensionToMime.put("7z", "application/x-7z-compressed");
        extensionToMime.put("tar", "application/x-tar");
        extensionToMime.put("gz", "application/gzip");
        
        // Code
        extensionToMime.put("java", "text/x-java-source");
        extensionToMime.put("py", "text/x-python");
        extensionToMime.put("js", "text/javascript");
        extensionToMime.put("cpp", "text/x-c++src");
        extensionToMime.put("c", "text/x-csrc");
        extensionToMime.put("cs", "text/x-csharp");
        extensionToMime.put("php", "text/x-php");
        extensionToMime.put("rb", "text/x-ruby");
        extensionToMime.put("go", "text/x-go");
        extensionToMime.put("rs", "text/x-rust");
        extensionToMime.put("swift", "text/x-swift");
        
        // Mapping MIME types -> Catégories
        mimeToCategory.put("application/pdf", "Document");
        mimeToCategory.put("application/msword", "Document");
        mimeToCategory.put("application/vnd.openxmlformats-officedocument.wordprocessingml.document", "Document");
        mimeToCategory.put("application/vnd.ms-excel", "Document");
        mimeToCategory.put("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "Document");
        mimeToCategory.put("application/vnd.ms-powerpoint", "Document");
        mimeToCategory.put("application/vnd.openxmlformats-officedocument.presentationml.presentation", "Document");
    }

    /**
     * Détecte le type MIME d'un fichier
     */
    public static String detecterTypeMime(Path fichier) {
        try {
            // Essayer d'abord avec Files.probeContentType
            String mimeType = Files.probeContentType(fichier);
            if (mimeType != null && !mimeType.isEmpty()) {
                return mimeType;
            }
        } catch (Exception e) {
            // Ignorer et utiliser le fallback
        }

        // Fallback : utiliser le mapping par extension
        String nomFichier = fichier.getFileName().toString().toLowerCase();
        int dernierPoint = nomFichier.lastIndexOf('.');
        if (dernierPoint > 0 && dernierPoint < nomFichier.length() - 1) {
            String extension = nomFichier.substring(dernierPoint + 1);
            return extensionToMime.getOrDefault(extension, "application/octet-stream");
        }

        return "application/octet-stream";
    }

    /**
     * Détermine la catégorie d'un fichier basée sur son type MIME
     */
    public static String determinerCategorie(String typeMime) {
        if (typeMime == null || typeMime.isEmpty()) {
            return "Autre";
        }

        // Vérifier d'abord le mapping direct
        String categorie = mimeToCategory.get(typeMime);
        if (categorie != null) {
            return categorie;
        }

        // Catégorisation par préfixe
        if (typeMime.startsWith("text/")) {
            // Vérifier si c'est du code
            if (typeMime.contains("java") || typeMime.contains("python") || 
                typeMime.contains("javascript") || typeMime.contains("c++") ||
                typeMime.contains("csharp") || typeMime.contains("php") ||
                typeMime.contains("ruby") || typeMime.contains("go") ||
                typeMime.contains("rust") || typeMime.contains("swift")) {
                return "Code";
            }
            return "Document";
        } else if (typeMime.startsWith("image/")) {
            return "Image";
        } else if (typeMime.startsWith("video/")) {
            return "Vidéo";
        } else if (typeMime.startsWith("audio/")) {
            return "Audio";
        } else if (typeMime.contains("zip") || typeMime.contains("rar") || 
                   typeMime.contains("7z") || typeMime.contains("tar") ||
                   typeMime.contains("gzip")) {
            return "Archive";
        }

        return "Autre";
    }

    /**
     * Extrait l'extension d'un fichier
     */
    public static String extraireExtension(Path fichier) {
        String nomFichier = fichier.getFileName().toString();
        int dernierPoint = nomFichier.lastIndexOf('.');
        if (dernierPoint > 0 && dernierPoint < nomFichier.length() - 1) {
            return nomFichier.substring(dernierPoint + 1).toLowerCase();
        }
        return "";
    }
}


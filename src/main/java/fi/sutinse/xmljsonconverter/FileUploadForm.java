package fi.sutinse.xmljsonconverter;

import java.io.InputStream;

/**
 * Record representing file upload form data for XML and JSON files.
 * Uses modern Java record syntax for immutable data transfer.
 * 
 * @param xmlFile  the XML input stream
 * @param jsonFile the JSON input stream
 */
public record FileUploadForm(InputStream xmlFile, InputStream jsonFile) {
    
    /**
     * Compact constructor with validation.
     */
    public FileUploadForm {
        // Validation is handled at the service layer to avoid unnecessary coupling
        // Records provide immutability and built-in equals/hashCode/toString
    }
}
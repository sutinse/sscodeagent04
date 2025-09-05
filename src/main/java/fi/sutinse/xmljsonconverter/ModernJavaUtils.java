package fi.sutinse.xmljsonconverter;

import java.util.List;
import java.util.Set;

/**
 * Utility class containing Java 21 performance optimizations and modern patterns.
 * Demonstrates best practices for memory efficiency and performance.
 */
public final class ModernJavaUtils {

    private ModernJavaUtils() {
        // Utility class - prevent instantiation
    }

    /**
     * Immutable collections using factory methods (JDK 9+).
     * More memory efficient than traditional collections.
     */
    public static final class Collections {
        
        // Predefined immutable sets for validation
        public static final Set<String> SUPPORTED_XML_ELEMENTS = Set.of(
            "person", "name", "age", "company", "employees", "employee", "department"
        );
        
        public static final List<String> COMMON_JSON_FIELDS = List.of(
            "name", "age", "department", "employees", "employee"
        );
        
        private Collections() {}
    }

    /**
     * String validation utilities using modern Java patterns.
     */
    public static final class Validation {
        
        /**
         * Validates content using pattern matching and null-safe operations.
         */
        public static boolean isValidContent(String content) {
            return content != null && !content.isBlank() && content.trim().length() > 0;
        }
        
        /**
         * Validates XML content using modern Java patterns.
         */
        public static ValidationResult validateXmlContent(String xmlContent) {
            if (xmlContent == null || xmlContent.isBlank()) {
                return new ValidationResult.Invalid("XML content cannot be empty");
            }
            
            // Basic XML validation using traditional approach for Java 21 compatibility
            var trimmed = xmlContent.trim();
            if (trimmed.startsWith("<") && trimmed.endsWith(">")) {
                return new ValidationResult.Valid();
            } else if (trimmed.startsWith("<")) {
                return new ValidationResult.Invalid("XML appears to be incomplete");
            } else {
                return new ValidationResult.Invalid("Content does not appear to be XML");
            }
        }
        
        /**
         * Validates JSON content using modern patterns.
         */
        public static ValidationResult validateJsonContent(String jsonContent) {
            if (jsonContent == null || jsonContent.isBlank()) {
                return new ValidationResult.Invalid("JSON content cannot be empty");
            }
            
            var trimmed = jsonContent.trim();
            return switch (trimmed.charAt(0)) {
                case '{', '[' -> new ValidationResult.Valid();
                default -> new ValidationResult.Invalid("Content does not appear to be JSON");
            };
        }
        
        private Validation() {}
    }

    /**
     * Sealed interface for validation results (JDK 17+).
     */
    public sealed interface ValidationResult
            permits ValidationResult.Valid, ValidationResult.Invalid {
        
        record Valid() implements ValidationResult {}
        record Invalid(String message) implements ValidationResult {}
        
        /**
         * Pattern matching method for handling validation results.
         */
        default boolean isValid() {
            return switch (this) {
                case Valid() -> true;
                case Invalid(var message) -> false;
            };
        }
        
        default String getMessage() {
            return switch (this) {
                case Valid() -> "Valid";
                case Invalid(var message) -> message;
            };
        }
    }

    /**
     * Memory-efficient string operations using modern Java features.
     */
    public static final class StringOps {
        
        /**
         * Efficient string truncation using substring and modern conditionals.
         */
        public static String truncate(String input, int maxLength) {
            return input.length() <= maxLength 
                ? input 
                : input.substring(0, maxLength) + "...";
        }
        
        /**
         * Safe string formatting with null handling.
         */
        public static String safeFormat(String template, Object... args) {
            try {
                return template.formatted(args);
            } catch (Exception e) {
                return template; // Return template if formatting fails
            }
        }
        
        private StringOps() {}
    }
}
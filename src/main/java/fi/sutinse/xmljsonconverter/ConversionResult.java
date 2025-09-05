package fi.sutinse.xmljsonconverter;

/**
 * Sealed interface representing the result of a conversion operation.
 * Uses modern Java sealed classes (JDK 17+) for better type safety.
 */
public sealed interface ConversionResult
        permits ConversionResult.Success, ConversionResult.Failure {

    /**
     * Represents a successful conversion.
     */
    record Success(String convertedJson, String providedJson, String report) implements ConversionResult {}

    /**
     * Represents a failed conversion.
     */
    record Failure(String message, Throwable cause) implements ConversionResult {}

    /**
     * Pattern matching method to handle results (Java 17+ switch expressions).
     */
    default String toResponse() {
        return switch (this) {
            case Success(var convertedJson, var providedJson, var report) -> report;
            case Failure(var message, var cause) -> 
                """
                ## ❌ Conversion Failed
                
                Error: %s
                
                %s
                """.formatted(message, 
                    cause != null ? "Cause: " + cause.getMessage() : "");
        };
    }
}
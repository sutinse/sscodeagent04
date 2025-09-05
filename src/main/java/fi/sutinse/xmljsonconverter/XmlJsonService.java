package fi.sutinse.xmljsonconverter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import jakarta.enterprise.context.ApplicationScoped;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@ApplicationScoped
public class XmlJsonService {

    private final XmlMapper xmlMapper = new XmlMapper();
    private final ObjectMapper jsonMapper = new ObjectMapper();

    /**
     * Converts XML to JSON and compares with provided JSON.
     * Uses sealed types for better type safety and modern error handling.
     */
    public String convertXmlToJsonAndCompare(FileUploadForm form) {
        return processConversion(form).toResponse();
    }

    /**
     * Internal method that returns a ConversionResult for better type safety.
     */
    private ConversionResult processConversion(FileUploadForm form) {
        try {
            validateInputs(form);

            // Convert XML to JSON using streaming for memory efficiency
            String convertedJson = convertXmlToJsonStream(form.xmlFile());

            // Read provided JSON using optimized streaming
            String providedJson = readJsonStreamOptimized(form.jsonFile());

            // Compare the JSONs and generate report
            String report = compareJsonsWithModernFormatting(convertedJson, providedJson);

            return new ConversionResult.Success(convertedJson, providedJson, report);

        } catch (IllegalArgumentException e) {
            return new ConversionResult.Failure("Invalid input: " + e.getMessage(), e);
        } catch (Exception e) {
            return new ConversionResult.Failure("Processing error: " + e.getMessage(), e);
        }
    }

    private void validateInputs(FileUploadForm form) {
        if (form.xmlFile() == null) {
            throw new IllegalArgumentException("XML file is required");
        }
        if (form.jsonFile() == null) {
            throw new IllegalArgumentException("JSON file is required");
        }
    }

    /**
     * Converts XML to JSON using streaming for better memory efficiency.
     * Uses try-with-resources for automatic resource management.
     */
    private String convertXmlToJsonStream(InputStream xmlInputStream) throws Exception {
        try (xmlInputStream) {
            // Use Jackson XML mapper to read XML and convert to JSON
            // This is more reliable than manual token parsing
            JsonNode xmlNode = xmlMapper.readTree(xmlInputStream);
            return jsonMapper.writeValueAsString(xmlNode);
        }
    }

    /**
     * Reads JSON using optimized streaming for better performance.
     * Uses modern stream operations for memory efficiency.
     */
    private String readJsonStreamOptimized(InputStream jsonInputStream) throws Exception {
        try (jsonInputStream;
             var reader = new BufferedReader(
                 new InputStreamReader(jsonInputStream, StandardCharsets.UTF_8))) {
            
            // Use stream operations for more efficient processing
            String jsonContent = reader.lines()
                    .collect(StringBuilder::new, 
                            (sb, line) -> sb.append(line).append('\n'),
                            StringBuilder::append)
                    .toString();
            
            // Validate that it's proper JSON and normalize formatting
            JsonNode jsonNode = jsonMapper.readTree(jsonContent);
            return jsonMapper.writeValueAsString(jsonNode);
        }
    }

    /**
     * Compares JSONs and generates a report using text blocks and pattern matching.
     * Uses modern Java features for cleaner code and better performance.
     */
    private String compareJsonsWithModernFormatting(String convertedJson, String providedJson) {
        var result = new StringBuilder();
        
        // Use text blocks for better readability (Java 13+)
        result.append("""
                # XML to JSON Conversion and Comparison Report
                
                """);

        // Enhanced comparison with better error handling
        ComparisonOutcome outcome = performComparison(convertedJson, providedJson);
        
        // Pattern matching for cleaner control flow (Java 17+)
        result.append(switch (outcome) {
            case ComparisonOutcome.Match() -> """
                    ## ✅ Comparison Result: MATCH
                    
                    The converted JSON matches the provided JSON (ignoring whitespace and field order).
                    
                    """;
            case ComparisonOutcome.Difference(var message) -> """
                    ## ❌ Comparison Result: DIFFERENCES FOUND
                    
                    ### Differences:
                    
                    %s
                    
                    """.formatted(message);
            case ComparisonOutcome.Error(var errorMessage) -> """
                    ## ⚠️ Comparison Error
                    
                    Error during JSON comparison: %s
                    
                    """.formatted(errorMessage);
        });

        // Add previews of both JSONs (truncated for large files)
        result.append(formatJsonPreview("Converted JSON Preview", convertedJson));
        result.append(formatJsonPreview("Provided JSON Preview", providedJson));

        return result.toString();
    }

    /**
     * Performs JSON comparison and returns a sealed type result.
     */
    private ComparisonOutcome performComparison(String convertedJson, String providedJson) {
        try {
            JSONAssert.assertEquals(providedJson, convertedJson, JSONCompareMode.LENIENT);
            return new ComparisonOutcome.Match();
        } catch (AssertionError e) {
            return new ComparisonOutcome.Difference(e.getMessage());
        } catch (Exception e) {
            return new ComparisonOutcome.Error(e.getMessage());
        }
    }

    /**
     * Sealed interface for comparison outcomes.
     */
    private sealed interface ComparisonOutcome
            permits ComparisonOutcome.Match, ComparisonOutcome.Difference, ComparisonOutcome.Error {
        record Match() implements ComparisonOutcome {}
        record Difference(String message) implements ComparisonOutcome {}
        record Error(String message) implements ComparisonOutcome {}
    }

    /**
     * Formats JSON preview using text blocks and efficient string operations.
     */
    private String formatJsonPreview(String title, String json) {
        return """
                ## %s
                
                ```json
                %s
                ```
                
                """.formatted(title, truncateForDisplay(formatJson(json)));
    }

    /**
     * Formats JSON with pretty printing, using Optional for better null handling.
     */
    private String formatJson(String json) {
        return Optional.ofNullable(json)
                .map(j -> {
                    try {
                        JsonNode jsonNode = jsonMapper.readTree(j);
                        return jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode);
                    } catch (Exception e) {
                        return j; // Return original if formatting fails
                    }
                })
                .orElse("null");
    }

    /**
     * Truncates content for display using efficient string operations and ternary.
     */
    private String truncateForDisplay(String content) {
        final int MAX_LENGTH = 1000;
        return content.length() <= MAX_LENGTH 
                ? content 
                : content.substring(0, MAX_LENGTH) + "\n... (truncated for display)";
    }
}
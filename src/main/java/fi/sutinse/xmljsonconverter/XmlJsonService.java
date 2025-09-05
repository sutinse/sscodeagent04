package fi.sutinse.xmljsonconverter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import jakarta.enterprise.context.ApplicationScoped;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import java.io.*;
import java.nio.charset.StandardCharsets;

@ApplicationScoped
public class XmlJsonService {

    private final XmlMapper xmlMapper = new XmlMapper();
    private final ObjectMapper jsonMapper = new ObjectMapper();

    public String convertXmlToJsonAndCompare(FileUploadForm form) throws Exception {
        validateInputs(form);

        // Convert XML to JSON using streaming for memory efficiency
        String convertedJson = convertXmlToJsonStream(form.xmlFile);

        // Read provided JSON
        String providedJson = readJsonStream(form.jsonFile);

        // Compare the JSONs
        String comparisonResult = compareJsons(convertedJson, providedJson);

        return comparisonResult;
    }

    private void validateInputs(FileUploadForm form) {
        if (form.xmlFile == null) {
            throw new IllegalArgumentException("XML file is required");
        }
        if (form.jsonFile == null) {
            throw new IllegalArgumentException("JSON file is required");
        }
    }

    private String convertXmlToJsonStream(InputStream xmlInputStream) throws Exception {
        try (xmlInputStream) {
            // Use Jackson XML mapper to read XML and convert to JSON
            // This is more reliable than manual token parsing
            JsonNode xmlNode = xmlMapper.readTree(xmlInputStream);
            return jsonMapper.writeValueAsString(xmlNode);
        }
    }

    private String readJsonStream(InputStream jsonInputStream) throws Exception {
        try (jsonInputStream;
             BufferedReader reader = new BufferedReader(
                 new InputStreamReader(jsonInputStream, StandardCharsets.UTF_8))) {
            
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
            
            // Validate that it's proper JSON
            JsonNode jsonNode = jsonMapper.readTree(sb.toString());
            return jsonMapper.writeValueAsString(jsonNode);
        }
    }

    private String compareJsons(String convertedJson, String providedJson) throws Exception {
        StringBuilder result = new StringBuilder();
        result.append("# XML to JSON Conversion and Comparison Report\n\n");

        try {
            // Use JSONAssert to compare with lenient mode (ignores whitespace and order)
            JSONAssert.assertEquals(providedJson, convertedJson, JSONCompareMode.LENIENT);
            
            result.append("## ✅ Comparison Result: MATCH\n\n");
            result.append("The converted JSON matches the provided JSON (ignoring whitespace and field order).\n\n");

        } catch (AssertionError e) {
            result.append("## ❌ Comparison Result: DIFFERENCES FOUND\n\n");
            result.append("### Differences:\n\n");
            result.append(e.getMessage()).append("\n\n");
        } catch (Exception e) {
            result.append("## ⚠️ Comparison Error\n\n");
            result.append("Error during JSON comparison: ").append(e.getMessage()).append("\n\n");
        }

        // Add previews of both JSONs (truncated for large files)
        result.append("## Converted JSON Preview\n\n");
        result.append("```json\n");
        result.append(truncateForDisplay(formatJson(convertedJson)));
        result.append("\n```\n\n");

        result.append("## Provided JSON Preview\n\n");
        result.append("```json\n");
        result.append(truncateForDisplay(formatJson(providedJson)));
        result.append("\n```\n");

        return result.toString();
    }

    private String formatJson(String json) {
        try {
            JsonNode jsonNode = jsonMapper.readTree(json);
            return jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode);
        } catch (Exception e) {
            return json; // Return original if formatting fails
        }
    }

    private String truncateForDisplay(String content) {
        final int MAX_LENGTH = 1000;
        if (content.length() <= MAX_LENGTH) {
            return content;
        }
        return content.substring(0, MAX_LENGTH) + "\n... (truncated for display)";
    }
}
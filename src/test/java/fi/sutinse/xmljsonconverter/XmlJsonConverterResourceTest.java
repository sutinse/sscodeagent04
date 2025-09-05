package fi.sutinse.xmljsonconverter;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;

@QuarkusTest
public class XmlJsonConverterResourceTest {

    @Test
    public void testJsonEndpointWithMatchingData() {
        // Using text blocks (Java 13+) for better readability
        String requestBody = """
                {
                  "xmlContent": "<person><name>John</name><age>30</age></person>",
                  "jsonContent": "{\\"name\\":\\"John\\",\\"age\\":\\"30\\"}"
                }
                """;

        given()
                .when()
                .contentType("application/json")
                .body(requestBody)
                .post("/convert/json")
                .then()
                .statusCode(200)
                .body(containsString("MATCH"));
    }

    @Test
    public void testJsonEndpointWithDifferentData() {
        String requestBody = """
                {
                  "xmlContent": "<person><name>John</name><age>30</age></person>",
                  "jsonContent": "{\\"name\\":\\"Jane\\",\\"age\\":\\"25\\"}"
                }
                """;

        given()
                .when()
                .contentType("application/json")
                .body(requestBody)
                .post("/convert/json")
                .then()
                .statusCode(200)
                .body(containsString("DIFFERENCES FOUND"));
    }

    @Test
    public void testJsonEndpointWithMissingXml() {
        String requestBody = """
                {
                  "jsonContent": "{\\"name\\":\\"John\\",\\"age\\":\\"30\\"}"
                }
                """;

        given()
                .when()
                .contentType("application/json")
                .body(requestBody)
                .post("/convert/json")
                .then()
                .statusCode(400); // Now correctly returns 400 due to improved validation in record
    }

    @Test
    public void testJsonEndpointWithComplexXml() {
        String requestBody = """
                {
                  "xmlContent": "<company><employees><employee><name>John</name><department>IT</department></employee><employee><name>Jane</name><department>HR</department></employee></employees></company>",
                  "jsonContent": "{\\"employees\\":{\\"employee\\":[{\\"name\\":\\"John\\",\\"department\\":\\"IT\\"},{\\"name\\":\\"Jane\\",\\"department\\":\\"HR\\"}]}}"
                }
                """;

        given()
                .when()
                .contentType("application/json")
                .body(requestBody)
                .post("/convert/json")
                .then()
                .statusCode(200)
                .body(containsString("XML to JSON Conversion and Comparison Report"));
    }
}